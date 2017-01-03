package data

/**
  * Simple line values holder
  */
class Line(val id: String, val name: String, val time: String)

object Line {
  def apply(id: String, name: String, time: String): Line = new Line(id, name, time)
}

