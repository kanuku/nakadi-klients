Nakadi Klients
==============

Implementation of a client accessing the low level API of the [Nakadi event bus](https://github.com/zalando/nakadi).

## Prerequisites
- Java >= 1.8
- Scala >=  2.11


## Tutorial

### Instantiate Client

`Scala`
```scala
val klient = KlientBuilder()
              .withEndpoint(new URI("localhost"))
              .withPort(8080)
              .withSecuredConnection(false)
              .withTokenProvider(() => "<my token>")
              .withScoop(Some(scoop))
              .withScoopTopic(Some("scoop"))
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

### Scoop integration
`Nakadi-Klients` has [Scoop](https://github.com/zalando/scoop) integrated to reduce the consumption of the same event 
by multiple instances of an application where each instance subscribes to `Nakadi` This feature is rather specific for 
[STUPS](https://github.com/zalando-stups) deployments. Please checkout the 
[Scoop documentation](https://github.com/zalando/scoop), if you want to use this feature.

`Scala`
```scala
val scoop = new Scoop().withBindHostName("hecate")
                       .withClusterPort(25551)
                       .withPort(25551)
                       .withAwsConfig()

val klient = KlientBuilder().withEndpoint(new URI("localhost"))
                            .withPort(8080)
                            .withSecuredConnection(false)
                            .withTokenProvider(() => "<my token>")
                            .withScoop(Some(scoop))
                            .withScoopTopic(Some("scoop"))
                            .build()
```

`Java`
```java
final Scoop scoop = new Scoop().withBindHostName("hecate")
                               .withClusterPort(25551)
                               .withPort(25551)
                               .withAwsConfig();

final Client client = new KlientBuilder()
                .withEndpoint(new URI("localhost"))
                .withPort(8080)
                .withSecuredConnection(false)
                .withJavaTokenProvider(() -> "<my token>")
                .withScoop(new Some<>(scoop))
                .withScoopTopic(new Some<>("scoop"))
                .buildJavaClient();
```


## See
- [Nakadi event bus](https://github.com/zalando/nakadi)
- [STUPS](https://github.com/zalando-stups)
- [STUPS' tokens library](https://github.com/zalando-stups/tokens)
- [Scoop](https://github.com/zalando/scoop)

## TODO
- [ ] handle case where separate clusters consisting of 1 member are built
- [ ] automated tests for Scoop integration

## License
http://opensource.org/licenses/MIT