import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

      private static final int PORT = 4221;

      public static void main(String[] args) {
          System.out.println("Logs from your program will appear here!");

          ExecutorService executor = Executors.newCachedThreadPool();

          try (ServerSocket serverSocket = new ServerSocket(PORT)) {
              serverSocket.setReuseAddress(true);
              System.out.println("Server is listening on port " + PORT);

              while (true) {
                  Socket clientSocket = serverSocket.accept();
                  System.out.println("New client connected");
                  executor.execute(new ClientHandler(clientSocket));
              }
          } catch (IOException e) {
              System.out.println("IOException: " + e.getMessage());
          } finally {
              executor.shutdown();
          }
      }
}


class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream output = clientSocket.getOutputStream()) {

            Request request = parseRequest(inputStream);

            if (request.getPath().equals("/")) {
                output.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
            } else if (request.getPath().startsWith("/echo/")) {
                String queryParam = request.getPath().split("/")[2];
                output.write(("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " +
                        queryParam.length() + "\r\n\r\n" + queryParam).getBytes());
            } else if (request.getPath().contains("/user-agent")) {
                String userAgent = request.getHeaders().get("User-Agent");
                String reply = String.format("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %s\r\n\r\n%s\r\n",
                        userAgent.length(), userAgent);
                output.write(reply.getBytes());
            } else {
                output.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Request parseRequest(InputStream inputStream) throws IOException {
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
            //h
        }
        request.setHeaders(headers);
        return request;
    }
}
