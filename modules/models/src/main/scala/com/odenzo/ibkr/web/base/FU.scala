package com.odenzo.ibkr.web.base

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*

import java.util.concurrent.CompletableFuture
import scala.concurrent.Future

trait FU:

  def eitherToError[F[_]: ApplicativeThrow, A](t: Either[Throwable, A]): F[A] = ApplicativeError[F, Throwable].fromEither(t)

  def optionToError[F[_]: ApplicativeThrow, A](t: Option[A])(err: Throwable): F[A] =
    t match
      case None    => ApplicativeThrow[F].raiseError(err)
      case Some(t) => ApplicativeThrow[F].pure(t)

  def toIO[A](fn: => CompletableFuture[A]): IO[A] =
    import scala.jdk.FutureConverters.*
    val ff: cats.effect.IO[Future[A]] = IO.delay(fn.asScala)
    IO.fromFuture(ff) // Does IO.async under the hood.

  /** Allows toIO(Future.successful("a") or toIO(callReturningFuture(args)) such that the future is deflation because fn is call-by-name */
  def fromFuture[A](fn: => Future[A]): IO[A] =
    IO.fromFuture(IO(fn))

  /** Ensures list has exactly one element or raisesError */
  def exactlyOne[F[_]: ApplicativeThrow, A](msg: String)(l: List[A]): F[A] =
    if l.length != 1 then ApplicativeThrow[F].raiseError[A](Throwable(s"Requires List Size 1 but  ${l.length}: $msg"))
    else ApplicativeThrow[F].pure(l.head) // This is .head is ok.  OK

  /** Ensures 0 or 1 elements in a list, errors if > 1 */
  def optionOne[F[_]: ApplicativeThrow, A](msg: String)(l: List[A]): F[Option[A]] =
    if (l.length > 1) then ApplicativeThrow[F].raiseError(Throwable(s"Expected List Size 0 or 1 but  ${l.length}: $msg"))
    else ApplicativeThrow[F].pure(l.headOption)

  /** Raises an Option.empty to Error */
  def required[F[_]: ApplicativeThrow, A](msg: String)(o: Option[A]): F[A] =
    o match
      case None    => ApplicativeThrow[F].raiseError(Throwable(s"Required Value Missing: $msg"))
      case Some(t) => ApplicativeThrow[F].pure(t)

  /** Get Or ElseM */
  def whenEmpty[F[_]: ApplicativeThrow, A](x: F[A])(o: Option[A]): F[A] = o.fold(x)(ApplicativeThrow[F].pure(_))

  def requireNone[F[_]: ApplicativeThrow, A](msg: String)(o: Option[A]): F[Unit] =
    o match
      case None    => ApplicativeThrow[F].pure(())
      case Some(v) => ApplicativeThrow[F].raiseError(Throwable(s"Option Value Must Be Empty: $v -- $msg"))

  def nonEmptyList[F[_]: ApplicativeThrow, A](msg: String)(l: List[A]): F[NonEmptyList[A]] =
    NonEmptyList.fromList(l) match
      case None    => ApplicativeThrow[F].raiseError(Throwable(s"NEL Requires List of 1+ $msg"))
      case Some(t) => ApplicativeThrow[F].pure(t)

object FU extends FU

object IOU:
  def notDone = IO.raiseError(Throwable("Not Implemented"))
