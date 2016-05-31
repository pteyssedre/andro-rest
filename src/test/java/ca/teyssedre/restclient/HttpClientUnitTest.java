package ca.teyssedre.restclient;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class HttpClientUnitTest {

    @Test
    public void client_default_request_type() throws Exception {
        HttpClient client = new HttpClient("http://example.com");

        assertEquals(HttpRequestType.GET, client.getType());
    }

    @Test
    public void client_change_request_type() throws Exception {
        HttpClient client = new HttpClient("http://example.com", HttpRequestType.POST);
        assertEquals(HttpRequestType.POST, client.getType());
    }

    @Test
    public void client_add_form_data() throws Exception {
        HttpClient client = new HttpClient("http://example.com");
        HttpForm data = new HttpForm();
        data.add("p1", "v1").add("p2", "v2");
        client.addFormData(data);
    }

    @Test
    public void test_httpRequest_response() throws Exception {
        HttpClient client = new HttpClient();

        HttpRequest get = new HttpRequest("https://www.google.com/#q=hello");

        client.execute(get);

        Exception exception = get.getResponse().getException();

        Assert.assertNull(exception);
        System.out.println(get.getResponse().getStringResponse());
        Assert.assertEquals(200, get.getResponse().getStatusCode());
    }

}
