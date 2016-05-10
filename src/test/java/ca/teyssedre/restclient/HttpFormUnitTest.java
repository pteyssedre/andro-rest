package ca.teyssedre.restclient;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class HttpFormUnitTest {

    @Test
    public void httpForm_data_serialize_empty() throws Exception {
        HttpForm form = new HttpForm();
        assertEquals("", form.serialize());
    }

    @Test
    public void httpForm_data_serialize() throws Exception {
        HttpForm form = new HttpForm();
        form.add("param1", "param1").add("param2", "param2");
        assertEquals("param1=param1&param2=param2", form.serialize());
    }

    @Test
    public void httpForm_data_serialize_encoded() throws Exception {
        HttpForm form = new HttpForm();
        form.add("param1", "param1 and 2").add("param2", "param2@de");
        assertEquals("param1=param1+and+2&param2=param2%40de", form.serialize());
    }
}