package com.odenzo.ibkr.web.flexquery.parsing

import cats.effect.*
import cats.effect.syntax.all.*
import cats.*
import cats.data.*
import cats.syntax.all.*
import com.odenzo.ibkr.web.flexquery.BaseTest
import fs2.data.xml.{XmlEvent, events, normalize}

class XMLParsingTest extends BaseTest {

  // Necessay to help poor IJ
  import munit.CatsEffectAssertions.*
  import munit.{CatsEffectAssertions => CA}
  test("GoodXML") {
    val res: IO[List[XmlEvent]] = XMLParsing.parseToEvents("<good>morning</good>")
    res
  }

  test("BadXML") {
    CA.interceptIO[fs2.data.xml.XmlException](XMLParsing.parseToEvents("<good>morning</bad>"))
  }

//  test("HangingXML") {
//    CA.interceptIO[fs2.data.xml.XmlException](XMLParsing.parseToEvents("<good>morning"))
//  }

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
