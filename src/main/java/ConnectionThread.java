import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConnectionThread extends Thread{
    Socket socket;
    BufferedReader in;
    DataOutputStream out;
    private boolean closed;

    //This holds our defined http codes
    //Made static so it is immutable.
    private static final Map<Integer,String> httpCodes;
    static{
        httpCodes = new HashMap<Integer, String>();
        httpCodes.put(200,"200 OK");
        httpCodes.put(403,"403 Forbidden");
        httpCodes.put(404,"404 File not found");
        httpCodes.put(501,"501 Not Implemented");
    }

    public ConnectionThread(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        super.run();
        //Read in our stream
        String line = null;
        try {
            line = in.readLine();

            if (line == null || line.length()==0)
                return;
            //Split out our first header line
            String[] request = line.split(" ");
            System.out.println("Received "+line);
            //GET requests should be served
            if (request[0].equals("GET")) {
                String url = request[1];
                byte[] data = Cache.getInstance().getPage(url);
                //Make sure our socket didn't close
                if (socket.isConnected() && data != null) {
                    out.write(data);
                }
            } else {
                send501Error(out);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        /*
        String s = new String(Cache.getInstance().getPage(url));
        //System.out.println(s);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Cache.getInstance().getPage(url);
        */
    }
    /**
     * Closes our sockets nicely
     */
    public void close() {
        closed=true;
        //Close it quietly
        try {
            socket.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private void send501Error(DataOutputStream out) {
        String html="<head></head><body>501 Not implemented</body>";
        printHeaders(out,501,html.length(),"text/html");
        try {
            out.writeBytes(html);
            out.flush();
        } catch (IOException e) {
           // e.printStackTrace();
        }
    }

    /**
     * Prints out our headers to the stream
     * @param out Stream we should send the headers out on
     * @param status Status code to set in the headers
     * @param length Length of the body of the response
     * @param type Type of file being served
     */
    private void printHeaders(DataOutputStream out,Integer status, long length,String type) {
        try {
            out.writeBytes("HTTP/1.1 "+httpCodes.get(status)+"\r\n");
            out.writeBytes("Content-Length: "+length+"\r\n");
            out.writeBytes("Content-Type: "+type+"\r\n");
            out.writeBytes("Connection: Closed\r\n");
            out.writeBytes("\r\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Make sure we have closed the socket when we are GC'ed
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (!closed)
            close();
    }
}
