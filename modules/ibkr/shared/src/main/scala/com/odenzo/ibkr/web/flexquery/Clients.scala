package com.odenzo.ibkr.web.flexquery

import cats.effect.{Async, IO, Resource}
import org.http4s.client.{Client, middleware}
import org.http4s.ember.client.EmberClientBuilder

/**
  * Factory methods to generate the underlying HTTP4S Clients which are delegated to. Basically a chance to introduce middleware of change
  * from Blaze to come other client.
  */
object Clients:

  /** Make the resource for use somewhere else, this makes */
  def loggingRedirectsClient(logHeaders: Boolean = true, logBody: Boolean = false)(using Async[IO]): Resource[IO, Client[IO]] =
    import org.http4s.client.middleware.*
    val baseClient: Nothing = EmberClientBuilder.default[IO]

    baseClient.map(c => middleware.FollowRedirect(4)(c))
      .map(c => middleware.Logger(logHeaders = true, logBody = true)(c))
