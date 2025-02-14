package webapp;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class App {
    public static void main(String[] args) {
        WebServer.staticfiles("src/main/resources"); //Change the directory to the resources folder
        try {
            WebServer.getInstance().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
