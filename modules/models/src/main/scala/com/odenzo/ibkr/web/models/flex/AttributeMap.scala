package com.odenzo.ibkr.web.models.flex

import cats.data.Chain
import cats.effect.*

/** Attribute Map is used to take all the String attribute values from XML, handy for the FlexReports */
opaque type AttrMap = Map[String, String]

object AttrMap:
  def apply(raw: Map[String, String]): AttrMap = raw

  extension (x: AttrMap)
    def removed(key: String)           = x.removed(key)
    def removedAll(keys: String*)      = x.removedAll(keys)
    def get(key: String)               = x.get(key)
    def required(key: String)          = IO.fromOption(x.get(key))(Throwable(s"Key $key was not present in attributes"))
    def toList: List[(String, String)] = x.toList

/** Giving up on Tuples for now, need to re-read. No Shapeless yet */
case class RecordItem[T](name: String, value: T) {
  def get: T = value
}

case class TupleRecord(tups: Tuple) {
  val size: Any = tups.size
}
