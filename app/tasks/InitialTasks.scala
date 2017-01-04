package tasks

import javax.inject.{Inject, Singleton}

import dao.LinesDao
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.DBIO
import slick.driver.JdbcProfile

/**
  * Created by joao on 04/01/17.
  */
@Singleton
class InitialTasks @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, lineDao: LinesDao) {

  Logger.info("Initial task")

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val setup = DBIO.seq(
    lineDao.create()
  )

  val setupFuture = dbConfig.db.run(setup)

}
