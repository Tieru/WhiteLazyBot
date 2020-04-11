package repository.user

import entity.user.UserEntity
import javax.inject.Inject
import schema.data.Tables
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class UserRepositoryImpl @Inject()(db: Database)(implicit ec: ExecutionContext) extends UserRepository with Tables {

  override val profile = slick.jdbc.PostgresProfile

  override def getUser(userId: Int): Future[Option[UserEntity]] = {
    val query = Users.filter(user => user.id === userId).result.headOption
    db.run(query).map(rowToUser)
  }

  override def getByUsername(username: String): Future[Option[UserEntity]] = {
    val query = Users.filter(user => user.username === username).result.headOption
    db.run(query).map(rowToUser)
  }

  override def createUser(userId: Int, username: String, isRegistered: Boolean = false): Future[UserEntity] = db.run {
    (Users returning Users.map(_.id)
      into ((_, id) => UserEntity(id, isRegistered, isBanned = false, username = username, isAdmin = false))
      ) += UsersRow(userId, isRegistered, isBanned = false, isAdmin = false, username)
  }

  override def updateUser(user: UserEntity): Future[UserEntity] = {
    val query = Users.filter(u => u.id === user.id)
      .map(u => (u.isRegistered, u.username, u.isBanned))
      .update((user.isRegistered, user.username, user.isBanned))

    db.run(query)
      .flatMap(_ => getUser(user.id))
      .map(_.get)
  }

  private def rowToUser(rowOpt: Option[UsersRow]): Option[UserEntity] = {
    rowOpt match {
      case None => None
      case Some(row) => Some(rowToUser(row))
    }
  }

  private def rowToUser(row: UsersRow): UserEntity = {
    UserEntity(
      row.id,
      row.isRegistered,
      row.isBanned,
      row.username,
      row.isAdmin
    )
  }
}
