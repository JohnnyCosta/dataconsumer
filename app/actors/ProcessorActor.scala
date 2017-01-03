package actors

import javax.inject.Inject

import akka.actor.Actor
import com.google.inject.assistedinject.Assisted
import data.Line
import play.api.Logger

import scala.io.Source

/**
  * Created by joao on 02/01/17.
  */
object ProcessorActor {

  case object ProcessMessage

  trait Factory {
    def apply(message: String): Actor
  }

}

class ProcessorActor @Inject()(@Assisted message: String) extends Actor {

  import ProcessorActor._

  def receive = {
    case ProcessMessage =>
      sender() ! process(message)
  }

  def process(message: String): Unit = {
    Logger.info(s"Processing '$message'")

    val file = Source.fromFile(message)
    readFromLines(file).foreach(l => {
      Logger.info(s"${l.id}")
    })

    Logger.info(s"Finished processing '$message'")
  }

  def readFromLines(input: Source, skipHeader: Boolean = true) = {
    val lines = if (skipHeader == true) input.getLines().drop(1) else input.getLines()
    lines map (ln => {
      val Array(id, name, time, _, _, _) = ln.split(",", -1)
      Line(id, name, time)
    })
  }

}
