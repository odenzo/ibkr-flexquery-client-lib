package com.odenzo.ibkr.web.flexquery

import cats.effect.*
import cats.effect.syntax.all.*
import cats.*
import cats.data.*
import cats.syntax.all.*
import com.odenzo.ibkr.web.base.*
import com.odenzo.ibkr.web.base.OPrint.*
import io.circe.Decoder

import retry.*
import org.http4s.Method.*
import org.http4s.client.*
import scala.concurrent.duration.*
import scala.util.control.NoStackTrace
import scala.xml.Elem
import network.*

/** This is a bid more rigid then the other APIs, since simpler. Just use this object with Client[IO] and FlexContext */
object FlexAPI {

  def requestAndGetReport(queryId: String, attempts: Int = 5, delay: FiniteDuration = 1.minutes)(
      using client: Client[IO],
      ctx: FlexContext,
      io: Async[IO]
  ): IO[Elem] = IO.raiseError(Throwable("Not Implemented"))

}
