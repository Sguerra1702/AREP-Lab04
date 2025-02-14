package webapp.services;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String path;
    private final Map<String, String> queryParams = new HashMap<>();

    public Request(String path, String query) {
        this.path = path;

        if (!query.isEmpty()) { // Procesar parámetros solo si existen
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length == 2) {
                    queryParams.put(
                            URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                    );
                }
            }
        }
    }

    public String getPath() {
        return path;
    }

    public String getValues(String key) {
        return queryParams.getOrDefault(key, ""); // Retorna vacío si el parámetro no existe
    }
}


