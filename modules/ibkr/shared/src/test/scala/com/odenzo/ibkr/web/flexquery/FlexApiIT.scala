package com.odenzo.ibkr.web.flexquery

import cats.effect.*
import cats.syntax.all.*
import com.odenzo.ibkr.*
import com.odenzo.ibkr.web.flexquery.FlexAPI
import com.odenzo.ibkr.webutils.base.OPrint.oprint
import com.odenzo.ibkr.webutils.base.http4s.*
import munit.*
import org.http4s.client.Client
import org.junit.experimental.categories.Category
import retry.{RetryPolicies, retryingOnSomeErrors}

import scala.concurrent.duration.*
import scala.xml.Elem

class FlexApiIT extends RestTest:
  override def munitIgnore: Boolean = true
  val xmlprint                      = new scala.xml.PrettyPrinter(80, 4)

  test("IT=Pickup Existing") {
    val ref                               = "4108446371"
    val theTest: com.odenzo.ibkr.FlexTest = {
      for {
        noretry <- FlexAPI.pickupFlexReport(ref)
        _        = scribe.info(s"${oprint(noretry)}")
      } yield ()
    }
    testFlexReportApi(theTest)

    // Even better
    testFlexReportApi {
      for {
        noretry <- FlexAPI.pickupFlexReport(ref)
        _        = scribe.info(s"${oprint(noretry)}")
      } yield ()
    }
  }

  test("IT-RT") {
    testFlexReportApi {
      val queryId = "645780"
      for {
        report <- FlexAPI.requestAndGetReport(queryId, 5, 10.seconds)
        _       = scribe.info(s"Report: \n ${xmlprint.formatNodes(report)}")

      } yield ()
    }
  }
