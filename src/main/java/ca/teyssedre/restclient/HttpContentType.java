package ca.teyssedre.restclient;

/**
 * Enum to specify the Content-Type of a request.
 *
 * @version 1.0
 */
public enum HttpContentType {

    APPLICATION_JSON(0, "application/json"),
    APPLICATION_XML(1, "application/xml"),
    OCTET_STREAM(2, "octet/stream");

    private final int code;
    private final String value;

    HttpContentType(int i, String v) {
        code = i;
        value = v;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static HttpContentType parse(int i) {
        switch (i) {
            case 0:
                return APPLICATION_JSON;
            case 1:
                return APPLICATION_XML;
            case 2:
                return OCTET_STREAM;
            default:
                return APPLICATION_JSON;
        }
    }

    public static HttpContentType parse(String v) {
        if (v != null) {
            String i = v.toLowerCase().trim();
            if (i.length() > 0) {
                if ("application/json".equals(i)) {
                    return APPLICATION_JSON;
                } else if ("application/xml".equals(i)) {
                    return APPLICATION_XML;
                } else if ("octet/stream".equals(i)) {
                    return OCTET_STREAM;
                }
            }
            return APPLICATION_JSON;
        }
        return APPLICATION_JSON;
    }
}
