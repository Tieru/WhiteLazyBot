package service.trigger

import bot.RequestContext
import com.bot4s.telegram.methods.SendMessage
import com.bot4s.telegram.models.ChatId
import com.google.inject.Singleton
import entity.user.TriggerEntity
import javax.inject.Inject
import repository.trigger.TriggerRepository
import slogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerMessagingService @Inject()(private val requestContext: RequestContext,
                                        private val triggerRepository: TriggerRepository)(implicit val ec: ExecutionContext) extends LazyLogging {

  def sendTriggerText(triggerId: Int): Future[Boolean] = {
    triggerRepository.getTriggerById(triggerId)
      .flatMap {
        case Some(trigger) => if (trigger.isEnabled) {
          sendTriggerText(trigger)
        } else {
          Future.successful(false)
        }
        case _ => Future.successful(false)
      }
  }

  private def sendTriggerText(trigger: TriggerEntity): Future[Boolean] = {
    val text = trigger.text
    val chatId = ChatId(trigger.chatId)
    val sendMessageRequest = SendMessage(chatId, text)
    requestContext.request(sendMessageRequest)
      .map { _ =>
        logger.error(s"Scheduled message was successfully sent to chat ${trigger.chatId}")
        true
      }
      .recoverWith {
        case e =>
          logger.error(s"Unable to send trigger message to chat $chatId " + e.toString)
          disableTrigger(trigger)
      }
  }

  private def disableTrigger(trigger: TriggerEntity): Future[Boolean] = {
    triggerRepository.updateTrigger(trigger.copy(isEnabled = false))
      .map(_ => false)
      .recoverWith {
        case _ => Future.successful(false)
      }
  }
}
