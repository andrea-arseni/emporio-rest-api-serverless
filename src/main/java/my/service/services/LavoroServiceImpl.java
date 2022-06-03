package my.service.services;

import my.service.entities.Lavoro;
import my.service.entities.Step;
import my.service.entities.User;
import my.service.repositories.LavoroDAO;
import my.service.repositories.StepDAO;
import my.service.repositories.UserDAO;
import my.service.transporters.LavoroTrans;
import my.service.types.LavoroType;
import my.service.types.UserType;
import my.service.utilities.*;
import my.service.wrappers.StepWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class LavoroServiceImpl implements LavoroService {

    @Autowired
    private LavoroDAO lavoroDAO;

    @Autowired
    private StepDAO stepDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    @Transactional
    public ResponseList getAllLavori(String filter, String value, String sort, Integer pageNumber, Integer numberOfResults) {
        QueryClause whereClause = ListHandler.getQueryWhereClause(Lavoro.class, filter, "", 0, value, "", "", "", "");
        String sortClause = ListHandler.getSortClause(Lavoro.class, sort);
        List<Lavoro> lavori = this.lavoroDAO.getAllLavori(whereClause, sortClause, pageNumber, numberOfResults);
        List<LavoroTrans> res = new ArrayList<>();
        for(Lavoro lavoro : lavori){
            res.add(new LavoroTrans(lavoro));
        }
        Long count = this.lavoroDAO.getListNumber(whereClause);
        return new ResponseList(count, res);
    }

    @Override
    @Transactional
    public Lavoro getOneLavoro(Integer id) {
        Lavoro lavoroFound = this.lavoroDAO.getOneLavoro(id);
        if(lavoroFound==null) throw new ItemNotFoundException("Lavoro con id "+id+" non presente");
        List<Step> steps = lavoroFound.getSteps();
        steps.sort(Comparator.comparing(Step::getId).reversed());
        if(steps.size()>20) steps = steps.subList(0,20);
        lavoroFound.setSteps(steps);
        return lavoroFound;
    }

    @Override
    @Transactional
    public Lavoro postLavoro(Lavoro lavoro, String userData) {

        lavoro.setStatus(LavoroType.APERTO.name());
        lavoro.setSteps(null);

        User user = this.retrieveUser(userData);
        Lavoro postedLavoro = this.lavoroDAO.addLavoro(lavoro);
        Step step = this.stepDAO.addStep(new Step("Apertura lavoro", LocalDateTime.now(), postedLavoro, user));
        postedLavoro.addStep(step);
        return postedLavoro;
    }

    @Override
    @Transactional
    public Lavoro patchLavoro(Integer id, Lavoro patchLavoro, String userData) {

        // retrieve originalObject, if not found throw error
        Lavoro originalLavoro = this.lavoroDAO.getOneLavoro(id);
        if(originalLavoro==null) throw new ItemNotFoundException("Lavoro con id "+id+" non presente");

        // l'oggetto non può cambiare gli step
        patchLavoro.setSteps(null);

        // crei un nuovo step, in ogni caso
        User user = this.retrieveUser(userData);
        Step step = new Step(null, LocalDateTime.now(), originalLavoro, user);
        String message = "";

        // parte status
        String status = patchLavoro.getStatus();

        if(status!=null){
            patchLavoro.setStatus(patchLavoro.getStatus().toUpperCase());
            Boolean statusValid = false;
            for (LavoroType lavoroType : LavoroType.values()){
                if(lavoroType.name().equalsIgnoreCase("aperto")) continue;
                if(lavoroType.name().equalsIgnoreCase(status)) statusValid = true;
            }
            if(!statusValid) throw new BadRequestException("Status indicato non valido: può essere \"attivo\", \"bloccato\", \"annullato\", \"finito\"");
        }

        if(!status.equalsIgnoreCase(originalLavoro.getStatus())){
            message = message+"["+originalLavoro.getStatus()+" -> "+status.toUpperCase()+"]";
        }

        // parte titolo
        String titolo = patchLavoro.getTitolo();

        if(titolo!=null && titolo.length()>45)
            throw new BadRequestException("Il titolo scelto è troppo lungo, usare al massimo 45 caratteri");

        String originalTitolo = originalLavoro.getTitolo();
        if(titolo!=null && !titolo.equalsIgnoreCase(originalTitolo)){
            if(originalTitolo.length()>45) originalTitolo = originalTitolo.substring(0,42)+"...";
            message = message + " Titolo :\""+originalTitolo+"\" cambiato";
        }

        step.setDescrizione(message);
        this.stepDAO.addStep(step);

        return (Lavoro) ObjectPatcher.patchObject(Lavoro.class, originalLavoro, patchLavoro);
    }

    @Override
    @Transactional
    public String deleteLavoro(Integer id, String userData) {

        User user = this.retrieveUser(userData);
        if(!user.getRole().equalsIgnoreCase(UserType.ADMIN.name()))
            throw new ForbiddenException("User non ha i diritti per effettuare questa operazione");

        Lavoro lavoroFound = this.lavoroDAO.getOneLavoro(id);
        if(lavoroFound==null) throw new ItemNotFoundException("Lavoro con id "+id+" non presente");
        this.lavoroDAO.deleteLavoro(lavoroFound);
        return "Lavoro con id "+id+": '"+lavoroFound.getTitolo()+"' cancellato con successo";
    }

    @Override
    @Transactional
    public ResponseList getAllSteps(Integer idLavoro,
                                  String filter,
                                  String value,
                                  String startDate,
                                  String endDate,
                                  String sort,
                                  Integer pageNumber,
                                  Integer numberOfResults) {
        Lavoro lavoroFound = this.lavoroDAO.getOneLavoro(idLavoro);
        if(lavoroFound==null) throw new ItemNotFoundException("Lavoro con id "+idLavoro+" non presente");
        QueryClause whereClause = ListHandler.getQueryWhereClause(Step.class, filter, "lavoro", idLavoro, value, startDate, endDate, "", "");
        String sortClause = ListHandler.getSortClause(Step.class, sort);
        List<Step> steps = this.stepDAO.getSteps(whereClause, sortClause, pageNumber, numberOfResults);
        Long count = this.stepDAO.getListNumber(whereClause);
        return new ResponseList(count, steps);
    }

    @Override
    @Transactional
    public Step getOneStep(Integer idLavoro, Integer idStep) {
        Lavoro lavoroFound = this.lavoroDAO.getOneLavoro(idLavoro);
        if(lavoroFound==null) throw new ItemNotFoundException("Lavoro con id "+idLavoro+" non presente");
        List<Step> steps = lavoroFound.getSteps();
        for(Step step: steps){
            if(step.getId()==idStep) return step;
        }
        throw new ItemNotFoundException("Step con id "+idStep+" non presente");
    }

    @Override
    @Transactional
    public Step postStep(Integer idLavoro, StepWrapper stepWrapper, String userData) {
        // trova il lavoro - se non c'è errore
        Lavoro lavoroFound = this.lavoroDAO.getOneLavoro(idLavoro);
        if(lavoroFound==null) throw new ItemNotFoundException("Lavoro con id "+idLavoro+" non presente");
        // check status - deve essere valido per il lavoro
        if(stepWrapper.getLavoroStatus()!=null){
            String status = stepWrapper.getLavoroStatus().toUpperCase().replace("_", " ");
            Boolean statusValid = false;
            for(LavoroType validStatus : LavoroType.values()){
                if(status.equals(validStatus.name())) statusValid = true;
            }
            if(!statusValid) throw new BadRequestException("Status indicato non valido: può essere \"attivo\", \"bloccato\", \"annullato\", \"finito\"");
        }
        // patch lavoro
        Lavoro patchLavoro = new Lavoro(null, stepWrapper.getLavoroStatus());
        ObjectPatcher.patchObject(Lavoro.class, lavoroFound, patchLavoro);
        // create step
        User user = this.retrieveUser(userData);
        Step step = new Step(stepWrapper.getStepMessage(), LocalDateTime.now(), lavoroFound, user);
        lavoroFound.addStep(step);
        return step;
    }

    @Override
    @Transactional
    public Step patchStep(Integer idLavoro, Integer idStep, Step patchStep, String userData) {
        // cerca il lavoro, se non lo trovi raise exception
        Lavoro lavoroFound = this.lavoroDAO.getOneLavoro(idLavoro);
        if(lavoroFound==null) throw new ItemNotFoundException("Lavoro con id "+idLavoro+" non presente");
        // cerca lo step se non lo trovi raise exception
        Step stepFound = null;
        List<Step> steps = lavoroFound.getSteps();
        for(Step step: steps){
            if(step.getId()==idStep) stepFound = step;
        }
        if(stepFound==null) throw new ItemNotFoundException("Step con id "+idStep+" non presente");
        // retrieve user, se non è lo stesso e non è admin raise exception
        User user = this.retrieveUser(userData);
        if(!user.getId().equalsIgnoreCase(stepFound.getUser().getId()) && !user.getRole().equalsIgnoreCase(UserType.ADMIN.name())){
            throw new ForbiddenException(user.getName()+" non ha i diritti per modificare lo step");
        }
        // si può cambiare solo la descrizione - si sovrascrive lo user
        stepFound.setDescrizione(patchStep.getDescrizione());
        stepFound.setUser(this.retrieveUser(userData));
        return stepFound;
    }

    @Override
    @Transactional
    public String deleteStep(Integer idLavoro, Integer idStep, String userData) {
        // retrieve lavoro, se non c'è throw error
        Lavoro lavoroFound = this.lavoroDAO.getOneLavoro(idLavoro);
        if(lavoroFound==null) throw new ItemNotFoundException("Lavoro con id "+idLavoro+" non presente");
        // retrieve step se non c'è raise exception
        Step stepFound = null;
        List<Step> steps = lavoroFound.getSteps();
        for(Step step: steps){
            if(step.getId()==idStep) stepFound = step;
        }
        if(stepFound==null) throw new ItemNotFoundException("Step con id "+idStep+" non presente");
        // retrieve user, se non è lo stesso e non è admin raise exception
        User user = this.retrieveUser(userData);
        if(!user.getId().equalsIgnoreCase(stepFound.getUser().getId()) && !user.getRole().equalsIgnoreCase("admin")){
            throw new ForbiddenException(user.getName()+" non ha i diritti per cancellare lo step");
        }
        if(!this.stepDAO.deleteStep(idStep)) throw new ItemNotFoundException("Step con id "+idStep+" non trovato");
        return "Step con id "+idStep+" cancellato con successo";
    }

    private User retrieveUser(String userData){
        User userFound = this.userDAO.getOneUser(userData);
        if(userFound==null) throw new BadRequestException("User non trovato");
        return userFound;
    }
}
