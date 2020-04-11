package messages.handler

import bot.MessageContext
import com.bot4s.telegram.methods.GetChatMember
import com.bot4s.telegram.models.{ChatId, MemberStatus, Message}
import com.google.inject.{Inject, Singleton}
import com.osinka.i18n.{Lang, Messages}
import cron4s.Cron
import entity.message.OnCreateTrigger
import service.error.{AppException, ErrorCode, ErrorInfo}
import service.trigger.AddTriggerService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddTriggerMessageHandler @Inject()(private val triggersService: AddTriggerService)
                                        (implicit val ec: ExecutionContext) {

  private implicit val lang: Lang = Lang.apply("ru")

  def createTrigger(msg: Message, action: OnCreateTrigger)(implicit ctx: MessageContext): Future[Unit] = {
    isUserAdmin(ChatId(msg.chat.id), msg.from.get.id)
      .flatMap {
        case true => doCreateTrigger(msg, action)
        case _ => ctx.reply(Messages("trigger.add.error.unauthorized")).map(_ => ())
      }
  }

  private def isUserAdmin(chatId: ChatId, id: Int)(implicit ctx: MessageContext): Future[Boolean] = {
    val request = GetChatMember(chatId, id)
    ctx.request(request)
      .map(m => m.status == MemberStatus.Creator || m.status == MemberStatus.Administrator)
  }

  private def doCreateTrigger(msg: Message, action: OnCreateTrigger)(implicit ctx: MessageContext): Future[Unit] = {
    if (action.cron.isEmpty || action.cron.get.isEmpty) {
      ctx.reply(Messages("trigger.add.error.no_cron"))
        .flatMap(_ => Future.failed(AppException(ErrorInfo(ErrorCode.Validation))))
    } else if (action.text.isEmpty || action.text.get.isEmpty) {
      ctx.reply(Messages("trigger.add.error.no_cron"))
        .flatMap(_ => Future.failed(AppException(ErrorInfo(ErrorCode.Validation))))
    } else {
      Cron.parse(action.cron.get) match {
        case Left(err) => ctx.reply(Messages("trigger.add.error.invalid_cron")).map(_ => ())
          .flatMap(_ => Future.successful(()))
        case Right(_) => triggersService.addTrigger(msg.chat.id, msg.from.get.id, action.text.get, action.cron.get)
          .flatMap(
            _ => ctx.reply(Messages("trigger.add.success")).map(_ => Future.successful(()))
          )
      }
    }
  }
}
