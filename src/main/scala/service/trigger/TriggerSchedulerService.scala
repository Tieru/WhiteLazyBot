package service.trigger

import java.time.LocalTime

import cats.effect.{Concurrent, Sync, Timer}
import cats.implicits._
import cats.effect.{ContextShift, IO, Timer}
import com.google.inject.{Inject, Singleton}
import cron4s.Cron
import entity.user.TriggerEntity
import eu.timepit.fs2cron.awakeEveryCron
import fs2.Stream
import fs2.concurrent.SignallingRef
import repository.trigger.TriggerRepository
import slogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class TriggerSchedulerService @Inject()(private val triggerRepository: TriggerRepository,
                                        private val triggerMessagingService: TriggerMessagingService)(implicit ec: ExecutionContext) extends LazyLogging {

  implicit val timer: Timer[IO] = IO.timer(ec)

  def scheduleTrigger(triggerId: Int): Future[Unit] = {
    triggerRepository.getTriggerById(triggerId)
      .map {
        case Some(trigger) => schedule(trigger).unsafeRunSync()
        case _ => Future.successful(())
      }
  }

  private def schedule(trigger: TriggerEntity): IO[Unit] = {
    val evenSeconds = Cron.unsafeParse(trigger.cron)
    val task: IO[Option[Boolean]] = IO.async { cb =>
      triggerMessagingService.sendTriggerText(trigger.id)
        .map { result =>
          if (result) Some(result) else None
        }
        .onComplete {
          case Success(a) => cb(Right(a))
          case Failure(e) => cb(Left(e))
        }
    }
    val stream = Stream.eval(task)
    val scheduledTasks = awakeEveryCron[IO](evenSeconds) >> stream

    scheduledTasks.unNoneTerminate.compile.drain
  }
}
