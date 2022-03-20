package com.odenzo.ibkr.web.flexquery

import cats.effect.*
import com.odenzo.ibkr.web.flexquery.network.Clients
import munit.*
import munit.CatsEffectAssertions.assume
import org.http4s.client.Client

/** Integration tests which are ignored if `isCI` environment variable is defined. */
trait IntegrationTest extends BaseTest {
  override def munitIgnore: Boolean = inCI
}
