package injection

import akka.actor.ActorSystem
import bot.{ApplicationStartUp, RequestContext, WhiteBumBot}
import com.google.inject.AbstractModule
import database.provider.DatabaseProvider
import messages.GeneralMessageHandler
import messages.handler.{AddTriggerMessageHandler, ProfileMessageHandler}
import net.codingwell.scalaguice.ScalaModule
import slick.jdbc.JdbcBackend.Database
import net.codingwell.scalaguice.InjectorExtensions._
import repository.trigger.{TriggerRepository, TriggerRepositoryImpl}
import repository.user.{UserRepository, UserRepositoryImpl}
import service.auth.{UserAuthenticator, UserAuthenticatorImpl}
import service.profile.ProfileService
import service.trigger.{AddTriggerService, TriggerMessagingService, TriggerSchedulerService}

import scala.concurrent.ExecutionContext

class ApplicationModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[WhiteBumBot].asEagerSingleton()
    bind[RequestContext].toProvider[BotRequestContextProvider]

    // probably shouldn't be here
    bind[ExecutionContext].toInstance(ExecutionContext.Implicits.global)

    bind[HandlerProvider].asEagerSingleton()
    bind[GeneralMessageHandler].asEagerSingleton()
    bind[ProfileMessageHandler].asEagerSingleton()
    bind[AddTriggerMessageHandler].asEagerSingleton()

    bind[UserAuthenticator].to[UserAuthenticatorImpl].asEagerSingleton()
    bind[ProfileService].asEagerSingleton()
    bind[TriggerSchedulerService].asEagerSingleton()
    bind[TriggerMessagingService].asEagerSingleton()
    bind[AddTriggerService].asEagerSingleton()

    bind(classOf[Database]).toProvider(classOf[DatabaseProvider])
    bind[UserRepository].to[UserRepositoryImpl].asEagerSingleton()
    bind[TriggerRepository].to[TriggerRepositoryImpl].asEagerSingleton()

    bind[ApplicationStartUp].asEagerSingleton()

  }
}
