package com.odenzo.ibkr.web.models.flex

import cats.effect.*
import cats.effect.syntax.all.*
import cats.*
import cats.data.*
import cats.syntax.all.*
import io.circe.*
import io.circe.syntax.*

import com.odenzo.ibkr.web.models.*
import com.odenzo.ibkr.web.models.NotDecoded

import java.time.Instant

/**
  * 401 Signals Authentication Failure. I guess body returned no matter what.
  * @param LOGIN_TYPE
  *   1 for Live, 2 for Paper
  * @param expire
  *   Time in millis until session expires
  * @param RESULT
  *   true is session active, else false.
  */
case class SSOValidateRs(
    LOGIN_TYPE: Option[Int], // This fucker seems to randomly change between loginType and LOGIN_TYPE in Json Whats going on?
    loginType: Option[Int],  // This fucker seems to randomly change between loginType and LOGIN_TYPE in Json Whats going on?
    USER_NAME: String,
    USER_ID: Long,
    EXPIRES: Long,
    RESULT: Boolean,
    AUTH_TIME: Long,
    IS_FREE_TRIAL: Boolean,
    SF_ENABLED: Boolean,
    IP: String,              // IP Number
    QUALIFIED_FOR_MOBILE_AUTH: Option[Boolean],
    PAPER_USER_NAME: Option[String]
) derives Codec.AsObject:
  def loggedIn: Boolean = RESULT

case class SSOIServerValidateRs(
    authenticated: Boolean,
    competing: Boolean,
    connected: Boolean,
    fail: Option[String],
    message: Option[String],
    prompts: Option[NonEmptyList[String]]
) derives Codec.AsObject:
  def loggedIn: Boolean = authenticated

case class ConfirmedRs(confirmed: Boolean) derives Codec.AsObject

case class PortfolioAccountsRs(accounts: List[AccountInfo]) derives Codec.AsObject

case class AccountInfo(
    id: String,
    accountId: String,
    accountVan: String,
    accountTitle: String,
    displayName: String,
    accountAlias: Option[String],
    accountStatus: BigInt,
    currency: String,
    `type`: String, // DEMO or LIVE?
    tradingType: String,
    ibEntity: String,
    faclient: Boolean,
    clearingStatus: String,
    covestor: Boolean,
    // Ignoring Parent
    desc: String
) derives Codec.AsObject

case class AccountsRs(accts: NonEmptyList[AccountInfo])

/**
  * This is a value class, but no unwrapping. See if we can make a util for this so can use Derives.AsObject but Derives.AsUnwrappedObject
  */
object AccountsRs:
  given Decoder[AccountsRs] = summon[Decoder[NonEmptyList[AccountInfo]]]
    .map(l => AccountsRs(l))

case class OneUserRs(
    ispaper: Boolean,
    islite: Boolean,
    has2fa: Boolean,
    username: String,
    features: NotDecoded,
    applicants: List[NotDecoded],
    uar: NotDecoded,
    accts: Map[String, NotDecoded]
) derives Codec.AsObject

case class TickleRs(session: String, ssoExpires: Long, collission: Boolean, userId: Long, iserver: IServerStatus) derives Codec.AsObject

case class IServerStatus(authStatus: AuthStatus) derives Codec.AsObject

case class AuthStatus(authenticated: Boolean, competing: Boolean, connected: Boolean, message: String, MAC: String, serverInfo: ServerInfo)
    derives Codec.AsObject

case class ServerInfo(serverName: String, serverVersion: String) derives Codec.AsObject

case class Summary(amount: BigDecimal, currency: Option[String], isNull: Boolean, timestamp: Long, value: Option[String], severity: Int)
    derives Codec.AsObject

/** max-inlines scalac option needs to be increased */
case class LedgerEntry(
    commoditymarketvalue: BigDecimal,
    futuremarketvalue: BigDecimal,
    settledcash: BigDecimal,
    exchangerate: BigDecimal,
    sessionid: Long,
    cashbalance: BigDecimal,
    corporatebondsmarketvalue: BigDecimal,
    netliquidationvalue: BigDecimal,
    interest: BigDecimal,
    unrealizedpnl: BigDecimal,
    stockmarketvalue: BigDecimal,
    moneyfunds: BigDecimal,
    currency: String,
    realizedpnl: BigDecimal,
    funds: BigDecimal,
    acctcode: String,
    issueroptionsmarketvalue: BigDecimal,
    key: String,
    timestamp: Long,
    severity: Int,
    stockoptionmarketvalue: BigDecimal,
    futuresonlypnl: BigDecimal,
    tbondsmarketvalue: BigDecimal,
    cashbalancefxsegment: BigDecimal,
    secondkey: String,        // Currency
    endofbundle: Option[Int], // 1, is this a boolean
    dividends: BigDecimal,
    cryptocurrencyvalue: BigDecimal,
    tbillsmarketvalue: BigDecimal,
    futureoptionmarketvalue: BigDecimal,
    warrantsmarketvalue: BigDecimal
) derives Codec.AsObject

/** Can just ue Map[String,LedgerEntry] or custom codec for unwrapping this. Not sure AnyVal still works. Could use opqaue type */
case class LedgerRs(ledgerMap: Map[String, LedgerEntry])

object LedgerRs:
  private val valCodec: Codec[Map[String, LedgerEntry]] =
    Codec.from[Map[String, LedgerEntry]](summon[Decoder[Map[String, LedgerEntry]]], summon[Encoder[Map[String, LedgerEntry]]])

  val unwrapped: Codec[LedgerRs] = valCodec.iemap[LedgerRs](le => Right(LedgerRs(le)))(rs => rs.ledgerMap)
  given Codec[LedgerRs]          = unwrapped
