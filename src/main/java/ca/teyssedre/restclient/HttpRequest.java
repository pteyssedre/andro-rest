package ca.teyssedre.restclient;


import android.util.Base64;

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
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * Former object to create HTTP request to be execute through the {@link HttpClient} instance.
 *
 * @author pteyssedre
 * @version 2
 */
@SuppressWarnings("unused")
public class HttpRequest {

    private static final String TAG = "HttpRequest";
    //<editor-fold desc="properties">
    private UUID id;
    private HttpRequestType type;
    private HttpContentType contentType;
    private Set<HttpHeader> headers;
    private boolean https;
    private boolean anonymous;
    private SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    private String url;
    private String data;
    private byte[] binary;
    private int readTimeout = 15 * 1000;
    private int connectTimeout = 30 * 1000;
    private boolean read = true;
    private boolean write;
    private HttpResponse response;
    private boolean processed;
    private HttpURLConnection connection;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public HttpRequest() {
        id = UUID.randomUUID();
        headers = new HashSet<>();
        type = HttpRequestType.GET;
    }

    public HttpRequest(String url) throws Exception {
        id = UUID.randomUUID();
        headers = new HashSet<>();
        this.url = url;
        type = HttpRequestType.GET;
        validateUrl();
    }

    public HttpRequest(String url, HttpRequestType type) throws Exception {
        id = UUID.randomUUID();
        headers = new HashSet<>();
        this.url = url;
        setType(type);
        validateUrl();
    }

    public HttpRequest(String url, HttpContentType contentType) throws Exception {
        id = UUID.randomUUID();
        headers = new HashSet<>();
        this.url = url;
        setContentType(contentType);
        validateUrl();
    }

    public HttpRequest(String url, HttpRequestType type, HttpContentType contentType) throws Exception {
        id = UUID.randomUUID();
        headers = new HashSet<>();
        this.url = url;
        this.type = type;
        setContentType(contentType);
        sslFactory = new NoSSLValidation();
        validateUrl();
    }
    //</editor-fold>

    //<editor-fold desc="Public methods">

    public HttpRequest addBinary(byte[] binary) {
        this.binary = binary;
        this.type = HttpRequestType.POST;
        return this;
    }

    public HttpRequest addHeader(String key, String value) {
        headers.add(new HttpHeader(key, value));
        return this;
    }

    public HttpRequest addBasic(String credentials) {
        String encoded = "Basic " + Base64.encodeToString((credentials).getBytes(), Base64.NO_WRAP);
        addAuthorization(encoded);
        return this;
    }

    public HttpRequest addAuthorization(String encoded) {

        headers.add(new HttpHeader("Authorization", encoded));
        return this;
    }

    public HttpRequest addFormData(HttpForm data) {
        if (data != null) {
            try {
                String serialize = data.serialize();
                this.addData(serialize);
                this.contentType = HttpContentType.APPLICATION_WWW_FORM;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public HttpRequest addData(String data) {
        this.data = data;
        determineType(data);
        this.type = HttpRequestType.POST;
        this.write = true;
        return this;
    }

    private void determineType(String data) {
        // TODO: try to guess content-type
        try {
            JSONObject n = new JSONObject(data);
            setContentType(HttpContentType.APPLICATION_JSON);
        } catch (JSONException e) {
            e.printStackTrace();
            setContentType(HttpContentType.PLAIN_TEXT);
        }
    }

    public HttpRequest addUserAgent(boolean anonymous) {
        this.anonymous = anonymous;
        return this;
    }

    public HttpRequest addUserAgent(String userAgent) {
        headers.add(new HttpHeader("User-Agent", userAgent));
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="Private methods">
    protected HttpRequest processRequest() {
        response = new HttpResponse();
        processed = true;
        connection = null;
        try {
            URL url = new URL(this.url);
            if (https) {
                connection = (HttpsURLConnection) url.openConnection();
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }
            if (sslFactory != null) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(sslFactory);
            }
            if (!anonymous) {
                connection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            }
            if (contentType != null) {
                connection.setRequestProperty("Content-Type", contentType.getValue());
            }
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            connection.setRequestProperty("Connection", "keep-alive");
            // TODO: may change depending on content-type
            connection.setRequestProperty("Accept", "*/*");

            connection.setConnectTimeout(connectTimeout);

            switch (type) {
                case PUT:
                    connection.setRequestMethod("PUT");
                    break;
                case GET:
                    connection.setRequestMethod("GET");
                    break;
                case POST:
                    connection.setRequestMethod("POST");
                    if (data != null && data.length() > 0) {
                        connection.setRequestProperty("Content-Length", "" + String.valueOf(data.getBytes().length));
                    } else if (binary != null) {
                        connection.setRequestProperty("Content-Length", "" + String.valueOf(binary.length));
                    }
                    break;
                case DELETE:
                    connection.setRequestMethod("DELETE");
                    break;
            }
            if (headers != null) {
                for (HttpHeader header : headers) {
                    connection.setRequestProperty(header.getName(), header.getValue());
                }
            }
            connection.setReadTimeout(readTimeout);
            connection.connect();

        } catch (IOException e) {
            e.printStackTrace();
            response.setException(e);
        }

        return this;
    }

    protected void doRead() {
        if (connection == null) {
            return;
        }
        try {
            InputStream in = connection.getInputStream();
            if (in != null) {
                Charset charset = Charset.forName("UTF8");
                Reader reader;
                if ("gzip".equals(connection.getContentEncoding())) {
                    reader = new InputStreamReader(new GZIPInputStream(in), charset);
                } else {
                    reader = new InputStreamReader(in, charset);
                }
                BufferedReader rd = new BufferedReader(reader);
                String line;
                StringBuilder sbt = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    sbt.append(line);
                }
                rd.close();
                addStringResult(sbt.toString());
            }
        } catch (IOException e) {
            response.setException(e);
        }
    }

    private void addStringResult(String data) {
        if (response == null) {
            response = new HttpResponse();
        }
        response.setStringResponse(data);
    }

    protected void doWrite() {
        try {
            if (write) {
                OutputStream os = connection.getOutputStream();
                if (data != null) {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(data);
                    writer.flush();
                    writer.close();
                } else {
                    // binary to send could be very long ... should be able request
                    DataOutputStream writer = new DataOutputStream(os);
                    writer.write(binary);
                    writer.flush();
                    writer.close();
                }
            }
        } catch (IOException exception) {
            response.setException(exception);
        }
    }

    private void parseConnection() {
        if (connection != null) {
            if (response == null) {
                response = new HttpResponse();
            }
            response.setOrigin(url);
            try {
                response.setStatusCode(connection.getResponseCode());
                response.setContentType(connection.getContentType());
                response.setHeaders(connection.getHeaderFields());
                try {
                    if (https) {
                        HttpsURLConnection sslConnection = (HttpsURLConnection) connection;
                        response.setCertificates(sslConnection.getServerCertificates());
                        response.setCipherSuite(sslConnection.getCipherSuite());
                    }
                } catch (RuntimeException ignored) {

                }
            } catch (IOException ignored) {
                response.setException(ignored);
            }
        }
    }

    private void validateUrl() throws Exception {
        if (this.url == null) {
            throw new Exception("URL can't be null");
        }
        URL url = new URL(this.url);
        if (url.getProtocol().toLowerCase().contains("https")) {
            https = true;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Getters & Setters">
    public Set<HttpHeader> getHeaders() {
        return headers;
    }

    public void setHeaders(Set<HttpHeader> headers) {
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        try {
            validateUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public byte[] getBinary() {
        return binary;
    }

    public void setBinary(byte[] binary) {
        this.binary = binary;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public HttpRequestType getType() {
        return type;
    }

    public void setType(HttpRequestType type) {
        this.type = type;
    }

    public HttpContentType getContentType() {
        return contentType;
    }

    public void setContentType(HttpContentType contentType) {
        this.contentType = contentType;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public SSLSocketFactory getSslFactory() {
        return sslFactory;
    }

    public void setSslFactory(SSLSocketFactory sslFactory) {
        this.sslFactory = sslFactory;
    }

    public HttpResponse getResponse() {
        parseConnection();
        return response;
    }

    public UUID getId() {
        return id;
    }

    public boolean hasBeenProcessed() {
        return processed;
    }

    public boolean shouldWrite() {
        return write;
    }

    public boolean shouldRead() {
        return read;
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    //</editor-fold>
}
