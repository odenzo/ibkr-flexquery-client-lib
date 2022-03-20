# IBKR Client FlexQuery Lib

Required/Tested On: 
    - Scala 3+
    - JDK 11 / JDK 17
    - ScalaJS 1.9 (WIP)

Small little program to fetch IBKR Flex Queries already defined on Client Portal.
This uses IBKR Verion 3 API.
It has both Scala 3 and ScalaJS cross-compilation, but the XML support is still a bit weak on ScalaJS side.

Not much to this, other than it handles the retrying in case report is still being processed.
Tags the report with the report kind and just returns the XML, CSV, TSV etc in String form.


## Instructions for FlexQueries


https://guides.interactivebrokers.com/rg/reportguide.htm#reportguide/activity_flex_query_reference.htm
https://guides.interactivebrokers.com/am/am/reports/using_the_flex_web_service.htm

See: https://guides.interactivebrokers.com/am/am/reports/flex_web_service_version_3.htm for instructions.

Basically your go to Client Portal, construct a Flex Query report and token.
Note the token and the query id, and use as config to program.


FlexQueries can be XML, CSV, Text Pipe Delimited, Text Tab Delimited.
I tranform these into standard datastructures, but not strongly types.


For CVS you can have header and trailor records, column headers and/ secion code and line decriptor.

Account Alias instead of Account ID should be configured on IB since logging doesn't mask the account number.

Anyway, reportss are so configurable not much I will do on parsing. The purpose of this was to
try and make "Records" like Shapeless but just using the Scala 3 tuples.
- Failed so far :-/

## Future Work
+ Bring over the IBKR TWS model classes that are appropriate, this will always be partial
+ Finalize on a decent ScalaJS/Scala XML DOM that is x-platform. Currently fs2-data is used to parse into XMLEvents


