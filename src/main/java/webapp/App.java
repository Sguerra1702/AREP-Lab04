package webapp;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class App {
    public static void main(String[] args) {
        WebServer.staticfiles("src/main/resources");
        WebServer.get("/hello", (req, res) -> {
            String name = req.getValues("name");
            String decodedName = URLDecoder.decode(name, StandardCharsets.UTF_8);
            return "Hello, " + decodedName;
        });
        WebServer.get("/pi", (req, res) -> String.valueOf(Math.PI));

        try {
            WebServer.getInstance().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
