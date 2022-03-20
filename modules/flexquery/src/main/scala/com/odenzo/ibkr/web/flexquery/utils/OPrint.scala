package com.odenzo.ibkr.web.flexquery.utils

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*

import pprint.{PPrinter, Tree}

/**
  * This is a custom implementation of pprint. It has special handling for Circe Json and JsonObject and Secret case class. Secret is
  * automatically masked and JSON PrintyPrinted.
  * @example
  *   \```scala case class XX(a:Int, b:Int) val v = XX(1,2) scala logger.info(s"${oprint(v)} \```
  */

def oprint[A](a: A): String = pp.apply(a, 120, 10000).render

def secretHandler(a: Any): Option[Tree] =
  a match
    case a: Secret => pprint.Tree.Literal(f"Secret: ${a.toString}").some
//    case a: JsonObject => pprint.Tree.Literal(f"JsonObject: ${a.asJson.spaces4}").some
//    case a: Json       => pprint.Tree.Literal(f"Json: ${a.asJson.spaces4}").some

    case _ => Option.empty[Tree]

val pp =
  new PPrinter(
    defaultWidth = 100, // Because often after logback prefix
    defaultHeight = 1000,
    defaultIndent = 2,
    additionalHandlers = (secretHandler _).unlift,
    colorLiteral = fansi.Color.Yellow ++ fansi.Bold.On,
    colorApplyPrefix = fansi.Color.Magenta ++ fansi.Bold.On
  )
