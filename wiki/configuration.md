# Configuration

To be able to execute and use Eiffel Intelligence front-end, some configuration 
need to provided. All configurations is made via application.properties file. 
These properties can also be provided as Java properties arguments as well.

## Eiffel Intelligence Back-end

Eiffel Intelligence front-end is dependent of at least one Eiffel Intelligence 
back-end instance being up and running. It is possible to configure several back-end instances.

EI back-end instances is configured by setting **ei.backend.instances.list.json.content** property, example:

    ei.backend.instances.list.json.content=[{ "contextPath": "", "port": "8090", "name": "EI-Backend-1", "host": "ei-backend-host-1", "https": false, "defaultBackend": true},\
                                            { "contextPath": "", "port": "8090", "name": "EI-Backend-2", "host": "ei-backend-host-2", "https": false, "defaultBackend": false},\
                                            { "contextPath": "", "port": "8090", "name": "EI-Backend-3", "host": "ei-backend-host-3", "https": false, "defaultBackend": false},\
                                            { "contextPath": "", "port": "8090", "name": "EI-Backend-4", "host": "ei-backend-host-4", "https": false, "defaultBackend": false}]

You can set as many Eiffel Intelligence back-end instances as you intend to use.

## Eiffel Intelligence Front-end Server Settings

Which port Eiffel Intelligence front-end web-server should be started with is set by the property **server.port**:

    server.port=8080

**ei.frontend.service.host** property is the hostname on the host where Eiffel 
Intelligence front-end application is started on.

**ei.frontend.service.port** property is the port number on the host where Eiffel 
Intelligence front-end application is started on.

**ei.frontend.context.path** property is used when Eiffel Intelligence front-end 
application is started with a context path in web-server.

If running locally with "mvn spring-boot:run" no context-path is added which 
means you can leave the **ei.frontend.context.path** property empty.

    ei.frontend.service.host=localhost
    ei.frontend.service.port=${server.port}
    ei.frontend.context.path=

## HTTPS Security

If Eiffel Intelligence front-end needs to be executed with secure HTTPS connections, 
then these properties need to be set:

    ei.use.secure.http.frontend=false
    server.ssl.key-store: <keystore.p12>
    server.ssl.key-store-password: <mypassword>
    server.ssl.key-store-type: <PKCS12>
    server.ssl.key-alias: <tomcat>

Read more in Spring documentation about how to enable HTTPS security in Spring applications.

## Microsoft Entra Id Config

Set the below properties to enable Microsoft EntraId SSO and Multi-factor authentication.By default it is set to false. Make it true and provide all the necessary data to enable this functionality.

    spring.cloud.azure.active-directory.enabled=false
    spring.cloud.azure.active-directory.credential.client-id=<Your Client ID>    # The client ID of your application registered in Microsoft Entra ID.
    spring.cloud.azure.active-directory.credential.client-secret=<Your Azure App Secret>    # The client secret for your Azure application.
    spring.cloud.azure.active-directory.redirect-uri=<Redirect URI>    # The redirect URI configured in your Azure application.
    spring.cloud.azure.active-directory.profile.tenant-id=<Your Tenant ID>    # The tenant ID of your Microsoft Entra ID instance.
    spring.cloud.azure.active-directory.api-scope=<Application ID URI>    # The Application ID URI of your Microsoft Entra ID instance.

### Example values for Entra Id

    spring.cloud.azure.active-directory.enabled=true
    spring.cloud.azure.active-directory.credential.client-id=12345678-90ab-cdef-1234-567890abcdef
    spring.cloud.azure.active-directory.credential.client-secret=abcdef1234567890abcdef1234567890
    spring.cloud.azure.active-directory.redirect-uri=http://localhost:8080/login/oauth2/code/
    spring.cloud.azure.active-directory.profile.tenant-id=12345678-90ab-cdef-1234-567890abcdef
    spring.cloud.azure.active-directory.api-scope=api://12345678-90ab-cdef-1234-567890abcdef/.default

## Customize Documentation Links
It is possible to add and change Documentation url links that is seen in the Eiffel 
Intelligence front-end Web-UI. Documentation url links is configured by 
property **ei.eiffel.documentation.urls**, example:

    ei.eiffel.documentation.urls={ "EI front-end documentation": "https://eiffel-community.github.io/eiffel-intelligence-frontend",\
                               "EI front-end GitHub": "https://github.com/eiffel-community/eiffel-intelligence-frontend",\
                               "EI back-end documentation": "https://eiffel-community.github.io/eiffel-intelligence",\
                               "EI back-end GitHub": "https://github.com/eiffel-community/eiffel-intelligence",\
                               "Eiffel Github main page": "https://github.com/eiffel-community/eiffel",\
                               "User guide for test rules page": "https://github.com/eiffel-community/eiffel-intelligence-frontend/blob/master/wiki/test-rules.md" }

## Other Properties
All Eiffel Intelligence front-end properties can be found in [application.properties](../src/main/resources/application.properties) example file.
