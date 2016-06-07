
## VERY IMPORTANT NOTE!
There are 2 versions of Nakadi-klients:
* `1.**` - Is based on **an old version of nakadi API** which never becase production ready. **This version is discontinued and not supported anymore!!**
* `>= 2.0.0` - Based on the new Nakadi API, which **is still under development** and not production ready. 

Nakadi Klients
==============

Implementation of a non blocking client accessing the low level API of the [Nakadi event bus](https://github.com/zalando/nakadi). Internally, it uses [Akka](http://akka.io/) and [Akka Http](http://doc.akka.io/docs/akka-stream-and-http-experimental/2.0.2/scala/http/) to implement its communication tasks.

Please note that the client provides a Scala as well as a Java interface.

## Prerequisites
- Java >= 1.8
- Scala >=  2.11



## Tutorial

### Configuration

```
nakadi.client {
  noListenerReconnectDelay = 10 seconds   // if no listener could be found, no connection to Nakadi is established.
                                          // noListenerReconnectDelay specifies the delay  after which the actor
                                          // should try again to connect
  pollParallelism = 100                   // number of parallel polls from a specific host
  receiveBufferSize = 1024 bytes          // initial buffer size for event retrieval

  defaultBatchFlushTimeout = 5 seconds   // default batch flush timeout set in ListenParameters
  defaultBatchLimit = 1                  // default batch limit set in ListenParameters
  defaultStreamLimit = 0                 // default stream limit set in ListenParameters

  supervisor {
    // note: Supervisor strategy parameter names are from the Akka - keep them like this
    maxNrOfRetries = 100
    withinTimeRange = 5 minutes

    resolveActorTimeout = 1 second // timeout for resolving PartitionReceiver actor reference
  }
}
```


### Instantiate Client

`Scala`
```scala
val klient = KlientBuilder()
              .withEndpoint(new URI("localhost"))
              .withPort(8080)
              .withSecuredConnection(false)
              .withTokenProvider(() => "<my token>")
              .build()
```

`Java`
```Java
final Client client = new KlientBuilder()
                .withEndpoint(new URI("localhost"))
                .withPort(8080)
                .withSecuredConnection(false)
                .withJavaTokenProvider(() -> "<my token>")
                .buildJavaClient();
```

### Get Monitoring Metrics
NOTE: metrics format is not defined / fixed

`Scala`
```scala
klient.getMetrics map ( _ match {
    case Left(error)                      => fail(s"could not retrieve metrics: $error")
    case Right(metrics: Map[String, Any]) => logger.debug(s"metrics => $metrics") 
})
```

`Java`
```java
Future<Map<String, Object>> metrics = client.getMetrics();
```

### List All Known Topics

`Scala`
```scala
klient.getTopics map ( _ match {
    case Left(error)                => fail(s"could not retrieve topics: $error")
    case Right(topics: List[Topic]) => logger.debug(s"topics => $topics") 
})
```

`Java`
```java
Fuure<List<Topic>> topics = klient.getTopics();
```

### Post a Single Event to the Given Topic
Partition selection is done using the defined partition resolution. The partition resolution strategy is defined per 
topic and is managed by the event bus (currently resolved from a hash over `ordering_key`).

`Scala`
```scala
val event = Event("eventType",
                  "orderingKey",
                  Map("id"       -> "1234567890"),
                  Map("greeting" -> "hello",
                      "target"   -> "world"))
                       
klient.postEvent(topic, event) map (_ match {
        case Left(error) => fail(s"an error occurred while posting event to topic $topic")
        case Right(_)    => logger.debug("event post request was successful")
})
```

`Java`
```java
HashMap<String,Object> meta = Maps.newHashMap();
meta.put("id", "1234567890");

HashMap<String, String> body = Maps.newHashMap();
body.put("greeting", "hello");
body.put("target", "world");
 
Future<Void> f = client.postEvent("test", new Event("eventType", "orderingKey", meta, body);
```


### Get Partition Information of a Given Topic

`Scala`
```scala
klient.getPartitions("topic") map (_ match {
    case Left(error: String)                     => fail(s"could not retrieve partitions: $error")
    case Right(partitions: List[TopicPartition]) => partitions
})
```

`Java`
```java
Future<List<TopicPartition>> topics = klient.getPartitions("topic");
```

### Subscribe to a given topic
Non-blocking subscription to a topic requires a `Listener` implementation. The event listener does not have to be 
thread-safe because each listener is handled by its own `Akka` actor

`Scala`
```scala
klient.subscribeToTopic("topic", ListenParameters(), listener, autoReconnect = true) // autoReconnect default is true
```

`Java`
```java
public final class MyListener implements JListener {...}

client.subscribeToTopic("topic", 
                        ListenParametersUtils.defaultInstance(), 
                        new JListenerWrapper(new MyListener()), 
                        true);

```

### Subscribe to a Specific Partition
Non blocking subscription to events of specified topic and partition.

`Scala`
```scala
klient.listenForEvents("topic",
                       "partitionId",
                       ListenParameters(),
                       listener, 
                       autoReconnect = false) // default is false
```

`Java`
```java
public final class MyListener implements JListener {...}

client.listenForEvent("topic", 
                      "partitionId", 
                      ListenParametersUtils.defaultInstance(), 
                      new JListenerWrapper(new MyListener()));
```


## See
- [Nakadi event bus](https://github.com/zalando/nakadi)
- [STUPS](https://github.com/zalando-stups)
- [STUPS' tokens library](https://github.com/zalando-stups/tokens)

## TODO
- [ ] handle case where separate clusters consisting of 1 member are built

## License
http://opensource.org/licenses/MIT
