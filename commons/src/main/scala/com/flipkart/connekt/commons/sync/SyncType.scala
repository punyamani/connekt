/*
 *         -╥⌐⌐⌐⌐            -⌐⌐⌐⌐-
 *      ≡╢░░░░⌐\░░░φ     ╓╝░░░░⌐░░░░╪╕
 *     ╣╬░░`    `░░░╢┘ φ▒╣╬╝╜     ░░╢╣Q
 *    ║╣╬░⌐        ` ╤▒▒▒Å`        ║╢╬╣
 *    ╚╣╬░⌐        ╔▒▒▒▒`«╕        ╢╢╣▒
 *     ╫╬░░╖    .░ ╙╨╨  ╣╣╬░φ    ╓φ░╢╢Å
 *      ╙╢░░░░⌐"░░░╜     ╙Å░░░░⌐░░░░╝`
 *        ``˚¬ ⌐              ˚˚⌐´
 *
 *      Copyright © 2016 Flipkart.com
 */
package com.flipkart.connekt.commons.sync

import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer, JsonSerializer, SerializerProvider}

object SyncType extends Enumeration {
   type SyncType = Value
   val CLIENT_ADD, CLIENT_QUEUE_CREATE,  TEMPLATE_CHANGE, AUTH_CHANGE, STENCIL_CHANGE, STENCIL_BUCKET_CHANGE = Value
}


class SyncTypeToStringSerializer extends JsonSerializer[SyncType.Value] {
  override def serialize(t: SyncType.Value, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) = {
    jsonGenerator.writeObject(t.toString)
  }
}

class SyncTypeToStringDeserializer extends JsonDeserializer[SyncType.Value] {
  @Override
  override def deserialize(parser: JsonParser, context: DeserializationContext): SyncType.Value = {
    try {
      SyncType.withName(parser.getValueAsString.toUpperCase)
    } catch {
      case e: NoSuchElementException =>
        null
    }
  }
}
