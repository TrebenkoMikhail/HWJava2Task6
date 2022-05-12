import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    private static final int PORT = 8150;

    public static void main(String[] args) throws IOException {

        new Server().start(PORT);
    }

    private static void start(int port) throws IOException {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        Thread inputThread = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Сервер запущен");
            clientSocket = serverSocket.accept();
            System.out.println("Сервер подключился");
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

            runInputLoop(inputStream);
            runOutputLoop(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputThread != null) {
                inputThread.isInterrupted();
            } if (clientSocket != null) {
                clientSocket.close();
            } if (serverSocket != null) {
                serverSocket.close();
            }
        }
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
    private static void runInputLoop(DataInputStream inputStream) {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted())
                try {
                    String message = inputStream.readUTF();
                    System.out.println("From client: " + message);
                    if (message.startsWith("/end")) {
                        System.exit(0);
                    }
                } catch (IOException e) {
                    System.out.println("подключение прервано");
                    break;
                }
        }).start();
    }
}
