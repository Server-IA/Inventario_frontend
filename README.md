# coagronet-backend
mvn clean spring-boot:run       # perfil local por defecto
mvn -Pdeploy clean package      # para generar el WAR
mvn spring-javaformat:apply     # formatear fuentes sin compilar
mvn clean verify                # formatear, compilar, testear y empaquetar
mvn -q compile                  # sólo compilar (rápido)