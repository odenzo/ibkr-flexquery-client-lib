package com.odenzo.ibkr.web.flexquery.network

trait FatalError                            extends Throwable
trait RetryableError                        extends Throwable
case class BadHttpResponse(message: String) extends FatalError

case class IBFlexError(status: String, code: Int, message: String, tstamp: String) extends Throwable

object IBFlexError:
  def parse(s: String): Option[IBFlexError] = {
    val isErrRs = s.startsWith("FlexStatementResponse")
    None

  }
