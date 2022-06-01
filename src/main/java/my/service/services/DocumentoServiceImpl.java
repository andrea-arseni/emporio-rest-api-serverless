package my.service.services;

import my.service.entities.File;
import my.service.entities.User;
import my.service.repositories.FileDAO;
import my.service.repositories.UserDAO;
import my.service.transporters.FileTrans;
import my.service.types.FileStorageType;
import my.service.types.FileType;
import my.service.types.UserType;
import my.service.utilities.BadRequestException;
import my.service.utilities.FileUtils;
import my.service.utilities.ItemNotFoundException;
import my.service.utilities.UserRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DocumentoServiceImpl implements DocumentoService{

    @Autowired
    FileDAO fileDAO;

    @Autowired
    AWSS3FileService awss3FileService;

    @Autowired
    UserDAO userDAO;

    @Override
    @Transactional
    public FileTrans readFile(String name) {
        // retrieve file record from db
        File file = this.fileDAO.readFile(name);
        if(file==null) throw new ItemNotFoundException("Documento con nome \""+name+"\" non trovato");
        // get name of bucket
        String originalFileBucketName = this.awss3FileService.getOriginalFilesBucketName();
        // get codice bucket
        String key = file.getCodiceBucket();
        // read from bucket
        byte[] byteArray;
        try{
            byteArray = this.awss3FileService.readFile(key, originalFileBucketName);
        }catch(Exception e) {
            throw new BadRequestException("Lettura documento non riuscita, procedura annullata");
        }
        return new FileTrans(file, byteArray);
    }

    @Override
    @Transactional
    public File addFile(MultipartFile multipartFile, String name, String userData) {
        // get user
        User user = this.retrieveUser(userData);
        //check if it is admin
        if(!user.getRole().equals(UserType.ADMIN.name())) throw new BadRequestException("User non abilitato al caricamento del documento");
        // definisci e valida estensione, può essere solo pdf, word o excel
        String[] partiNome = name.split("\\.");
        if(partiNome.length<2) throw new BadRequestException("Necessario indicare l'estensione nel nome file");
        String estensioneNome = partiNome[partiNome.length-1];
        FileUtils.validateExtension(estensioneNome, FileStorageType.DOCUMENTI);
        // valuta l'estensione del file caricato, deve essere la stessa del nome
        String extension = FileUtils.getExtension(multipartFile);
        if(!estensioneNome.equals(extension)) throw new BadRequestException("L'estensione del nome e del file caricato non coincidono, operazione annullata");
        // cerca se c'è un file con lo stesso nome nel db, se c'è throw error
        File fileRecord = this.fileDAO.readFile(name);
        if(fileRecord!=null) throw new BadRequestException("Documento con nome \""+name+"\" già esistente. Cancellare il vecchio prima di caricare il nuovo.");
        // definisci tipologia
        String tipologia = FileType.DOCUMENTO.name();
        // create bucket code
        String bucketCode = "documenti/"+name;
        // get name of bucket
        String originalFileBucketName = this.awss3FileService.getOriginalFilesBucketName();
        java.io.File file = null;
        try {
            file = FileUtils.convertMultipartToFile(multipartFile);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        // s3 operation
        try {
            this.awss3FileService.addFile(file, bucketCode);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        // persist file
        File dbFile = new File(tipologia, bucketCode, name);
        dbFile = this.fileDAO.addFile(dbFile);
        file.delete();
        return dbFile;
    }

    @Override
    @Transactional
    public File renameFile(Integer id, String updatedName, String userData) {
        // get user
        User user = this.retrieveUser(userData);
        //check if it is admin
        if(!user.getRole().equals(UserType.ADMIN.name())) throw new BadRequestException("User non abilitato alla modifica del documento");
        // cerca se c'è un file con lo stesso id nel db, se non c'è throw error
        File fileRecord = this.fileDAO.readFile(id);
        if(fileRecord==null) throw new BadRequestException("Documento originale non trovato. Operazione annullata.");
        // check che l'estensione indicata nel nome coincida con l'estensione del file originale
        String[] partiNome = updatedName.split("\\.");
        if(partiNome.length<2) throw new BadRequestException("Necessario indicare l'estensione nel nome file");
        String estensioneNome = partiNome[partiNome.length-1];
        String[] partiNomeOriginale = fileRecord.getNome().split("\\.");
        String estensioneVecchioNome = partiNomeOriginale[partiNomeOriginale.length-1];
        if(!estensioneNome.equals(estensioneVecchioNome))
            throw new BadRequestException("L'estensione del nuovo file non coincide con l'estensione del vecchio, impossibile procedere");
        // cambia il nome
        fileRecord.setNome(updatedName);
        return fileRecord;
    }

    @Override
    @Transactional
    public String deleteFile(Integer id, String userData) {
        // get user
        User user = this.retrieveUser(userData);
        //check if it is admin
        if(!user.getRole().equals(UserType.ADMIN.name())) throw new BadRequestException("User non abilitato alla cancellazione del file");
        File fileRecord = this.fileDAO.readFile(id);
        if(fileRecord==null) throw new BadRequestException("Documento originale non trovato. Operazione annullata.");
        this.awss3FileService.deleteFile(fileRecord.getCodiceBucket());
        this.fileDAO.removeFile(id);
        return "Documento con nome \""+fileRecord.getNome()+"\" cancellato con successo";
    }

    private User retrieveUser(String userData){
        User user = UserRetriever.retrieveUser(userData);
        User userFound = this.userDAO.getOneUser(user.getId());
        if(userFound==null) this.userDAO.addUser(user);
        return userFound;
    }
}
