# web_service_design_style_demo

The demo project talks about the three primary API styles used by web services. The importance of selecting the right API style cannot be underestimated. The API styles could vary in interoperability, speed, flexibility, and the level of synchronization of the service. Also, it becomes very hard to change direction once a style is chosen.

1. RPC style

The demo uses JAX-WS (Java API for XML Web Services) based SOAP (Simple Object Access Protocol) protocol.

The blockchain clients would call remote methods running on the server, and the process is formatted by XML document. It provides a good platform and language independence!

2. Message style

The demo also uses JAX-WS based SOAP protocol.

The main difference between demo 1 and demo 2 is their ways of invoking remote server actions. In this demo, clients would only send a XML formatted message, a single message, to the server. 

The message style provides great flexibility, because you could easily add or modify a remote method API without messing up the client side code. You only invoke a remote API that takes the message. You specify the method to be invoked in the message!

3. Resource style

The demo uses a REST style. 

The project provides a demo of the same set of blockchain APIs based on the three styles. 
