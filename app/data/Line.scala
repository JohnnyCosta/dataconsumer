package data

/**
  * Created by joao on 03/01/17.
  */
class Line(val id: String, val name: String, val time: String)

object Line {
  def apply(id: String, name: String, time: String): Line = new Line(id, name, time)
}

