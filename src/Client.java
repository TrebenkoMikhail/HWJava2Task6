import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        new Client().start("localHost", 8150);
    }

    private static void start(String host, int port) throws IOException {
        Socket socket = null;
        Socket serverSocket = null;
        Thread inputThread = null;

        try {
            socket = new Socket(host, port);
            System.out.println("Клиент запущен");
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            runInputLoop(inputStream);
            runOutputLoop(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputThread != null) {
                inputThread.isInterrupted();
            } if (serverSocket != null) {
                serverSocket.close();
            } if (socket != null) {
                socket.close();
                        }
        }
    }




    private static void runInputLoop(DataInputStream inputStream) {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted())
              try {
                String message = inputStream.readUTF();
                  System.out.println("From server: " + message);
                  if (message.startsWith("/end")) {
                      System.exit(0);
                  }
            } catch (IOException e) {
                  System.out.println("подключение прервано");
                   break;
                }
        }).start();
    }

    private static void runOutputLoop(DataOutputStream outputStream) throws IOException {
        Scanner scan = new Scanner(System.in);
        while (true) {
            String message = scan.nextLine();
            outputStream.writeUTF(message);
            if (message.startsWith("/end")) {
                break;
            }

        }
    }
}
