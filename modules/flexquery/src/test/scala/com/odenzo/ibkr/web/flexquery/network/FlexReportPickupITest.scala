package com.odenzo.ibkr.web.flexquery.network

import cats.effect.*
import cats.effect.syntax.all.*
import cats.*
import cats.data.*
import cats.syntax.all.*
import com.odenzo.ibkr.web.flexquery.modelling.{FlexReport, FlexReportFormat}
import com.odenzo.ibkr.web.flexquery.parsing.FlexStatementRs
import com.odenzo.ibkr.web.flexquery.{BaseTest, RestTest}
import munit.FunSuite

import scala.concurrent.duration.*

class FlexReportPickupITest extends RestTest {
  import munit.CatsEffectAssertions.*

  override val munitTimeout: FiniteDuration = 10.minutes
  test("CSV") {
    val id = textPipe
    clientR.use {
      client =>
        scribe.debug("Running test in vcode works?")
        val result: IO[FlexReport] = for {
          order  <- FlexReportOrdering.reportRequestApp(id)(using client, context)
          _      <- IO.sleep(2.minutes)
          report <- FlexReportPickup.reportPickup(order)(using client, context)
        } yield report

        result.flatTap(r => IO(scribe.info(s"$r")))
//          .map {
//            case Left(errorRs: FlexStatementRs) => fail(s"Unexpected Error: $errorRs")
//            case Right(report)                  => assertEquals(report.format, FlexReportFormat.PIPED_TEXT)
//          }

    }
  }

  test("CSV-Retry".only) {
    val id = csvQuery
    clientR.use {
      client =>
        scribe.debug("Running test in vcode works?")
        val result: IO[FlexReport] = for {
          order  <- FlexReportOrdering.reportRequestApp(id)(using client, context)
          report <- FlexReportPickup.fetchReport(order)(using client, context)
        } yield report

        result.flatTap(r => IO(scribe.info(s"$r")))
      //          .map {
      //            case Left(errorRs: FlexStatementRs) => fail(s"Unexpected Error: $errorRs")
      //            case Right(report)                  => assertEquals(report.format, FlexReportFormat.PIPED_TEXT)
      //          }

    }
  }

}
