# IBKR Client FlexQuery Lib

Small little program to fetch IBKR Flex Queries already defined on Client Portal.


## Instructions for FlexQueries

Using Version 3.

https://guides.interactivebrokers.com/rg/reportguide.htm#reportguide/activity_flex_query_reference.htm
https://guides.interactivebrokers.com/am/am/reports/using_the_flex_web_service.htm

See: https://guides.interactivebrokers.com/am/am/reports/flex_web_service_version_3.htm for instructions.

Basically your go to Client Portal, construct a Flex Query report and token.
Note the token and the query id, and use as config to program.

This doesn't require a gateway so am seperating out from IBKR Client Portal.
And its requires XML support.


FlexQueries can be XML, CSV, Text Pipe Delimited, Text Tab Delimited.
I tranform these into standard datastructures, but not strongly types.


For CVS you can have header and trailor records, column headers and/ secion code and line decriptor.

Account Alias instead of Account ID should be configured on IB.
Anyway, reportss are so configurable not much I will do on parsing. The purpose of this was to
try and make "Records" like Shapeless but just using the Scala 3 tuples.
- Failed so far :-/

