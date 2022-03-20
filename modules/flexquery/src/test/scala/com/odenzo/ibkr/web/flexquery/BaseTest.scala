package com.odenzo.ibkr.web.flexquery

import cats.effect.*
import com.odenzo.ibkr.web.flexquery.BaseTest.resetAllToLevel
import com.odenzo.ibkr.web.flexquery.modelling.FlexContext
import munit.*
import scribe.Level

/** Base testing trait that includes Cats Support and Assertions */
trait BaseTest extends CatsEffectSuite {

  /** This will run fo each test, oh well. */
  if BaseTest.inCI then resetAllToLevel(Level.Warn) else resetAllToLevel(Level.Debug)
  import munit.CatsEffectAssertions.*

  lazy val context  = BaseTest.context
  lazy val inCI     = BaseTest.inCI
  lazy val xmlprint = new scala.xml.PrettyPrinter(80, 4)
  lazy val include  = new munit.Tag("include")
  lazy val exclude  = new munit.Tag("exclude")

}

object BaseTest {
  lazy val inCI: Boolean = {
    val env    = scala.sys.env.get("IS_CI")
    val prop   = scala.sys.props.get("CI")
    scribe.info(s"CI Config: $env $prop")
    val amInCI = env.contains("true") || prop.isDefined
    scribe.warn(s"****** Am IN CI: $amInCI")
    true
  }

  def resetAllToLevel(level: Level = Level.Debug) = {
    scribe.Logger.root.clearHandlers().clearModifiers().withHandler(minimumLevel = Some(level)).replace()
  }

  lazy val flexToken: Option[String] = scala.sys.env.get("IBKR_FLEX_TOKEN")

  lazy val context: FlexContext =
    Assertions.assert(flexToken.isDefined)
    FlexContext(flexToken.get)

}
