package service.trigger

import com.google.inject.Inject
import repository.trigger.TriggerRepository

import scala.concurrent.{ExecutionContext, Future}


class AddTriggerService @Inject()(private val triggerRepository: TriggerRepository,
                                  private val triggerSchedulerService: TriggerSchedulerService)(implicit val ec: ExecutionContext) {

  def addTrigger(chatId: Long, createdBy: Int, text: String, cron: String): Future[Unit] = {
    triggerRepository.addTrigger(chatId, createdBy, text, cron)
      .map(trigger => triggerSchedulerService.scheduleTrigger(trigger.id))
      .map(_ => ())
  }

}
