package dao

import data.Line
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}


/**
  * Test insert and list all
  */
class LinesDaoSpec extends PlaySpec with OneAppPerTest {
  "LinesDAO" should {

    "Insert lines" in {
      val dao = app.injector.instanceOf[LinesDao]

      val lines = Set(
        Line("1", "name1", "time1"),
        Line("2", "name2", "time2")
      )

      Await.result(Future.sequence(lines.map(dao.insert)), 1 seconds)
      val storedLines = Await.result(dao.all(), 1 seconds)

      storedLines.toSet mustBe lines
    }
  }
}
