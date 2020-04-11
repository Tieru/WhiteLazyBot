package messages.parser

object Commands {

  val START = "start"
  val CREATE_TRIGGER = "new_trigger"

  def makeInlineCommand(command: String) = s"/c_$command"

}

object SoftCommands {

  val RU_LETTERS = "буквы"

}
