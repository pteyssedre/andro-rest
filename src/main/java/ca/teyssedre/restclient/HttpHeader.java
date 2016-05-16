package ca.teyssedre.restclient;

/**
 *
 * @version 1.0
 * @author pteyssedre
 */
public class HttpHeader {

    private String name;
    private String value;

    public HttpHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public HttpHeader setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public HttpHeader setValue(String value) {
        this.value = value;
        return this;
    }
}
