package controllers

import java.io.File
import java.util.Properties
import javax.inject._

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import play.api.data.Form
import play.api.data.Forms.{mapping, _}
import play.api.i18n.MessagesApi
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc._
import play.api.{Logger, i18n}

/**
  * Home controller
  */
@Singleton
class HomeController @Inject()(val messagesApi: MessagesApi, configuration: play.api.Configuration) extends Controller with i18n.I18nSupport {

  /**
    * Index page
    */
  def index =
    Action { implicit request =>
      Logger.info("index page")
      Ok(views.html.index())
    }

}
