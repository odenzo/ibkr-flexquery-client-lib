package com.odenzo.ibkr.web.flexquery.network

import cats.effect.*
import org.http4s.client.{Client, middleware}
import org.http4s.ember.client.EmberClientBuilder

/**
  * Factory methods to generate the underlying HTTP4S Client You can use your own client instead. Must be Ember client though for SCalaJ
  * Usage.
  */
object Clients:

  /** Make the resource for use somewhere else, this makes */
  def loggingRedirectsClient(logHeaders: Boolean = true, logBody: Boolean = false)(using Async[IO]): Resource[IO, Client[IO]] =
    import org.http4s.client.middleware.*
    val baseClient = EmberClientBuilder.default[IO].build

    baseClient.map(c => middleware.FollowRedirect(4)(c))
      .map(c => middleware.Logger(logHeaders = true, logBody = true)(c))
