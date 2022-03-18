package com.odenzo.ibkr.web.flexquery

import cats.effect.*

import com.odenzo.ibkr.web.flexquery.network.{FlexContext, Clients}
import munit.*
import munit.CatsEffectAssertions.assume
import org.http4s.client.Client

val inCI = {
  val env    = scala.sys.env.get("IS_CI")
  val prop   = scala.sys.props.get("CI")
  scribe.info(s"CI Config: $env $prop")
  val amInCI = env.contains("true") || prop.isDefined
  scribe.error(s"****** Am IN CI: $amInCI")
  amInCI
}

/** Base testing trait that includes Cats Support and Assertions */
trait BaseTest extends CatsEffectSuite {}

/** Integration tests which are ignored if `isCI` environment variable is defined. */
trait IntegrationTest extends BaseTest:
  override def munitIgnore: Boolean = inCI

trait RestTest extends IntegrationTest:

  val flexToken: Option[String] = scala.sys.env.get("IBKR_FLEX_TOKEN")

  /** Test failure is used and not defined. */
  lazy val context: FlexContext =
    Assertions.assert(flexToken.isDefined)
    FlexContext(flexToken.get)

  val clientR: Resource[IO, Client[IO]] = Clients.loggingRedirectsClient(true, true)

  /** A prefconfigured FlexQuery that returns XML data */
  val xmlQuery = "643709" // YTD Dividends
  val csvQuery = "645780" // YTD PL including headers, trailers, columnss etc.
  val textPipe = "655930" // Misc

final val include = new munit.Tag("include")
final val exclude = new munit.Tag("exclude")
