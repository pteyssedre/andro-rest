package ca.teyssedre.restclient;

import org.junit.Test;

import static org.junit.Assert.*;

public class HttpClientUnitTest {

    @Test
    public void client_default_request_type() throws Exception {
        HttpClient client = new HttpClient("http://example.com");
        assertEquals(HttpRequestType.GET, client.getType());
    }

    @Test
    public void client_change_request_type() throws Exception {
        HttpClient client = new HttpClient("http://example.com");
        client.setType(HttpRequestType.POST);
        assertEquals(HttpRequestType.POST, client.getType());
    }

    public void client_add_form_data() throws Exception {
        HttpClient client = new HttpClient("http://example.com");
        HttpForm data = new HttpForm();
        data.add("p1","v1").add("p2","v2");
        client.addFormData(data);
    }
}
