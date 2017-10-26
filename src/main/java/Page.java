import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Page {
    long date=0;
    byte[] data;
    String url = "";

    public Page(String url) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        this.date = cal.getTime().getTime();
        this.url = url;
    }

    public synchronized byte[] getData() {
        try {
            //do conditional get
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setIfModifiedSince(date);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("update cache");
                date = connection.getHeaderFieldDate("Last-Modified",0);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] byteChunk = new byte[4096];
                int n;
                InputStream is = connection.getInputStream();
                while ((n=is.read(byteChunk))>0) {
                    baos.write(byteChunk,0,n);
                }
                data = baos.toByteArray();
            }
            return data;
        } catch (Exception e) {

        }
        return null;
    }
}
