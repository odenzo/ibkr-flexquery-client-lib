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
