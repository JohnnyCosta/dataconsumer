package schedulers

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.Inject
import com.google.inject.name.Named

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by joao on 02/01/17.
  */
class MainScheduler @Inject()(val system: ActorSystem, conf: play.api.Configuration,
                              @Named("consumer-actor") val consumerActor: ActorRef)(implicit ec: ExecutionContext) {
  val seconds = conf.getInt("scheduler.seconds").getOrElse(10)
  system.scheduler.schedule(
    0.microseconds, seconds.seconds, consumerActor, "consume")
}
