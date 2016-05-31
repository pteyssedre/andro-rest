package ca.teyssedre.restclient;

import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private int statusCode;
    private String origin;
    private String contentType;
    private Certificate[] certificates;
    private String cipherSuite;
    private Exception exception;
    private Map<String, List<String>> headers;
    private String stringResponse;

    public HttpResponse() {
    }

    public HttpResponse(int statusCode, String origin, String contentType, Certificate[] certificates, String cipherSuite) {
        this.statusCode = statusCode;
        this.origin = origin;
        this.contentType = contentType;
        this.certificates = certificates;
        this.cipherSuite = cipherSuite;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Certificate[] getCertificates() {
        return certificates;
    }

    public void setCertificates(Certificate[] certificates) {
        this.certificates = certificates;
    }

    public String getCipherSuite() {
        return cipherSuite;
    }

    public void setCipherSuite(String cipherSuite) {
        this.cipherSuite = cipherSuite;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        // TODO manage exception parsing custom exception should be creating in order easily manage them.
        this.exception = exception;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getStringResponse() {
        return stringResponse;
    }

    public void setStringResponse(String stringResponse) {
        this.stringResponse = stringResponse;
    }
}
