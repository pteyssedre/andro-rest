package ca.teyssedre.restclient;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLSocketFactory;

/**
 * HttpClient is a wrapper around {@link URLConnection} class to simplify the execution of
 * HTTP and HTTPS request. This class provide simple use of Http call, GET and POST.
 *
 * @version 2.0
 */
@SuppressWarnings("unused")
public class HttpClient {

    private static final String TAG = "HttpClient";
    private Set<HttpRequest> requests;

    /**
     * Default constructor of {@link HttpClient} class.
     *
     * @param url value of {@link String} which provide the endpoint.
     */
    public HttpClient(String url) {
        requests = new HashSet<>();
        try {
            requests.add(new HttpRequest(url, HttpRequestType.GET));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public HttpClient(HttpRequest request) {
        requests = new HashSet<>();
        requests.add(request);
    }

    /**
     * Constructor of {@link HttpClient} class.
     *
     * @param url  value of {@link String} which provide the endpoint.
     * @param type type {@link HttpRequestType} to indicate the type of HTTP method to execute.
     */
    public HttpClient(String url, HttpRequestType type) {
        requests = new HashSet<>();
        try {
            requests.add(new HttpRequest(url, type));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * If a custom {@link SSLSocketFactory} is require to perform the HTTP/HTTPS call :
     * for custom validation or some other specific operation.
     *
     * @param factory instance of {@link SSLSocketFactory} which provide the {@link javax.net.ssl.SSLContext}
     *                to create and validate handshake process.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient setSSLFactory(SSLSocketFactory factory) {
        for (HttpRequest request : requests) {
            request.setSslFactory(factory);
        }
        return this;
    }

    /**
     * Adding to the URL a specific path.
     *
     * @param path {@link String} path to join to the URL.
     * @return the current instance of {@link HttpClient}.
     * @deprecated not used
     */
    public HttpClient appendPath(String path) {
        return this;
    }

    /**
     * By adding string data to all requests, the type parameter will be changed to
     * {@link HttpRequestType#POST}.
     *
     * @param data {@link String} data to send.
     * @return the current instance of {@link HttpClient}.
     * @deprecated set the data to the request
     */
    public HttpClient addData(String data) {
        for (HttpRequest request : requests) {
            request.addData(data);
        }
        return this;
    }

    /**
     * Shorter to add www form data into all the requests.
     * The type parameter will be changed to {@link HttpRequestType#POST}.
     * contentType will be set at {@link HttpContentType#APPLICATION_WWW_FORM}
     *
     * @param data {@link HttpForm} instance to include in the request.
     * @return the current instance of {@link HttpClient}.
     * @deprecated set the data to the request
     */
    public HttpClient addFormData(HttpForm data) {
        for (HttpRequest request : requests) {
            request.addFormData(data);
        }
        return this;
    }

    /**
     * Shorter to add header pair inside all the requests of this instance.
     *
     * @param key   {@link String} header name.
     * @param value {@link String} header value.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient addHeader(String key, String value) {
        for (HttpRequest request : requests) {
            request.addHeader(key, value);
        }
        return this;
    }

    /**
     * @param binary array of byte to send through a POST request.
     * @return the current instance of {@link HttpClient}.
     * @deprecated should be set into the {@link HttpRequest} instance.
     */
    public HttpClient addBinary(byte[] binary) {
        for (HttpRequest request : requests) {
            request.addBinary(binary);
        }
        return this;
    }

    /**
     * @param credentials {@link String} value to put the 'Authorization' header.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient addCredentials(String credentials) {
        for (HttpRequest request : requests) {
            request.addHeader("Authorization", credentials);
        }
        return this;
    }

    /**
     * Adding Basic authentication by providing the username/password pair. This method will do the
     * {@link Base64#encode(byte[], int)} using the {@link Base64#NO_WRAP} flag.
     *
     * @param credentials {@link String} value of the pair "username" and "password".
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient addBasicCredentials(String credentials) {
        for (HttpRequest request : requests) {
            request.addBasic(credentials);
        }
        return this;
    }

    /**
     * Change the content type of the request.
     *
     * @param contentType {@link HttpContentType} value.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient setContentType(HttpContentType contentType) {
        for (HttpRequest request : requests) {
            request.setContentType(contentType);
        }
        return this;
    }

    /**
     * Change the flag to request the read of data by the request (GET method).
     *
     * @param read {@link Boolean} value of the flag.
     * @return the current instance of {@link HttpClient}.
     * @deprecated This should not be used, the read flag should be set on the type of the HttpClient.
     */
    public HttpClient setRead(boolean read) {
        for (HttpRequest request : requests) {
            request.setRead(read);
        }
        return this;
    }

    /**
     * Change the flag to request right by the request (POST method).
     *
     * @param write {@link Boolean} value of the flag.
     * @return the current instance of {@link HttpClient}.
     * @deprecated This should not be used, the write flag should be set on the type of the HttpClient.
     */
    public HttpClient setWrite(boolean write) {
        for (HttpRequest request : requests) {
            request.setWrite(write);
        }
        return this;
    }

    /**
     * Change the default connect timeout.
     *
     * @param millisecond {@link Integer} value of the connect timeout.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient setTimeout(int millisecond) {
        for (HttpRequest request : requests) {
            request.setConnectTimeout(millisecond);
        }
        return this;
    }

    /**
     * Change the default read timeout.
     *
     * @param millisecond {@link Integer} value of the read timeout.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient setReadTimeout(int millisecond) {
        for (HttpRequest request : requests) {
            request.setReadTimeout(millisecond);
        }
        return this;
    }

    /**
     * @return {@link InputStream} instance of the {@link URLConnection} instance.
     * @throws IOException
     */
    public InputStream execute() throws IOException {
        HttpURLConnection connection = prepare();
        assert connection != null;
        int responseCode = connection.getResponseCode();
        Log.d(TAG, "response code : " + responseCode);
        return connection.getInputStream();
    }

    /**
     * Helper function to execute the request through the call of {@link HttpClient#execute()} and parse
     * the result as a {@link JSONObject}.
     *
     * @return {@link JSONObject} instance parse from the {@link String} value of the {@link InputStream} of the request made.
     * @throws IOException   throw by the {@link InputStream} object in case of error.
     * @throws JSONException
     */
    public JSONObject getJson() throws IOException, JSONException {
        return new JSONObject(getString());
    }

    /**
     * Helper function to execute the request through the call of {@link HttpClient#execute()} and parse
     * the result as a {@link String}.
     *
     * @return {@link String} value of the {@link InputStream} of the request made.
     * @throws IOException could be throw
     */
    public String getString() throws IOException {
        return readAsString(prepare());
    }

    /**
     * Shorter to connect the {@link URL} object and setup the {@link HttpURLConnection} instance.
     *
     * @return {@link HttpURLConnection}
     * @throws IOException
     */
    private HttpURLConnection prepare() throws IOException {
        if (requests.size() > 0) {
            Iterator<HttpRequest> requestIterator = requests.iterator();
            HttpRequest request = requestIterator.next();
            return request.processRequest();
        }
        return null;
    }

    /**
     * Helper to retrieved from an {@link InputStream} object the {@link String} value.
     *
     * @param connection {@link HttpURLConnection}
     * @return the response of the server in a {@link String} format.
     * @throws IOException could be throw due to the {@link #execute()}
     */
    private String readAsString(HttpURLConnection connection) throws IOException {
        InputStream in = connection.getInputStream();
        if (in != null) {
            Charset charset = Charset.forName("UTF8");
            Reader reader;
            if ("gzip".equals(connection.getContentEncoding())) {
                reader = new InputStreamReader(new GZIPInputStream(connection.getInputStream()), charset);
            } else {
                reader = new InputStreamReader(connection.getInputStream(), charset);
            }
            BufferedReader rd = new BufferedReader(reader);
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        }
        return null;
    }

    public HttpRequestType getType() {
        HttpRequestType type = HttpRequestType.UNKNOWNS;
        if (this.requests.size() > 0) {
            type = this.requests.iterator().next().getType();
        }
        return type;
    }
}
