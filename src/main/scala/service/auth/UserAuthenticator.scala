package service.auth

import com.bot4s.telegram.models.User
import entity.user.UserEntity
import javax.inject.Inject
import repository.user.UserRepository
import service.auth.UserExtensions._

import scala.concurrent.{ExecutionContext, Future}

trait UserAuthenticator {
  def getUser(user: User): Future[UserEntity]
}

class UserAuthenticatorImpl @Inject()(userRepository: UserRepository)(implicit ec: ExecutionContext) extends UserAuthenticator {

  override def getUser(user: User): Future[UserEntity] = {
    userRepository.getUser(user.id)
      .flatMap {
        case Some(fetched) => updateUsernameIfRequired(fetched, user)
        case None => createNewUser(user)
      }
  }

  private def updateUsernameIfRequired(user: UserEntity, remoteUser: User): Future[UserEntity] = {
    if ("@" + remoteUser.username.getOrElse("") == user.username) {
      Future.successful(user)
    } else {
      val username = remoteUser.makeUsername()
      if (username == user.username) {
        Future.successful(user)
      } else {
        userRepository.updateUser(user.copy(username = username))
      }
    }
  }

  private def createNewUser(remoteUser: User): Future[UserEntity] = {
    userRepository.createUser(remoteUser.id, remoteUser.makeUsername())
  }
}
