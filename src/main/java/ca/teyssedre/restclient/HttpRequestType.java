package ca.teyssedre.restclient;

/**
 * Simple enum to manage request type.
 *
 * @version 1.0
 */
public enum HttpRequestType {

    PUT(0),
    GET(1),
    POST(2),
    DELETE(3);


    private final int code;

    HttpRequestType(int i) {
        code = i;
    }

    public int getCode() {
        return code;
    }

    public static HttpRequestType parse(int i) {
        switch (i) {
            case 0:
                return PUT;
            case 1:
                return GET;
            case 2:
                return POST;
            case 3:
                return DELETE;
            default:
                return GET;
        }
    }
}
