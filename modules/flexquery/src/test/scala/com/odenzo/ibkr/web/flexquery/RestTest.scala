package com.odenzo.ibkr.web.flexquery

import cats.effect.*
import com.odenzo.ibkr.web.flexquery.network.Clients
import munit.*
import munit.CatsEffectAssertions.assume
import org.http4s.client.Client

trait RestTest extends IntegrationTest:

  val clientR: Resource[IO, Client[IO]] = Clients.loggingRedirectsClient(true, true)

  /** A prefconfigured FlexQuery that returns XML data */
  val xmlQuery = "643709" // YTD Dividends
  val csvQuery = "645780" // YTD PL including headers, trailers, columnss etc.
  val textPipe = "655930" // Misc
