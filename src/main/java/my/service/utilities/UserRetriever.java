package my.service.utilities;

import my.service.entities.User;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

public class UserRetriever {

    public static User retrieveUser(String headerUserData){
        byte[] decodedBytes = Base64.decodeBase64(headerUserData);
        try {
            String decodedString = new String(decodedBytes, "UTF-8");
            String payload = decodedString.split("}")[1];
            String sub = getSpecificData(payload, "sub");
            String name = getSpecificData(payload, "username");
            String email = getSpecificData(payload, "email");
            return new User(sub, name, email, LocalDateTime.now());
        } catch (UnsupportedEncodingException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private static String getSpecificData(String payload, String attributeName){
        return payload.split(attributeName+"\"")[1].split("\",\"")[0].substring(2);
    }

}
