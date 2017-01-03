package modules

import actors.{ConsumerActor, ProcessorActor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import schedulers.MainScheduler

/**
  * Created by joao on 02/01/17.
  */
class JobModule extends AbstractModule with AkkaGuiceSupport {
  def configure() = {

    bindActor[ConsumerActor]("consumer-actor")

    bindActorFactory[ProcessorActor, ProcessorActor.Factory]

    bind(classOf[MainScheduler]).asEagerSingleton()
  }
}
