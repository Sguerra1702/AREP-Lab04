package webapp;

import webapp.annotations.*;
import webapp.services.Request;
import webapp.services.Response;
import webapp.services.WebService;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.function.BiFunction;

public class WebServer {
    private static WebServer instance;
    private static final Map<String, BiFunction<Request, Response, String>> getRoutes = new HashMap<>();
    private static String staticFilesDir = "static";

    public static WebServer getInstance() {
        if (instance == null) {
            instance = new WebServer();
        }
        return instance;
    }

    public void scanControllers(String packageName) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            System.out.println("Registrando ruta: " + path);
            while (resources.hasMoreElements()) {
                File directory = new File(resources.nextElement().getFile());
                if (directory.exists()) {
                    for (String file : Objects.requireNonNull(directory.list())) {
                        if (file.endsWith(".class")) {
                            String className = packageName + "." + file.replace(".class", "");
                            registerController(Class.forName(className));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerController(Class<?> controllerClass) {
        if (controllerClass.isAnnotationPresent(RestController.class)) {
            try {
                Object instance = controllerClass.getDeclaredConstructor().newInstance();
                for (Method method : controllerClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        GetMapping getMapping = method.getAnnotation(GetMapping.class);
                        String path = getMapping.value();
                        System.out.println("Registrando ruta: " + path);
                        getRoutes.put(path, (req, res) -> {
                            try {
                                return invokeMethod(instance, method, req);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return "500 Internal Server Error";
                            }
                        });
                    }
                }
                System.out.println("Registrando controlador: " + controllerClass.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String invokeMethod(Object instance, Method method, Request req) throws Exception {
        List<Object> params = new ArrayList<>();
        for (Parameter param : method.getParameters()) {
            if (param.isAnnotationPresent(RequestParam.class)) {
                RequestParam annotation = param.getAnnotation(RequestParam.class);
                String value = req.getValues(annotation.value());
                if (value == null || value.isEmpty()) {
                    value = annotation.defaultValue();
                }
                params.add(value);
            }
        }
        return (String) method.invoke(instance, params.toArray());
    }

    public void start() throws IOException {
        scanControllers("webapp.controller");
        ServerSocket server = new ServerSocket(35000);
        System.out.println("Servidor iniciado en el puerto 35000...");
        while (true) {
            Socket client = server.accept();
            handleRequest(client);
        }
    }

    public static void staticfiles(String directory) {
        staticFilesDir = directory;
    }

    private void handleRequest(Socket client) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             OutputStream out = client.getOutputStream()) {

            String line = in.readLine();
            if (line == null) return;

            String[] requestParts = line.split(" ")[1].split("\\?", 2);
            String requestPath = requestParts[0]; // Path sin parámetros

            System.out.println("Solicitud recibida: " + requestPath);
            Request request = new Request(requestPath, requestParts.length > 1 ? requestParts[1] : "");
            Response response = new Response();

            if (requestPath.equals("/") || requestPath.isEmpty()) {
                serveStaticFile(out, "index.html");
            } else if (getRoutes.containsKey(requestPath)) {
                String responseBody = getRoutes.get(requestPath).apply(request, response);
                String header = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\n";
                out.write((header + responseBody).getBytes());
            } else if (requestPath.contains(".")) {
                serveStaticFile(out, requestPath.substring(1));
            } else {
                serveStaticFile(out, "404.html");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void serveStaticFile(OutputStream out, String fileName) {
        WebService service = WebService.getInstance();
        String filePath = staticFilesDir + "/" + fileName;
        System.out.println("Sirviendo archivo: " + filePath);
        try {
            String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1) : "html";

            if (extension.matches("png|jpg|jpeg|gif")) {
                byte[] resource = service.getBinaryResource(filePath);
                String header = service.getImageHeader(extension, "200 OK", resource.length);
                out.write(header.getBytes());
                out.write(resource);
            } else {
                String header = service.getHeader(extension, "200 OK");
                String resource = service.getResource(filePath);
                out.write((header + resource).getBytes());
            }
        } catch (RuntimeException | IOException e) {
            try {
                System.out.println("Archivo no encontrado: " + filePath + ". Sirviendo 404.html...");
                String header = service.getHeader("html", "404 Not Found");
                String resource = service.getResource(staticFilesDir + "/404.html");
                out.write((header + resource).getBytes());
            } catch (IOException ex) {
                System.err.println("Error al escribir la respuesta 404: " + ex.getMessage());
            }
        }
    }

}
