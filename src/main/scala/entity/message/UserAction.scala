package entity.message

sealed trait UserAction

sealed trait UserCommandAction extends UserAction

class OnStartAction extends UserCommandAction

case class OnCreateTrigger(cron: Option[String], text: Option[String]) extends UserCommandAction
