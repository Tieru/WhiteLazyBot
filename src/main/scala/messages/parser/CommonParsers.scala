package messages.parser

import org.parboiled2.{CharPredicate, Parser}

trait CommonParsers { this: Parser ⇒

  def Whitespace = rule { anyOf(" \n\t") }

  def EOL = rule {
    "\r\n" | "\n"
  }

  def DoubleEOL = rule {
    EOL ~ EOL
  }

  def Number = rule {
    capture(Digits) ~> (_.toInt)
  }

  def Digits = rule {
    oneOrMore(CharPredicate.Digit)
  }

  def Skip = rule {
    oneOrMore(ANY)
  }

  def Emoji = rule {
    oneOrMore(CharPredicate('\u203C' to '\u3299') | CharPredicate('\uD83C' to '\uDFFF'))
  }
}
