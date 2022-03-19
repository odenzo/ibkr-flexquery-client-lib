package com.odenzo.ibkr.web.flexquery.errors

import scala.util.control.NoStackTrace

/** Warning with Error Code 1019 */
case class ReportGenerationInProgress() extends Throwable

case class MalformedResponse(message: String) extends Throwable

val NoErrorInfo: MalformedResponse = MalformedResponse("Missing Either ErrorCode or Error Message")

/** Scala 3 style Enum just to understand. Advantages over a trait, dunno TODO: REVISIT THIS WITH SCALA 3 ENUM */
case class BadResponseStatus(status: String, errorCode: Int, message: String) extends Throwable with NoStackTrace

case class BadHttpResponse(code: Int, message: String) extends Throwable

case class BadContentType(message: String) extends Throwable
