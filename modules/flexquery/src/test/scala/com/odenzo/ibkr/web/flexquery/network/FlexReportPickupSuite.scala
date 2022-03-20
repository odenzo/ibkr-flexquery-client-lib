package com.odenzo.ibkr.web.flexquery.network

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*
import com.odenzo.ibkr.web.flexquery.errors.MalformedResponse
import com.odenzo.ibkr.web.flexquery.modelling.FlexReportFormat
import com.odenzo.ibkr.web.flexquery.{BaseTest, RestTest}

import munit.Assertions.*
import scala.concurrent.duration.*

class FlexReportPickupSuite extends BaseTest {
  override val munitTimeout: FiniteDuration = 10.minutes

  case class Scenario(s: String, check: Option[FlexReportFormat])

  List(
    Scenario("FOO,BAR,CSV", FlexReportFormat.CSV.some),
    Scenario("FOO|BAR|PIPED|", FlexReportFormat.PIPED_TEXT.some),
    Scenario("FOO\tBAR\tTABBED", FlexReportFormat.TABBED_TEXT.some),
    Scenario("", None),
    Scenario("FOOBARPIPED SPEC ISJF", None)
  ).foreach(check)

  def check(exp: Scenario)(implicit loc: munit.Location): Unit = {
    test(s"Sniffer ${exp.s}") {
      val res = FlexReportPickup.sniffDelimeted(exp.s).attempt.map {
        case Left(err: MalformedResponse) => assert(exp.check.isEmpty, err)
        case Left(err)                    => fail("Unexpected Error", err)
        case Right(f)                     => assertEquals(f.some, exp.check, "Wrong Format")
      }
    }
  }
}
