package com.odenzo.ibkr.web.models.flex.errors

import scala.util.control.NoStackTrace

/** Warning with Error Code 1019 */
case class ReportGenerationInProgress() extends Throwable

case class MalformedResponse(message: String) extends Throwable

val NoErrorInfo: MalformedResponse = MalformedResponse("Missing Either ErrorCode or Error Message")

/** Scala 3 style Enum just to understand. Advantages over a trait, dunno TODO: REVISIT THIS WITH SCALA 3 ENUM */
sealed trait BadResponseStatus extends Throwable with NoStackTrace:
  def errorCode: Int
  def message: String

case class ErrorStatus(errorCode: Int, message: String) extends BadResponseStatus
case class WarnStatus(errorCode: Int, message: String)  extends BadResponseStatus
