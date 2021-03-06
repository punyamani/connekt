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
package com.flipkart.connekt.commons.dao

import com.flipkart.connekt.commons.factories.THTableFactory
import com.flipkart.connekt.commons.iomodels.{CallbackEvent, ChannelRequestInfo}

class EmailCallbackDao(tableName: String, hTableFactory: THTableFactory) extends CallbackDao(tableName: String, hTableFactory: THTableFactory) {
  override def channelEventPropsMap(channelCallbackEvent: CallbackEvent): Map[String, Array[Byte]] = ???

  override def mapToChannelEvent(channelEventPropsMap: Map[String, Array[Byte]]): CallbackEvent = ???

  override def fetchEventMapFromList(event: List[CallbackEvent]): Map[String, List[CallbackEvent]] = ???

  override def fetchCallbackEvents(requestId: String, event: ChannelRequestInfo, fetchRange: Option[(Long, Long)]): Map[String, List[CallbackEvent]] = ???
}
