package bot

import com.bot4s.telegram.methods.ParseMode.ParseMode
import com.bot4s.telegram.models.{Message, ReplyMarkup}

import scala.concurrent.Future

trait MessageContext extends RequestContext {

  def reply(text: String,
            parseMode: Option[ParseMode] = None,
            disableWebPagePreview: Option[Boolean] = None,
            disableNotification: Option[Boolean] = None,
            replyToMessageId: Option[Int] = None,
            replyMarkup: Option[ReplyMarkup] = None
           ): Future[Message]

}
