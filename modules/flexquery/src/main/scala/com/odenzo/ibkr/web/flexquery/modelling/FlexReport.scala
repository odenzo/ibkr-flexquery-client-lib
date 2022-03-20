package com.odenzo.ibkr.web.flexquery.modelling

enum FlexReportFormat:
  case XML, CSV, PIPED_TEXT, TABBED_TEXT

case class FlexReport(format: FlexReportFormat, body: String)
