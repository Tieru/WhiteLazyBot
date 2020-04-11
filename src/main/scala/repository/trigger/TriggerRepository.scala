package repository.trigger

import entity.user.TriggerEntity

import scala.concurrent.Future

trait TriggerRepository {

  def getTriggerById(id: Int): Future[Option[TriggerEntity]]

  def getActiveTriggers: Future[Seq[TriggerEntity]]

  def addTrigger(chatId: Long, createdBy: Int, text: String, cron: String): Future[TriggerEntity]

  def updateTrigger(trigger: TriggerEntity): Future[TriggerEntity]

}
