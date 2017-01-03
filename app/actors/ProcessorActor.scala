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
    Logger.info(s"Starting to process '$message'")
    val file = Source.fromFile(message)

    val grouped = readFromLines(file).toStream
      .withFilter(file => file.isDefined)
      .map(line => line.get)
      .groupBy(_.id)

    val reduced = grouped map {
      case (key, list) => {
        val red = list.reduce((a, b) => {
          val name = if (b.name == null || b.name.isEmpty) {
            a.name
          } else b.name
          val time = if (b.time == null || b.time.isEmpty) {
            a.time
          } else b.time
          Line(a.id, name, time)
        })
        (key, red)
      }
    }

    reduced foreach {
      case (key, line) => {
        Logger.info(s"key '$key' line: '${line.id}:${line.name}:${line.time}'")
      }
    }

    Logger.info(s"Finished processing '$message'")
  }

  def readFromLines(input: Source, skipHeader: Boolean = true) = {
    val lines = if (skipHeader == true) input.getLines().drop(1) else input.getLines()
    lines map (ln => {
      if (ln == ",,,,,") {
        // Ignore
        None
      } else if (ln == "Id,name,time_of_start,Obs.,,") {
        // Ignore
        None
      } else {
        val Array(id, name, time, _, _, _) = ln.split(",", -1)
        if (id == null || id.isEmpty) {
          // Ignore
          None
        } else {
          Option(Line(id, name, time))
        }

      }
    })
  }

}
