package webapp.services;

import java.io.*;
import java.nio.file.*;

/**
 * Implementación de los servicios web.
 */
public class WebService implements RESTInterface {

    private static WebService instancia;

    private WebService() {}

    public static WebService getInstance() {
        if (instancia == null) {
            instancia = new WebService();
        }
        return instancia;
    }

    @Override
    public String getHeader(String type, String code) {
        return "HTTP/1.1 " + code + "\r\n"
                + "Content-Type: text/" + type + "\r\n"
                + "\r\n";
    }
    
    public String getImageHeader(String type, String code, int length) {
        return "HTTP/1.1 " + code + "\r\n"
                + "Content-Type: image/" + type + "\r\n"
                + "Content-Length: " + length + "\r\n"
                + "\r\n";
    }


    @Override
    public String getResource(String ruta) {
        byte[] resource;
        try {
            resource =  Files.readAllBytes(Paths.get(ruta));
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el recurso: " + ruta, e);
        }
        return new String(resource);
    }

    public byte[] getBinaryResource(String ruta) {
        try {
            return Files.readAllBytes(Paths.get(ruta));
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el recurso: " + ruta, e);
        }
    }
}

