package com.odenzo.ibkr.web.flexquery.parsing

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*
import com.odenzo.ibkr.web.flexquery.BaseTest
import fs2.data.xml.{XmlEvent, events, normalize}
import munit.Assertions.intercept

import scala.concurrent.duration.*
class XMLParsingBugReport extends BaseTest {
  import cats.effect.unsafe.implicits.global

  val hangers     = """<root>    Incomplete end tag. </root"""
  val fixerDOwner = """<root>    Incomplete end tag. <"""
  val bad: String =
    """<root>    Incomplete end tag. 
      |
      |""".stripMargin

  test("DirectFailure".fail) {
    // This will never complete no matter what the timeout.
    fs2.Stream.emit(bad).through(events[IO, String]).compile.toList.timeout(2.seconds)

  }
  test("OK") {
    fs2.Stream.emit(fixerDOwner).through(events[IO, String]).compile.toList.timeout(5.seconds).intercept[fs2.data.xml.XmlException]
  }

}
