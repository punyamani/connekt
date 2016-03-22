package com.flipkart.connekt.busybees.tests.streams.topologies

import java.util.concurrent.atomic.AtomicLong

import akka.stream.scaladsl.{Flow, Sink, Source}
import com.flipkart.connekt.busybees.streams.flows.RenderFlow
import com.flipkart.connekt.busybees.streams.flows.dispatchers.HttpDispatcher
import com.flipkart.connekt.busybees.tests.streams.TopologyUTSpec
import com.flipkart.connekt.commons.entities.Channel
import com.flipkart.connekt.commons.factories.ServiceFactory
import com.flipkart.connekt.commons.iomodels.ConnektRequest
import com.flipkart.connekt.commons.metrics.Instrumented
import com.flipkart.connekt.commons.services.ConnektConfig
import com.flipkart.connekt.commons.utils.StringUtils._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * @author aman.shrivastava on 16/03/16.
 */
class StencilBenchmarkTest extends TopologyUTSpec with Instrumented {

  override def beforeAll() = {
    super.beforeAll()
    HttpDispatcher.init(ConnektConfig.getConfig("react").get)
  }

  val counter: AtomicLong = new AtomicLong(0)
  val counterTS: AtomicLong = new AtomicLong(System.currentTimeMillis)

  "StencilBenchmarkTest" should "log stencil rendering rates for a vanilla graph Velocity" in {

    val topic = ServiceFactory.getPNMessageService.getTopicNames(Channel.PUSH).get.head
    //    val kSource = new KafkaSource[ConnektRequest](getKafkaConsumerHelper, topic)(Promise[String]().future)
    val qps = meter("android.send")

    val repeatSource = Source.repeat {
      """
        |{
        |	"channel": "PN",
        |	"sla": "H",
        | "templateId": "cktSampleApp-stn0VEL",
        | "channelDataModel": {
        |     "message" : "hello",
        |     "id" : "abcd",
        |     "triggerSound" : false,
        |     "notificationType" : "text",
        |     "title" : "velocity stencil"
        | },
        |	"channelInfo" : {
        |	    "type" : "PN",
        |	    "ackRequired": true,
        |    	"delayWhileIdle": true,
        |     "platform" :  "android",
        |     "appName" : "ConnectSampleApp",
        |     "deviceId" : ["513803e45cf1b344ef494a04c9fb650a"]
        |	},
        |	"meta": {
        |   "x-perf-test" : "true"
        | }
        |}
      """.stripMargin.getObj[ConnektRequest]
    }

    val renderFlow = Flow.fromGraph(new RenderFlow).map(rT => {
      qps.mark()
      if(0 == (counter.incrementAndGet() % 1000)) {
        val now = System.currentTimeMillis
        val rate = 1000*1000/(now - counterTS.getAndSet(now))
        println(s"Processed ${counter.get()} messages by $now, RATE = $rate,  MR[${qps.getMeanRate}}], 1MR[${qps.getOneMinuteRate}}]")
      }
    })


    //Run the benchmark topology
    val rF = Source.fromGraph(repeatSource).via(renderFlow).runWith(Sink.ignore)

    Await.result(rF, 180.seconds)
  }

  "StencilBenchmarkTest" should "log stencil rendering rates for a vanilla graph Groovy" in {

    val topic = ServiceFactory.getPNMessageService.getTopicNames(Channel.PUSH).get.head
    //    val kSource = new KafkaSource[ConnektRequest](getKafkaConsumerHelper, topic)(Promise[String]().future)
    val qps = meter("android.send")

    val repeatSource = Source.repeat {
      """
        |{
        |	"channel": "PN",
        |	"sla": "H",
        | "templateId": "cktSampleApp-stn0x1",
        | "channelDataModel": {
        |     "message" : "hello",
        |     "id" : "abcd",
        |     "triggerSound" : false,
        |     "notificationType" : "text",
        |     "title" : "groovy stencil"
        | },
        |	"channelInfo" : {
        |	    "type" : "PN",
        |	    "ackRequired": true,
        |    	"delayWhileIdle": true,
        |     "platform" :  "android",
        |     "appName" : "ConnectSampleApp",
        |     "deviceId" : ["513803e45cf1b344ef494a04c9fb650a"]
        |	},
        |	"meta": {
        |   "x-perf-test" : "true"
        | }
        |}
      """.stripMargin.getObj[ConnektRequest]
    }

    val renderFlow = Flow.fromGraph(new RenderFlow).map(rT => {
      qps.mark()
      if(0 == (counter.incrementAndGet() % 1000)) {
        val now = System.currentTimeMillis
        val rate = 1000*1000/(now - counterTS.getAndSet(now))
        println(s"Processed ${counter.get()} messages by $now, RATE = $rate,  MR[${qps.getMeanRate}}], 1MR[${qps.getOneMinuteRate}}]")
      }
    })


    //Run the benchmark topology
    val rF = Source.fromGraph(repeatSource).via(renderFlow).runWith(Sink.ignore)

    Await.result(rF, 180.seconds)
  }


}
