package messages.parser

import entity.message.{OnCreateTrigger, OnStartAction, UserCommandAction}
import org.parboiled2.{Parser, ParserInput, Rule1}

//noinspection CaseClassParam,TypeAnnotation
case class MessageParser(val input: ParserInput) extends Parser with CommonParsers {

  def Input: Rule1[UserCommandAction] = rule {
    CommandExpression ~ EOI
  }

  def CommandExpression: Rule1[UserCommandAction] = rule {
    CommandRule
  }

  def CommandRule: Rule1[UserCommandAction] = rule {
    ch('/') ~ CommandsRule
  }

  def CommandsRule: Rule1[UserCommandAction] = rule {
    Commands.START ~ push(new OnStartAction()) |
      Commands.CREATE_TRIGGER ~ optional(BotName) ~ " " ~ optional(Argument) ~ " " ~ optional(Argument) ~> OnCreateTrigger
  }

  def Argument: Rule1[String] = rule {
    '"' ~ capture(oneOrMore(!'"' ~ ANY)) ~ '"'
  }

  def BotName = rule {
    "@WhiteBumBot"
  }


}
