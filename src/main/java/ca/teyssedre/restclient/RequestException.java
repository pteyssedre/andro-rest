package ca.teyssedre.restclient;

public class RequestException extends Exception {
    public int statusCode;
    public String message;
}
