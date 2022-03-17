package com.odenzo.ibkr.web.models

import io.circe.*

/** Hack to represent a JSONObject I was too lazy to decode property */
type NotDecoded = Map[String, Json]
