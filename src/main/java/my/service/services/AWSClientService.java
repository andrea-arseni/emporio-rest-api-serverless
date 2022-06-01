package my.service.services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class AWSClientService {

    // AmazonS3 Client, in this object you have all AWS API calls about S3.
    private AmazonS3 amazonS3;

    @Value("${amazon.s3.bucket-original-files}")
    private String originalFilesBucketName;

    @Value("${amazon.s3.bucket-signed-photos}")
    private String signedPhotosBucketName;

    // Getters for parents.
    public AmazonS3 getClient() {
        return this.amazonS3;
    }

    public String getOriginalFilesBucketName() {
        return this.originalFilesBucketName;
    }

    public String getSignedPhotosBucketName() { return this.signedPhotosBucketName; }

    @PostConstruct
    private void init() {

        // Init your AmazonS3 credentials using BasicAWSCredentials.
        //BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        // Start the client using AmazonS3ClientBuilder
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                //.withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

}
