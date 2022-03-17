package com.ibkr.web.models.atoms

import munit.FunSuite

import scala.::

class AttrMapTest extends FunSuite {
//https://stackoverflow.com/questions/64339583/scala-3-extract-tuple-of-wrappers-and-inversemap-on-first-order-type
  test("TupRecord") {
    // Homogoneous
    val tarray: Tuple = Tuple.fromArray(List("a" -> "1", "b" -> "2", "c" -> "3", "d" -> 4).toArray)

    dumpTuple(tarray, "TArray")
    val dataFN = (s: String) => s"$s$s"
    // val typeFN = ???

    //   val trec: TupleRecord = TupleRecord(tarray)
    scribe.info(s"Tuple Info: ${tarray.size}")
    // scribe.info(s"Tuple Info: ${pprint(trec)}")
    scribe.info(s"Tuple Info: ${pprint(tarray)}")
    val t1 = tarray *: (Tuple2("v", "12"))             // Doesn't do what I think.
    val t2 = tarray ++ (Tuple2("v", "12"), EmptyTuple) // Doesn't do what I think either

    scribe.info(s"Tuple Info: ${pprint(t1)}   ${t1.size}")
    scribe.info(s"Tuple Info: ${pprint(t2)}   ${t2.size}")

//    val foo: (RecordItem[String], RecordItem[String]) = (RecordItem("a", "1"), RecordItem("b", "2"))
//    val boo: (RecordItem[String], RecordItem[Int])    = (RecordItem("c", "1"), RecordItem("d", 2))
//    // RecordItem[T] T will always be type erased? Can we make dependant function to get?
//    val find: Option[RecordItem[?]]                   = boo.productElement(1) match {
//      case kv: RecordItem[?] if kv.name == "d" => Some(kv)
//      case _                                   => None
//    }
//    // Thought out matchable would help out here.
//    scribe.info(s"Found: $find ${oprint(find)}")
//    val unsafeVal: RecordItem[?]                      = find.get
//    unsafeVal match {
//      case v: RecordItem[String] => scribe.info(s"Var was String $v")
//      case v: RecordItem[Int]    => scribe.info(s"Var was Int $v")
//      case v                     => scribe.info(s"Var was Other $v")
//    }
//    dumpTuple(boo, "boo")
//    val soo: Tuple                                    = boo
//    dumpTuple(soo, "soo")
//    val bar: Tuple                                    = foo ++ boo
//
//    val rum: Tuple = bar.splitAt(2)
//    dumpTuple(bar, "bar")
//    dumpTuple(rum, "rum")
//
//    val man = bar *: Tuple1(RecordItem("z", "1"))
    // scribe.info(s"BAR: $bar \n $man")
  }

  def dumpTuple(t: Tuple, desc: String): Unit =
    scribe.info(s"$desc Size: ${t.size} $t")
    scribe.info(s"$desc OPrinted ${pprint(t)} ")
    scribe.info(s"Elem Names: ${t.productElementNames.toList}")
    scribe.info(s"Elements: ${pprint(t.productElement)}")
}
