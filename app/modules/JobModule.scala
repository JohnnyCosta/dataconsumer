package modules

import actors.ConsumerActor
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import schedulers.Scheduler

/**
  * Created by joao on 02/01/17.
  */
class JobModule extends AbstractModule with AkkaGuiceSupport {
  def configure() = {
    bindActor[ConsumerActor]("scheduler-actor")
    bind(classOf[Scheduler]).asEagerSingleton()
  }
}
