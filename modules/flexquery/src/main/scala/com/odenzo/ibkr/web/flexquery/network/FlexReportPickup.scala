package com.odenzo.ibkr.web.flexquery.network

import cats.*
import cats.data.*
import cats.effect.*
//import cats.effect.syntax.all.*
import cats.syntax.all.*
import com.odenzo.ibkr.web.flexquery.utils.*

import org.http4s.*
import org.http4s.Method.*
import org.http4s.client.Client
import org.http4s.Headers.{*, given}
import org.http4s.client.dsl.io.*
import org.http4s.syntax.all.*
import retry.*
import retry.RetryDetails.*
import retry.syntax.all.*
import com.odenzo.ibkr.web.flexquery.errors.*
import com.odenzo.ibkr.web.flexquery.parsing.FlexStatementRs
import com.odenzo.ibkr.web.flexquery.modelling.{FlexReport, *}

import scala.concurrent.duration.*
import scala.util.control.NoStackTrace
import scala.xml.Elem
import scala.xml.parsing.FatalError
import com.odenzo.ibkr.web.flexquery.parsing.FlexStatementRs.FlexStatementErrorRs

/** This is a bid more rigid then the other APIs, since simpler. Just use this object with Client[IO] and FlexContext */
object FlexReportPickup {

  /** ALways returns 200. Status only present for Warn / Error not success it seems. */
  def reportPickupRq(referenceCode: String)(using ctx: FlexContext): Request[IO] =
    Request(GET, uri = (ctx.baseUri / "FlexStatementService.GetStatement").withQueryParam("q", referenceCode))

  val maxAttempts: Int      = 10
  val delay: FiniteDuration = 10.seconds

  def sniffDelimeted(v: String): IO[FlexReportFormat] = {
    import com.odenzo.ibkr.web.flexquery.modelling.FlexReportFormat.*
    val delims: List[(Char, FlexReportFormat)] = List(',' -> CSV, '\t' -> TABBED_TEXT, '|' -> PIPED_TEXT)

    // Yes this is atrocious, at least doing first line only
    val firstLine                                    = v.takeWhile(c => c != '\n')
    scribe.info(s"FirstLine: $firstLine")
    val found: List[((Char, FlexReportFormat), Int)] = delims.flatMap {
      d =>
        val i = firstLine.indexOf(d._1)
        if i < 0 then None else Some(d, i)
    }

    if found.isEmpty then IO.raiseError(MalformedResponse("Could not determined delimineted file format"))
    else found.minBy(_._2)._1._2.pure
  }

  /** A single attempt to pickup a report alreadt ordered. Expected raised error ReportGenerationInProgres */
  def reportPickup(referenceCode: String)(using client: Client[IO], ctx: FlexContext): IO[FlexReport] = {
    /* Generally this will return a response in the sselected format on web site for flexquery, but error seeem to come in XML always.
     *  We don't deal with succes body in general btw.

     * 200 Content-Type: text/xml;charset=ISO-8859-1, and FlexStatementResponse returned if report not ready, definately retry
     * 200CT: Text/plain for CSV data.  Slack... gonna have to sniff it.
    text/xml type for success XML reports.
    **/
    client.run(reportPickupRq(referenceCode)).use {
      rs =>
        IO.raiseUnless(rs.status.isSuccess)(BadHttpResponse(rs.status.code, rs.status.toString))
        val media: IO[MediaType] = rs.contentType
          .map((v: org.http4s.headers.`Content-Type`) => v.mediaType)
          .fold(IO.raiseError(BadContentType("NoContentType for ReportPickup")))(v => v.pure)

        for {
          bodyText  <- rs.bodyText.compile.string
          mediaType <- media
          report    <- mediaType match {
                         case MediaType.text.xml => FlexStatementRs.sniffXml(bodyText).map(xml => FlexReport(FlexReportFormat.XML, xml))
                         case _                  => sniffDelimeted(bodyText).map(t => FlexReport(t, bodyText))
                       }
        } yield report
    }
  }

  /** This fetches a report, waiting up to 10 minutes for it to be available. */
  def fetchReport(referenceCode: String)(using client: Client[IO], ctx: FlexContext): IO[FlexReport] = {
    val action: IO[FlexReport] = reportPickup(referenceCode)

    val policy: RetryPolicy[IO] = RetryPolicies.limitRetries[IO](maxAttempts).join(RetryPolicies.exponentialBackoff[IO](delay))

    val logit: (Throwable, RetryDetails) => IO[Unit] =
      (res: Throwable, details: RetryDetails) => IO(scribe.warn(s"Failure In Retry: $details because of ${oprint(res)}"))

    def retryWhen(e: Throwable): IO[Boolean] = e match {
      case e: ReportGenerationInProgress => IO.pure(true)
      case _                             => IO.pure(false)
    }

    retryingOnSomeErrors[FlexReport](
      isWorthRetrying = retryWhen _,
      policy = policy,
      onError = logit
    )(action)

  }
}
