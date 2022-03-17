package com.odenzo.ibkr.web.models.flex

import cats.data.NonEmptyList
import com.odenzo.ibkr.web.models.*
import io.circe.*

/**
  * @param accountIds
  *   AccountID, make opaque object to Type it, or check Refined for Scala 3/ScalaJS
  * @param connIds
  * @param currency
  *   ISO Currency Code
  * @param days
  *   Number of days to get history for, I think 90 is the max, try 365 or YTD
  */
case class PortfolioAnalysisTxnHistoryBody(
    accountIds: NonEmptyList[String],
    connIds: NonEmptyList[Int] = NonEmptyList.one(0),
    currency: String = "USD",
    days: Int = 90
) derives Codec.AsObject
