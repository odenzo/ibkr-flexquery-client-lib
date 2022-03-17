package com.odenzo.ibkr.web.flexquery

import cats.effect.*
import cats.syntax.all.*
import com.odenzo.ibkr.*
import com.odenzo.ibkr.webutils.base.OPrint.oprint
import com.odenzo.ibkr.webutils.base.http4s.*
import munit.*
import org.http4s.client.Client
import retry.{RetryPolicies, retryingOnSomeErrors}

import scala.concurrent.duration.*
import scala.xml.Elem

class FlexReportSpec extends BaseTest:
  val badResponse: Elem = <FlexStatementResponse timestamp='20 February, 2022 05:52 PM EST' foo='bar'>
    <Status>Warn</Status>
    <ErrorCode>1019</ErrorCode>
    <ErrorMessage>This is a FAKE MESSAGE Statement generation in progress. Please try again shortly.</ErrorMessage>
  </FlexStatementResponse>

  val goodResponse = <FlexQueryResponse queryName="YTD_PL" type="AF">
    <FlexStatements count="2">
      <FlexStatement accountId="U555" fromDate="20220103" toDate="20220221" period="YearToDate" whenGenerated="20220222;212323">
        <FIFOPerformanceSummaryInBase>
          <FIFOPerformanceSummaryUnderlying accountId="U5556677" model="" assetCategory="STK" symbol="FUV" description="ARCIMOTO INC" conid="290088022" expiry="" costAdj="0" realizedSTProfit="0" realizedSTLoss="0" realizedLTProfit="0" realizedLTLoss="0" totalRealizedPnl="0" totalRealizedCapitalGainsPnl="0" totalRealizedFxPnl="0" unrealizedProfit="0" unrealizedLoss="-3497.5" unrealizedSTProfit="0" unrealizedSTLoss="-3497.5" unrealizedLTProfit="0" unrealizedLTLoss="0" totalUnrealizedPnl="-3497.5" totalUnrealizedCapitalGainsPnl="-3497.5" totalUnrealizedFxPnl="0" totalFifoPnl="-3497.5" totalCapitalGainsPnl="-3497.5" totalFxPnl="0" transferredPnl="0" transferredCapitalGainsPnl="0" transferredFxPnl="0" />
          <FIFOPerformanceSummaryUnderlying accountId="U5556677" model="" assetCategory="" symbol="" description="Total (All Assets)" conid="" expiry="" costAdj="0" realizedSTProfit="0" realizedSTLoss="0" realizedLTProfit="0" realizedLTLoss="0" totalRealizedPnl="0" totalRealizedCapitalGainsPnl="0" totalRealizedFxPnl="0" unrealizedProfit="0" unrealizedLoss="-3497.5" unrealizedSTProfit="0" unrealizedSTLoss="-3497.5" unrealizedLTProfit="0" unrealizedLTLoss="0" totalUnrealizedPnl="-3497.5" totalUnrealizedCapitalGainsPnl="-3497.5" totalUnrealizedFxPnl="0" totalFifoPnl="-3497.5" totalCapitalGainsPnl="-3497.5" totalFxPnl="0" transferredPnl="0" transferredCapitalGainsPnl="0" transferredFxPnl="0" />
        </FIFOPerformanceSummaryInBase>
      </FlexStatement>
      <FlexStatement accountId="U555" fromDate="20220103" toDate="20220221" period="YearToDate" whenGenerated="20220222;212323">
      <FIFOPerformanceSummaryInBase>
        <FIFOPerformanceSummaryUnderlying accountId="U5556666" model="" assetCategory="STK" symbol="AAPL" description="APPLE INC" conid="265598" expiry="" costAdj="0" realizedSTProfit="0" realizedSTLoss="0" realizedLTProfit="0" realizedLTLoss="0" totalRealizedPnl="0" totalRealizedCapitalGainsPnl="0" totalRealizedFxPnl="0" unrealizedProfit="0" unrealizedLoss="-1071.8772" unrealizedSTProfit="0" unrealizedSTLoss="-1071.8772" unrealizedLTProfit="0" unrealizedLTLoss="0" totalUnrealizedPnl="-1071.8772" totalUnrealizedCapitalGainsPnl="-407.907792" totalUnrealizedFxPnl="-663.969408" totalFifoPnl="-1071.8772" totalCapitalGainsPnl="-407.907792" totalFxPnl="-663.969408" transferredPnl="0" transferredCapitalGainsPnl="0" transferredFxPnl="0" />
        <FIFOPerformanceSummaryUnderlying accountId="U5556666" model="" assetCategory="STK" symbol="DM" description="DESKTOP METAL INC-A" conid="459450995" expiry="" costAdj="0" realizedSTProfit="0" realizedSTLoss="0" realizedLTProfit="0" realizedLTLoss="0" totalRealizedPnl="0" totalRealizedCapitalGainsPnl="0" totalRealizedFxPnl="0" unrealizedProfit="0" unrealizedLoss="-317.303" unrealizedSTProfit="0" unrealizedSTLoss="-317.303" unrealizedLTProfit="0" unrealizedLTLoss="0" totalUnrealizedPnl="-317.303" totalUnrealizedCapitalGainsPnl="-238.752" totalUnrealizedFxPnl="-78.551" totalFifoPnl="-317.303" totalCapitalGainsPnl="-238.752" totalFxPnl="-78.551" transferredPnl="0" transferredCapitalGainsPnl="0" transferredFxPnl="0" />
        <FIFOPerformanceSummaryUnderlying accountId="U5556666" model="" assetCategory="STK" symbol="HUM" description="HUMM GROUP LTD" conid="457983944" expiry="" costAdj="0" realizedSTProfit="0" realizedSTLoss="0" realizedLTProfit="0" realizedLTLoss="0" totalRealizedPnl="0" totalRealizedCapitalGainsPnl="0" totalRealizedFxPnl="0" unrealizedProfit="0" unrealizedLoss="-2484.013464" unrealizedSTProfit="0" unrealizedSTLoss="0" unrealizedLTProfit="0" unrealizedLTLoss="-2484.013464" totalUnrealizedPnl="-2484.013464" totalUnrealizedCapitalGainsPnl="-2317.83804" totalUnrealizedFxPnl="-166.175424" totalFifoPnl="-2484.013464" totalCapitalGainsPnl="-2317.83804" totalFxPnl="-166.175424" transferredPnl="0" transferredCapitalGainsPnl="0" transferredFxPnl="0" />
        <FIFOPerformanceSummaryUnderlying accountId="U5556666" model="" assetCategory="STK" symbol="IBM" description="INTL BUSINESS MACHINES CORP" conid="8314" expiry="" costAdj="0" realizedSTProfit="0" realizedSTLoss="0" realizedLTProfit="0" realizedLTLoss="0" totalRealizedPnl="0" totalRealizedCapitalGainsPnl="0" totalRealizedFxPnl="0" unrealizedProfit="0" unrealizedLoss="-1125.439875" unrealizedSTProfit="0" unrealizedSTLoss="-1125.439875" unrealizedLTProfit="0" unrealizedLTLoss="0" totalUnrealizedPnl="-1125.439875" totalUnrealizedCapitalGainsPnl="-933.3711" totalUnrealizedFxPnl="-192.068775" totalFifoPnl="-1125.439875" totalCapitalGainsPnl="-933.3711" totalFxPnl="-192.068775" transferredPnl="0" transferredCapitalGainsPnl="0" transferredFxPnl="0" />
        <FIFOPerformanceSummaryUnderlying accountId="U5556666" model="" assetCategory="STK" symbol="INTC" description="INTEL CORP" conid="270639" expiry="" costAdj="0" realizedSTProfit="0" realizedSTLoss="0" realizedLTProfit="0" realizedLTLoss="0" totalRealizedPnl="0" totalRealizedCapitalGainsPnl="0" totalRealizedFxPnl="0" unrealizedProfit="0" unrealizedLoss="-1578.73881" unrealizedSTProfit="0" unrealizedSTLoss="-1578.73881" unrealizedLTProfit="0" unrealizedLTLoss="0" totalUnrealizedPnl="-1578.73881" totalUnrealizedCapitalGainsPnl="-1275.38334" totalUnrealizedFxPnl="-303.35547" totalFifoPnl="-1578.73881" totalCapitalGainsPnl="-1275.38334" totalFxPnl="-303.35547" transferredPnl="0" transferredCapitalGainsPnl="0" transferredFxPnl="0" />
        <FIFOPerformanceSummaryUnderlying accountId="U5556666" model="" assetCategory="STK" symbol="MSFT" description="MICROSOFT CORP" conid="272093" expiry="" costAdj="0" realizedSTProfit="0" realizedSTLoss="0" realizedLTProfit="0" realizedLTLoss="0" totalRealizedPnl="0" totalRealizedCapitalGainsPnl="0" totalRealizedFxPnl="0" unrealizedProfit="0" unrealizedLoss="-1662.989028" unrealizedSTProfit="0" unrealizedSTLoss="-1662.989028" unrealizedLTProfit="0" unrealizedLTLoss="0" totalUnrealizedPnl="-1662.989028" totalUnrealizedCapitalGainsPnl="-1082.173284" totalUnrealizedFxPnl="-580.815744" totalFifoPnl="-1662.989028" totalCapitalGainsPnl="-1082.173284" totalFxPnl="-580.815744" transferredPnl="0" transferredCapitalGainsPnl="0" transferredFxPnl="0" />
        <FIFOPerformanceSummaryUnderlying accountId="U5556666" model="" assetCategory="STK" symbol="NET" description="CLOUDFLARE INC - CLASS A" conid="382633646" expiry="" costAdj="0" realizedSTProfit="0" realizedSTLoss="0" realizedLTProfit="0" realizedLTLoss="0" totalRealizedPnl="0" totalRealizedCapitalGainsPnl="0" totalRealizedFxPnl="0" unrealizedProfit="0" unrealizedLoss="-4658.8098" unrealizedSTProfit="0" unrealizedSTLoss="-4658.8098" unrealizedLTProfit="0" unrealizedLTLoss="0" totalUnrealizedPnl="-4658.8098" totalUnrealizedCapitalGainsPnl="-4051.323" totalUnrealizedFxPnl="-607.4868" totalFifoPnl="-4658.8098" totalCapitalGainsPnl="-4051.323" totalFxPnl="-607.4868" transferredPnl="0" transferredCapitalGainsPnl="0" transferredFxPnl="0" />
        <FIFOPerformanceSummaryUnderlying accountId="U5556666" model="" assetCategory="STK" symbol="NVDA" description="NVIDIA CORP" conid="4815747" expiry="" costAdj="0" realizedSTProfit="0" realizedSTLoss="0" realizedLTProfit="0" realizedLTLoss="0" totalRealizedPnl="0" totalRealizedCapitalGainsPnl="0" totalRealizedFxPnl="0" unrealizedProfit="0" unrealizedLoss="-3810.129354" unrealizedSTProfit="0" unrealizedSTLoss="-3810.129354" unrealizedLTProfit="0" unrealizedLTLoss="0" totalUnrealizedPnl="-3810.129354" totalUnrealizedCapitalGainsPnl="-1896.556356" totalUnrealizedFxPnl="-1913.572998" totalFifoPnl="-3810.129354" totalCapitalGainsPnl="-1896.556356" totalFxPnl="-1913.572998" transferredPnl="0" transferredCapitalGainsPnl="0" transferredFxPnl="0" />
        <FIFOPerformanceSummaryUnderlying accountId="U5556666" model="" assetCategory="STK" symbol="WBC" description="WESTPAC BANKING CORPORATION" conid="4036833" expiry="" costAdj="0" realizedSTProfit="0" realizedSTLoss="0" realizedLTProfit="0" realizedLTLoss="0" totalRealizedPnl="0" totalRealizedCapitalGainsPnl="0" totalRealizedFxPnl="0" unrealizedProfit="157.9915" unrealizedLoss="-1281.890215" unrealizedSTProfit="0" unrealizedSTLoss="-1281.890215" unrealizedLTProfit="157.9915" unrealizedLTLoss="0" totalUnrealizedPnl="-1123.898715" totalUnrealizedCapitalGainsPnl="-3268.001444" totalUnrealizedFxPnl="2144.102729" totalFifoPnl="-1123.898715" totalCapitalGainsPnl="-3268.001444" totalFxPnl="2144.102729" transferredPnl="0" transferredCapitalGainsPnl="0" transferredFxPnl="0" />
        <FIFOPerformanceSummaryUnderlying accountId="U5556666" model="" assetCategory="STK" symbol="WBCPK" description="WSTP 1.70105 PERP" conid="515606202" expiry="" costAdj="0" realizedSTProfit="0" realizedSTLoss="0" realizedLTProfit="0" realizedLTLoss="0" totalRealizedPnl="0" totalRealizedCapitalGainsPnl="0" totalRealizedFxPnl="0" unrealizedProfit="721.411976" unrealizedLoss="0" unrealizedSTProfit="721.411976" unrealizedSTLoss="0" unrealizedLTProfit="0" unrealizedLTLoss="0" totalUnrealizedPnl="721.411976" totalUnrealizedCapitalGainsPnl="383.171976" totalUnrealizedFxPnl="338.24" totalFifoPnl="721.411976" totalCapitalGainsPnl="383.171976" totalFxPnl="338.24" transferredPnl="0" transferredCapitalGainsPnl="0" transferredFxPnl="0" />
        <FIFOPerformanceSummaryUnderlying accountId="U5556666" model="" assetCategory="STK" symbol="WBK" description="WESTPAC BANKING CORP-SP ADR" conid="13657" expiry="" costAdj="0" realizedSTProfit="0" realizedSTLoss="0" realizedLTProfit="0" realizedLTLoss="0" totalRealizedPnl="0" totalRealizedCapitalGainsPnl="0" totalRealizedFxPnl="0" unrealizedProfit="0" unrealizedLoss="-171.24632" unrealizedSTProfit="0" unrealizedSTLoss="-171.24632" unrealizedLTProfit="0" unrealizedLTLoss="0" totalUnrealizedPnl="-171.24632" totalUnrealizedCapitalGainsPnl="0" totalUnrealizedFxPnl="-171.24632" totalFifoPnl="-171.24632" totalCapitalGainsPnl="0" totalFxPnl="-171.24632" transferredPnl="0" transferredCapitalGainsPnl="0" transferredFxPnl="0" />
        <FIFOPerformanceSummaryUnderlying accountId="U5556666" model="" assetCategory="" symbol="" description="Total (All Assets)" conid="" expiry="" costAdj="0" realizedSTProfit="0" realizedSTLoss="0" realizedLTProfit="0" realizedLTLoss="0" totalRealizedPnl="0" totalRealizedCapitalGainsPnl="0" totalRealizedFxPnl="0" unrealizedProfit="879.403476" unrealizedLoss="-18162.437066" unrealizedSTProfit="721.411976" unrealizedSTLoss="-15678.423602" unrealizedLTProfit="157.9915" unrealizedLTLoss="-2484.013464" totalUnrealizedPnl="-17283.03359" totalUnrealizedCapitalGainsPnl="-15088.13438" totalUnrealizedFxPnl="-2194.89921" totalFifoPnl="-17283.03359" totalCapitalGainsPnl="-15088.13438" totalFxPnl="-2194.89921" transferredPnl="0" transferredCapitalGainsPnl="0" transferredFxPnl="0" />
      </FIFOPerformanceSummaryInBase>
    </FlexStatement>
    </FlexStatements>
  </FlexQueryResponse>

  test("Getting Attributes") {
    val attr = xtractAttributes(badResponse)
    assertEquals(attr.size, 2)
    val none = xtractAttributes(<foo><bar></bar></foo>)
    assert(none.isEmpty)
    assert(xtractAttributes(<foo><bar nested="skip"></bar></foo>).isEmpty)
    assert(xtractAttributes(<foo />).isEmpty)
    assert(xtractAttributes(<foo abar="99" />).size == 1)

  }

  test("xtactNodeName") {

    assertEquals(xtractNodeName(<foo><bar nested="skip"></bar></foo>), "foo")
    assertEquals(xtractNodeName(<foo />), "foo")

  }

  test("FullReport") {
    // transformReport(goodResponse)
  }