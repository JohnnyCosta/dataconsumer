package processor

import javax.inject.Inject

import dao.LinesDao
import data.Line
import play.api.Logger
import processor.ProcessorUtil._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

/**
  * Created by joao on 04/01/17.
  */
class ProcessorLogic @Inject()(lineDao: LinesDao) {

  def process(message: String): Unit = {
    Logger.info(s"Starting to process '$message'")
    val file = Source.fromFile(message)
    readFromLines(file).foreach(l => {
      if (l.isDefined) {
        val line = l.get
        Logger.info(s"Processing id '${line.id}'")

        val dbLineFind = lineDao.findById(line.id)

        dbLineFind onSuccess { case dbLinePos =>
          if (dbLinePos.isEmpty) {
            lineDao.insert(line)
          } else {
            val dbLine = dbLinePos.get
            var insert = false
            val name = if (dbLine.name.isEmpty) {
              insert = true
              line.name
            } else dbLine.name
            val time = if (dbLine.time.isEmpty) {
              insert = true
              line.time
            } else dbLine.time

            if (insert)
              lineDao.insert(Line(dbLine.id, name, time))
          }
        }

        dbLineFind onFailure {
          case f => Logger.error(s"An error has occured: ${f.getMessage}")
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
