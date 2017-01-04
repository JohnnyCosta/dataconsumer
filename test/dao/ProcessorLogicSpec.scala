package dao

import java.io.{File, FileOutputStream, OutputStream}

import data.Line
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import processor.ProcessorLogic

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt


/**
  * Test processor logic
  */
class ProcessorLogicSpec extends PlaySpec with OneAppPerTest {

  def writeTo(path: String): OutputStream = new FileOutputStream(path)

  "ProcessorLogic" should {

    "process the lines" in {
      val processor = app.injector.instanceOf[ProcessorLogic]

      val tempFile = File.createTempFile("output", ".csv");
      val output = writeTo(tempFile.getAbsolutePath)
      val header = "Id,name,time_of_start,Obs.,,\n"
      val emptyline = ",,,,,\n"
      val line1 = "1,John,12-06-1980 12:00:12,Not satisfied,,\n"
      val line2 = "2,Mary,13-06-1980 12:00:12,Not satisfied,,\n"
      val line3 = "1,George,14-06-1980 12:00:12,Not satisfied,,\n"
      val line4 = "2,Michael,14-06-1980 12:00:12,Not satisfied,,\n"
      val line5 = "1,Mario,14-06-1980 12:00:12,Not satisfied,,\n"
      val line6 = "2,James,14-06-1980 12:00:12,Not satisfied,,\n"
      output.write(header.getBytes)
      output.write(line1.getBytes)
      output.write(line2.getBytes)
      output.write(line3.getBytes)
      output.write(line4.getBytes)
      output.write(line5.getBytes)
      output.write(line6.getBytes)
      output.write(emptyline.getBytes)
      output.close()

      val dao = app.injector.instanceOf[LinesDao]

      processor.process(tempFile.getAbsolutePath, dao)

      // Wait to arrive database
      Thread.sleep(1000)

      val lines = Set(
        Line("1", "john", "1980-06-12T10:00:12.000Z"),
        Line("2", "mary", "1980-06-13T10:00:12.000Z")
      )

      val storedLines = Await.result(dao.all(), 1 seconds)
      storedLines.toSet mustBe lines

    }
  }
}
