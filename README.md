# **Web Framework for REST Services and Static File Management**  

## **📌 Project Overview**  
This project extends a basic HTTP server into a **web framework** that supports **REST services** and **static file handling**. The framework enables developers to:  

- Define REST endpoints using **lambda functions**.  
- Extract query parameters from requests.  
- Serve static files (HTML, CSS, JavaScript, images).  

The framework is designed to be **lightweight**, **efficient**, and **easy to use** for building web applications.  

---

## **📂 Project Structure**  

```
/Lab02-Final
│── /src
│    ├── /main
│    │    ├── /java
│    │    │    ├── webapp
│    │    │    │    ├── /services
│    │    │    │    │    ├── Request.java       <-- Handles HTTP Requests
│    │    │    │    │    ├── Response.java      <-- Handles HTTP Responses
│    │    │    │    │    ├── RESTInterface.java <-- Defines REST API Methods
│    │    │    │    │    ├── WebService.java    <-- Manages Web Services
│    │    │    │    ├── App.java                <-- Example Application
│    │    │    │    ├── WebServer.java          <-- Core Web Server
│    │    ├── /resources
│    │    │    ├── 404.html                     <-- Custom 404 Page
│    │    │    ├── 404.css
│    │    │    ├── 404.png
│    │    │    ├── home.html                    <-- Home Page
│    │    │    ├── home.js
│    ├── /test
```

---

## **🛠️ Architecture**  

The framework is built using **Java** and follows a **request-response model**, processing both REST requests and static file requests.  

### **1️⃣ Core Components**
- `WebServer.java` → Listens on **port 35000** and routes requests.  
- `Request.java` → Parses **HTTP request details** (headers, query parameters).  
- `Response.java` → Builds **HTTP responses** (headers, content).  
- `WebService.java` → Manages REST **service mappings**.  
- `RESTInterface.java` → Defines an interface for REST methods.  

### **2️⃣ REST Services (`get()`)**
Developers can define REST endpoints using **lambda functions**:  
```java
get("/hello", (req, res) -> "Hello " + req.getValues("name"));
```
The framework maps URLs to handlers that return **dynamic responses**.  

### **3️⃣ Static File Serving (`staticfiles()`)**
Static resources (HTML, CSS, JS, images) are **automatically served** from a defined directory:  
```java
staticfiles("src/main/resources");
```

---

## **🚀 How to Run the Project**  

### **🔹 1️⃣ Clone the Repository**  
```sh
git clone https://github.com/Sguerra1702/AREP-Lab02.git
cd AREP-Lab02
```

### **🔹 2️⃣ Build the Project**  
```sh
mvn clean package
```

### **🔹 3️⃣ Run the Web Server**  
```sh
java -cp target/Lab02-Final.jar webapp.App
```
The webserver can also be run by running the App.java file located in /src/main/java/webapp with your preferred IDE.

### **🔹 4️⃣ Access the Application**  

The webserver ca be accessed typing [http://localhost:3500](), this will redirect the user to the html file home.html located in /src/main/resources.

| Feature                   | URL                                                                                   |
|---------------------------|---------------------------------------------------------------------------------------|
| 🏠 **Home Page**          | [http://localhost:35000](http://localhost:35000/home.html)                            |
| 🔹 **REST: Hello Name**   | [http://localhost:35000/hello?name=Pedro](http://localhost:35000/hello?name=Pedro)    |
| 🔹 **REST: PI**           | [http://localhost:35000/PI](http://localhost:35000/hello?name=Pedro)                  |
| 🔹 **REST: static files** | [http://localhost:35000/file_name.extension](http://localhost:35000/hello?name=Pedro) |
---

## **📌 Example Application (`App.java`)**  

Developers can define REST endpoints and static files easily:  

```java
package webapp;

public class App {
    public static void main(String[] args) {
        WebServer.staticfiles("src/main/resources");
        WebServer.get("/hello", (req, res) -> "Hello " + req.getValues("name"));
        
        try {
            WebServer.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

---

## **✅ Testing the Web Framework**  

### **📝 Manual Testing**
**1️⃣ Static Files:**  
- Open [http://localhost:35000](http://localhost:35000/home.html). 
- Type the name of the file you want to access, for example [http://localhost:35000/404.png]().  

**2️⃣ REST API:**  
- Open [http://localhost:35000/hello?name=Pedro](http://localhost:35000/hello?name=Pedro) → Should return `"Hello Pedro"`.  



## **👨‍💻 Author**
Developed by **Santiago Guerra Penagos**.  

🔹 GitHub: [Sguerra1702](https://github.com/Sguerra1702)  
🔹 Email: santiago.guerra@mail.escuelaing.edu.co