package my.service.services;

import my.service.entities.*;
import my.service.repositories.ImmobileDAO;
import my.service.repositories.PersonaDAO;
import my.service.repositories.UserDAO;
import my.service.repositories.VisitaDAO;
import my.service.transporters.VisitaTrans;
import my.service.types.UserType;
import my.service.utilities.*;
import my.service.wrappers.VisitaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class VisitaServiceImpl implements VisitaService{

    @Autowired
    private VisitaDAO visitaDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PersonaDAO personaDAO;

    @Autowired
    private ImmobileDAO immobileDAO;

    @Override
    @Transactional
    public ResponseList getListVisite(String filter, String value, String startDate, String endDate, String sort,
                                           Integer pageNumber, Integer numberOfResults, Integer immobile, Integer persona) {
        QueryClause whereClause = ListHandler.getQueryWhereClause(Visita.class, filter, "", 0, value, startDate, endDate, "", "");
        String sortClause = ListHandler.getSortClause(Visita.class, sort);
        if(immobile!=null){
            whereClause.setQueryText(this.updateTextQueryClause(whereClause.getQueryText()));
            whereClause.setQueryText(whereClause.getQueryText()+" immobile = "+immobile+" ");
        }
        if(persona!=null){
            whereClause.setQueryText(this.updateTextQueryClause(whereClause.getQueryText()));
            whereClause.setQueryText(whereClause.getQueryText()+" persona = "+persona+" ");
        }
        List<Visita> visite = this.visitaDAO.getListVisite(whereClause, sortClause, pageNumber, numberOfResults);
        List<VisitaTrans> res = new ArrayList<>();
        for(Visita visita : visite){
            res.add(new VisitaTrans(visita));
        }
        Long numbersOfResults = this.visitaDAO.getListNumber(whereClause);
        return new ResponseList(numbersOfResults, res);
    }

    @Override
    @Transactional
    public VisitaTrans getVisita(Integer id) {
        return new VisitaTrans(this.retrieveVisita(id));
    }

    @Override
    @Transactional
    public VisitaTrans addVisita(VisitaWrapper visitaWrapper, String userData) {
        // valida quando clause
        this.validateWhenAttribute(visitaWrapper.getQuando(), true);
        // retrieve persona - può essere null
        Persona persona = null;
        if(visitaWrapper.getIdPersona()!=null){
            persona = this.personaDAO.getOnePersona(visitaWrapper.getIdPersona());
            if(persona==null) throw new BadRequestException("Persona indicata inesistente");
        }
        // retrieve immobile - può essere null
        Immobile immobile = null;
        if(visitaWrapper.getIdImmobile()!=null){
            immobile = this.immobileDAO.getOneImmobile(visitaWrapper.getIdImmobile());
            if(immobile==null) throw new BadRequestException("Immobile indicato inesistente");
        }
        // note null + dove null + persona null - throw error
        if((visitaWrapper.getNote()==null || visitaWrapper.getNote().trim().equals("")) &&
                (visitaWrapper.getDove()==null || visitaWrapper.getDove().trim().equals("")) &&
                persona==null && immobile == null){
            throw new BadRequestException("E' obbligatorio indicare almeno uno tra luogo, persona, immobile o note.");
        }
        // retrieve user - se null imposti chi ha creato l'evento
        User user = null;
        if(visitaWrapper.getIdUser()!=null) user = this.userDAO.getOneUser(visitaWrapper.getIdUser());
        if(user==null) user = this.retrieveUser(userData);
        // se dove non esplicitamente indicato ma presente immobile è l'indirizzo dell'immobile
        String dove = visitaWrapper.getDove();
        if((dove==null || dove.trim().equals("") ) && immobile!=null){
            dove = immobile.getIndirizzo()+" ("+immobile.getComune()+")";
        }
        // create visita
        Visita visita = new Visita(persona, immobile, user,
                dove, visitaWrapper.getQuando(),
                visitaWrapper.getNote());
        // if persona crea evento
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String res = formatter.format(visita.getQuando());
        String message = "Visita fissata il "+res+" per "+visita.getDove();
        this.postNewEvent(persona, immobile, user, message);
        // persist
        return new VisitaTrans(this.visitaDAO.addVisita(visita));
    }

    @Override
    @Transactional
    public VisitaTrans patchVisita(Integer id, VisitaWrapper visitaWrapper, String userData) {
        // retrieve visita originale
        Visita originalVisita = this.retrieveVisita(id);
        // crea patchVisita solo con dove, quando, note (persona immobile user = null)
        Visita patchVisita = new Visita(visitaWrapper.getDove(), visitaWrapper.getQuando(),
                visitaWrapper.getNote());
        // valida quando clause
        if(visitaWrapper.getQuando()!=null && !originalVisita.getQuando().isEqual(visitaWrapper.getQuando())){
            this.validateWhenAttribute(visitaWrapper.getQuando(), false);
        }
        // set Immobile
        Immobile immobile = null;
        if(visitaWrapper.getIdImmobile()!=null){
            immobile = this.immobileDAO.getOneImmobile(visitaWrapper.getIdImmobile());
            if(immobile==null) throw new BadRequestException("Immobile indicato inesistente");
            patchVisita.setImmobile(immobile);
            // fatto l'immobile se non c'è dove metti l'indirizzo immobile
            String dove = visitaWrapper.getDove();
            if((dove==null || dove.trim().equals("") ) && immobile!=null){
                dove = immobile.getIndirizzo()+" ("+immobile.getComune()+")";
                patchVisita.setDove(dove);
            }
        }
        // set Persona
        Persona persona = null;
        if(visitaWrapper.getIdPersona()!=null) {
            persona = this.personaDAO.getOnePersona(visitaWrapper.getIdPersona());
            if(persona==null) throw new BadRequestException("Persona indicata inesistente");
            patchVisita.setPersona(persona);
        }
        // gestisci eventi
        this.handleEvents(originalVisita, patchVisita, userData);
        patchVisita.setImmobile(null);
        patchVisita.setPersona(null);
        // patch
        ObjectPatcher.patchObject(Visita.class, originalVisita, patchVisita);
        originalVisita.setImmobile(immobile);
        originalVisita.setPersona(persona);
        // if user different set
        User user = null;
        if(visitaWrapper.getIdUser()!=null){
            user = this.userDAO.getOneUser(visitaWrapper.getIdUser());
            if(user==null) throw new BadRequestException("User indicato inesistente");
        }else{
            user = this.retrieveUser(userData);
        }
        originalVisita.setUser(user);
        // return
        return new VisitaTrans(originalVisita);
    }

    @Override
    @Transactional
    public String deleteVisita(Integer id, String userData) {
        // retrieve visita
        Visita visita = this.retrieveVisita(id);
        // check admin o user indicato
        User user = this.retrieveUser(userData);
        if(visita.getUser()!=null && !user.getId().equals(visita.getUser().getId()) && !user.getRole().equalsIgnoreCase(UserType.ADMIN.name())){
            throw new ForbiddenException(user.getName()+" non è abilitato a cancellare visite");
        }
        // se c'è una persona delete event
        Persona persona = visita.getPersona();
        if(persona!=null){
            this.postNewEvent(persona, visita.getImmobile(), user, "Visita annullata");
        }
        //delete visita
        this.visitaDAO.deleteVisita(visita);
        return "Visita con id "+id+" cancellata con successo";
    }

    private Visita retrieveVisita(Integer id){
        Visita visita = this.visitaDAO.getVisita(id);
        if(visita==null) throw new ItemNotFoundException("Visita con id "+id+" non presente");
        return visita;
    }

    private User retrieveUser(String userData){
        User user = UserRetriever.retrieveUser(userData);
        User userFound = this.userDAO.getOneUser(user.getId());
        if(userFound==null) this.userDAO.addUser(user);
        return userFound;
    }

    private void validateWhenAttribute(LocalDateTime whenAttribute, Boolean isNewVisit){
        // quando è obbligatorio
        if(whenAttribute==null){
            if(!isNewVisit) return;
            throw new BadRequestException("E' obbligatorio indicare quando sarà la visita");
        }
        // nel passato non si può
        if(whenAttribute.isBefore(LocalDateTime.now())) throw new BadRequestException("Non si possono mettere appuntamenti nel passato");
        // domenica non si accettano appuntamenti
        if(whenAttribute.getDayOfWeek().toString().equals("SUNDAY")) throw new BadRequestException("Non si accettano appuntamenti di domenica");
        // i secondi si settano a zero
        whenAttribute = whenAttribute.withSecond(0);
        // i minuti possono essere solo 00 - 15 - 30 - 45
        if(whenAttribute.getMinute()!=0 && whenAttribute.getMinute()!=15
        && whenAttribute.getMinute()!=30 && whenAttribute.getMinute()!=45){
            throw new BadRequestException("Minuti indicati per l'appuntamento non validi");
        }
        if(whenAttribute.getHour() < 8 || whenAttribute.getHour() > 20){
            throw new BadRequestException("Si accettano appuntamenti solo dalle 8 alle 20");
        }
        // se ci sono già due res alla stessa ora throw error
        ZonedDateTime correctMoment = ZonedDateTime.of(whenAttribute, ZoneId.systemDefault());
        LocalDateTime UTCDateTime = LocalDateTime.ofInstant(correctMoment.toInstant(), ZoneOffset.UTC);
        QueryClause whereClause = new QueryClause(" WHERE quando = '"+UTCDateTime+"'", new HashMap<>());
        List<Visita> visite = this.visitaDAO.getListVisite(whereClause, "", 1, 1000);
        if(visite.size()>1)throw new BadRequestException("Orario non disponibile, ci sono già troppi impegni.");
    }

    private void postNewEvent(Persona persona, Immobile immobile, User user, String message){
        Evento evento = new Evento(
                LocalDateTime.now(),
                message,
                persona,
                immobile==null ? null : immobile,
                user
        );
        persona.addEvento(evento);
    }

    private void handleEvents(Visita oldVisita, Visita newVisita, String userData){
        // definisci ora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime data = newVisita.getQuando()!=null ? newVisita.getQuando() : oldVisita.getQuando();
        String ora = formatter.format(data);
        // definisci luogo
        String luogo = "";
        if(newVisita.getDove()!=null){
            luogo = newVisita.getDove();
        }else if(newVisita.getImmobile()!=null){
            luogo = newVisita.getImmobile().getIndirizzo()+" ("+newVisita.getImmobile().getComune()+")";
        }else if(oldVisita.getDove()!=null){
            luogo = oldVisita.getDove();
        }
        // definisci immobile
        Immobile immobile = null;
        if(newVisita.getImmobile()!=null){
            immobile = newVisita.getImmobile();
        }else if(oldVisita.getImmobile()!=null){
            immobile = oldVisita.getImmobile();
        }
        // definisci user
        User user = newVisita.getUser()!=null ? newVisita.getUser() : this.retrieveUser(userData);
        // definisci persone
        Persona oldPersona = oldVisita.getPersona();
        Persona newPersona = newVisita.getPersona();
        // se non ci sono persona ritorna
        if(oldPersona==null && newPersona==null) {
            return;
        // se c'è solo la vecchia persona oppure le persona sono la stessa indica cambio visita
        } else if(oldPersona!=null && newPersona==null ||
                oldPersona!=null && newPersona!=null &&
                oldPersona.getId()-newPersona.getId()==0){
            String message = "Visita modificata: ora fissata il "+ora;
            if(!luogo.trim().equals("")){
                message = message+" in "+luogo;
            }
            this.postNewEvent(oldPersona, immobile, user, message);
        // se c'è solo la nuova persona crea
        } else if(oldPersona==null && newPersona!=null){
            String message = "Visita fissata il "+ora;
            if(!luogo.trim().equals("")){
                message = message+" in "+luogo;
            }
            this.postNewEvent(newPersona, immobile, user, message);
        // se ci sono entrambe due nuovi eventi
        }else if(oldPersona!=null && newPersona!=null && oldPersona.getId()- newPersona.getId()!=0){
            Immobile oldImmobile = oldVisita.getImmobile()==null ? null : oldVisita.getImmobile();
            this.postNewEvent(oldPersona, oldImmobile, user, "Visita annullata");
            String message = "Visita fissata il "+ora;
            if(!luogo.trim().equals("")){
                message = message+" in "+luogo;
            }
            this.postNewEvent(newPersona, immobile, user, message);
        }
    }

    private String updateTextQueryClause(String queryText){
        if(queryText.trim().equals("")){
            return " WHERE ";
        }else{
            return queryText+" AND ";
        }
    }

}
