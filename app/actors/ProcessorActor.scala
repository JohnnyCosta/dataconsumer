package actors

import javax.inject.Inject

import akka.actor.Actor
import com.google.inject.assistedinject.Assisted
import dao.LinesDao
import processor.ProcessorLogic


/**
  * Action to process the message
  */
object ProcessorActor {

  case object ProcessMessage

  trait Factory {
    def apply(message: String): Actor
  }

}

class ProcessorActor @Inject()(@Assisted message: String, processor: ProcessorLogic, lineDao: LinesDao) extends Actor {

  import ProcessorActor._

  def receive = {
    case ProcessMessage =>
      sender() ! processor.process(message, lineDao)
  }
}
