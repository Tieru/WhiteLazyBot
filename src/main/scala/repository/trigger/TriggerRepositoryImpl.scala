package repository.trigger

import java.sql.Timestamp

import entity.user.TriggerEntity
import javax.inject.Inject
import schema.data.Tables
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class TriggerRepositoryImpl @Inject()(db: Database)(implicit ec: ExecutionContext) extends TriggerRepository with Tables {

  override val profile = slick.jdbc.PostgresProfile

  override def getTriggerById(id: Int): Future[Option[TriggerEntity]] = {
    val query = Triggers.filter(t => t.id === id).result.headOption
    db.run(query).map(triggerRowToEntity)
  }

  override def getActiveTriggers: Future[Seq[TriggerEntity]] = {
    val query = Triggers.filter(t => t.isEnabled === true).result
    db.run(query).map(_.map(triggerRowToEntity))
  }

  override def addTrigger(chatId: Long, createdBy: Int, text: String, cron: String): Future[TriggerEntity] = db.run {
    val createdAt = new Timestamp(System.currentTimeMillis())
    val insert = (chatId, createdBy, createdAt, createdAt, true, text, cron)
    (Triggers.map(t => (t.chatId, t.createdBy, t.createdAt, t.lastUpdated, t.isEnabled, t.text, t.cron))
      returning Triggers.map(_.id)
      into ((_, id) => TriggerEntity(id, chatId, createdBy, createdAt, createdAt, isEnabled = true, text, cron))
      ) += insert
  }

  override def updateTrigger(trigger: TriggerEntity): Future[TriggerEntity] = {
    val lastUpdated = new Timestamp(System.currentTimeMillis())
    val query = Triggers.filter(u => u.id === trigger.id)
      .map(t => (t.lastUpdated, t.text, t.cron))
      .update((lastUpdated, trigger.text, trigger.cron))

    db.run(query)
      .flatMap(_ => getTriggerById(trigger.id))
      .map(_.get)
  }

  private def triggerRowToEntity(rowOpt: Option[TriggersRow]): Option[TriggerEntity] = {
    rowOpt match {
      case None => None
      case Some(row) => Some(triggerRowToEntity(row))
    }
  }

  private def triggerRowToEntity(row: TriggersRow): TriggerEntity = {
    TriggerEntity(
      row.id,
      row.chatId,
      row.createdBy,
      row.createdAt,
      row.lastUpdated,
      row.isEnabled,
      row.text,
      row.cron
    )
  }
}
