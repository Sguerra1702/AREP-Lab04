package webapp.services;

/**
 * Interfaz para definir los métodos de un servicio web.
 */
public interface RESTInterface {

    /**
     * Generar el encabezado HTTP de la respuesta
     * @param type Tipo de contenido (ej. html, json)
     * @param code Código de respuesta HTTP
     * @return Encabezado HTTP
     */
    String getHeader(String type, String code);

    /**
     * Obtener el contenido del recurso solicitado
     * @param ruta Ubicación del recurso
     * @return Contenido como arreglo de bytes
     */
    String getResource(String ruta);
    
    /**
     * Generar el encabezado HTTP de la respuesta para imágenes
     * @param type Tipo de contenido (ej. html, json)
     * @param code Código de respuesta HTTP
     * @return Encabezado HTTP
     */
    String getImageHeader(String type, String code, int length);

    byte[] getBinaryResource(String ruta);
}

