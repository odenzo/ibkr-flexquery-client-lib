package com.odenzo.ibkr.web.flexquery.parsing

import cats.effect.*
import cats.syntax.all.*
import com.odenzo.ibkr.*
import com.odenzo.ibkr.web.flexquery.{BaseTest, FlexAPI}
import fs2.data.xml.{XmlEvent, events, normalize}
import munit.*
import org.http4s.client.Client
import retry.{RetryPolicies, retryingOnSomeErrors}

import scala.concurrent.duration.*
import scala.xml.Elem

class FlexXmlSuiteJVM extends BaseTest:
  val badResponse: String = """<FlexStatementResponse timestamp='20 February, 2022 05:52 PM EST'>
    <Status>Warn</Status>
    <ErrorCode>1019</ErrorCode>
    <ErrorMessage>This is a FAKE MESSAGE Statement generation in progress. Please try again shortly.</ErrorMessage>
  </FlexStatementResponse>"""

  test("Native Scala") {
    val xml: Elem = scala.xml.XML.loadString(badResponse)
    Assertions.assertEquals(xml.label, "FlexStatementResponse")
  }
  test("FS2 Parsing") {
    assert(true)
    // Seems no namespaces or references to resolve
    val xml: IO[List[XmlEvent]] = fs2.Stream.emit(badResponse).through(events[IO, String]).through(normalize)
      .compile.toList

    // Well, now I have XMLEventss in a total custom datamodel, now what?
  }
//  test("dealWithEWrrors") {
//    FlexAPI.dealWithErrors(badResponse).attempt.flatTap {
//      case Left(err) => scribe.info(s"dealthWithErrors OK").pure
//      case Right(v)  => scribe.info("Had a Value").pure
//    }
//  }
//
//  test("XML Splicing") {
//    val pp          = new scala.xml.PrettyPrinter(80, 4)
//    val txt: String = pp.format(badResponse)
//    scribe.info("BadResponse Sample:\n $txt")
//    // No Good XML library that does scalaJS. Phobos for the JVM? fs2-data-xml
//    // badResponse.t
//  }

//  val retryProg: IO[WarnStatus] = IO.sleep(2.seconds) >>
//    IO(scribe.info("GotBadResult")) >>
//    IO.raiseError(WarnStatus(1019, "Testing"))
//
//  test("Manial Retry") {
//    val foo: IO[WarnStatus] = retryingOnSomeErrors(
//      isWorthRetrying = FlexAPI.shouldRetry _,
//      policy = RetryPolicies.limitRetries[IO](3) join RetryPolicies.constantDelay(1.seconds),
//      onError = FlexAPI.logError _
//    )(retryProg)
//    foo.attempt map {
//      case Left(err) => scribe.info(s"OK Failed As Expectd, Last Error: ${oprint(err)}")
//      case Right(v)  => fail(s"Should have been an error on reties but got ${oprint(v)}")
//    }
//
//  }
