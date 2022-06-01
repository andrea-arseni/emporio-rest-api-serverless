package my.service.services;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.*;


@Service
public class AWSS3FileService extends AWSClientService {

    // aggiungi solo nel bucket file
    public void addFile(java.io.File file, String fileName){
        this.getClient().putObject(new PutObjectRequest(this.getOriginalFilesBucketName(), fileName, file));
    }

    // cancelli in entrambi i bucket
    public void deleteFile(String name) {
        this.getClient().deleteObject(new DeleteObjectRequest(this.getOriginalFilesBucketName(), name));
        this.getClient().deleteObject(new DeleteObjectRequest(this.getSignedPhotosBucketName(), name));
    }

    // leggi in base ad un parametro in ingresso
    public byte[] readFile(String key, String bucketName){
        S3Object s3Object = this.getClient().getObject(new GetObjectRequest(bucketName, key));
        InputStream inputStream = s3Object.getObjectContent();
        byte[] byteArray = null;
        try {
            byteArray = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return byteArray;
    }

}
