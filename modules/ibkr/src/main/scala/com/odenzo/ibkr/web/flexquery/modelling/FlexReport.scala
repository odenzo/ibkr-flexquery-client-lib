package com.odenzo.ibkr.web.flexquery.modelling

import cats.*
import cats.data.*
import cats.implicits.*

import com.odenzo.ibkr.web.base.OPrint.oprint
import io.circe.*
import io.circe.syntax.*
import scala.util.chaining.scalaUtilChainingOps
import scala.xml.{*, given}


enum FlexReportFormat:
  case XML, CSV, PIPED_TEXT, TABBED_TEXT

case class FlexReport(format:FlexReportFormat, body:String)

/** Flex Report Comes Back as XML Dependant on the Flex configuration mostly. This has some helpers at least. */
object FlexReport {

  /** TOp of reports should be on FlexQueryResponse elem, with N FLexQueryStatements */

  // def transformReport(report: Elem, statementParser: String): Unit = {
  //   scribe.info(s"Transforming Report: ${xtractAttributes(report)}")
  //   val topElemName             = xtractNodeName(report)
  //   val topAttr                 = xtractAttributes(report)
  //   scribe.info(s"Top Attributes: $topAttr")
  //   val queryNumber             = topAttr.get("queryNum")
  //   val flexStatements: NodeSeq = report \ "FlexStatements"
  //   val count: String           = (flexStatements \@ "count")
  //   scribe.info(s"Statement Count $count")

  //   // Get ALL the FlexStatement elements and sort them out by attributes
  //   val flexStatementList: NodeSeq = report \\ "FlexStatement"
  //   flexStatementList.foreach(n => scribe.info(n.label))
  //   flexStatementList.map {
  //     l =>
  //       // Maybe actually using Validated is good here.
  //       val smd = StatementMetaData.fromAttr(l.attributes.asAttrMap).getOrElse(Throwable("Invalid Statement MetaData"))
  //       scribe.info(s"MetaData: ${oprint(smd)}")
  //       scribe.info(s"Statements Count: ${count} == Actual ${flexStatementList.size}")
  //       scribe.info(s"Items Per Statement${flexStatementList.map(_.size)}")

  //       Json.Null

  //   }

  //   /** This parses ONE of the N possible statements. One per account so far. This particlar one is FIFO PerformanceSumaryInBase. */
  //   def queryParserYTD_PL(stmt: Elem, md: MetaData): collection.Seq[Map[String, String]] =
  //     // FIFOPerformanceSummaryInBase, we pick out the underlying.
  //     val rawData = (stmt \\ "FIFOPerformanceSummaryUnderlying").map(elem => elem.attributes.asAttrMap)
  //     // Sometimes w( (acc, (k,v)) => Tuple. acc.)e may want to convert the raw attribute data to case classes, or do grouping etc
  //     // For now I would like a way to extract a case class nicely from the Map (removing from Map) and converting as needed
  //     rawData

  //   case class StatementMetaData(
  //       accountId: String,
  //       fromDateYYYYMMDD: String,
  //       toDate: String,
  //       period: String,
  //       whenGenerated: String // ="20220222;212323"
  //   )

  //   object StatementMetaData:
  //     def fromAttr(attr: Map[String, String]): Option[StatementMetaData] = {
  //       (attr.get("accountId"), attr.get("fromDate"), attr.get("toDate"), attr.get("period"), attr.get("whenGenerated"))
  //         .mapN(StatementMetaData.apply)
  //     }
  //   case class PL(
  //       accountId: String,
  //       model: String,
  //       assetCategory: String,
  //       symbol: String,
  //       description: String,
  //       conid: String,
  //       expiry: Option[String],
  //       costAdr: String
  //   )

  //   case class RealizedPL(
  //       stProfit: BigDecimal,
  //       stLoss: BigDecimal,
  //       ltProfit: BigDecimal,
  //       ltLoss: BigDecimal,
  //       totalPnl: String,
  //       totalCapitalGains: BigDecimal,
  //       totalFxPnl: BigDecimal
  //   )

  //   case class UnRealizedPL(
  //       profit: BigDecimal,
  //       loss: BigDecimal,
  //       stProfit: BigDecimal,
  //       stLoss: BigDecimal,
  //       ltProfit: BigDecimal,
  //       ltLoss: BigDecimal,
  //       totalPnl: String,
  //       totalCapitalGains: BigDecimal,
  //       totalFxPnl: BigDecimal
  //   )
  // }
}
