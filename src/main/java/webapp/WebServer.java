package webapp;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import webapp.services.Request;
import webapp.services.Response;
import webapp.services.WebService;

/**
 * Servidor HTTP que atiende solicitudes REST y archivos estáticos.
 */
public class WebServer {

    private static WebServer instance;
    private static Map<String, BiFunction<Request, Response, String>> getRoutes = new HashMap<>();
    private static String staticFilesDir = "src/main/resources"; // Carpeta por defecto

    public static WebServer getInstance() {
        if (instance == null) {
            instance = new WebServer();
        }
        return instance;
    }

    public static void get(String path, BiFunction<Request, Response, String> handler) {
        getRoutes.put(path, handler);
    }

    public static void staticfiles(String directory) {
        staticFilesDir = directory;
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

            String line;
            boolean firstline = true;
            String query = "/home.html"; // Por defecto, servirá home.html

            Request request = null;
            Response response = new Response();

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

                    // Crear objeto Request
                    request = new Request(requestPath);

                    firstline = false;
                }
                if (!in.ready()) {
                    break;
                }
            }

            if (request != null && getRoutes.containsKey(request.getPath())) {
                // Es una ruta REST
                String responseBody = getRoutes.get(request.getPath()).apply(request, response);
                String header = "HTTP/1.1 " + response.getStatusCode() + " OK\r\n" +
                        "Content-Type: " + response.getContentType() + "\r\n\r\n";
                out.write((header + responseBody).getBytes());
            } else {
                // Servir archivo estático
                processResource(out, query);
            }

        } catch (IOException e) {
            System.err.println("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    private void processResource(OutputStream out, String recurso) {
        WebService service = WebService.getInstance();
        try {
            String tipo = recurso.contains(".") ? recurso.split("\\.")[1] : "html";
            String ruta = staticFilesDir + recurso;

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
            String resource = service.getResource(staticFilesDir + "/404.html");
            try {
                out.write((header + resource).getBytes());
            } catch (IOException ex) {
                System.err.println("Error al escribir la respuesta: " + ex.getMessage());
            }
        }
    }
}
