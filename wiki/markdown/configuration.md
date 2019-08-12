# Configuration

To be able to execute and use Eiffel-Intelligence Front-end, some configuration need to provided.
All configurations in made via application.properties file. These properties can also be provided as Java properties arguments as well.

## Eiffel-Intelligence Backend

Eiffel-Intelligence Frontend is dependent that at least one EI Backend instance is up and running.
It is possible to configure several EI Backend instances.

EI Backebnd instances is configured by setting "ei.backend.instances.list.json.content" property, example:

    ei.backend.instances.list.json.content=[{ "contextPath": "", "port": "8090", "name": "EI-Backend-1", "host": "ei-backend-host-1", "https": false, "defaultBackend":                                             true},\
                                            { "contextPath": "", "port": "8090", "name": "EI-Backend-2", "host": "ei-backend-host-2", "https": false, "defaultBackend": false},\
                                            { "contextPath": "", "port": "8090", "name": "EI-Backend-3", "host": "ei-backend-host-3", "https": false, "defaultBackend": false},\
                                            { "contextPath": "", "port": "8090", "name": "EI-Backend-4", "host": "ei-backend-host-4", "https": false, "defaultBackend": false}]

You can set as many EI Backend instances as you intend to use.

## Eiffel-Intelligence Frontend server settings

Which port EIffel-Intelligence Front-end web-server should be started with is set by property "server.port":

    server.port=8080

"ei.frontend.service.host" property is the hostname on the host where EI Frontend application is started on.
"ei.frontend.service.port" property is the port number on host where EI Frontend application is started on.
"ei.frontend.context.path" property is used when EI Frontend applications is started with any context path in web-server.
If running locally with "mvn spring-boot:run" no context-path is added which means you can leave the ei.frontend.context.path property empty.

    ei.frontend.service.host=localhost
    ei.frontend.service.port=${server.port}
    ei.frontend.context.path=

## HTTPS security

If Eiffel-Intelligence Frontend need to be execute with secure HTTPS connections, then these properties need to be set:

    ei.use.secure.http.frontend=false
    server.ssl.key-store: <keystore.p12>
    server.ssl.key-store-password: <mypassword>
    server.ssl.key-store-type: <PKCS12>
    server.ssl.key-alias: <tomcat>

Read more in Spring documentations about how to enable HTTPS security in Spring applications.

## Customize documenations links

It is possible to add and change Documentation url links that is seen in the Eiffel-Intelligence Front-end Web-UI.
Documentaiton url links is configured by property "ei.eiffel.documentation.urls", example:

    ei.eiffel.documentation.urls={ "EI Frontend Documentation": "https://eiffel-community.github.io/eiffel-intelligence-frontend",\
                               "EI Frontend GitHub": "https://github.com/eiffel-community/eiffel-intelligence-frontend",\
                               "EI Backend Documentation": "https://eiffel-community.github.io/eiffel-intelligence",\
                               "EI Backend GitHub": "https://github.com/eiffel-community/eiffel-intelligence",\
                               "Eiffel Github main page": "https://github.com/eiffel-community/eiffel",\
                               "Test Rules User Guide": "https://github.com/eiffel-community/eiffel-intelligence-frontend/blob/master/wiki/markdown/test-rules.md" }

## Other properties

All Eiffel-Intelligence Front-end properties can be found in [application.properties](https://github.com/Ericsson/eiffel-intelligence-frontend/blob/master/src/main/resources/application.properties) example file.
