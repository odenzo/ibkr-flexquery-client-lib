package com.odenzo.ibkr.web.flexquery

import cats.effect.*
import com.odenzo.ibkr.web.flexquery.network.FlexContext
import munit.*

/** Base testing trait that includes Cats Support and Assertions */
trait BaseTest extends CatsEffectSuite {
  import munit.CatsEffectAssertions.*

  val xmlprint      = new scala.xml.PrettyPrinter(80, 4)
  final val include = new munit.Tag("include")
  final val exclude = new munit.Tag("exclude")

  val flexToken: Option[String] = scala.sys.env.get("IBKR_FLEX_TOKEN")

  /** Test failure is used and not defined. */
  lazy val context: FlexContext =
    Assertions.assert(flexToken.isDefined)
    FlexContext(flexToken.get)

}
