# Configuration

To be able to execute and use Eiffel-Intelligence front-end, some configuration need to provided.
All configurations is made via application.properties file. These properties can also be provided as Java properties arguments as well.

## Eiffel-Intelligence back-end

Eiffel-Intelligence front-end is dependent that at least one EI back-end instance is up and running.
It is possible to configure several EI Backend instances.

EI back-end instances is configured by setting **ei.backend.instances.list.json.content** property, example:

    ei.backend.instances.list.json.content=[{ "contextPath": "", "port": "8090", "name": "EI-Backend-1", "host": "ei-backend-host-1", "https": false, "defaultBackend": true},\
                                            { "contextPath": "", "port": "8090", "name": "EI-Backend-2", "host": "ei-backend-host-2", "https": false, "defaultBackend": false},\
                                            { "contextPath": "", "port": "8090", "name": "EI-Backend-3", "host": "ei-backend-host-3", "https": false, "defaultBackend": false},\
                                            { "contextPath": "", "port": "8090", "name": "EI-Backend-4", "host": "ei-backend-host-4", "https": false, "defaultBackend": false}]

You can set as many EI back-end instances as you intend to use.

## Eiffel-Intelligence front-end server settings

Which port EIffel-Intelligence front-end web-server should be started with is set by the property **server.port**:

    server.port=8080

**ei.frontend.service.host** property is the hostname on the host where EI front-end application is started on.
**ei.frontend.service.port** property is the port number on the host where EI front-end application is started on.
**ei.frontend.context.path** property is used when EI front-end application is started with a context path in web-server.
If running locally with "mvn spring-boot:run" no context-path is added which means you can leave the **ei.frontend.context.path** property empty.

    ei.frontend.service.host=localhost
    ei.frontend.service.port=${server.port}
    ei.frontend.context.path=

## HTTPS security

If Eiffel-Intelligence front-end need to be executed with secure HTTPS connections, then these properties need to be set:

    ei.use.secure.http.frontend=false
    server.ssl.key-store: <keystore.p12>
    server.ssl.key-store-password: <mypassword>
    server.ssl.key-store-type: <PKCS12>
    server.ssl.key-alias: <tomcat>

Read more in Spring documentation about how to enable HTTPS security in Spring applications.

## Customize documentation links

It is possible to add and change Documentation url links that is seen in the Eiffel-Intelligence front-end Web-UI.
Documentaiton url links is configured by property **ei.eiffel.documentation.urls**, example:

    ei.eiffel.documentation.urls={ "EI front-end documentation": "https://eiffel-community.github.io/eiffel-intelligence-frontend",\
                               "EI front-end GitHub": "https://github.com/eiffel-community/eiffel-intelligence-frontend",\
                               "EI back-end documentation": "https://eiffel-community.github.io/eiffel-intelligence",\
                               "EI back-end GitHub": "https://github.com/eiffel-community/eiffel-intelligence",\
                               "Eiffel Github main page": "https://github.com/eiffel-community/eiffel",\
                               "User guide for test rules page": "https://github.com/eiffel-community/eiffel-intelligence-frontend/blob/master/wiki/markdown/test-rules.md" }

## Other properties

All Eiffel-Intelligence front-end properties can be found in [application.properties](https://github.com/Ericsson/eiffel-intelligence-frontend/blob/master/src/main/resources/application.properties) example file.
