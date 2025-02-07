package webapp;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        WebServer server = WebServer.getInstance();
        server.start();
    }
}
