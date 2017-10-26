import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            ServerThread serverThread = new ServerThread();
            serverThread.start();
            while (System.in.available() == 0) {}
            serverThread.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
