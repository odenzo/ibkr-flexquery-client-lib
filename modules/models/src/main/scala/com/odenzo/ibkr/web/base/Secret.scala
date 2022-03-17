package com.odenzo.ibkr.web.base

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*
import io.circe.*

import scala.util.Random

/** Note: User oprint instead of pprint to continue masking */
case class Secret(secret: String) derives Codec.AsObject:
  override def toString = s"${secret.take(2)}...${secret.takeRight(2)}"

object Secret:

  def generatePassword(len: Int = 15): String = Random.nextString(len)
  def generate: Secret                        = Secret(generatePassword())
  def generate(len: Int = 15): Secret         = Secret(generatePassword(len))

//implicit val fooDecoder: Decoder[Secret] = io.circe.derivation.semiauto.deriveDecoder[Secret]

// implicit  show: Show[Secret]          = Show.fromToString[Secret]

case class LoginCreds(user: String, password: Secret)

object LoginCreds:
  def genForUser(user: String) = LoginCreds(user, Secret.generate(15))
