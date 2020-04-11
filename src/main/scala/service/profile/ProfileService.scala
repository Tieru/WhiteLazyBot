package service.profile

import entity.user.UserEntity
import javax.inject.Inject
import repository.user.UserRepository
import service.error.ErrorRecoverExtensions._

import scala.concurrent.{ExecutionContext, Future}

class ProfileService @Inject()(userRepository: UserRepository)(implicit ec: ExecutionContext) {

  def registerUser(user: UserEntity): Future[Unit] = {
    userRepository.updateUser(user.copy(isRegistered = true))
      .recoverWithDefaultError()
      .map(_ => Unit)
  }

}
