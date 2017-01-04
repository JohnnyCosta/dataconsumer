package processor

import data.Line

import scala.io.Source

/**
  * Processing util
  */
object ProcessorUtil {

  def readFromLines(input: Source, skipHeader: Boolean = true): Iterator[Option[Line]] = {
    val lines = if (skipHeader) input.getLines().drop(1) else input.getLines()
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
