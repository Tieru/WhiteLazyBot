package bot

import com.google.inject.{Guice, Injector}
import injection.ApplicationModule
import net.codingwell.scalaguice.InjectorExtensions._
import slogging.{LazyLogging, LogLevel, LoggerConfig, PrintLoggerFactory}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Application extends LazyLogging {

  def main(args: Array[String]): Unit = {

    LoggerConfig.factory = PrintLoggerFactory()
    LoggerConfig.level = LogLevel.TRACE

    logger.info("Starting bot...")

    val injector: Injector = Guice.createInjector(new ApplicationModule())
    val bot = injector.instance[WhiteBumBot]
    val eol = bot.run()

    val starter = injector.instance[ApplicationStartUp]
    starter.onApplicationStart()

    println("Press [ENTER] to shutdown the bot, it may take a few seconds...")
    scala.io.StdIn.readLine()
    bot.shutdown()

    Await.result(eol, Duration.Inf)
  }

}
