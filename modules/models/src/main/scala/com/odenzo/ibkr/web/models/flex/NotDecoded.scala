package com.odenzo.ibkr.web.models.flex

import io.circe.*

/** Hack to represent a JSONObject I was too lazy to decode property */
type NotDecoded = Map[String, Json]
