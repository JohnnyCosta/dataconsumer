package modules

import actors.{ConsumerActor, ProcessorActor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import schedulers.MainScheduler

/**
  * Configure all akka actors related to the the scheduler
  */
class JobModule extends AbstractModule with AkkaGuiceSupport {
  def configure() = {

    bindActor[ConsumerActor]("consumer-actor")

    bindActorFactory[ProcessorActor, ProcessorActor.Factory]

    bind(classOf[MainScheduler]).asEagerSingleton()
  }
}
