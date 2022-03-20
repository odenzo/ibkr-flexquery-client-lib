package com.odenzo.ibkr.web.flexquery.parsing

import cats.effect.*
import cats.syntax.all.*
import com.odenzo.ibkr.*
import com.odenzo.ibkr.web.flexquery.utils.*

import com.odenzo.ibkr.web.flexquery.{BaseTest, FlexAPI}
import fs2.data.xml.{QName, XmlEvent, events, normalize}
import munit.*
import org.http4s.client.Client
import retry.{RetryPolicies, retryingOnSomeErrors}

import scala.concurrent.duration.*

/** Seeing what we can find that is JVM *and* ScalaJS compatible */
class FlexStatementRsSuite extends BaseTest:
  import _root_.munit.CatsEffectAssertions.*

  val badResponse: String = """<FlexStatementResponse timestamp='20 February, 2022 05:52 PM EST'>
    <Status>Warn</Status>
    <ErrorCode>1019</ErrorCode>
    <ErrorMessage>This is a FAKE MESSAGE Statement generation in progress. Please try again shortly.</ErrorMessage>
  </FlexStatementResponse>"""

  val goodResponse: String = """<FlexStatementResponse timestamp='20 February, 2022 05:52 PM EST'>
    <Status>Success</Status>
    <ReferenceCode>666</ReferenceCode>
  </FlexStatementResponse>"""

  val badRs: FlexStatementRs =
    FlexStatementRs.FlexStatementErrorRs("Warn", 1019, "This is a FAKE MESSAGE Statement generation in progress. Please try again shortly.")

  test("RS-Bad") {

    val res: IO[FlexStatementRs] =
      FlexStatementRs.fromXml(badResponse).flatTap(a => IO(scribe.info(s"Filtered Head: $a \n\n ${oprint(a)}")))
    res.map(fs => assertEquals(fs, badRs))
    res
  }

  test("RS-Good") {
    FlexStatementRs.fromXml(goodResponse).flatTap(a => IO(scribe.info(s"Filtered Head: $a \n\n ${oprint(a)}"))) map {
      assertEquals(_, FlexStatementRs.FlexStatementSuccessRs("Success", "666"))
    }
  }

  test("RS-BadXML") {
    val error: IO[Either[Throwable, FlexStatementRs]] = FlexStatementRs.fromXml("<is><noClose>vcalidXML</isNot>").attempt
    error.map {
      case Left(value)  => scribe.info(s"Err: ${value}")
      case Right(value) => fail("XML Parsing Should Have Failed and Raised Exception")
    }
  }
