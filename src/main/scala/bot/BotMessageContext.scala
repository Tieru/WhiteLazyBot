package bot

import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.api.declarative.Messages
import com.bot4s.telegram.methods.ParseMode.ParseMode
import com.bot4s.telegram.models.{Message, ReplyMarkup}

import scala.concurrent.{ExecutionContext, Future}

class BotMessageContext(handler: RequestHandler, messages: Messages)(implicit message: Message, ec: ExecutionContext)
  extends BotRequestContext(handler) with MessageContext {

  override def reply(text: String,
                     parseMode: Option[ParseMode],
                     disableWebPagePreview: Option[Boolean],
                     disableNotification: Option[Boolean],
                     replyToMessageId: Option[Int],
                     replyMarkup: Option[ReplyMarkup]): Future[Message] = {

    messages.reply(text, parseMode, disableWebPagePreview, disableNotification, replyToMessageId, replyMarkup)
  }

}
