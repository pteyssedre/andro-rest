package ca.teyssedre.restclient;

/**
 * Enum to specify the Content-Type of a request.
 *
 * @version 1.1
 */
public enum HttpContentType {

    APPLICATION_JSON(0, "application/json"),
    APPLICATION_XML(1, "application/xml"),
    OCTET_STREAM(2, "octet/stream"),
    TEXT_HTML(3, "text/html"),
    APPLICATION_WWW_FORM(4, "application/x-www-form-urlencoded"),
    PLAIN_TEXT(5, "text/plain");

    private final int code;
    private final String value;

    HttpContentType(int i, String v) {
        code = i;
        value = v;
    }

    public static HttpContentType parse(int i) {
        switch (i) {
            case 0:
                return APPLICATION_JSON;
            case 1:
                return APPLICATION_XML;
            case 2:
                return OCTET_STREAM;
            case 3:
                return TEXT_HTML;
            case 4:
                return APPLICATION_WWW_FORM;
            case 5:
                return PLAIN_TEXT;
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
                } else if ("text/html".equals(i)) {
                    return TEXT_HTML;
                } else if ("text/plain".equals(i)) {
                    return PLAIN_TEXT;
                }
            }
            return APPLICATION_JSON;
        }
        return APPLICATION_JSON;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
