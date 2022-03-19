package com.odenzo.ibkr.web.flexquery.network

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.{given, *}
import com.odenzo.ibkr.web.base.*
import com.odenzo.ibkr.web.flexquery.errors.*
import com.odenzo.ibkr.web.flexquery.parsing.*
import com.odenzo.ibkr.web.base.OPrint.*
import io.circe.Decoder
import org.http4s.*
import org.http4s.Method.*
import org.http4s.client.Client
import org.http4s.client.dsl.io.*
import org.http4s.syntax.all.*
import retry.*

import scala.concurrent.duration.*
import scala.util.control.NoStackTrace
import scala.xml.Elem

/** Orders an existing FlexQuery report and gets the referenceCode/ticket back to pickup actual report later. */
object FlexReportOrdering {

  /** Simple call with token to reqyest a report. No retries or waiting etc. */
  def reportRequestApp(flexQueryId: String)(using client: Client[IO], ctx: FlexContext): IO[String] =
    val rq: Request[IO] = Request(
      Method.GET,
      uri = (ctx.baseUri / "FlexStatementService.SendRequest").withQueryParam("q", flexQueryId)
    )

    // No XML Handling in ScalaJS version of HTTP4S so do by hand with fs2-data
    val resultXmlStr: IO[String] = client.expect[String](rq).flatTap(s => IO(scribe.info(s"Result XML ${oprint(s)}")))
    // We need to check for errors or get the "ticket number"
    resultXmlStr.flatMap(FlexStatementRs.fromXml(_)).flatMap {
      case FlexStatementRs.FlexStatementSuccessRs(status, referenceCode)   => referenceCode.pure
      case FlexStatementRs.FlexStatementErrorRs(status, errorCode, errMsg) => IO.raiseError(BadResponseStatus(status, errorCode, errMsg))
    }
}
