package com.odenzo.ibkr.web.flexquery.parsing

import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*
import com.odenzo.ibkr.*
import com.odenzo.ibkr.web.flexquery.utils.*

import com.odenzo.ibkr.web.flexquery.{BaseTest, FlexAPI}
import fs2.data.xml.{QName, XmlEvent, events, normalize}
import munit.*
import org.http4s.client.Client
import retry.{RetryPolicies, retryingOnSomeErrors}

import scala.concurrent.duration.*

/** Seeing what we can find that is JVM *and* ScalaJS compatable */
class XMLParsingSuite extends BaseTest:

  // Hmmm, a good respone will haevv FlexQueryRespone?
  val badResponse: String = """<FlexStatementResponse timestamp='20 February, 2022 05:52 PM EST'>
    <Status>Warn</Status>
    <ErrorCode>1019</ErrorCode>
    <ErrorMessage>This is a FAKE MESSAGE Statement generation in progress. Please try again shortly.</ErrorMessage>
  </FlexStatementResponse>"""

  test("FS2Parsing") {
    assert(true)
    // Seems no namespaces or references to resolve
    val tags: List[fs2.data.xml.XmlEvent] = fs2.Stream.emit(badResponse).through(events[IO, String]).through(normalize)
      .compile.toList
      .unsafeRunSync()

    tags.foreach(x => scribe.info(s"Parsed Events ==> \n ${oprint(x)}"))
    import monocle.*
    import monocle.syntax.all.*

    val eachEv = Traversal.fromTraverse[List, XmlEvent]
    // val xmlStr: Prism[XmlEvent, String] = Prism.partial[XmlEvent, String] { case XMLString(v, false) => v }(XmlEvent)

    val res = eachEv.find {
      case XmlEvent.StartTag(QName(_, "FlexStatementResponse"), ts, _) => true
      case _                                                           => false
    }(tags)

    scribe.info(s"Filtered Head: $res \n\n ${oprint(res)}")

  }
