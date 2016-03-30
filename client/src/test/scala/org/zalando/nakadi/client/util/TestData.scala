package org.zalando.nakadi.client.util

object TestData {
  val events = """{
    "name": "article",
    "owning_application": "article-producer",
    "category": "data",
    "partition_resolution_strategy": null,
    "partitioning_key_fields": [],
    "schema": {
      "type": "json_schema",
      "schema": "{\"Article\": { \"properties\": { \"sku\": { \"type\": \"string\" } } }}"
    }
  },
  {
    "name": "myarticle",
    "owning_application": "article-producer",
    "category": "data",
    "partition_resolution_strategy": null,
    "partitioning_key_fields": [],
    "schema": {
      "type": "json_schema",
      "schema": "{\"Article\": { \"properties\": { \"sku\": { \"type\": \"string\" } } }}"
    }
  }"""
}