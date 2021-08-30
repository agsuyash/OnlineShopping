# OnlineStore

## Requiremenent
   - Product Service:
    - Add Product
    - Retrieve a Product with Product Id.
    - Retrieve all Products
    - Delete Product
   - Order Service:
    - Add Order Item
    - Retrieve one Order with Order Id
    - Retrieve all Orders
 	- Delete Order Item
	
	
## Softwares Pre-requisite
  - Java 1.8
  - Gradle 7.1.1 
  - Vertx 4.1.2 https://mvnrepository.com/artifact/io.vertx/vertx-web
  - junit 5.7.1
  - Eclipse Neon(4.6.3+) - Optional. You can run projects using Gradle
  
  ## How to Run the project
  ### Run Product Service
    Import 'ProductService' as Gradle project and Run "gradlew build".
	Or Run "gradlew build shadowJar" , then go in the directory \ProductService\lib\build\libs and run "java -jar lib-all.jar" ( this will start main class com.onlinestore.products.ProductServiceApplication on port 3001).
	You can then run curl commands "add products.cmd", "add discount.cmd", "delete discount.cmd". The values can be checked "http://localhost:3001/api/products","http://localhost:3001/api/discount/1", /api/discount/add, /api/discount/:id
  ### Run Order Service
    Import 'OrderService' as Gradle project and Run "gradlew build".
	Or Run "gradlew build shadowJar" , then go in the directory \OrderService\lib\build\libs and run "java -jar lib-all.jar" ( this will start main class com.onlinestore.products.OrderServiceApplication on port 3002).
	You can then run curl commands "add cart items.cmd". To check total price of the cart : http://localhost:3002/api/total, /api/order/add, /api/order/:id, /api/order
