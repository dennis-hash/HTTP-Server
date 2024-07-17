import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Main {
  public static void main(String[] args) {

    System.out.println("Logs from your program will appear here!");


     ServerSocket serverSocket = null;
     Socket clientSocket = null;
     //DataOutputStream out = null;
     //Map<String, String> httpRequest = new HashMap<>();

     try {
       serverSocket = new ServerSocket(4221);
       serverSocket.setReuseAddress(true);
       clientSocket = serverSocket.accept();
       //System.out.println("accepted new connection");

         InputStream inputStream = clientSocket.getInputStream();
         Request request = parseRequest(inputStream);


         OutputStream output = clientSocket.getOutputStream();

         if (request.getPath().equals("/")) {
             clientSocket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
         } else if (request.getPath().startsWith("/echo/")) {
             String queryParam = request.getPath().split("/")[2];
             clientSocket.getOutputStream().write(
                     ("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " +
                             queryParam.length() + "\r\n\r\n" + queryParam)
                             .getBytes());
         } else if (request.getPath().contains("/user-agent")) {
             String userAgent = request.getHeaders().get("User-Agent");
             String reply = String.format(
                     "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %s\r\n\r\n%s\r\n",
                     userAgent.length(), userAgent);
             output.write(reply.getBytes());
         } else {
             clientSocket.getOutputStream().write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
         }
//         clientSocket.getOutputStream().flush();
//         System.out.println("accepted new connection" + httpRequest.get("userAgent"));

     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }finally {
//         if (out != null) {
//             out.close();
//         }
     }
  }


    private static Request parseRequest(InputStream inputStream) throws IOException {

        byte[] textAsBytes = inputStream.readNBytes(inputStream.available());
        Request request = new Request();
        String rq = new String(textAsBytes);
        String[] requestLines = rq.split("\r\n");
        String[] requestType = requestLines[0].split(" ");
        request.setMethod(requestType[0]);
        request.setPath(requestType[1]);
        request.setHttpVersion(requestType[2]);
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < requestLines.length; i++) {
            String[] hlLine = requestLines[i].split(":");
            headers.put(hlLine[0].strip(), hlLine[1].strip());
        }
        request.setHeaders(headers);
        return request;
    }
}
