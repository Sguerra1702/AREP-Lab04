package webapp.services;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private String path;
    private Map<String, String> queryParams = new HashMap<>();

    public Request(String url) {
        if (url.contains("?")) {
            String[] parts = url.split("\\?");
            this.path = parts[0];
            String[] params = parts[1].split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        } else {
            this.path = url;
        }
    }

    public String getPath() {
        return path;
    }

    public String getValues(String key) {
        return queryParams.getOrDefault(key, null);
    }
}
