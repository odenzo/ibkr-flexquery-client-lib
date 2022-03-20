package com.odenzo.ibkr.web.flexquery.parsing

import cats.effect.*
import cats.effect.syntax.all.*
import cats.*
import cats.data.*
import cats.syntax.all.*
import cats.effect.IO
import com.odenzo.ibkr.web.flexquery.errors.{BadResponseStatus, ReportGenerationInProgress}
import fs2.data.xml.*
import fs2.data.xml.XmlEvent.*

import scala.util.chaining.{*, given}
import com.odenzo.ibkr.web.flexquery.parsing.FlexStatementRs.FlexStatementErrorRs

/** Cannot make one of these extend Throwable unfortunatley. *Must& extend anum T */
enum FlexStatementRs:
  case FlexStatementSuccessRs(status: String, referenceCode: String) extends FlexStatementRs
  case FlexStatementErrorRs(status: String, code: Int, msg: String)  extends FlexStatementRs

/** This is returned when ordering a report, and when picking up the report to convey warnings and errors */
object FlexStatementRs extends XMLParsing:

  /** A Scala JS/JVM compliant hack. We know - Possibly raises errors but not normally. */
  def fromXml(xmlString: String): IO[FlexStatementRs] =
    parseToEvents(xmlString).map {
      elems =>
        firstTagsText("Status", elems) match {
          case "Success" =>
            val ticket = firstTagsText("ReferenceCode", elems)
            FlexStatementSuccessRs("Success", ticket)
          case st        =>
            val errorCode = firstTagsText("ErrorCode", elems).toInt
            val msg       = firstTagsText("ErrorMessage", elems)
            FlexStatementErrorRs(st, errorCode, msg)
        }
    }

  /**
    * Raise exception if XML cannot be parssd, else return sxml string unles its Raises ReportGenerationInProgress or BadResponseSstatus on
    * expected errors.
    */
  def sniffXml(x: String): IO[String] = parseToEvents(x).flatMap {
    evs =>
      firstTagsText("Status", evs) match {
        case status if status.isBlank => IO.pure(x)
        case status                   =>
          val errorCode        = firstTagsText("ErrorCode", evs).toInt
          val msg              = firstTagsText("ErrorMessage", evs)
          val error: Throwable = errorCode match {
            case 1019 => ReportGenerationInProgress()
            case _    => BadResponseStatus(status, errorCode, msg)
          }
          IO.raiseError[String](error)

      }
  }
