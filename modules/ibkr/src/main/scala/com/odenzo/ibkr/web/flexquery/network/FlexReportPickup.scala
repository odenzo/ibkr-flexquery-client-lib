package com.odenzo.ibkr.web.flexquery.network

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.IO.{IOCont, Uncancelable}
import cats.effect.syntax.all.*
import cats.syntax.all.*
import com.odenzo.ibkr.web.base.*
import com.odenzo.ibkr.web.base.OPrint.*
import io.circe.Decoder
import org.http4s.*
import org.http4s.Method.*
import org.http4s.client.Client
import org.http4s.Headers.{given, *}
import org.http4s.client.dsl.io.*
import org.http4s.syntax.all.*
import retry.*
import com.odenzo.ibkr.web.flexquery.errors.*
import com.odenzo.ibkr.web.flexquery.parsing.FlexStatementRs
import com.odenzo.ibkr.web.flexquery.modelling.*
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

  val maxAttempts = 10
  val delay       = 10.seconds
  val retryPolicy = RetryPolicies.limitRetries[IO](maxAttempts).join(RetryPolicies.exponentialBackoff[IO](delay))

  def shouldRetry(err: Throwable): IO[Boolean]                     = {
    scribe.info(s"Worth Retrying  ${oprint(err)}")
    err match
      case other => false.pure
  }
  def logError(e: Throwable, retryDetails: RetryDetails): IO[Unit] = IO(scribe.warn(s"Retry $retryDetails because of ${oprint(e)}"))

  def sniffDelimeted(v: String): IO[FlexReportFormat] = {
    import com.odenzo.ibkr.web.flexquery.modelling.FlexReportFormat.*
    val delims: List[(Char, FlexReportFormat)] = List(',' -> CSV, '\t' -> TABBED_TEXT, '|' -> PIPED_TEXT)

    // Yes this is atrocious, at least doing first line only
    val firstLine                            = v.takeWhile(c => c != '\n')
    val min: ((Char, FlexReportFormat), Int) = delims.fproduct(d => firstLine.indexOf(d._1)).minBy(_._2)
    if min._2 < 0 then IO.raiseError(MalformedResponse("Could not determined delimineted file format"))
    else min._1._2.pure
  }

  /** A single attempt to pickup a report alreadt ordered. */
  def reportPickup(referenceCode: String)(using client: Client[IO], ctx: FlexContext): IO[Either[FlexStatementErrorRs, FlexReport]] = {
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
          report      <- mediaType match {
                         case MediaType.text.xml =>                          FlexStatementRs.sniffXml(bodyText).map(eether =>  eether.map(v => FlexReport(FlexReportFormat.XML,v)))
                         case _                  =>                            sniffDelimeted(bodyText).map(t => FlexReport(t, bodyText).asRight)

                       }
        } yield report
    }
  }
}

/** PIcks up a flex report, there is currently no retry implemented, ReportGenerationInProgress can be retried. */
// def retryingPickup(referenceCode: String)(using client: Client[IO], ctx: FlexContext): IO[Elem] =
//   for {
//     _            <- IO(scribe.info(s"Picking Up Flex Report for $referenceCode"))
//     report       <- retryingOnSomeErrors(
//                       isWorthRetrying = shouldRetry _,T
//                       onError = logError _
//                     )(reportPickup(referenceCode))
//     report: Elem <- client.expect[Elem]()
//     _             = scribe.info(s"Resulting XML Respone on Pickup: $report.")
//     v            <- dealWithErrors(report) // Will raise exception here
//   } yield report

// /** Assumes only 0 or 1 named element */
// private def extractChildText(root: Elem, childName: String): Option[String] = (root \ childName).headOption.map(_.text)

// def errorM(root: Elem): IO[(Int, String)] = IO.delay {
//   scribe.info(s"Executing Error M in Delay")
//   val code: Option[Int]   = extractChildText(root, "ErrorCode").map(_.toInt)
//   val msg: Option[String] = extractChildText(root, "ErrorMessage")
//   code.zip(msg)
// }.flatMap(IO.fromOption(_)(NoErrorInfo))

// /** Raises any errors or plain Unit if successful */
// def dealWithErrors(root: Elem): IO[Elem] =
//   for {
//     status <- IO(extractChildText(root, "Status").getOrElse("Success")) // No Status on success for result, boo
//     _       = scribe.info(s"Result Status: $status")
//     res    <- status match
//                 case "Success" => IO.pure(root)
//                 case "Warn"    =>
//                   scribe.info(s"WARNING")
//                   errorM(root).map((x: (Int, String)) => WarnStatus.apply(x._1, x._2))
//                     .flatTap(e => IO(scribe.warn(s"Raising Warning: ${oprint(e)}")))
//                     .flatMap(IO.raiseError[Elem])
//                 case "Error"   =>
//                   errorM(root).map((x: (Int, String)) => ErrorStatus.apply(x._1, x._2))
//                     .flatTap(e => IO(scribe.warn(s"Raising Error: ${oprint(e)}")))
//                     .flatMap(IO.raiseError[Elem](Throwable("Some Error")))
//                 case other     => IO.raiseError[Elem](MalformedResponse(s"Invalid Response $root - Status"))
//   } yield res
