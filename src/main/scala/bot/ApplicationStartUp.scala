package bot

import com.google.inject.{Inject, Singleton}
import repository.trigger.TriggerRepository
import service.trigger.TriggerSchedulerService

import scala.concurrent.ExecutionContext


@Singleton
class ApplicationStartUp @Inject()(private val repository: TriggerRepository,
                                   private val triggerScheduler: TriggerSchedulerService)(implicit ec: ExecutionContext) {

  def onApplicationStart(): Unit = {
    repository.getActiveTriggers
      .map(triggers => triggers.foreach { trigger => triggerScheduler.scheduleTrigger(trigger.id) })
  }

}
