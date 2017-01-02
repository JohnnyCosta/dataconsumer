package actors

import java.util.Properties
import javax.inject.{Inject, Singleton}

import akka.actor.Actor
import org.apache.kafka.clients.consumer.KafkaConsumer
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.collection.JavaConverters._
import scala.concurrent.Future

/**
  * Created by joao on 02/01/17.
  */
@Singleton
class ConsumerActor @Inject()(val configuration: play.api.Configuration, appLifecycle: ApplicationLifecycle) extends Actor {

  def receive = {
    case "consume" => consume()
    case "kill" => stopEvent()
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
    Logger.info("running consume actor")

    consumer.poll(100).iterator().asScala.foreach(record => {
      val offset = record.offset()
      val key = record.key()
      val value = record.value()

      Logger.info(s"offset = $offset, value = $value");

    })

    Logger.info("finished to consume")
  }

  def stopEvent() = {
    Logger.info("Stopping scheduler actor")
    consumer.close()
  }

  appLifecycle.addStopHook { () =>
    Future.successful(stopEvent())
  }

}
