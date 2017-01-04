package actors

import javax.inject.Inject

import akka.actor.Actor
import com.google.inject.assistedinject.Assisted
import processor.ProcessorLogic


/**
  * Created by joao on 02/01/17.
  */
object ProcessorActor {

  case object ProcessMessage

  trait Factory {
    def apply(message: String): Actor
  }

}

class ProcessorActor @Inject()(@Assisted message: String, processor: ProcessorLogic) extends Actor {

  import ProcessorActor._

  def receive = {
    case ProcessMessage =>
      sender() ! processor.process(message)
  }
}
