package messages

import entity.message.{OnCreateTrigger, OnStartAction}
import messages.parser.MessageParser
import org.scalatest.FlatSpec

class MessageParserSpec extends FlatSpec {

  "Parser" should "recognize /start command" in {
    testInput[OnStartAction]("/start")
  }

  it should "recognize add trigger command" in {
    val triggerText = "/new_trigger \"111\" \"222\""
    val result = MessageParser(triggerText).Input.run().get.asInstanceOf[OnCreateTrigger]
    assert(result.cron.get == "111")
    assert(result.text.get == "222")
  }

  private def testInput[T](message: String) = {
    val result = MessageParser(message).Input.run().get
    assert(result.isInstanceOf[T])
  }

}
