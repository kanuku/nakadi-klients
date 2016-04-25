# ScalaJS and Play base project with React and MDL

This branch using [ScalaJS - React](https://github.com/japgolly/scalajs-react) with [MDL](http://www.getmdl.io/) in the client side

This project using [Macwire](https://github.com/adamw/macwire) for dependency injection and ignore Play layout.

The frontend-backend communication is based is [autowire](https://github.com/lihaoyi/autowire) and [boopickle](https://github.com/ochrons/boopickle)

## The application structure

The application have three parts.

* shared
* server
* client

#### Shared

The shared directory contains the frontend and backend shared codes. We put here the communication objects and the API traits.

#### Server

This is the play server directory. The application override the default play application loader because it's using MacWire for dependency injection and [Play SIRD](https://www.playframework.com/documentation/2.4.x/ScalaSirdRouter) for the routing.

##### Depth

The application loading started in the `GlobalApplicationLoader` class. The `BuiltInComponentFromContextWithPlayWorkaround` class is a workaround to Play, because Play doesn't handle well the big file upload, when you not using guice for dependency injection.
The Routing is in the `ApplicationComponents` class, this file depend to the `Controller` and `Service` trait which contains the dependent classes.

The `ApiController` methods will handle the different API calls. Each api entry point must exists in the `ApplicationComponents` router (See sampleApi call)
