package com.odenzo.ibkr.web.flexquery.network

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*
import com.odenzo.ibkr.web.base.*
import com.odenzo.ibkr.web.base.OPrint.*
import com.odenzo.ibkr.web.models.flex.errors.{MalformedResponse, WarnStatus}
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
import scala.xml.parsing.FatalError

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
      case WarnStatus(1019, _) => true.pure // This is "not ready" error code with warning status
      case other               => false.pure
  }
  def logError(e: Throwable, retryDetails: RetryDetails): IO[Unit] = IO(scribe.warn(s"Retry $retryDetails because of ${oprint(e)}"))

  def reportPickup(referenceCode: String)(using client: Client[IO], ctx: FlexContext) = {
    client.run(reportPickupRq(referenceCode)).use {
      rs =>
        IO.raiseUnless(rs.status.isSuccess)(BadHttpResponse(rs.status.toString))
        val contentType = rs.contentType
        rs.bodyText.compile.string.flatMap {
          body =>
            IBFlexError.parse(body) match {
              case None      => body.pure
              case Some(msg) => IO.raiseError(msg)
            }
        }

    }
  }

  /** PIcks up a flex report, there is currently no retry implemented, ReportGenerationInProgress can be retried. */
  // def retryingPickup(referenceCode: String)(using client: Client[IO], ctx: FlexContext): IO[Elem] =
  //   for {
  //     _            <- IO(scribe.info(s"Picking Up Flex Report for $referenceCode"))
  //     report       <- retryingOnSomeErrors(
  //                       isWorthRetrying = shouldRetry _,
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

}
