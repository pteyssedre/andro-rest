package ca.teyssedre.restclient;


import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

public class HttpRequestUnitTest {

    @Test
    public void httRequest_with_valid_URL_do_not_trough_error() throws Exception {
        HttpRequest request = new HttpRequest("http://example.com/data.json");
        assertNotEquals(request, null);
    }

    @Test(expected = Exception.class)
    public void httRequest_with_invalid_URL_do_trough_error() throws Exception {
        HttpRequest request = new HttpRequest("htp://exampleom/datason");
        assertNotEquals(request, null);
    }
}
