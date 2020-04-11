package repository.user

import entity.user.UserEntity

import scala.concurrent.Future

trait UserRepository {

  def getUser(userId: Int): Future[Option[UserEntity]]

  def getByUsername(username: String): Future[Option[UserEntity]]

  def createUser(userId: Int, username: String, isRegistered: Boolean = false) : Future[UserEntity]

  def updateUser(userEntity: UserEntity): Future[UserEntity]

}
