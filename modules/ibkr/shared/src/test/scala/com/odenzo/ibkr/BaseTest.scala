package com.odenzo.ibkr

import munit.CatsEffectSuite
import munit.*
import cats.effect.*
import com.odenzo.ibkr.web.flexquery.FlexContext
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

  val client: Resource[IO, RestClient]                                   = IbkrPortal.asResource
  val fixtureArgs: Resource[IO, (Client[IO], RestClient, PortalContext)] = IbkrPortal.asResourceTriplet(config)

  /**
    * An experiment using Context functions, I like it!
    * @param fn
    *   Function that will get a Client and FlexContext implicitly inserted. It is reponsible for asserting in IO and will be run
    *   automatically by the test framework
    */
  def testFlexReportApi(fn: FlexTest): IO[Unit] =
    Clients.loggingRedirectsClient().use {
      client =>
        given flex: FlexContext = getFlexContext
        fn(using client)
    }

  def testPortalApi(fn: PortalTest): IO[Unit] =
    fixtureArgs.use { (client, restClient, context) => fn(using client, restClient, context) }

  def getFlexContext: FlexContext = scala.sys.env.get("IBKR_FLEX_TOKEN") match
    case Some(value) => FlexContext(value)
    case None        => fail("No IBKR_FLEX_TOKEN environment variable found")

  val ensureLoggedIn: PortalTest = SessionCalls.ssoValidate().expect.map(rs => assume(rs.loggedIn, "Not Logged In"))

  /** Idea was to have a simple way to test sending a request and getting a response back in JSON, decoded and no erroors. */
  def genericTest[T](name: String, rq: ORequest[T])(using pc: PortalContext, rc: RestClient): IO[T] =
    rq.debug.flatTap(model => IO(scribe.debug(s"$name Generic Test with Model: ${oprint(model)}")))
      .handleError {
        e =>
          scribe.error(s"Handline Error in Generic Test $name", e)
          throw e
      }

final val include = new munit.Tag("include")
final val exclude = new munit.Tag("exclude")
