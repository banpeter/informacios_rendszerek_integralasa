# informacios_rendszerek_integralasa
# Color JMS Application

## Verziók
- WildFly 27
- Java 21
- Maven

## WildFly konfiguráció

### 1. Application user létrehozása

Windows:
```bat
WILDFLY_HOME\bin\add-user.bat
```
Linux:
```bash
WILDFLY_HOME/bin/add-user.sh
```

A kérdéseknél:
- **Management or Application User?** → `b` (Application User)
- **Username:** `quser`
- **Password:** `Password_1`
- **Groups:** `guest`

### 2. Queues hozzáadása a standalone-full.xml-hez

A `WILDFLY_HOME/standalone/configuration/standalone-full.xml` fájlban meg kell keresni a `<jms-destinations>` részt és hozzáadni:

```xml
<jms-queue name="colorQueue" entries="java:/queue/colorQueue java:jboss/exported/jms/queue/colorQueue"/>
<jms-queue name="colorStatistics" entries="java:/queue/colorStatistics java:jboss/exported/jms/queue/colorStatistics"/>
```

### 3. WildFly indítása

Windows:
```bat
WILDFLY_HOME\bin\standalone.bat -c standalone-full.xml
```
Linux:
```bash
WILDFLY_HOME/bin/standalone.sh -c standalone-full.xml
```

## Build

A gyökér mappában:
```bash
mvn clean package
```

## Deploy

Másold a `color-server` jar-t a WildFly deployments mappájába:

Windows:
```bat
copy color-server\target\color-server-1.0-SNAPSHOT.jar WILDFLY_HOME\standalone\deployments\
```
Linux:
```bash
cp color-server/target/color-server-1.0-SNAPSHOT.jar WILDFLY_HOME/standalone/deployments/
```




## Futtatás

### StatisticsClient (1. terminál)
```bash
java --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED -cp color-client/target/color-client-1.0-SNAPSHOT-jar-with-dependencies.jar org.example.StatisticsClient
```

### ColorSender (2. terminál)
```bash
java --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED -cp color-client/target/color-client-1.0-SNAPSHOT-jar-with-dependencies.jar org.example.ColorSender
```

## Várt kimenet

A `StatisticsClient` terminálon kb. 10 másodpercenként:
- **10 'GREEN' messages has been processed** 
- **10 'BLUE' messages has been processed**
- **10 'RED' messages has been processed**
