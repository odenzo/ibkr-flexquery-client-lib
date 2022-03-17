package com.odenzo.ibkr.web.flexquery

import cats.effect.*
import cats.effect.syntax.all.*
import cats.*
import cats.data.*
import cats.syntax.all.*
import FlexAPI.extractChildText
import com.odenzo.ibkr.web.models.flex.errors.{MalformedResponse, WarnStatus}
import com.odenzo.ibkr.web.base.*
import com.odenzo.ibkr.web.base.OPrint.*
import com.odenzo.ibkr.web.flexquery.FlexContext.baseUri
import io.circe.Decoder
import org.http4s.*
import org.http4s.client.Client
import org.http4s.client.dsl.io.*
import org.http4s.syntax.all.{*, given}
import retry.*
import org.http4s.Method.*
import scala.concurrent.duration.*
import scala.util.control.NoStackTrace
import scala.xml.Elem

/** This is a bid more rigid then the other APIs, since simpler. Just use this object with Client[IO] and FlexContext */
object FlexAPI {

  def requestAndGetReport(queryId: String, attempts: Int = 5, delay: FiniteDuration = 1.minutes)(
      using client: Client[IO],
      ctx: FlexContext,
      io: Async[IO]
  ): IO[Elem] =
    for {
      ref    <- orderFlexReport(queryId)
      _       = scribe.info(s"Got Ref $ref")
      report <- retryingOnSomeErrors(
                  isWorthRetrying = shouldRetry _,
                  policy = RetryPolicies.limitRetries(attempts) join RetryPolicies.exponentialBackoff(delay),
                  onError = logError _
                )(pickupFlexReport(ref))
      _       = scribe.info("GOT REPORT")
    } yield report

  /** Assumes only 0 or 1 named element */
  private def extractChildText(root: Elem, childName: String): Option[String] = (root \ childName).headOption.map(_.text)

  def errorM(root: Elem): IO[(Int, String)] = IO.delay {
    scribe.info(s"Executing Error M in Delay")
    val code: Option[Int]   = extractChildText(root, "ErrorCode").map(_.toInt)
    val msg: Option[String] = extractChildText(root, "ErrorMessage")
    code.zip(msg)
  }.flatMap(IO.fromOption(_)(NoErrorInfo))

}
