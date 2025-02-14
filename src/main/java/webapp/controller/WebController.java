package webapp.controller;


import webapp.WebServer;
import webapp.annotations.GetMapping;
import webapp.annotations.RequestParam;
import webapp.annotations.RestController;

import java.util.Date;

@RestController
public class WebController {
    @GetMapping("/greeting")
    public static String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }

    @GetMapping("/pi")
    public static String pi(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.valueOf(Math.PI);
    }

    @GetMapping("/time")
    public static String time(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.valueOf(new Date(System.currentTimeMillis()));
    }

    @GetMapping("/status")
    public static String status() {
        return "OK";
    }

    @GetMapping("/author")
    public static String author() {
        return "Santiago Guerra Penagos";
    }

    @GetMapping("/version")
    public String version() {
        return "JDK " + System.getProperty("java.version")
                + "\nServer version: 1.0.0" ;
    }
}
