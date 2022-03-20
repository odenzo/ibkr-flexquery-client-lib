package com.odenzo.ibkr.web.flexquery

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*
import com.odenzo.ibkr.web.flexquery.modelling.FlexReport
import com.odenzo.ibkr.web.flexquery.network.*
import io.circe.Decoder
import org.http4s.Method.*
import org.http4s.client.*
import retry.*

import scala.concurrent.duration.*
import scala.util.control.NoStackTrace
import scala.xml.Elem

/** This is a bid more rigid then the other APIs, since simpler. Just use this object with Client[IO] and FlexContext */
object FlexAPI {

  def fetchReport(flexQueryId: String, flexToken: String): IO[FlexReport] = {
    given FlexContext = FlexContext(flexToken)

    Clients.loggingRedirectsClient(true, false).use {
      client => // Nothing special about client, roll your own if want.
        given Client[IO] = client
        for {
          referenceCode <- FlexReportOrdering.reportRequestApp(flexQueryId)
          report        <- FlexReportPickup.fetchReport(referenceCode)
        } yield report
    }
  }

}
