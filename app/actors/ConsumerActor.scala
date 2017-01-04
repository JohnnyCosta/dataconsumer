package actors

import java.util.Properties
import javax.inject.{Inject, Singleton}

import actors.ProcessorActor.ProcessMessage
import akka.actor.{Actor, ActorRef}
import org.apache.kafka.clients.consumer.KafkaConsumer
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import play.api.libs.concurrent.InjectedActorSupport

import scala.collection.JavaConverters._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.duration._
import akka.pattern.ask

/**
  * Created by joao on 02/01/17.
  */
@Singleton
class ConsumerActor @Inject()(val configuration: play.api.Configuration,
                              appLifecycle: ApplicationLifecycle, processorFactory: ProcessorActor.Factory)
  extends Actor with InjectedActorSupport {

  def receive = {
    case "consume" => consume()
  }

  val group = "datagroup"
  val topic = "datatopic"
  val topics = Array(topic).toList.asJava

  val props = new Properties()
  props.put("bootstrap.servers", configuration.getString("kafka.servers").getOrElse("localhost:9092"))
  props.put("group.id", group)
  props.put("enable.auto.commit", "true")
  props.put("auto.commit.interval.ms", "1000")
  props.put("session.timeout.ms", "30000")
  props.put("key.deserializer",
    "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer",
    "org.apache.kafka.common.serialization.StringDeserializer")

  val consumer = new KafkaConsumer[String, String](props);
  consumer.subscribe(topics)
  Logger(s"Subscribed to topic '$topic'")

  def consume(): Unit = {
    Logger.info("running consumer actor")

    consumer.poll(100).iterator().asScala.foreach(record => {
      val offset = record.offset()
      val key = record.key()
      val value = record.value()

      Logger.info(s"offset = '$offset', value = '$value'");

      processValue(value)

    })

    Logger.info("finished to consume")
  }

  def processValue(value: String): Unit = {
    val actorName = value map { case '\\' => '_'  case '/' => '_'  case c => c }
    Logger.info(s"Sending to process '$value' with actor name '$actorName'")
    val processor: ActorRef = injectedChild(processorFactory(value), actorName)
    (processor ! ProcessMessage)
  }

  private def stopEvent() = {
    Logger.info("Stopping scheduler actor")
    consumer.close()
  }

  appLifecycle.addStopHook { () =>
    Future.successful(stopEvent())
  }

}
