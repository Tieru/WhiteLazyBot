package database.provider

import javax.inject.{Inject, Provider, Singleton}
import slick.jdbc.JdbcBackend.Database

@Singleton
class DatabaseProvider @Inject() extends Provider[Database] {
  lazy val get = Database.forConfig("app.database")
}
