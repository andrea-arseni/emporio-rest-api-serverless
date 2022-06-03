package my.service.services;

import my.service.entities.*;
import my.service.repositories.*;
import my.service.transporters.EventoTrans;
import my.service.transporters.FileTrans;
import my.service.transporters.ImmobileTrans;
import my.service.transporters.PersonaTrans;
import my.service.types.*;
import my.service.utilities.*;
import my.service.wrappers.EventoWrapper;
import my.service.wrappers.PersonaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;



@Service
public class PersonaServiceImpl implements PersonaService {

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    EventoDAO eventoDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    ImmobileDAO immobileDAO;

    @Autowired
    FileDAO fileDAO;

    @Autowired
    AWSS3FileService awss3FileService;

    @Override
    @Transactional
    public ResponseList getListPersone(String filter, String value, String sort, Integer pageNumber, Integer numberOfResults) {
        QueryClause whereClause = ListHandler.getQueryWhereClause(Persona.class, filter, "", 0, value, "", "", "", "");
        String sortClause = ListHandler.getSortClause(Persona.class, sort);
        List<Persona> persone = this.personaDAO.getListPersone(whereClause, sortClause, pageNumber, numberOfResults);
        List<PersonaTrans> res = new ArrayList<>();
        for(Persona persona : persone){
            res.add(new PersonaTrans(persona));
        }
        Long numberRes = this.personaDAO.getListNumber(whereClause);
        return new ResponseList(numberRes, res);
    }

    @Override
    @Transactional
    public PersonaTrans getPersona(Integer id) {
        Persona persona = this.retrievePersona(id);
        PersonaTrans res = new PersonaTrans(persona);
        QueryClause whereClause = new QueryClause(" WHERE proprietario = "+id+" ", new HashMap<>());
        List<Immobile> immobili = this.immobileDAO.getAllImmobili(
                whereClause,
                "",
                1,
                1000);
        List<ImmobileTrans> immobiliProprieta = new ArrayList<>();
        for(Immobile immobile : immobili){
            immobiliProprieta.add(new ImmobileTrans(immobile));
        }
        res.setImmobili(immobiliProprieta);
        List<EventoTrans> eventi = new ArrayList<>();
        for(Evento evento : persona.getEventi()){
            eventi.add(new EventoTrans(evento));
        }
        eventi.sort(Comparator.comparing(EventoTrans::getId).reversed());
        if(eventi.size()>20) eventi = eventi.subList(0,20);
        res.setEventi(eventi);
        return res;
    }

    @Override
    @Transactional
    public PersonaTrans addPersona(PersonaWrapper personaWrapper, String userData, String forceOperation) {
        // crea persona
        Persona persona = new Persona(personaWrapper);
        // check already existing name or number or email
        if(!forceOperation.equals("true")) this.checkAlreadyExistentData(persona, null);
        // check provenienza
        this.checkProvenienza(persona);
        // check immobiliProprietà =! null ed effettiva esistenza
        this.handleRealEstateProperties(persona, personaWrapper.getImmobiliProprieta(), 0);
        // check immobileAffitto =! null ed effettiva esistenza
        this.handleRealEstateLocation(persona, personaWrapper.getImmobileAffitto());
        // check immobile interesse
        Immobile immobileInteresse = this.retrieveImmobileInteresse(personaWrapper);
        // crea evento di creazione ed assegna
        User user = this.retrieveUser(userData);
        Evento evento = new Evento(LocalDateTime.now(), "Persona creata", persona, immobileInteresse, user);
        persona.addEvento(evento);
        // correggi persona e persisti
        persona.setStatus(PersonaType.ATTIVA.name());
        Persona personaSalvata = this.personaDAO.addPersona(persona);
        // correggi immobili
        for(Immobile immobile : persona.getImmobili()){
            immobile.setProprietario(personaSalvata);
        }
        if(persona.getImmobileInquilino()!=null){
            persona.getImmobileInquilino().addInquilino(personaSalvata);
        }
        return new PersonaTrans(personaSalvata);
    }

    @Override
    @Transactional
    public String addPersona(PersonaWrapper personaWrapper){
        // check che abbia scritto nome ed almeno uno tra telefono ed email
        String telefono = personaWrapper.getTelefono()!=null ? personaWrapper.getTelefono() : null;
        String email = personaWrapper.getEmail()!=null ? personaWrapper.getEmail() : null;
        if(personaWrapper.getNome()==null || email==null && telefono==null){
            throw new BadRequestException("E' obbligatorio indicare il nome ed almeno un contatto tra telefono ed email");
        }
        // se esiste il telefono effettuare validazione
        if(telefono!=null && !telefono.matches("^(\\+\\d{1,3})?(\\d{8,12})$")){
            throw new BadRequestException("Numero di telefono invalido, per favore controllare");
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        if(email!=null && !email.matches(emailRegex)){
            throw new BadRequestException("Email in formato non valido, per favore ricontrollare");
        }
        // crea Persona estraendo solo nome, numero ed email - provenienza SITO - status ATTIVA
        Persona persona = new Persona(personaWrapper.getNome(), personaWrapper.getTelefono(), personaWrapper.getEmail(),
                    "ATTIVA", "SITO");
        // crea Evento creazione dal sito
        String descrizione = "Privato dal sito: "+personaWrapper.getNote();
        Evento evento = new Evento(LocalDateTime.now(), descrizione, persona, null, null);
        persona.addEvento(evento);
        this.personaDAO.addPersona(persona);
        return "Procedura completata con successo! Verrà richiamato al più presto da un nostro responsabile per organizzare un appuntamento gratuito";
    }

    @Override
    @Transactional
    public PersonaTrans patchPersona(Integer id, PersonaWrapper patchPersonaWrapper, String userData, String forceOperation) {
        // retrieve persona originale e persona corretta
        Persona originalPersona = this.retrievePersona(id);
        Persona patchPersona = new Persona(patchPersonaWrapper);
        // check ripetizione di dati univochi
        if(!forceOperation.equals("true")) this.checkAlreadyExistentData(patchPersona, id);
        // check status
        this.checkStatus(patchPersona);
        // check provenienza patchPersona
        this.checkProvenienza(patchPersona);
        // handle immobili di proprietà
        this.handleRealEstateProperties(patchPersona, patchPersonaWrapper.getImmobiliProprieta(), id);
        // handle immobile dove sta se è inquilino
        this.handleRealEstateLocation(patchPersona, patchPersonaWrapper.getImmobileAffitto());
        // get ImmobileInquilino e ImmobiliProprietà
        Immobile immobileInquilino = patchPersona.getImmobileInquilino();
        // togli proprietario agli immobili che non l'hanno più
        List<Immobile> immobiliProprieta = patchPersona.getImmobili();
        List<Immobile> immobiliOriginali = originalPersona.getImmobili();
        List<Immobile> immobiliConfermati = new ArrayList<>();
        for(Immobile nuovoImmobile : immobiliProprieta){
            for(Immobile originalImmobile : immobiliOriginali){
                if(originalImmobile.getId()-nuovoImmobile.getId()==0) immobiliConfermati.add(originalImmobile);
            }
        }
        immobiliOriginali.removeAll(immobiliConfermati);
        for(Immobile immobile : immobiliOriginali){
            immobile.setProprietario(null);
        }
        // effettua il patch
        patchPersona.setImmobileInquilino(null);
        patchPersona.setImmobili(null);
        ObjectPatcher.patchObject(Persona.class, originalPersona, patchPersona);
        originalPersona.setImmobili(new ArrayList<>());
        originalPersona.setImmobileInquilino(immobileInquilino);
        for(Immobile immobile : immobiliProprieta){
            originalPersona.addImmobile(immobile);
        }
        return new PersonaTrans(originalPersona);
    }

    @Override
    @Transactional
    public String deletePersona(Integer id, String userData) {
        Persona persona = this.retrievePersona(id);
        User user = this.retrieveUser(userData);
        if(!user.getRole().equalsIgnoreCase(UserType.ADMIN.name())){
            throw new ForbiddenException("User non abilitato alla cancellazione di persone");
        }
        this.personaDAO.deletePersona(persona);
        return persona.getNome()+" cancellato con successo";
    }

    @Override
    @Transactional
    public ResponseList getListEventi(Integer idPersona,
                                           String filter,
                                           String value,
                                           String startDate,
                                           String endDate,
                                           String sort,
                                           Integer pageNumber,
                                           Integer numberOfResults) {
        QueryClause whereClause = ListHandler.getQueryWhereClause(Evento.class, filter, "persona", idPersona, value, startDate, endDate, "", "");
        String sortClause = ListHandler.getSortClause(Evento.class, sort);
        List<Evento> eventi = this.eventoDAO.getEventList(whereClause, sortClause, pageNumber, numberOfResults);
        List<EventoTrans> res = new ArrayList<>();
        for(Evento evento : eventi){
            res.add(new EventoTrans(evento));
        }
        Long eventsNumber = this.eventoDAO.getListNumber(whereClause);
        return new ResponseList(eventsNumber, res);
    }

    @Override
    @Transactional
    public EventoTrans getEvento(Integer idPersona, Integer idEvento) {
        Persona persona = this.retrievePersona(idPersona);
        Evento evento = this.retrieveEvento(idEvento, persona);
        return new EventoTrans(evento);
    }

    @Override
    @Transactional
    public EventoTrans addEvento(Integer idPersona, EventoWrapper eventoWrapper, String userData) {
        // check esistenza persona
        Persona persona = this.retrievePersona(idPersona);
        // check esistenza immobile
        Immobile immobile = null;
        if(eventoWrapper.getIdImmobile()!=null) immobile = this.immobileDAO.getOneImmobile(eventoWrapper.getIdImmobile());
        // retrieve user
        User user = this.retrieveUser(userData);
        // if status changed update status
        if(eventoWrapper.getStatusPersona()!=null){
            Persona testPersona = new Persona();
            testPersona.setStatus(eventoWrapper.getStatusPersona());
            this.checkStatus(testPersona);
            // update persona
            persona.setStatus(eventoWrapper.getStatusPersona());
        }
        Evento evento = new Evento(LocalDateTime.now(), eventoWrapper.getDescrizione(), persona, immobile, user);
        evento = this.eventoDAO.addEvento(evento);
        return new EventoTrans(evento);
    }

    @Override
    @Transactional
    public EventoTrans patchEvento(Integer idPersona, Integer idEvento, EventoWrapper eventoWrapper, String userData) {
        // retrieve Persona ed evento originale
        Persona persona = this.retrievePersona(idPersona);
        Evento originalEvent = this.retrieveEvento(idEvento, persona);
        // check users if not admin and not the same throw error
        User user = this.retrieveUser(userData);
        if(!user.getRole().equalsIgnoreCase(UserType.ADMIN.name())){
            this.checkSameUser(userData, originalEvent, "modificare");
        }
        // prendi nuova descrizione
        Evento patchEvento = new Evento(eventoWrapper.getDescrizione());
        ObjectPatcher.patchObject(Evento.class, originalEvent, patchEvento);
        return new EventoTrans(originalEvent);
    }

    @Override
    @Transactional
    public String deleteEvento(Integer idPersona, Integer idEvento, String userData) {
        Persona persona = this.retrievePersona(idPersona);
        Evento evento = this.retrieveEvento(idEvento, persona);
        User user = this.retrieveUser(userData);
        if(!user.getRole().equalsIgnoreCase(UserType.ADMIN.name())){
            this.checkSameUser(userData, evento, "cancellare");
        }
        this.eventoDAO.removeEvento(idEvento);
        return "Evento con id "+evento.getId()+" cancellato con successo";
    }

    @Override
    @Transactional
    public void removeUnnecessaryPeople() {
        // retrieve people unnecessary
        String whereClause = " WHERE (status = 'NON_RICHIAMARE' OR status = 'RICHIAMA_LEI') AND proprietario = false AND inquilino = false " +
                "AND importante = false AND (ruolo IS NULL OR ruolo = '' )";
        List<Persona> personaInutili = this.personaDAO.getListPersone(new QueryClause(whereClause, new HashMap<>()), "", 1, 10000);
        if(personaInutili.size()==0) return;
        // get events for each of those people
        for(Persona personaInutile : personaInutili){
            List<Evento> eventi = personaInutile.getEventi();
            eventi.sort(Comparator.comparing(Evento::getData));
            Evento lastEvent = eventi.get(eventi.size()-1);
            LocalDate lastUpdateDate = lastEvent.getData().toLocalDate();
            // if last event is more than 90 days before now delete person
            if(DAYS.between(lastUpdateDate, LocalDate.now())>90){
                this.personaDAO.deletePersona(personaInutile);
            }
        }
    }

    @Override
    @Transactional
    public FileTrans getFile(Integer idPersona, Integer id) {
        String originalFileBucketName = this.awss3FileService.getOriginalFilesBucketName();
        // retrieve persona
        Persona persona = this.retrievePersona(idPersona);
        // retrieve fileDB
        File fileDB = this.getFileFromId(id, persona);
        // get codice bucket
        String key = fileDB.getCodiceBucket();
        // initialize byteArray
        byte[] byteArray = null;
        // if file == doc read doc
        byteArray = this.awss3FileService.readFile(key, originalFileBucketName);
        return new FileTrans(fileDB, byteArray);
    }

    @Override
    @Transactional
    public File addFile(Integer idPersona, MultipartFile multipartFile, String userData) {
        // get user
        User user = this.retrieveUser(userData);
        //check if it is admin
        if(!user.getRole().equals(UserType.ADMIN.name())) throw new BadRequestException("User non abilitato all'aggiunta del file");
        // retrieve persona
        Persona persona = this.retrievePersona(idPersona);
        // retrieve estensione
        String extension = FileUtils.getExtension(multipartFile);
        if(!extension.equals("pdf")) throw new BadRequestException("Estensione file non valida, può essere solo \"pdf\"");
        // check whether is already existent
        if(this.fileDAO.isFileAlreadyExisting(persona)) throw new BadRequestException("Identificativo già presente, cancellare il vecchio prima di inserire il nuovo");
        // definisci tipologia
        String tipologia = FileType.DOCUMENTO.name();
        // definisci posizione e nome e log message
        String nome = "identificativo.pdf";
        String codiceBucket = "persone/"+persona.getId() + "/" +nome;
        // converti multipartfile to file
        java.io.File file = null;
        try {
            file = FileUtils.convertMultipartToFile(multipartFile);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        // s3 operation
        try {
            this.awss3FileService.addFile(file, codiceBucket);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        // persist file
        File dbFile = new File(persona, tipologia, codiceBucket, nome);
        dbFile = this.fileDAO.addFile(dbFile);
        file.delete();
        return dbFile;
    }

    @Override
    @Transactional
    public String deleteFile(Integer idPersona, Integer id, String userData) {
        // check persona esista
        Persona persona = this.retrievePersona(idPersona);
        // check file esista
        File file = this.getFileFromId(id, persona);
        // check user sia admin
        User user = this.retrieveUser(userData);
        if (!user.getRole().equalsIgnoreCase(UserType.ADMIN.name()))
            throw new BadRequestException(user.getName() + " non ha diritti per cancellare il file");
        // delete file from bucket
        try {
            this.awss3FileService.deleteFile(file.getCodiceBucket());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        // delete file from db
        if (!this.fileDAO.removeFile(id))
            throw new BadRequestException("Cancellazione del file \"" + file.getNome() + "\" non riuscita");

        return "Identificativo di \"" + persona.getNome() + "\" cancellato con successo";
    }

    private Persona retrievePersona(Integer id){
        Persona persona = this.personaDAO.getOnePersona(id);
        if(persona==null) throw new ItemNotFoundException("Persona con id "+id+" non trovata");
        return persona;
    }

    private Evento retrieveEvento(Integer id, Persona persona){
        for(Evento evento: persona.getEventi()){
            if(evento.getId()==id) return evento;
        }
        throw new ItemNotFoundException("Evento con id "+id+" non trovato");
    }

    private User retrieveUser(String userData){
        User userFound = this.userDAO.getOneUser(userData);
        if(userFound==null) throw new BadRequestException("User non trovato");
        return userFound;
    }

    private void checkSameUser(String userData, Evento evento, String azione){
        User user = this.retrieveUser(userData);
        if(!user.getId().equalsIgnoreCase(evento.getUser().getId())) throw new ForbiddenException(
                user.getName().toUpperCase()+" non può "+azione+" un evento creato da "+
                        evento.getUser().getName().toUpperCase());
    }

    // cerca se c'è un record che contiene già nome, numero o email con id diverso
    private void checkAlreadyExistentData(Persona persona, Integer idUpdate){
        // definisci i campi da ricercare
        Map<String, String> fieldsToCheck = new HashMap<>();
        if(persona.getNome()!=null) fieldsToCheck.put("nome", persona.getNome());
        if(persona.getTelefono()!=null) fieldsToCheck.put("telefono", persona.getTelefono());
        if(persona.getEmail()!=null) fieldsToCheck.put("email", persona.getEmail());
        // costruisci clausola where
        String whereClause = " WHERE ";
        // se si tratta di un update considera solo altri record
        if(idUpdate!=null) whereClause=whereClause+"id != "+idUpdate+" AND ( ";
        Boolean virgin = true;
        for(String key : fieldsToCheck.keySet()){
            if(!virgin) whereClause=whereClause+"OR ";
            whereClause = whereClause+key+" = :"+key+" ";
            virgin = false;
        }
        if(idUpdate!=null) whereClause=whereClause+")";
        // effettua query
        Persona personFound = this.personaDAO.getOnePersona(whereClause, fieldsToCheck);
        // se res!=null throw BRE con id trovato
        if(personFound!=null){
            // build exception message
            String message = "E' già presente a sistema una persona con ";
            List<String> attributiUguali = new ArrayList<>();
            if(checkEquality(persona.getNome(), personFound.getNome())) attributiUguali.add("nome");
            if(checkEquality(persona.getTelefono(), personFound.getTelefono())) attributiUguali.add("telefono");
            if(checkEquality(persona.getEmail(), personFound.getEmail())) attributiUguali.add("email");
            message = message + attributiUguali.get(0);
            if(attributiUguali.size()==1) message = message+" uguale.";
            if(attributiUguali.size()==2) message = message + " e "+attributiUguali.get(1)+" uguali.";
            if(attributiUguali.size()==3){
                message = message+", "+attributiUguali.get(1)+" e "+attributiUguali.get(2)+" uguali.";
            }
            message = message + " E' la persona con id "+personFound.getId();
            throw new BadRequestException(message);
        }
    }

    private boolean checkEquality(String originalAttribute, String foundAttribute){
        return originalAttribute!=null && foundAttribute!=null && originalAttribute.equalsIgnoreCase(foundAttribute);
    }

    private void checkProvenienza(Persona persona){
        if(persona.getProvenienza()!=null){
            persona.setProvenienza(persona.getProvenienza().toUpperCase());
            Boolean provenienzaCorretta = false;
            String types = "";
            for(ProvenienzaType provenienzaType : ProvenienzaType.values()){
                types = types+"\""+provenienzaType.name()+"\" ";
                if(provenienzaType.name().equals(persona.getProvenienza())) provenienzaCorretta = true;
            }
            if(!provenienzaCorretta)
                throw new BadRequestException("Provenienza può essere solo: "+types);
        }
    }

    private void checkStatus(Persona persona){
        if(persona.getStatus()!=null){
            persona.setStatus(persona.getStatus().toUpperCase());
            Boolean statusCorretto = false;
            String types = "";
            for(PersonaType personaType : PersonaType.values()){
                types = types+"\""+personaType.name()+"\" ";
                if(personaType.name().equals(persona.getStatus())) statusCorretto = true;
            }
            if(!statusCorretto)
                throw new BadRequestException("Status può essere solo: "+types);
        }
    }

    private void handleRealEstateProperties(Persona persona, List<Integer> immobiliProprieta, Integer idRecord){
        // se non c'è il proprietario non ci sono immobili di proprietà e non si entra
        if(persona.getIsProprietario()){
            // creiamo where clause
            String whereClause = " WHERE";
            Boolean virgin = true;
            for(Integer id : immobiliProprieta){
                if(!virgin) whereClause = whereClause+" OR ";
                whereClause = whereClause + " id = "+id+ " ";
                virgin = false;
            }
            QueryClause whereQueryClause = new QueryClause(whereClause, new HashMap<>());
            // retriviamo immobili con gli id selezionati
            List <Immobile> immobili = this.immobileDAO.getAllImmobili(whereQueryClause, "", 1, 1000);
            // se il numero non corrisponde, alcuni non sono stati trovati throw exception
            if(immobili.size()-immobiliProprieta.size()!=0)
                throw new BadRequestException("Immobili indicati non corretti, operazione annullata");
            // per ognuno se c'è già un proprietario e non è il nostro throw exception
            for(Immobile immobile : immobili){
                if(immobile.getProprietario()!=null &&
                        immobile.getProprietario().getId()-idRecord!=0)
                    throw new BadRequestException("Immobile con ref "+immobile.getRef()+" ha già un proprietario");
            }
            persona.setImmobili(immobili);
        }else{
            persona.setImmobili(new ArrayList<>());
        }
    }

    private void handleRealEstateLocation(Persona persona, Integer id){
        if(persona.getIsInquilino()){
            Immobile immobile = this.immobileDAO.getOneImmobile(id);
            if(immobile==null) throw new BadRequestException("Immobile in locazione indicato non corretto, operazione annullata");
            persona.setImmobileInquilino(immobile);
        }
    }

    private Immobile retrieveImmobileInteresse(PersonaWrapper personaWrapper){
        Immobile immobileInteresse = null;
        if(personaWrapper.getImmobileInteresse()!=null){
            Immobile immobile = this.immobileDAO.getOneImmobile(personaWrapper.getImmobileInteresse());
            if(immobile==null) throw new BadRequestException("Immobile interessato indicato non corretto, operazione annullata");
            immobileInteresse = immobile;
        }
        return immobileInteresse;
    }

    private File getFileFromId(Integer id, Persona persona) {
        for (File file : persona.getFiles()) {
            if (file.getId() == id) return file;
        }
        throw new ItemNotFoundException("File con id " + id + " non presente");
    }

}
