### SQL
- ClientData: Username y Password de los clientes
- ClientRepository: A lo que le haces las peticiones:
```java
ClientRepository c = new ClientRepository();

// Registrar nuevo cliente
c.registerClient("test", "test");

// Todos los clientes del a db
List<ClientData> clients = c.getClients();
for (ClientData cd : clients) {
    System.out.println(cd);
}

// Lo de autenticar
System.out.println(c.verifyClient("test", "test")); // true
System.out.println(c.verifyClient("test", "asdlfasjf")); // false
System.out.println(c.verifyClient("as;fklasj", "asdlfasjf")); // false
```
