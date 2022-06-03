package my.service.services;

import my.service.entities.*;
import my.service.repositories.*;
import my.service.transporters.FileTrans;
import my.service.transporters.ImmobileTrans;
import my.service.transporters.PersonaTrans;
import my.service.types.*;
import my.service.utilities.*;
import my.service.wrappers.ImmobileWrapper;
import my.service.wrappers.StringWrapper;
import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImmobileServiceImpl implements ImmobileService {

    @Autowired
    private ImmobileDAO immobileDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LogDAO logDAO;

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private PersonaDAO personaDAO;

    @Autowired
    private AWSS3FileService awss3FileService;

    @Override
    @Transactional
    public ResponseList getAllImmobili(String filter, String value, String min, String max,
                                              String startDate, String endDate, String sort, Integer pageNumber,
                                              Integer numberOfResults, String contratto, String categoria,
                                              String priceMin, String priceMax, String userData) {
        QueryClause whereClause = null;
        // 1. check se esiste userData
        if(userData==null) {
            // 2. se non esiste crea whereClause con solo contratto categoria e price e attivo = true
            String contrattoWhereClause = null;
            if(contratto.equalsIgnoreCase("TUTTI")){
                contrattoWhereClause = "";
            }else{
                for (ImmobileContrattoType contrattoType : ImmobileContrattoType.values()) {
                    if (contrattoType.name().equalsIgnoreCase(contratto)) contrattoWhereClause = " AND contratto = '"+contrattoType.name()+"'";
                }
                if (contrattoWhereClause == null) throw new BadRequestException("Tipologia di contratto indicata invalida, può essere solo: TUTTI, VENDITA, AFFITTO");
            }
            String categoriaWhereClause = null;
            if(categoria.equalsIgnoreCase("TUTTI")){
                categoriaWhereClause = "";
            }else{
                for (ImmobileTipologiaType tipologiaType : ImmobileTipologiaType.values()) {
                    if (tipologiaType.name().equalsIgnoreCase(categoria)) categoriaWhereClause =  " AND categoria = '"+tipologiaType.name()+"'";
                }
                if (categoriaWhereClause == null) throw new BadRequestException("Tipologia di categoria indicata invalida, può essere solo: TUTTI, RESIDENZIALE, COMMERCIALE");
            }
            Integer minimunPrice = 0;
            Integer maximunPrice = 10000000;
            try{
                minimunPrice = Integer.parseInt(priceMin);
                maximunPrice = Integer.parseInt(priceMax);
            }catch(NumberFormatException e){
                throw new BadRequestException("Prezzi minimo e massimo indicati non validi, digitare solo cifre");
            }
            whereClause = new QueryClause(" WHERE prezzo BETWEEN "+minimunPrice+" AND "+maximunPrice+ " AND status = 'ATTIVO' "+contrattoWhereClause+categoriaWhereClause, new HashMap<>());
        }else{
            // 3. se esiste check che sia effettivamente uno user
            this.retrieveUser(userData);
            // 4. crea where clause normale
            whereClause = ListHandler.getQueryWhereClause(Immobile.class, filter, "", 0, value, startDate, endDate, min, max);
        }
        // 5. sorting clause
        String sortClause = ListHandler.getSortClause(Immobile.class, sort);
        // 6. query
        List<Immobile> immobili = this.immobileDAO.getAllImmobili(whereClause, sortClause, pageNumber, numberOfResults);
        List<ImmobileTrans> res = new ArrayList<>();
        for (Immobile immobile : immobili) {
            if(immobile.getFiles().size() > 0){
                List<File> photos = immobile.getFiles().stream().filter(file -> file.getTipologia().equalsIgnoreCase("FOTO") && file.getNome().equalsIgnoreCase("1")).collect(Collectors.toList());
                immobile.setFiles(photos);
            }
            ImmobileTrans record = new ImmobileTrans(immobile);
            record.setProprietario(null);
            record.setCaratteristicheImmobile(null);
            record.setInquilini(null);
            res.add(record);
        }
        Long count = this.immobileDAO.getListNumber(whereClause);
        return new ResponseList(count, res);
    }

    // invocare insieme i logs è insostenibile, si fà una call separata

    @Override
    @Transactional
    public ImmobileTrans getOneImmobile(Integer id, String userData) {
        Immobile immobile = null;
        // 1. Check se esiste l'header dello user
        if(userData==null){
            // 2. Se non esiste query con id e status
            QueryClause whereClause = new QueryClause(" WHERE id = "+id+" AND status = 'ATTIVO' ", new HashMap<>());
            List<Immobile> immobili = this.immobileDAO.getAllImmobili(whereClause, "", 1, 1);
            // 3. Se non trovato NotFoundException
            if(immobili.size()!=1) throw new ItemNotFoundException("Immobile con id "+id+" non trovato.");
            immobile = immobili.get(0);
        }else{
            // 4. Se esiste user normal flow
            this.retrieveUser(userData);
            immobile = this.getImmobileFromId(id);
        }
        ImmobileTrans res = new ImmobileTrans(immobile);
        res.setInquilini(null);
        res.setProprietario(null);
        return res;
    }

    @Override
    @Transactional
    public ImmobileTrans postImmobile(ImmobileWrapper immobileWrapper, String userData) {
        // Definisci identità user
        User user = this.retrieveUser(userData);
        // check presenza attributi obbligatori in req body
        this.checkImmobileWrapper(immobileWrapper);
        // retrieve proprietario
        Persona proprietario = this.getProprietario(immobileWrapper.getProprietario(), user);
        // prepari l'oggetto immobile aggiungendo caratteristiche e proprietario
        Immobile immobile = immobileWrapper.getImmobile();
        if(immobile.getTitolo().length()>60) throw new BadRequestException("Titolo troppo lungo. Può essere massimo 60 caratteri");
        immobile.setCaratteristicheImmobile(immobileWrapper.getCaratteristicheImmobile());
        proprietario.setIsProprietario(true);
        proprietario.addImmobile(immobile);
        immobile.setProprietario(proprietario);
        if (immobile.getRef() == null) {
            immobile.setRef(this.immobileDAO.getLastRef() + 1);
        }
        immobile.setStatus(ImmobileType.DISATTIVO.name());
        // persisti l'immobile
        immobile = this.immobileDAO.addImmobile(immobile);
        //crei log di creazione immobile
        Log log = new Log("Creazione annuncio", LocalDateTime.now(), immobile, user);
        immobile.addLog(log);
        this.handlePropertyEvent("Aggiunta", immobile.getRef(), proprietario, user);
        return new ImmobileTrans(immobile);
    }

    @Override
    @Transactional
    public ImmobileTrans postImmobile(ImmobileWrapper immobileWrapper) {
        // check presenza attributi obbligatori in req body
        this.checkImmobileWrapper(immobileWrapper);
        // retrieve proprietario
        Persona proprietarioIndicato = immobileWrapper.getProprietario();
        // crea Persona estraendo solo nome, numero ed email - provenienza SITO - status ATTIVA
        Persona proprietario = new Persona(proprietarioIndicato.getNome(), proprietarioIndicato.getTelefono(), proprietarioIndicato.getEmail(),
                "ATTIVA", "SITO");
        proprietario = this.personaDAO.addPersona(proprietario);
        // prepari l'oggetto immobile aggiungendo caratteristiche e proprietario
        Immobile immobile = immobileWrapper.getImmobile();
        immobile.setCaratteristicheImmobile(immobileWrapper.getCaratteristicheImmobile());
        proprietario.setIsProprietario(true);
        proprietario.addImmobile(immobile);
        immobile.setProprietario(proprietario);
        if (immobile.getRef() == null) {
            immobile.setRef(this.immobileDAO.getLastRef() + 1);
        }
        immobile.setStatus(ImmobileType.DISATTIVO.name());
        // persisti l'immobile
        immobile = this.immobileDAO.addImmobile(immobile);
        //crei log di creazione immobile
        Log log = new Log("Creazione annuncio", LocalDateTime.now(), immobile, null);
        immobile.addLog(log);
        // crea Evento creazione dal sito
        Evento evento = new Evento(LocalDateTime.now(), "Privato dal sito", proprietario, null, null);
        proprietario.addEvento(evento);
        this.handlePropertyEvent("Aggiunta", immobile.getRef(), proprietario, null);
        return new ImmobileTrans(immobile);
    }

    @Override
    @Transactional
    public ImmobileTrans patchImmobile(Integer id, ImmobileWrapper immobileWrapper, String userData) {
        // retrieve user
        User user = this.retrieveUser(userData);
        // retrieve immobile
        Immobile originalImmobile = this.getImmobileFromId(id);
        // definisci immobile
        Immobile patchImmobile = immobileWrapper.getImmobile();
        // non si possono creare nuovi proprietati con il patch immobile
        // si può indicare un proprietario già esistente che verrà associato c'è manca un originale o c'è un originale diverso
        this.handleLandOwners(originalImmobile, patchImmobile, user, immobileWrapper.getProprietario());
        // check prezzo
        if (patchImmobile.getPrezzo() != null && patchImmobile.getPrezzo() - originalImmobile.getPrezzo() != 0) {
            Log priceLog = new Log("Prezzo modificato da " + originalImmobile.getPrezzo() + " a " + patchImmobile.getPrezzo(),
                    LocalDateTime.now(), originalImmobile, user);
            originalImmobile.addLog(priceLog);
        }
        // check status
        if (patchImmobile.getStatus() != null && !patchImmobile.getStatus().equalsIgnoreCase(originalImmobile.getStatus())) {
            Boolean correctValue = false;
            for (ImmobileType immobileType : ImmobileType.values()) {
                if (immobileType.name().equalsIgnoreCase(patchImmobile.getStatus())) correctValue = true;
            }
            if (!correctValue)
                throw new BadRequestException("Status può essere solo \"ATTIVO\" o \"DISATTIVO\"");
            String newStatus = patchImmobile.getStatus().toUpperCase();
            Log activeLog = new Log(newStatus,
                    LocalDateTime.now(), originalImmobile, user);
            originalImmobile.addLog(activeLog);
        }
        // inizializza lista fields
        List<String> updatedFields = new ArrayList<>();

        // check fields cambiati
        updatedFields.addAll(ObjectPatcher.getListOfDifferentFields(CaratteristicheImmobile.class, originalImmobile.getCaratteristicheImmobile(), immobileWrapper.getCaratteristicheImmobile()));
        updatedFields.addAll(ObjectPatcher.getListOfDifferentFields(Immobile.class, originalImmobile, patchImmobile));
        //rimuovi prezzo, proprietario e status dal conteggio dei field modificati
        updatedFields.removeAll(Arrays.asList("prezzo", "proprietario", "status"));
        // patch caratteristiche
        CaratteristicheImmobile caratteristichePatched = (CaratteristicheImmobile) ObjectPatcher.patchObject(CaratteristicheImmobile.class, originalImmobile.getCaratteristicheImmobile(), immobileWrapper.getCaratteristicheImmobile());
        // indica che non cambierai persona, logs e caratteristiche mediante il patch
        patchImmobile.setCaratteristicheImmobile(null);
        patchImmobile.setLogs(null);
        patchImmobile.setProprietario(null);
        // patch immobile
        ObjectPatcher.patchObject(Immobile.class, originalImmobile, patchImmobile);
        originalImmobile.setCaratteristicheImmobile(caratteristichePatched);
        // produce generic log
        if (updatedFields.size() > 0) {
            String updatedFieldsMessage = "";
            Boolean virgin = true;
            for (String field : updatedFields) {
                updatedFieldsMessage = updatedFieldsMessage + (virgin ? field : ", " + field);
                virgin = false;
            }
            updatedFieldsMessage = updatedFieldsMessage + (updatedFields.size() == 1 ? " modificato" : " modificati");
            Log genericLog = new Log(updatedFieldsMessage,
                    LocalDateTime.now(), originalImmobile, user);
            originalImmobile.addLog(genericLog);
        }
        // return immobile
        return new ImmobileTrans(originalImmobile);
    }

    @Override
    @Transactional
    public String deleteImmobile(Integer id, String userData) {
        Immobile immobile = this.getImmobileFromId(id);
        User user = this.retrieveUser(userData);
        if (!user.getRole().equalsIgnoreCase(UserType.ADMIN.name())) {
            throw new ForbiddenException("User non abilitato alla cancellazione di immobili");
        }
        // if proprietario != null remove immobile
        Persona proprietario = immobile.getProprietario();
        if(proprietario!=null){
            proprietario.setIsProprietario(proprietario.getImmobili().size()>1);
            this.handlePropertyEvent("Rimozione", immobile.getRef(), proprietario, user);
        }
        this.immobileDAO.deleteImmobile(immobile);
        return "Immobile con id " + id + ": '" + immobile.getTitolo() + "' cancellato con successo";
    }

    @Override
    @Transactional
    public ImmobileTrans duplicateImmobile(Integer id, String userData) {
        // check immobile
        Immobile immobile = this.getImmobileFromId(id);
        // crea nuovo immobile
        Immobile duplicateImmobile = new Immobile(immobile);
        duplicateImmobile.setRef(this.immobileDAO.getLastRef() + 1);
        // copia caratteristiche
        CaratteristicheImmobile duplicateCaratteristiche = new CaratteristicheImmobile(immobile.getCaratteristicheImmobile());
        duplicateImmobile.setCaratteristicheImmobile(duplicateCaratteristiche);
        // proprietario set null
        duplicateImmobile.setProprietario(null);
        /*if (immobile.getProprietario() != null) {
            Persona proprietario = immobile.getProprietario();
            duplicateImmobile.setProprietario(proprietario);
        }*/
        // copia logs
        for (Log originalLog : immobile.getLogs()) {
            duplicateImmobile.addLog(new Log(originalLog.getAzione(), originalLog.getData(), duplicateImmobile, originalLog.getUser()));
        }
        //retrieve user
        User user = this.retrieveUser(userData);
        // crea logs di duplicazione
        Log logDuplicazione = new Log("Immobile duplicato da Ref " + immobile.getRef(), LocalDateTime.now(), duplicateImmobile, user);
        duplicateImmobile.addLog(logDuplicazione);
        return new ImmobileTrans(this.immobileDAO.addImmobile(duplicateImmobile));
    }

    @Override
    @Transactional
    public ResponseList getAllLogs(Integer idImmobile,
                                String filter,
                                String value,
                                String startDate,
                                String endDate,
                                String sort,
                                Integer pageNumber,
                                Integer numberOfResults) {
        this.getImmobileFromId(idImmobile);
        QueryClause whereClause = ListHandler.getQueryWhereClause(Log.class, filter, "immobile", idImmobile, value, startDate, endDate, "", "");
        String sortClause = ListHandler.getSortClause(Log.class, sort);
        List<Log> logs = this.logDAO.getLogs(whereClause, sortClause, pageNumber, numberOfResults);
        Long count = this.logDAO.getListNumber(whereClause);
        return new ResponseList(count, logs);
    }

    @Override
    @Transactional
    public Log getOneLog(Integer idImmobile, Integer idLog) {
        Immobile immobile = this.getImmobileFromId(idImmobile);
        Log logFound = null;
        for (Log log : immobile.getLogs()) {
            if (log.getId() == idLog) {
                logFound = log;
                break;
            }
        }
        if (logFound == null) throw new ItemNotFoundException("Log con id " + idLog + " non trovato");
        return logFound;
    }

    @Override
    @Transactional
    public Log addLog(Integer idImmobile, String azione, String userData) {
        Immobile immobile = this.getImmobileFromId(idImmobile);
        User user = this.retrieveUser(userData);
        Log log = new Log(azione, LocalDateTime.now(), immobile, user);
        immobile.addLog(log);
        return log;
    }

    @Override
    @Transactional
    public Log patchLog(Integer idImmobile, Integer idLog, String newAzione, String userData) {
        // retrieve immobile
        Immobile immobile = this.getImmobileFromId(idImmobile);
        // retrieve log
        Log logFound = null;
        for (Log log : immobile.getLogs()) {
            if (log.getId() == idLog) logFound = log;
        }
        // se log non c'è throw error
        if (logFound == null) throw new ItemNotFoundException("Log con id " + idLog + " non presente");
        // retrieve user
        User user = this.retrieveUser(userData);
        // se user log != user patch throw error
        if (logFound.getUser() != null &&
                !user.getId().equalsIgnoreCase(logFound.getUser().getId()) &&
                !user.getRole().equalsIgnoreCase(UserType.ADMIN.name())) {
            throw new ForbiddenException(user.getName() + " non ha i diritti per modificare il log");
        }
        // modifica azione e data
        logFound.setAzione(newAzione);
        logFound.setUser(user);
        return logFound;
    }

    @Override
    @Transactional
    public String deleteLog(Integer idImmobile, Integer idLog, String userData) {
        Immobile immobile = this.getImmobileFromId(idImmobile);
        Log logFound = null;
        for (Log log : immobile.getLogs()) {
            if (log.getId() == idLog) logFound = log;
        }
        if (logFound == null) throw new ItemNotFoundException("Log con id " + idLog + " non presente");
        User user = this.retrieveUser(userData);
        if (!user.getRole().equalsIgnoreCase(UserType.ADMIN.name())) {
            throw new ForbiddenException(user.getName() + " non ha i diritti per cancellare il log");
        }
        if (!this.logDAO.removeLog(idLog)) throw new ItemNotFoundException("Log con id " + idLog + " non trovato");
        return "Log rimosso con successo";
    }

    @Override
    @Transactional
    public FileTrans getFile(Integer idImmobile, Integer id, String photoType) {
        String originalFileBucketName = this.awss3FileService.getOriginalFilesBucketName();
        String signedPhotoBucketName = this.awss3FileService.getSignedPhotosBucketName();
        // retrieve immobile
        Immobile immobile = this.getImmobileFromId(idImmobile);
        // retrieve fileDB
        File fileDB = this.getFileFromId(id, immobile);
        String tipologia = fileDB.getTipologia();
        String key = fileDB.getCodiceBucket();
        // initialize byteArray
        byte[] byteArray = null;
        // if file == doc read doc
        if (tipologia.equalsIgnoreCase(FileType.DOCUMENTO.name())) {
            byteArray = this.awss3FileService.readFile(key, originalFileBucketName);
            // if file == photo check type
        } else if (tipologia.equalsIgnoreCase(FileType.FOTO.name())) {
            if (photoType.equalsIgnoreCase(PhotoType.SIGNED.name())) {
                byteArray = this.awss3FileService.readFile(key, signedPhotoBucketName);
            } else if (photoType.equalsIgnoreCase(PhotoType.ORIGINAL.name())) {
                byteArray = this.awss3FileService.readFile(key, originalFileBucketName);
            } else {
                throw new BadRequestException("Tipologia foto scorretta, può essere solo: SIGNED o ORIGINAL");
            }
        } else {
            throw new BadRequestException("Tipologia file scorretta, può essere solo: DOCUMENTO o FOTO");
        }
        return new FileTrans(fileDB, byteArray);
    }

    @Override
    @Transactional
    public File addFile(Integer idImmobile, MultipartFile multipartFile, String userData) {
        // retrieve immobile
        Immobile immobile = this.getImmobileFromId(idImmobile);
        // retrieve estensione
        String extension = FileUtils.getExtension(multipartFile);
        FileUtils.validateExtension(extension, FileStorageType.IMMOBILI);
        // retrieve tipologia
        String tipologia = extension.equals("pdf") ? FileType.DOCUMENTO.name() : FileType.FOTO.name();
        // definisci posizione e nome e log message
        String nome = null;
        String codiceBucket = "immobili/"+immobile.getRef() + "/" + new Date().getTime() + "." + extension;
        //String logMessage = null;
        //se documento posizione resta null e nome = nome
        if (tipologia.equals(FileType.DOCUMENTO.name())) {
            nome = FileUtils.getFileName(multipartFile);
            if (nome.length() > 40) nome = nome.substring(0, 40) + ".pdf";
            // check che non ci sia già un file con quel nome nel caso throw exception
            if (this.fileDAO.isFileAlreadyExisting(immobile, nome)) {
                throw new BadRequestException("Esiste già un file con nome \"" + nome + "\", sceglierne un altro");
            }
            //logMessage = "Aggiunto file: \"" + nome + "\"";
        } else {
            //logMessage = "Aggiunta foto";
            // check che non ce ne siano già 20 otherwise exception
            List<File> photos = this.fileDAO.getPhoto(immobile);
            if (photos.size() >= 20) {
                throw new BadRequestException("Raggiunto il limite di 20 foto per l'immobile. Aggiunta annullata.");
            }
            // posizione = last posizione + 1
            Integer posizione = 0;
            for (File file : photos) {
                Integer posizioneFile = Integer.parseInt(file.getNome());
                if (posizioneFile > posizione) posizione = posizioneFile;
            }
            posizione++;
            nome = posizione.toString();
        }
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
        File dbFile = new File(immobile, tipologia, codiceBucket, nome);
        dbFile = this.fileDAO.addFile(dbFile);
        // crea il log
        /*Log log = new Log(logMessage, LocalDateTime.now(), immobile, this.retrieveUser(userData));
        immobile.addLog(log);*/
        // finito il processo cancella il file dove l'hai creato
        file.delete();
        return dbFile;
    }

    @Override
    @Transactional
    public File patchFile(Integer idImmobile, Integer id, StringWrapper file, String userData) {
        // operazione di patch puoi cambiare solo nome
        String nuovoNome = file.getName();
        if (nuovoNome == null) throw new BadRequestException("Correzione non indicata, impossibile procedere");
        // se immobile non c'è errore
        Immobile immobile = this.getImmobileFromId(idImmobile);
        // se file non c'è errore
        File originalFile = this.getFileFromId(id, immobile);
        // se il file è un documento assegni il nuovo nome
        if (originalFile.getTipologia().equalsIgnoreCase(FileType.DOCUMENTO.name())) {
            if (!nuovoNome.toLowerCase().contains(".pdf")) nuovoNome = nuovoNome + ".pdf";
            originalFile.setNome(nuovoNome);
            // se il file è una foto
        } else {
            Integer posizione;
            // check che sia un vero numero
            try {
                posizione = Integer.parseInt(nuovoNome);
            } catch (NumberFormatException e) {
                throw new BadRequestException("Per una foto bisogna indicare un numero");
            }
            if(posizione>20) throw new BadRequestException("Posizione inammissibile, si possono salvare al massimo 20 foto");
            // check se esiste un file con quella posizione, nel caso li scambi
            File fileFound = this.fileDAO.getPhoto(immobile, posizione);
            if (fileFound != null && fileFound.getId() - originalFile.getId() != 0) {
                fileFound.setNome(originalFile.getNome());
            }
            originalFile.setNome(nuovoNome);
        }
        return originalFile;
    }

    @Override
    @Transactional
    public String deleteFile(Integer idImmobile, Integer id, String userData) {
        // check immobile esista
        Immobile immobile = this.getImmobileFromId(idImmobile);
        // check file esista
        File file = this.getFileFromId(id, immobile);
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
        // log cancellazione
        //Log log = new Log("Cancellato file \"" + file.getNome() + "\"", LocalDateTime.now(), immobile, this.retrieveUser(userData));
        //immobile.addLog(log);

        return "File con nome \"" + file.getNome() + "\" cancellato con successo";
    }

    private void handleLandOwners(Immobile originalImmobile, Immobile patchImmobile, User user, Persona proprietarioIndicato){
        Integer ref = patchImmobile.getRef()!=null ? patchImmobile.getRef() : originalImmobile.getRef();
        Integer idProprietarioIndicato = proprietarioIndicato.getId();
        Persona proprietarioOriginale = originalImmobile.getProprietario();
        // se è indicato un id e non c'è un proprietario oppure l'id è diverso effettua sostituzione
        if (idProprietarioIndicato != null &&
                (proprietarioOriginale == null || idProprietarioIndicato - proprietarioOriginale.getId() != 0)) {
            // se l'id indicato non corrisponde a nessun proprietario throw error
            Persona nuovoProprietario = this.personaDAO.getOnePersona(idProprietarioIndicato);
            if(nuovoProprietario==null) throw new BadRequestException("Proprietario indicato inesistente");
            // il nuovoProprietario è un proprietario
            nuovoProprietario.setIsProprietario(true);
            // il vecchioProprietario se ha solo un immobile non sarà più un proprietario, altrimenti sì
            proprietarioOriginale.setIsProprietario(proprietarioOriginale.getImmobili().size()>1);
            // crea eventi persona
            this.handlePropertyEvent("Rimozione", ref, proprietarioOriginale, user);
            this.handlePropertyEvent("Aggiunta", ref, nuovoProprietario, user);
            // crea log
            String message = proprietarioOriginale == null ?
                    "Aggiunto proprietario" : "Sostituito vecchio proprietario " + proprietarioOriginale.getNome();
            Log personLog = new Log(message, LocalDateTime.now(), originalImmobile, user);
            originalImmobile.addLog(personLog);
            originalImmobile.setProprietario(nuovoProprietario);
            patchImmobile.setProprietario(null);
        }
    }

    private void handlePropertyEvent(String tipologiaEvento, Integer ref, Persona proprietario, User user){
        Evento evento = new Evento(LocalDateTime.now(), tipologiaEvento+" proprietà immobile ref. "+ref, proprietario, null, user);
        proprietario.addEvento(evento);
    }

    private Persona getProprietario(Persona proprietarioIndicato, User user){
        Persona proprietario = null;
        // se è indicato un id allora lo retrievi dal db
        if (proprietarioIndicato.getId() != null) {
            proprietario = this.personaDAO.getOnePersona(proprietarioIndicato.getId());
            if (proprietario == null) throw new BadRequestException("Proprietario indicato non trovato, operazione annullata");
        } else {
            // se non è indicato l'id è nuovo e lo crei
            this.checkAlreadyExistentData(proprietarioIndicato);
            proprietario = this.personaDAO.addPersona(proprietarioIndicato);
            proprietario.addEvento(new Evento(LocalDateTime.now(), "Persona creata", proprietario, null, user));
        }
        return proprietario;
    }

    private Immobile getImmobileFromId(Integer id) {
        Immobile immobile = this.immobileDAO.getOneImmobile(id);
        if (immobile == null) throw new ItemNotFoundException("Immobile con id " + id + " non presente");
        return immobile;
    }

    private User retrieveUser(String userData){
        User userFound = this.userDAO.getOneUser(userData);
        if (userFound == null) throw new BadRequestException("User sconosciuto, operazione annullata");
        return userFound;
    }

    private File getFileFromId(Integer id, Immobile immobile) {
        for (File file : immobile.getFiles()) {
            if (file.getId() - id == 0) return file;
        }
        throw new ItemNotFoundException("File con id " + id + " non presente");
    }

    private void checkImmobileWrapper(ImmobileWrapper immobileWrapper) {
        String exceptionMessage = "";
        Integer errorNumber = 0;
        if (immobileWrapper.getProprietario() == null) {
            exceptionMessage = exceptionMessage + "Proprietario";
            errorNumber++;
        }
        if (immobileWrapper.getImmobile() == null) {
            if (errorNumber > 0) exceptionMessage = exceptionMessage + ", ";
            exceptionMessage = exceptionMessage + "Immobile";
            errorNumber++;
        }
        if (immobileWrapper.getCaratteristicheImmobile() == null) {
            if (errorNumber > 0) exceptionMessage = exceptionMessage + ", ";
            exceptionMessage = exceptionMessage + "Caratteristiche";
            errorNumber++;
        }
        if (errorNumber != 0) {
            exceptionMessage = exceptionMessage + (errorNumber == 1
                    ? " è un campo obbligatorio da indicare"
                    : " sono campi obbligatori da indicare");
            throw new BadRequestException(exceptionMessage);
        }
    }

    // cerca se c'è un record che contiene già nome, numero o email con id diverso
    private void checkAlreadyExistentData(Persona persona){
        // definisci i campi da ricercare
        Map<String, String> fieldsToCheck = new HashMap<>();
        if(persona.getNome()!=null) fieldsToCheck.put("nome", persona.getNome());
        if(persona.getTelefono()!=null) fieldsToCheck.put("telefono", persona.getTelefono());
        if(persona.getEmail()!=null) fieldsToCheck.put("email", persona.getEmail());
        // costruisci clausola where
        String whereClause = " WHERE ";
        // se si tratta di un update considera solo altri record
        Boolean virgin = true;
        for(String key : fieldsToCheck.keySet()){
            if(!virgin) whereClause=whereClause+"OR ";
            whereClause = whereClause+key+" = :"+key+" ";
            virgin = false;
        }
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

    private QueryClause updateTextQuery(QueryClause whereClause){
        if(whereClause.getQueryText().equals("")){
            whereClause.setQueryText(" WHERE ");
        }else{
            whereClause.setQueryText(whereClause.getQueryText() + " AND ");
        }
        return whereClause;
    }

}
