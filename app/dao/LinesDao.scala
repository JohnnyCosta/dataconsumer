package dao

import javax.inject.{Inject, Singleton}

import data.Line
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Created by joao on 03/01/17.
  */
@Singleton
class LinesDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val lines = TableQuery[Lines]

  def create() = {
    lines.schema.create
  }

  def all(): Future[Seq[Line]] = db.run(lines.result)

  def insert(line: Line): Future[Unit] = db.run(lines += line).map { _ => () }

  def findById(id: String): Future[Option[Line]] ={
    db.run(lines.filter(_.id === id).result.headOption)
  }

  class Lines(tag: Tag) extends Table[Line](tag, "LINES") {

    def id = column[String]("ID", O.PrimaryKey)

    def name = column[String]("NAME")

    def time = column[String]("TIME")

    def * = (id, name, time) <> (Line.tupled, Line.unapply)
  }

}