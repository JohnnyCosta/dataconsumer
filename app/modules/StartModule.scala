package modules

import com.google.inject.AbstractModule
import tasks.InitialTasks

/**
  * Module related to the start process
  */
class StartModule extends AbstractModule{
  def configure(): Unit = {
    bind(classOf[InitialTasks]).asEagerSingleton()
  }
}
