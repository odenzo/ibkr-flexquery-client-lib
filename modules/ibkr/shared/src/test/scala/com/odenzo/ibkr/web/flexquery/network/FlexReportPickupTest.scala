package com.odenzo.ibkr.web.flexquery.network

import cats.effect.*
import cats.effect.syntax.all.*

import cats.*
import cats.data.*
import cats.syntax.all.*

import com.odenzo.ibkr.web.flexquery.{BaseTest, RestTest}
import munit.FunSuite
import scala.concurrent.duration.*

class FlexReportPickupTest extends RestTest {
  override val munitTimeout: FiniteDuration = 10.minutes
  test("CSV") {
    val id = textPipe
    clientR.use {
      client =>
        for {
          order  <- FlexReportOrdering.reportRequestApp(id)(using client, context)
          _      <- IO.sleep(4.minutes)
          report <- FlexReportPickup.reportPickup(order)(using client, context)
        } yield report
    }
  }

}
