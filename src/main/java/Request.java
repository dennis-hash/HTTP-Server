import java.util.Map;

public class Request {
    private String method;
    private String httpVersion;
    private String path;
    private Map<String, String> headers;
    private String requestBody;
    public void setMethod(String method) { this.method = method; }
    public void setPath(String path) { this.path = path; }
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public Map<String, String> getHeaders() { return headers; }
    public String getRequestBody() { return requestBody; }
    public String getHttpVersion() { return httpVersion; }
    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }
}
