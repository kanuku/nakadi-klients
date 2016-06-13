package org.zalando.nakadi.client.scala.model

import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext

class EmptyListToOptional  extends JsonDeserializer[Option[Seq[AnyRef]]] {

  def deserialize(p: JsonParser, context: DeserializationContext) = {
    
    p.getValueAsString match {
      case v => 
        println("#######")
        println("#######")
        println("#######")
        println(s"Value $v")
        println("#######")
        println("#######")
        println("#######")
        None
    }
    
  }
}