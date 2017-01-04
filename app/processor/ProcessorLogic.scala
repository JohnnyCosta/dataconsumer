package processor

import java.util.concurrent.atomic.AtomicInteger

import dao.LinesDao
import data.Line
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import play.api.Logger
import processor.ProcessorUtil._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

/**
  * Main processor logic
  */
class ProcessorLogic {

  val pattern = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss")

  def fixFine(line: Line): Line = {

    val time = if (!line.time.isEmpty) {
      val datetime = pattern.parseDateTime(line.time)
      val localdatetime = datetime.withZone(DateTimeZone.getDefault())
      datetime.withZone(DateTimeZone.UTC).toString
    } else line.time

    val name = if (!line.name.isEmpty) line.name.toLowerCase() else line.name

    Line(line.id, name, time)
  }

  def process(message: String, lineDao: LinesDao): Unit = {
    Logger.info(s"Starting to process '$message'")
    val file = Source.fromFile(message)
    @volatile var lineCounter = new AtomicInteger(0)
    readFromLines(file).foreach(l => {
      val lineNum = lineCounter.incrementAndGet()
      if (l.isDefined) {
        val line = fixFine(l.get)

        Logger.info(s"inserting line '$lineCounter' : '${line.id},${line.name},${line.time}', to database")
        lineDao.insert(line).value match {
          case None => {
            Logger.info(s"Success to insert id '${line.id}'")
          }
          case insertRes => {
            val res = insertRes.get
            if (res.isSuccess) {
              Logger.info(s"Error to insert id '${}'")
            } else {
              Logger.error(s"An error has occured: ${res.failed.get.getMessage}")
            }
          }
        }
      }
    })
    // Read lines from database
    val dbLines = lineDao.all()
    dbLines onSuccess { case lines =>
      Logger.info(s"database contains '${lines.length}' lines")

      lines foreach {
        case line => {
          Logger.info(s"line: '${line.id},${line.name},${line.time}'")
        }
      }

      Logger.info(s"Finished processing '$message'")
    }
    dbLines onFailure {
      case f => Logger.error(s"An error has occured: ${f.getMessage}")
    }
  }


}
