package bot

import java.net.{Authenticator, InetSocketAddress, PasswordAuthentication, Proxy}

import com.bot4s.telegram.api.declarative.{Callbacks, Commands}
import com.bot4s.telegram.api.{Polling, RequestHandler, TelegramBot}
import com.bot4s.telegram.clients.ScalajHttpClient
import com.bot4s.telegram.models.Message
import com.google.inject.Injector
import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.okhttp.OkHttpFutureBackend
import com.typesafe.config.ConfigFactory
import entity.user.UserEntity
import javax.inject.{Inject, Singleton}
import messages.GeneralMessageHandler
import service.auth.UserAuthenticator
import net.codingwell.scalaguice.InjectorExtensions._

import scala.concurrent.Future

@Singleton
class WhiteBumBot @Inject()(injector: Injector) extends TelegramBot
  with Polling
  with Commands
  with Callbacks {

  private lazy val authenticator = {
    injector.instance[UserAuthenticator]
  }
  private lazy val messageHandler = {
    injector.instance[GeneralMessageHandler]
  }

  implicit val backend: SttpBackend[Future, Nothing] = OkHttpFutureBackend()

  override val client: RequestHandler = {
    val configs = ConfigFactory.load()
    val token = configs.getString("bot.token")

    if (configs.hasPath("bot.useProxy") && configs.getBoolean("bot.useProxy")) {
      new ScalajHttpClient(token, buildProxySettings())
    } else {
      new ScalajHttpClient(token)
    }
  }

  override def receiveMessage(msg: Message): Unit = {
    super.receiveMessage(msg)

    if (msg.newChatMembers.isDefined) {
      processUsersJoinedChat(msg)
    } else if (msg.from.isDefined) {
      if (msg.text.getOrElse("").startsWith("/")) {
        authenticator.getUser(msg.from.get)
          .flatMap(processMessage(_, msg))
      }
    }
  }

  private def processMessage(user: UserEntity, msg: Message): Future[Unit] = {
    implicit val message: Message = msg
    implicit val context: MessageContext = new BotMessageContext(request, this)
    messageHandler.message(user, msg)
      .recoverWith {
        case e =>
          logTopError(e)
          Future.successful(Unit)
      }
  }

  private def logTopError(t: Throwable): Unit = {
    val cause = t.getMessage
    logger.error(s"Exception reached top lvl: $cause", t)
    t.printStackTrace()
  }

  private def processUsersJoinedChat(msg: Message): Future[Unit] = {
    Future.successful(())
  }

  private def buildProxySettings(): Proxy = {
    val configs = ConfigFactory.load()
    val host = configs.getString("bot.proxyHost")

    if (configs.hasPath("bot.proxyUser")) {
      val proxyUser = configs.getString("bot.proxyUser")
      val proxyPassword = configs.getString("bot.proxyPassword")

      val auth = new Authenticator() {
        override def getPasswordAuthentication: PasswordAuthentication =
          new PasswordAuthentication(proxyUser, proxyPassword.toCharArray)
      }
      Authenticator.setDefault(auth)
    }

    val port = configs.getInt("bot.proxyPort")
    new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(host, port))
  }

  logger.info("Bot started successfully")

}
