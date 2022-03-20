package com.odenzo.ibkr.web.flexquery.utils

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*

import scala.util.Random

/** Note: User oprint instead of pprint to continue masking */
case class Secret(secret: String):
  override def toString = s"${secret.take(2)}...${secret.takeRight(2)}"

object Secret:

  def generatePassword(len: Int = 15): String = Random.nextString(len)
  def generate: Secret                        = Secret(generatePassword())
  def generate(len: Int = 15): Secret         = Secret(generatePassword(len))

case class LoginCreds(user: String, password: Secret)

object LoginCreds:
  def genForUser(user: String): LoginCreds = LoginCreds(user, Secret.generate(15))
