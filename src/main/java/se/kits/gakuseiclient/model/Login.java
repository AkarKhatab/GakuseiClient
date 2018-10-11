package se.kits.gakuseiclient.model;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component
public class Login implements Serializable {

    public static final String REST_SERVICE_URI = "http://localhost:8080";

    private String cookie;


    public Login(){ }

    @PostConstruct
    public void init(){
        System.out.println("inside init, getting cookie");

        //login2();
        login("test", "test");
        System.out.println("Getting logged in user");

        getLoggedInUser();
        System.out.println("the end");
    }

/*
    private void login2() {
        OkHttpClient client = new OkHttpClient();
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("username", "test");
        form.add("password", "test");

        Request request = new Request.Builder()
                .url("http://localhost:8080/auth?username=test&password=test")
                .post(null)
                .addHeader("cache-control", "no-cache")
                .build();
        try{
            Response response = client.newCall(request).execute();
            System.out.println(response.headers().get("Set-Cookie").split(";")[0].split("=")[1]);
        }catch (Exception e) {
            System.out.println("Exception is: " + e.getStackTrace());
        }
    }*/
    public void login(String username, String password){

        StatefullRestTemplate restTemplate = new StatefullRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> response;

        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("username", "test");
        form.add("password", "test");

        //headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        try {
            response = restTemplate.exchange(REST_SERVICE_URI+"/auth", HttpMethod.POST, request, String.class);
            String resString = response.getBody();
            headers = response.getHeaders();

        } catch (HttpClientErrorException e) {

            System.out.println("Status code exception "+e.getStatusCode());
            System.out.println("Responsebody as string "+e.getResponseBodyAsString());
        }

        System.out.println("between header and setcookie");
        List<String> setCookie = headers.get(headers.SET_COOKIE);
        this.setCookie(setCookie.get(0).split(";")[0]);
        System.out.println("Cookie: " +cookie);
        String jsessionid = cookie.split("=")[1];
        System.out.println("Session id " + jsessionid);
    }

    //@PostConstruct

    public void getLoggedInUser(){

        StatefullRestTemplate restTemplate = new StatefullRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity<>(null, headers);
        System.out.println("the cookie is still: " + cookie);
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.add("Cookie", this.getCookie());
        //ResponseEntity<String> responseEntity = restTemplate.exchange(REST_SERVICE_URI+"/username",
          //      HttpMethod.GET, httpEntity, String.class);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(REST_SERVICE_URI+"/username", String.class, httpEntity);

        System.out.println("The logged in user is: " + responseEntity.getBody());

    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}
