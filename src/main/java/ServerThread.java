import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerThread extends Thread {

    private boolean running=true;
    private ServerSocket serverSocket;
    private static final int HTTP_PORT = 8080;
    private static final int THREADS = 50;

    public ServerThread() {
        try {
            this.serverSocket = new ServerSocket(HTTP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        while (running){
            try {
                System.out.println("Waiting for connection");
                //if threads is 1 then it will wait until the thread finishes before accepting another connection.
                executor.execute(new ConnectionThread(serverSocket.accept()));
                System.out.println("Connection accepted");
            } catch (SocketException e) {
                //if we are no longer running discard the socket closed error
                if (!running)
                    System.out.println("Closing socket");
                else //we should of been running still, something bad happened
                    e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Close our socket
     */
    public void close() {
        running=false;
        //Close quietly
        try {
            serverSocket.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
}
