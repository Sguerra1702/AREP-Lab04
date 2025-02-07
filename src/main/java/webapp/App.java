package webapp;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        WebServer.staticfiles("src/main/resources");
        WebServer.get("/hello", (req, res) -> "Hello " + req.getValues("name"));
        WebServer.get("/pi", (req, res) -> String.valueOf(Math.PI));

        try {
            WebServer.getInstance().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
