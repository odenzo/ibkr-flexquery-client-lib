package com.odenzo.ibkr.web.flexquery

import cats.effect.*
import com.odenzo.ibkr.web.flexquery.network.{Clients, FlexContext}
import munit.*
import munit.CatsEffectAssertions.assume
import org.http4s.client.Client

/** Integration tests which are ignored if `isCI` environment variable is defined. */
trait IntegrationTest extends BaseTest:
  val inCI: Boolean                 = {
    val env    = scala.sys.env.get("IS_CI")
    val prop   = scala.sys.props.get("CI")
    scribe.info(s"CI Config: $env $prop")
    val amInCI = env.contains("true") || prop.isDefined
    scribe.warn(s"****** Am IN CI: $amInCI")
    amInCI
  }
  override def munitIgnore: Boolean = inCI
