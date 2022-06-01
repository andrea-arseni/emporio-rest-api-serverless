package my.service.repositories;

import my.service.entities.File;
import my.service.entities.Immobile;
import my.service.entities.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class FileDAOImpl implements FileDAO{

    @Autowired
    EntityManager entityManager;

    @Override
    public List<File> getPhoto(Immobile immobile) {
        Query query = this.entityManager.createQuery("FROM File WHERE tipologia = 'FOTO' AND immobile = :id");
        query.setParameter("id", immobile);
        return query.getResultList();
    }

    @Override
    public File getPhoto(Immobile immobile, Integer posizione) {
        Query query = this.entityManager.createQuery("FROM File WHERE tipologia = 'FOTO' AND immobile = :immobile AND persona IS NULL AND nome = :nome");
        query.setParameter("immobile", immobile);
        query.setParameter("nome", posizione.toString());
        return query.getResultList().size()==0 ? null : (File)query.getResultList().get(0);
    }

    @Override
    public Boolean isFileAlreadyExisting(Persona persona) {
        Query query = this.entityManager.createQuery("FROM File WHERE tipologia = 'DOCUMENTO' AND persona = :persona AND IMMOBILE IS NULL AND nome = 'identificativo.pdf'");
        query.setParameter("persona", persona);
        List<File> files = query.getResultList();
        return files.size()>=1;
    }

    @Override
    public File readFile(String nome) {
        Query query = this.entityManager.createQuery("FROM File WHERE tipologia = 'DOCUMENTO' AND persona IS NULL AND immobile IS NULL AND nome = :nome");
        query.setParameter("nome", nome);
        List<File> files = query.getResultList();
        return files.size()>=1 ? files.get(0) : null;
    }

    @Override
    public File readFile(Integer id) {
        Query query = this.entityManager.createQuery("FROM File WHERE tipologia = 'DOCUMENTO' AND persona IS NULL AND immobile IS NULL AND id = :id");
        query.setParameter("id", id);
        List<File> files = query.getResultList();
        return files.size()>=1 ? files.get(0) : null;
    }

    @Override
    public Boolean isFileAlreadyExisting(Immobile immobile, String name) {
        Query query = this.entityManager.createQuery("FROM File WHERE immobile = :immobile AND nome = :name");
        query.setParameter("immobile", immobile);
        query.setParameter("name", name);
        List<File> files = query.getResultList();
        return files.size()>=1;
    }

    @Override
    public File addFile(File file) {
        return this.entityManager.merge(file);
    }

    @Override
    public Boolean removeFile(Integer id) {
        Query query = this.entityManager.createQuery("DELETE FROM File WHERE id = :id");
        query.setParameter("id", id);
        Integer res = query.executeUpdate();
        return res==1;
    }
}
