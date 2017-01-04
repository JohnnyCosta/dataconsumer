package modules

import com.google.inject.AbstractModule
import tasks.InitialTasks

/**
  * Created by joao on 04/01/17.
  */
class StartModule extends AbstractModule{
  def configure(): Unit = {
    bind(classOf[InitialTasks]).asEagerSingleton()
  }
}
