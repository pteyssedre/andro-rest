package ca.teyssedre.restclient;


import android.util.Base64;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * Former object to create HTTP request to be execute through the {@link HttpClient} instance.
 *
 * @author pteyssedre
 * @version 1.0
 */
public class HttpRequest {

    //<editor-fold desc="properties">
    private HttpRequestType type;
    private HttpContentType contentType;
    private Set<HttpHeader> headers;
    private boolean https;
    private boolean anonymous;
    private SSLSocketFactory sslFactory = null;
    private String url;
    private String data;
    private byte[] binary;
    private int readTimeout = 15 * 1000;
    private int connectTimeout = 30 * 1000;
    private boolean read = true;
    private boolean write = true;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public HttpRequest() {
        headers = new HashSet<>();
    }

    public HttpRequest(String url) throws Exception {
        headers = new HashSet<>();
        this.url = url;
        validateUrl();
    }

    public HttpRequest(String url, HttpRequestType type) throws Exception {
        headers = new HashSet<>();
        this.url = url;
        this.type = type;
        validateUrl();
    }

    public HttpRequest(String url, HttpContentType contentType) throws Exception {
        headers = new HashSet<>();
        this.url = url;
        this.contentType = contentType;
        validateUrl();
    }

    public HttpRequest(String url, HttpRequestType type, HttpContentType contentType) throws Exception {
        headers = new HashSet<>();
        this.url = url;
        this.type = type;
        this.contentType = contentType;
        validateUrl();
    }
    //</editor-fold>

    //<editor-fold desc="Public methods">

    public HttpRequest addBinary(byte[] binary) {
        this.binary = binary;
        return this;
    }

    public HttpRequest addHeader(String key, String value) {
        headers.add(new HttpHeader(key, value));
        return this;
    }

    public HttpRequest addBasic(String credentials) {
        String encoded = "Basic " + Base64.encodeToString((credentials).getBytes(), Base64.NO_WRAP);
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
        return this;
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
    protected HttpURLConnection processRequest() throws IOException {
        URL url = new URL(this.url);
        HttpURLConnection connection;
        if (https) {
            connection = (HttpsURLConnection) url.openConnection();
        } else {
            connection = (HttpURLConnection) url.openConnection();
        }
        if (type == HttpRequestType.POST) {
            connection.setDoOutput(true);
        }
        if (sslFactory != null) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(sslFactory);
        }
        if (!anonymous) {
            connection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
        }
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        connection.setRequestProperty("Connection", "keep-alive");
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
                if (data != null && !data.isEmpty()) {
                    connection.setRequestProperty("Content-Length", "" + String.valueOf(data.getBytes().length));
                } else if (binary != null) {
                    connection.setRequestProperty("Content-Length", "" + String.valueOf(binary.length));
                }
                break;
            case DELETE:
                connection.setRequestMethod("DELETE");
                break;
        }
        if (contentType != null) {
            connection.setRequestProperty("Content-Type", contentType.getValue());
        }
        if (headers != null) {
            for (HttpHeader header : headers) {
                connection.setRequestProperty(header.getName(), header.getValue());
            }
        }
        if (read) {
            connection.setDoInput(true);
            connection.setReadTimeout(readTimeout);
        }

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

        return connection;
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
    //</editor-fold>
}
