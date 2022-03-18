package com.odenzo.ibkr.web.flexquery.parsing

import cats.effect.*
import cats.syntax.all.*
import com.odenzo.ibkr.*
import com.odenzo.ibkr.web.base.OPrint.oprint
import com.odenzo.ibkr.web.flexquery.{BaseTest, FlexAPI}
import fs2.data.xml.{QName, XmlEvent, events, normalize}
import munit.*
import org.http4s.client.Client
import retry.{RetryPolicies, retryingOnSomeErrors}

import scala.concurrent.duration.*

/** Seeing what we can find that is JVM *and* ScalaJS compatable */
class FlexStatementRsSuite extends BaseTest:

  // Hmmm, a good respone will haevv FlexQueryRespone?
  val badResponse: String = """<FlexStatementResponse timestamp='20 February, 2022 05:52 PM EST'>
    <Status>Warn</Status>
    <ErrorCode>1019</ErrorCode>
    <ErrorMessage>This is a FAKE MESSAGE Statement generation in progress. Please try again shortly.</ErrorMessage>
  </FlexStatementResponse>"""

  test("FS2Parsing") {

    val res = FlexStatementRs.fromXml(badResponse).flatTap(a => IO(scribe.info(s"Filtered Head: $a \n\n ${oprint(a)}")))

    res
  }
