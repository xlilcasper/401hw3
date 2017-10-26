import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    private static Cache instance = null;
    ConcurrentHashMap<String,Page> cacheMap = new ConcurrentHashMap();
    public static Cache getInstance(){
        if (instance == null) {
            synchronized (Cache.class) {
                if (instance==null) {
                    instance = new Cache();
                }
            }
        }
        return instance;
    }

    public byte[] getPage(String url) {
        if (!cacheMap.containsKey(url)) {
            System.out.println("First visit to the page");
            Page page = new Page(url);
            cacheMap.putIfAbsent(url,page);
        }
        System.out.println("Serve page");
        return cacheMap.get(url).getData();
    }
}
