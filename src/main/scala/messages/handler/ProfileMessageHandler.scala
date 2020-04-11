package messages.handler

import bot.MessageContext
import com.osinka.i18n.{Lang, Messages}
import entity.user.UserEntity
import javax.inject.Inject
import service.error.ErrorRecoverExtensions._
import service.profile.ProfileService
import slogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class ProfileMessageHandler @Inject()(service: ProfileService)
                                     (implicit ec: ExecutionContext) extends LazyLogging {

  private implicit val lang: Lang = Lang.apply("ru")

  def onStart(user: UserEntity)(implicit context: MessageContext): Future[Unit] = {
    onUserStart(user)
      .recoverFromAppError {
        case _ => invokeDefaultErrorHandling()
      }
  }

  private def onUserStart(user: UserEntity)(implicit context: MessageContext): Future[Unit] = {
    service.registerUser(user)
      .flatMap(_ => context.reply(Messages("start.welcome")))
      .map(_ => Unit)
  }

  private def invokeDefaultErrorHandling()(implicit context: MessageContext): Future[Unit] = {
    logger.info("Profile command failed with unknown error")
    context.reply(Messages("error.unknown"))
    Future.successful(Unit)
  }
}
