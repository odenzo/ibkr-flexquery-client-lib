package com.odenzo.ibkr.web.flexquery

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*
import com.odenzo.ibkr.web.base.*
import com.odenzo.ibkr.web.base.OPrint.*
import com.odenzo.ibkr.web.flexquery.FlexAPI.extractChildText
import com.odenzo.ibkr.web.flexquery.FlexContext.baseUri
import com.odenzo.ibkr.web.models.flex.errors.{MalformedResponse, WarnStatus}
import io.circe.Decoder
import org.http4s.*
import org.http4s.Method.*
import org.http4s.client.Client
import org.http4s.client.dsl.io.*
import org.http4s.syntax.all.{*, given}
import retry.*

import scala.concurrent.duration.*
import scala.util.control.NoStackTrace
import scala.xml.Elem

/** This is a bid more rigid then the other APIs, since simpler. Just use this object with Client[IO] and FlexContext */
object FlexReportOrdering {

  /** Simple call with token to reqyest a report. No retries or waiting etc. */
  def reportRequestApp(flexQueryId: String)(using client: Client[IO], ctx: FlexContext): IO[String] =
    val rq                       = Request(
      Method.GET,
      uri = (baseUri / "FlexStatementService.SendRequest").withQueryParam("t", ctx.token).withQueryParam("q", flexQueryId)
    )
    // We expect an XML String Back, but we have no ScalaJS x-platform parser
    // Isn't that strange, I think brower has XML parsser, and of course scala-xml has JDK parser
    val resultXmlStr: IO[String] = client.expect[String]
    // We need to check for errors or get the "ticket number"
    // refCode <- IO.fromOption(extractChildText(ticket, "ReferenceCode"))(MalformedResponse("No Valid Refcode"))
    val ticket                   = "obladi".pure
    ticket
}