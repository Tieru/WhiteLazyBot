package messages

import bot.MessageContext
import com.bot4s.telegram.models.Message
import entity.message.{OnCreateTrigger, OnStartAction, UserAction}
import entity.user.UserEntity
import injection.HandlerProvider
import javax.inject.Inject
import messages.handler.{AddTriggerMessageHandler, ProfileMessageHandler}
import messages.parser.MessageParser
import org.parboiled2.ParseError
import slogging.LazyLogging

import scala.concurrent.Future
import scala.util.{Failure, Success}

class GeneralMessageHandler @Inject()(private val handlerProvider: HandlerProvider) extends LazyLogging {

  def message(user: UserEntity, message: Message)(implicit context: MessageContext): Future[Unit] = {
    val text = message.text.getOrElse("")
    MessageParser(text).Input.run() match {
      case Success(action) => processMessage(user, action, message)
      case Failure(e: ParseError) => log(s"On command parsing error: $e")
      case Failure(t) => log(s"On command handling error: $t")
    }
  }

  private def log(message: String): Future[Unit] = {
    logger.debug(message)
    Future.successful(Unit)
  }

  private def processMessage(user: UserEntity, action: UserAction, msg: Message)(implicit context: MessageContext): Future[Unit] = {
    action match {
      case _: OnStartAction => onStart(user)
      case newTrigger: OnCreateTrigger => onAddNewTrigger(msg, newTrigger)
    }
  }

  private def onStart(user: UserEntity)(implicit context: MessageContext): Future[Unit] = {
    val handler = handlerProvider.provide[ProfileMessageHandler]()
    handler.onStart(user)
  }

  private def onAddNewTrigger(message: Message, action: OnCreateTrigger)(implicit context: MessageContext): Future[Unit] = {
    val handler = handlerProvider.provide[AddTriggerMessageHandler]()
    handler.createTrigger(message, action)
  }

}
