package com.odenzo.ibkr.web.flexquery.parsing

import cats.effect.*
import cats.effect.syntax.all.*

import cats.*
import cats.data.*
import cats.syntax.all.*

import cats.effect.IO
import fs2.data.xml.*
import fs2.data.xml.XmlEvent.*
import scala.util.chaining.{given, *}
import com.odenzo.ibkr.web.flexquery.parsing.FlexStatementRs.FlexStatementErrorRs

enum FlexStatementRs:
  case FlexStatementSuccessRs(status: String, referenceCode: String)        extends FlexStatementRs
  case FlexStatementErrorRs(status: String, errorCode: Int, errMsg: String) extends FlexStatementRs

/** This is returned when ordering a report, and when picking up the report to convey warnings and errors */
object FlexStatementRs extends XMLParsing:

  /** A Scala JS/JVM compliant hack. We know - Possibly raises errors but not normally. */
  def fromXml(xmlString: String): IO[FlexStatementRs] =
    parseToEvents(xmlString).map {
      elems =>
        firstTagsText("Status", elems) match {
          case "Success" =>
            val ticket = firstTagsText("ReferenceCode", elems)
            FlexStatementSuccessRs("success", ticket)
          case st        =>
            val errorCode = firstTagsText("ErrorCode", elems).toInt
            val msg       = firstTagsText("ErrorMessage", elems)
            FlexStatementErrorRs(st, errorCode, msg)
        }
    }

  /**
    * Ensures given string is XML. If yes, checks if it is an error response. Tries to use the new x | y type.
    * @returns
    *   None if not XML, Some Left if its error message or Some Right with original validated XML String. This is oddity because don't have
    *   a decent x-platform XML DOM worked out yet.
    */
  def sniffXml(x: String): IO[Either[FlexStatementErrorRs, String]] = parseToEvents(x).map {
    evs =>
      firstTagsText("Status", evs) match {
        case status if status.isBlank => x.asRight
        case status                   =>
          val errorCode = firstTagsText("ErrorCode", evs).toInt
          val msg       = firstTagsText("ErrorMessage", evs)
          FlexStatementErrorRs(status, errorCode, msg).asLeft
      }
  }
