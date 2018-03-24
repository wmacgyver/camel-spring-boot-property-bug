#UPDATE

turns out it's because when you extend CamelTestSupport, it setup its own container, which conflict with the Soring one.

the solution is

1. do NOT extend extends CamelTestSupport or any base class
2. don't declare the route in the Test, test the route in the production route builder instead

---

Running ExampleRouteTest method testWithContent

    @Value("${fromUser}")
    private String fromUser;

    @Value("${toUser}")
    private String toUser;
    
works correctly. 

Both have values from applciation.properties


                    .to("smtp://localhost?from={{fromUser}}&to={{toUser}}&subject=Test+Done");
                    
does not. 
                
                
You get

    org.apache.camel.FailedToCreateRouteException: Failed to create route route1 at: >>> To[smtp://localhost?from={{fromUser}}&to={{toUser}}&subject=Test+Done] <<< in route: Route(route1)[[From[direct:startRoute]] -> [SetBody[simple{S... because of Property with key [fromUser] not found in properties from text: smtp://localhost?from={{fromUser}}&to={{toUser}}&subject=Test+Done

    ...
    Caused by: java.lang.IllegalArgumentException: Property with key [fromUser] not found in properties from text: smtp://localhost?from={{fromUser}}&to={{toUser}}&subject=Test+Done
    	at org.apache.camel.component.properties.DefaultPropertiesParser$ParsingContext.getPropertyValue(DefaultPropertiesParser.java:270)
    	at org.apache.camel.component.properties.DefaultPropertiesParser$ParsingContext.readProperty(DefaultPropertiesParser.java:156)
    	at org.apache.camel.component.properties.DefaultPropertiesParser$ParsingContext.doParse(DefaultPropertiesParser.java:115)
    	at org.apache.camel.component.properties.DefaultPropertiesParser$ParsingContext.parse(DefaultPropertiesParser.java:99)
    	at org.apache.camel.component.properties.DefaultPropertiesParser.parseUri(DefaultPropertiesParser.java:62)
    	at org.apache.camel.component.properties.PropertiesComponent.parseUri(PropertiesComponent.java:235)
    	at org.apache.camel.component.properties.PropertiesComponent.parseUri(PropertiesComponent.java:178)
    	at org.apache.camel.impl.DefaultCamelContext.resolvePropertyPlaceholders(DefaultCamelContext.java:2550)
    	at org.apache.camel.model.ProcessorDefinitionHelper.resolvePropertyPlaceholders(ProcessorDefinitionHelper.java:735)
    	at org.apache.camel.model.ProcessorDefinition.makeProcessorImpl(ProcessorDefinition.java:537)
    	at org.apache.camel.model.ProcessorDefinition.makeProcessor(ProcessorDefinition.java:523)
    	at org.apache.camel.model.ProcessorDefinition.addRoutes(ProcessorDefinition.java:239)
    	at org.apache.camel.model.RouteDefinition.addRoutes(RouteDefinition.java:1300)
