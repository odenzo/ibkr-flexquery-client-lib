package com.odenzo.ibkr.web.flexquery.parsing

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*
import fs2.data.xml.{XmlEvent, events, normalize}

class XMLParsingBugReport extends munit.FunSuite {
  import cats.effect.unsafe.implicits.global
  test("DirectFailure") {
    val hangers     = """<root>    Incomplete end tag. </root"""
    val fixerDOwner = """<root>    Incomplete end tag. <"""
    val bad         =
      """<root>    Incomplete end tag. 
        |
        |""".stripMargin

    val res = fs2.Stream.emit(bad).through(events[IO, String]).compile.toList.unsafeRunSync() // .through(normalize).compile.toList

    res

  }

}
