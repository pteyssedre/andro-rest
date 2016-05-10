package ca.teyssedre.restclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpForm {

    private Map<String, String> data;

    public HttpForm() {
        data = new HashMap<>();
    }

    public HttpForm add(String key, String value) {
        //TODO: validate key url compatible
        data.put(key.trim(), value);
        return this;
    }

    public String serialize() throws UnsupportedEncodingException {
        String str = "";
        Set<String> keys = data.keySet();
        int i = 0;
        for (String key : keys) {
            String value = data.get(key);
            str += key + "=" + URLEncoder.encode(value, "UTF-8");
            if (++i < keys.size()) {
                str += "&";
            }
        }
        return str;
    }
}
