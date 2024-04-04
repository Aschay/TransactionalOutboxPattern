
curl -i -X POST localhost:8083/connectors -H 'Content-Type: application/json' -d @mysql-connector.json 

curl -i -X GET localhost:8083/connectors

curl -i -X DELETE localhost:8083/connectors/outbox-db-connector/

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


# for redpanda
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
    "schema.history.internal.kafka.bootstrap.servers": "redpanda:9092",  
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
docker exec -it redpanda rpk topic list
docker exec -it redpanda rpk topic consume dbserver.customerdb.customer