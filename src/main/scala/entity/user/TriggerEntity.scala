package entity.user

import java.sql.Timestamp

case class TriggerEntity
(
  id: Int,
  chatId: Long,
  createdBy: Int,
  createdAt: Timestamp,
  lastUpdated: Timestamp,
  isEnabled: Boolean,
  text: String,
  cron: String,
)
