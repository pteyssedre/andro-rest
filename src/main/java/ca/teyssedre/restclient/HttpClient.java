package ca.teyssedre.restclient;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * HttpClient is a wrapper around {@link URLConnection} class to simplify the execution of
 * HTTP and HTTPS request. This class provide simple use of Http call, GET and POST.
 *
 * @version 1.1
 */
public class HttpClient {

    private static final String TAG = "HttpClient";
    private boolean _https;
    private HttpRequestType _type;
    private HttpContentType _contentType;
    private SSLSocketFactory _sslFactory;
    private String _url;
    private String _data;
    private byte[] _binary;
    private int _readTimeout = 15 * 1000;
    private int _connectTimeout = 30 * 1000;
    private boolean _authentication = false;
    private String _credentials = null;
    private boolean _read = true;
    private boolean _write = true;
    private String _path = "";
    private HashMap<String, String> _headers;
    private boolean _anonymous = true;

    /**
     * Default constructor of {@link HttpClient} class.
     *
     * @param url value of {@link String} which provide the endpoint.
     */
    public HttpClient(String url) {
        this._url = url;
        if (_url.contains("https")) {
            _https = true;
        }
        this._type = HttpRequestType.GET;
    }

    /**
     * Constructor of {@link HttpClient} class.
     *
     * @param url  value of {@link String} which provide the endpoint.
     * @param type type {@link HttpRequestType} to indicate the type of HTTP method to execute.
     */
    public HttpClient(String url, HttpRequestType type) {
        this._url = url;
        if (_url.contains("https")) {
            _https = true;
        }
        this._type = type;
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
        this._sslFactory = factory;
        return this;
    }

    /**
     * Adding to the URL a specific path.
     *
     * @param path {@link String} path to join to the URL.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient appendPath(String path) {
        this._path = path;
        return this;
    }

    /**
     * By adding string data to the request, the {@link #_type} parameter will be changed to
     * {@link HttpRequestType#POST}.
     *
     * @param data {@link String} data to send.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient addData(String data) {
        this._data = data;
        this._type = HttpRequestType.POST;
        return this;
    }

    /**
     * Shorter to add www form data into the request.
     * The {@link #_type} parameter will be changed to {@link HttpRequestType#POST}.
     * {@link #_contentType} will be set at {@link HttpContentType#APPLICATION_WWW_FORM}
     *
     * @param data {@link HttpForm} instance to include in the request.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient addFormData(HttpForm data) {
        if (data != null) {
            try {
                String serialize = data.serialize();
                this.addData(serialize);
                this._contentType = HttpContentType.APPLICATION_WWW_FORM;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * Shorter to add header pair inside the request.
     *
     * @param key   {@link String} header name.
     * @param value {@link String} header value.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient addHeader(String key, String value) {
        if (this._headers == null) {
            this._headers = new HashMap<>();
        }
        this._headers.put(key, value);
        return this;
    }

    /**
     * @param binary array of byte to send through a POST request.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient addBinary(byte[] binary) {
        this._binary = binary;
        this._type = HttpRequestType.POST;
        return this;
    }

    /**
     * @param credentials {@link String} value to put the 'Authorization' header.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient addCredentials(String credentials) {
        if (credentials == null || credentials.length() == 0) {
            return this;
        }
        _authentication = true;
        this._credentials = credentials;
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
        if (credentials == null || credentials.length() == 0) {
            return this;
        }
        _authentication = true;
        this._credentials = "Basic " + Base64.encodeToString((credentials).getBytes(), Base64.NO_WRAP);
        return this;
    }

    /**
     * Change the content type of the request.
     *
     * @param contentType {@link HttpContentType} value.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient setContentType(HttpContentType contentType) {
        _contentType = contentType;
        return this;
    }

    /**
     * Change the flag to request the read of data by the request (GET method).
     *
     * @param read {@link Boolean} value of the flag.
     * @return the current instance of {@link HttpClient}.
     * @deprecated This should not be used, the {@link #_read} flag should be set on the {@link #_type} of the HttpClient.
     */
    public HttpClient setRead(boolean read) {
        _read = read;
        return this;
    }

    /**
     * Change the flag to request right by the request (POST method).
     *
     * @param write {@link Boolean} value of the flag.
     * @return the current instance of {@link HttpClient}.
     * @deprecated This should not be used, the {@link #_write} flag should be set on the {@link #_type} of the HttpClient.
     */
    public HttpClient setWrite(boolean write) {
        _write = write;
        return this;
    }

    /**
     * Change the default connect timeout.
     *
     * @param millisecond {@link Integer} value of the connect timeout.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient setTimeout(int millisecond) {
        _connectTimeout = millisecond;
        return this;
    }

    /**
     * Change the default read timeout.
     *
     * @param millisecond {@link Integer} value of the read timeout.
     * @return the current instance of {@link HttpClient}.
     */
    public HttpClient setReadTimeout(int millisecond) {
        _readTimeout = millisecond;
        return this;
    }

    /**
     * @return {@link InputStream} instance of the {@link URLConnection} instance.
     * @throws IOException
     */
    public InputStream execute() throws IOException {
        HttpURLConnection connection = prepare();
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
        URL url = new URL(_url + _path);
        HttpURLConnection conn;
        if (_https) {
            conn = (HttpsURLConnection) url.openConnection();
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }
        if (_write && _type == HttpRequestType.POST) {
            conn.setDoOutput(true);
        }
        if (_sslFactory != null) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(_sslFactory);
        }
        if (_authentication) {
            conn.setRequestProperty("Authorization", _credentials);
        }
        if (!_anonymous) {
            conn.setRequestProperty("User-Agent", System.getProperty("http.agent"));
        }
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Accept", "*/*");

        conn.setConnectTimeout(_connectTimeout);

        switch (_type) {
            case PUT:
                conn.setRequestMethod("PUT");
                break;
            case GET:
                conn.setRequestMethod("GET");
                break;
            case POST:
                conn.setRequestMethod("POST");
                if (_data != null && !_data.isEmpty()) {
                    conn.setRequestProperty("Content-Length", "" + String.valueOf(_data.getBytes().length));
                } else if (_binary != null) {
                    conn.setRequestProperty("Content-Length", "" + String.valueOf(_binary.length));
                }
                break;
            case DELETE:
                conn.setRequestMethod("DELETE");
                break;
        }
        if (_contentType != null) {
            conn.setRequestProperty("Content-Type", _contentType.getValue());
        }
        if (_headers != null) {
            for (String value : _headers.keySet()) {
                conn.setRequestProperty(value, _headers.get(value));
            }
        }
        if (_read) {
            //Read
            conn.setDoInput(true);
            conn.setReadTimeout(_readTimeout);
        }

        if (_type == HttpRequestType.POST) {
            OutputStream os = conn.getOutputStream();
            if (_data != null) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(_data);
                writer.flush();
                writer.close();
            } else {
                // binary to send could be very long ... should be able request
                DataOutputStream writer = new DataOutputStream(os);
                writer.write(_binary);
                writer.flush();
                writer.close();
            }
        }
        return conn;
    }

    /**
     * Helper to retrieved from an {@link InputStream} object the {@link String} value.
     *
     * @param connection {@link HttpURLConnection}
     * @return the response of the server in a {@link String} format.
     * @throws IOException could be throw due to the {@link #execute()}
     */
    private String readAsString(HttpURLConnection connection) throws IOException {
        InputStream in = execute();
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
        return _type;
    }

    public void setType(HttpRequestType type) {
        this._type = type;
    }

    public void includeUserAgent(boolean value) {
        this._anonymous = value;
    }
}
