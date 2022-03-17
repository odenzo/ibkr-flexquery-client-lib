package com.ibkr.models

import cats.effect.*
import munit.*

/** Base testing trait that includes Cats Support and Assertions */
trait BaseTest extends CatsEffectSuite {}

final val include = new munit.Tag("include")
final val exclude = new munit.Tag("exclude")
