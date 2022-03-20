package com.odenzo.ibkr.web.flexquery.network

import org.http4s.*
import org.http4s.client.*
import org.http4s.client.dsl.io.*
import org.http4s.dsl.*
import org.http4s.dsl.io.*
import org.http4s.syntax.all.*

/**
  * There is only one base endpooint for Flex API -- I think nothing about different logins (paper/live etc) But we do have a Token. This
  * token is "awlays" logged in, go to the website to issue a new one if needed.
  */
case class FlexContext(token: String) {
  val baseUri: Uri = uri"https://gdcdyn.interactivebrokers.com/Universal/servlet/"
    .withQueryParam("v", 3)
    .withQueryParam("t", token)
}
