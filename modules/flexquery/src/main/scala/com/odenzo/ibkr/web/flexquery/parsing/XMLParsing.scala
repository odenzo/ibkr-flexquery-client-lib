package com.odenzo.ibkr.web.flexquery.parsing

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*
import com.odenzo.ibkr.web.flexquery.errors.MalformedResponse
import fs2.data.xml.XmlEvent.*
import fs2.data.xml.*

import scala.concurrent.duration.*
import scala.util.chaining.{*, given}

/** SScala JS / JVM Cross Platform Parsing */
trait XMLParsing {

  /**
    * This parses to event and will generally throw error if not Well Formed. Unfortunately it can also hang on not-well-formed documents
    * There isbug where <foo>vlahblah will hang the stream, so we add a timeout.
    */
  def parseToEvents(xml: String): IO[List[XmlEvent]] = fs2.Stream.emit(xml).through(events[IO, String]).through(normalize)
    .compile
    .toList
    .timeoutTo(30.seconds, IO.raiseError(MalformedResponse("XML Not Well Formed Timeout Parssing")))

  def extractEvents(tagName: String, texts: List[XmlEvent]): List[XmlEvent] = {
    texts.dropWhile {
      case StartTag(QName(_, tag), _, _) if tag == tagName => false
      case _                                               => true
    }.takeWhile {
      case EndTag(QName(_, tag)) if tag == tagName => false
      case _                                       => true
    }
  }

  /** Empty String if not XmlString evens in list. */
  def combineAllText(s: List[XmlEvent]): String =
    s.collect[String] {
      case XmlString(s, _) => s
    }.foldLeft("")((b, a) => b + a)

  /** Can return empty string if no text in elem, and alo if element not found. Assumes valid XML already */
  def firstTagsText(tagName: String, events: List[XmlEvent]): String = extractEvents(tagName, events).pipe(combineAllText)

}

object XMLParsing extends XMLParsing
