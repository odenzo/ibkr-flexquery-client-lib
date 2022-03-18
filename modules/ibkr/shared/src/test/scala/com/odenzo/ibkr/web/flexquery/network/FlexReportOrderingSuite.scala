package com.odenzo.ibkr.web.flexquery.network

import cats.effect.*
import cats.syntax.all.*
import com.odenzo.ibkr.*
import com.odenzo.ibkr.web.flexquery.{FlexAPI, RestTest}
import munit.*
import org.http4s.client.Client
import org.junit.experimental.categories.Category
import retry.{RetryPolicies, retryingOnSomeErrors}

import scala.concurrent.duration.*
import scala.xml.Elem

class FlexReportOrderingSuite extends RestTest:
  override def munitIgnore: Boolean = false
  val xmlprint                      = new scala.xml.PrettyPrinter(80, 4)
  given FlexContext                 = context

  test("RequestBadNumber") {
    scribe.info("OK")
    clientR.use {
      client =>
        given Client[IO] = client
        FlexReportOrdering.reportRequestApp("666").flatTap { ticketNum => IO(scribe.info(s"Ticket Number $ticketNum")) }
    }
  }

  test("XML") {
    clientR.use {
      client =>
        given Client[IO] = client
        FlexReportOrdering.reportRequestApp(xmlQuery).flatTap { ticketNum => IO(scribe.info(s"Ticket Number $ticketNum")) }
    }
  }
//  test("IT=Pickup Existing") {
//    val ref                               = "4108446371"
//    val theTest: com.odenzo.ibkr.FlexTest = {
//      for {
//        noretry <- FlexAPI.pickupFlexReport(ref)
//        _        = scribe.info(s"${oprint(noretry)}")
//      } yield ()
//    }
//    testFlexReportApi(theTest)
//
//    // Even better
//    testFlexReportApi {
//      for {
//        noretry <- FlexAPI.pickupFlexReport(ref)
//        _        = scribe.info(s"${oprint(noretry)}")
//      } yield ()
//    }
//  }

//  test("IT-RT") {
//    testFlexReportApi {
//      val queryId = "645780"
//      for {
//        report <- FlexAPI.requestAndGetReport(queryId, 5, 10.seconds)
//        _       = scribe.info(s"Report: \n ${xmlprint.formatNodes(report)}")
//
//      } yield ()
//    }
//  }
