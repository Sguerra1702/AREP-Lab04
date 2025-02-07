package webapp;

import java.io.*;
import java.net.*;
import java.util.Arrays;

import webapp.services.WebService;

/**
 * Servidor HTTP que atiende solicitudes REST.
 */
public class WebServer {

    private static WebServer instance;

    public static WebServer getInstance() {
        if (instance == null) {
            instance = new WebServer();
        }
        return instance;
    }

    public void start() throws IOException {
        ServerSocket server = null;
        try {
            server = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("No se pudo iniciar en el puerto 35000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket client = null;
            try {
                System.out.println("Esperando conexión...");
                client = server.accept();
                System.out.println("Listo para recibir ...");
            } catch (IOException e) {
                System.err.println("Error en la conexión.");
                System.exit(1);
            }

            handleRequest(client);
        }
        server.close();
    }

    private void handleRequest(Socket client) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStream out = client.getOutputStream()) {
            String line, respuesta = "";
            boolean firstline = true;
            String query = "/home.html"; // Por defecto, servirá home.html

            while ((line = in.readLine()) != null) {
                if (firstline) {
                    String requestPath = line.split(" ")[1];

                    // Redirigir '/' o '/app' a '/app/home.html'
                    if (requestPath.equals("/") || requestPath.equals("/app")) {
                        query = "/home.html";
                    } else if (requestPath.startsWith("/app")) {
                        query = requestPath.substring(5); // Remueve '/app'
                    } else {
                        query = requestPath;
                    }
                    firstline = false;
                }
                if (!in.ready()) {
                    break;
                }
            }

            respuesta = processResource(out, query);


        } catch (IOException e) {
            System.err.println("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    private String processResource(OutputStream out, String recurso) {
        WebService service = WebService.getInstance();
        try {
            String tipo = recurso.contains(".") ? recurso.split("\\.")[1] : "html";
            String ruta = "src/main/resources/" + recurso;

            if (tipo.equals("png") || tipo.equals("jpg") || tipo.equals("jpeg") || tipo.equals("gif")) {
                byte[] resource = service.getBinaryResource(ruta);
                String header = service.getImageHeader(tipo, "200 OK", resource.length);
                out.write(header.getBytes());
                out.write(resource);
            } else {
                String header = service.getHeader(tipo, "200 OK");
                String resource = service.getResource(ruta);
                out.write((header + resource).getBytes());
            }
        } catch (RuntimeException | IOException e) {
            String header = service.getHeader("html", "404 Not Found");
            String resource = service.getResource("src/main/resources/404.html");
            try {
                out.write((header + resource).getBytes());
            } catch (IOException ex) {
                System.err.println("Error al escribir la respuesta: " + ex.getMessage());
            }
        }
        return recurso;
    }
}
