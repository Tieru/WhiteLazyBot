package injection

import bot.{BotRequestContext, RequestContext, WhiteBumBot}
import com.google.inject.{Inject, Provider}

import scala.concurrent.ExecutionContext

class BotRequestContextProvider @Inject()(private val bot: WhiteBumBot)(implicit val ec: ExecutionContext) extends Provider[RequestContext] {

  override def get(): RequestContext = {
    new BotRequestContext(bot.request)
  }
}
