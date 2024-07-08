import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Main {
  public static void main(String[] args) throws IOException {

    System.out.println("Logs from your program will appear here!");


     ServerSocket serverSocket = null;
     Socket clientSocket = null;
     //DataOutputStream out = null;
     Map<String, String> httpRequest = new HashMap<>();

     try {
       serverSocket = new ServerSocket(4221);

       serverSocket.setReuseAddress(true);
       clientSocket = serverSocket.accept();
       //System.out.println("accepted new connection");



         DataInputStream in = new DataInputStream(clientSocket.getInputStream());
         int readableBytes = clientSocket.getInputStream().available();
         byte[] textAsBytes = clientSocket.getInputStream().readNBytes(readableBytes);

         //parse the text
         parseRequest(httpRequest, textAsBytes);

         if ("/".equals(httpRequest.get("target"))) {
             clientSocket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
         } else if (httpRequest.get("target").startsWith("/echo/")) {
             String queryParam = httpRequest.get("target").split("/")[2];
             clientSocket.getOutputStream().write(
                     ("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " +
                             queryParam.length() + "\r\n\r\n" + queryParam)
                             .getBytes());
         } else if (httpRequest.get("target").equals("/user-agent")) {
             String queryParam = httpRequest.get("userAgent");
             clientSocket.getOutputStream().write(
                     ("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " +
                             queryParam.length() + "\r\n\r\n" + queryParam)
                             .getBytes());
         } else {
             clientSocket.getOutputStream().write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
         }
         clientSocket.getOutputStream().flush();
         System.out.println("accepted new connection" + httpRequest.get("userAgent"));

     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }finally {
//         if (out != null) {
//             out.close();
//         }
     }
  }


   private static void parseRequest(Map<String, String> ret, byte[] text){
      String s = new String(text);
      String[] requestSplit = s.split("\r\n");
      parseRequestLine(ret, requestSplit[0]);
      parseUserAgent(ret, requestSplit[3]);
   }

    private static void parseRequestLine(Map<String, String> ret, String s) {
        String[] requestLineSplit = s.split(" ");
        ret.put("method", requestLineSplit[0].trim());
        ret.put("target", requestLineSplit[1].trim());
        ret.put("version", requestLineSplit[2].trim());
    }

    private static void parseUserAgent(Map<String, String> ret, String s){
        String[] userAgent = s.split(" ");
        ret.put("userAgent", userAgent[1].trim());
    }
}
