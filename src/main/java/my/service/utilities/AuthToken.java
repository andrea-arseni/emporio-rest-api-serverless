package my.service.utilities;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AuthToken {

    private String idClient;
    private String secretIdClient;
    private String url = "https://emporiocaseloginsystem.auth.eu-central-1.amazoncognito.com/oauth2/token";
    private String charset = java.nio.charset.StandardCharsets.UTF_8.name();
    private String code;
    private String redirect_uri = "https://arsecasa.link";

    public AuthToken(String code, String idClient, String secretIdClient) {
        this.code = code;
        this.idClient = idClient;
        this.secretIdClient = secretIdClient;
    }

    public String produceToken() {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost request = new HttpPost(this.url);

        // Request parameters and other properties.
        try {
            // headers
            request.addHeader("content-type", "application/x-www-form-urlencoded");

            String encodeBytes = Base64.getEncoder().encodeToString((this.idClient + ":" + this.secretIdClient).getBytes());

            request.addHeader("Authorization", "Basic " + encodeBytes);

            //body
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("code", this.code));
            urlParameters.add(new BasicNameValuePair("redirect_uri", this.redirect_uri));
            urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
            urlParameters.add(new BasicNameValuePair("client_id", this.idClient));

            request.setEntity(new UrlEncodedFormEntity(urlParameters));

        } catch (UnsupportedEncodingException e) {
            throw new BadRequestException(e.getMessage());
        }

        //Execute and get the response.
        HttpResponse response = null;
        try {
            response = httpclient.execute(request);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
        var entity = response.getEntity();

        try (InputStream instream = entity.getContent()) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = instream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }

            return result.toString(this.charset);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }

    }

}
