# Transactional outbox pattern 
This pattern resolves the dual write operations issue that occurs in distributed systems when a single operation involves both a database write operation and a message or event notification.\
Usually it is used in microservices architecture in saga pattern  where a service that participates in a saga needs to update business entities and send messages/events **atomically** Similarly, a service that publishes a domain event must update an aggregate and publish an event  **atomically**  to avoid data inconsistency and bugs.\
2PC can acheive distributed transaction however its not an option where database and/or the message broker might not support it.
Also, it is not undesirable to couple the service to both the database and the message broker.
We can implement outbox either with cdc via debezium to catch the changes out our outbox tables on trailling logs ..(still not finished)
## 1.Transactional outbox pattern based on CDC with debezium ,kafka connect and springboot
Debezium simplify the usage of the outbox with its a ready-to-use SMT single message **transformations** which a feature by debezium to modify the data before getting to publish to kafka topic  with routing outbox events.there are different transformation depending on the usage.\
To implement the outbox pattern with debezium ,we need to configure a Debezium connector to:\
1.Capture changes in an outbox table (what database and outbox table to track , database configurations)\
2.Apply the Debezium outbox event router (SMT) configuration .\
The connector that is configured to apply the outbox SMT should **only** capture changes that occur in an outbox table like our demo.
```shell
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json"  localhost:8083/connectors/ -d '{
  "name": "customer-outbox-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "mysql",
    "database.port": "3306",
    "database.user": "root",
    "database.password": "root",
    "database.dbname": "customerdb",
    "database.server.id": "20175",
    "database.server.name" :"outboxserver",
    "topic.prefix": "app" ,  
    "table.include.list": "customerdb.outbox",
    "schema.history.internal.kafka.bootstrap.servers": "kafka:29092",  
    "schema.history.internal.kafka.topic": "schema-changes-history",
    "transforms": "outbox",
    "transforms.outbox.type": "io.debezium.transforms.outbox.EventRouter",
    "transforms.outbox.table.field.event.id ":"id",
    "transforms.outbox.table.field.event.key": "aggregateid",
    "transforms.outbox.table.field.event.payload": "payload",
    "transforms.outbox.route.by.field": "agregatetype",
    "transforms.outbox.table.fields.additional.placement":"type:envelope:type"
  }
}'
```
However in mysql(the database we use ) schema changes(dll statement) are logged as well and use internally by the connector in case of failure so we have to configure them as well.
```json
"schema.history.internal.kafka.bootstrap.servers": "kafka:29092",  
"schema.history.internal.kafka.topic": "schema-changes-history"
```
Dual writes here both publish events to kafka and saving customers to database .\
For example :
```java
@Transactional
public UUID processAdd(CustomerRequest request) throws  JsonProcessingException {
	UUID customer_id = UUID.randomUUID();
	Customer c = new Customer();
	c.setId(customer_id);
	c.setEmail(request.getEmail());
	c.setUsername(request.getUsername());
	customerRepo.save(c);
	String payload = objectMapper.writeValueAsString(c);
	Outbox outboxEvent = new Outbox();
	outboxEvent.setId(UUID.randomUUID());
	outboxEvent.setAggregateid(customer_id.toString());
	outboxEvent.setType("CustomerCreated");
	outboxEvent.setPayload(payload);
	outboxEvent.setAgregatetype(aggrOutbox);
	outboxRepo.save(outboxEvent);
	log.info("\nCustomer created with id " + c.getId() + " - \t\t and Outbox entity created with Id: {}", outboxEvent.getId());
	outboxRepo.delete(outboxEvent);
  return c.getId();
}
```
