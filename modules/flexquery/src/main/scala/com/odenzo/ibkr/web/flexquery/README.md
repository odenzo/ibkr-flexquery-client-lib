# IBKR Flex Reporting Query API

IBKR Flex Queries are a great way to get reporting data.
You can also get them on-demand via APIs, but cannot change the parameters via the API, just whatever the UI settings are at in Web Portal.

This small helper lets you fetch those reports, main entry i in `FlexAPI`

```scala
def fetchReport(flexQueryId: String, flexToken: String): IO[FlexReport] = {
    given FlexContext = FlexContext(flexToken)

    Clients.loggingRedirectsClient(true, false).use {
      client => // Nothing special about client, roll your own if want.
        given Client[IO] = client
        for {
          referenceCode <- FlexReportOrdering.reportRequestApp(flexQueryId)
          report        <- FlexReportPickup.fetchReport(referenceCode)
        } yield report
    }
}
```
It will automatically retry on "report not ready" error and sniff the true respone format.

FlexReport returns the type of the report (XML, CSV, TSV, or Pipe Text)
Up to you to chooe your favorite tools to manipulate the report.


## First setup some FlexQueries
+ Enable in Account Settings and issue yourself a FLEX TOKEN for authorization.
+ Define a flexquery and the parameters to call it in Client Portal. Note the QueryID (a number not the unique name)


## IBKR References

- Flex Web Services info -- https://guides.interactivebrokers.com/am/am/reports/using_the_flex_web_service.htm
- now look under the User Guide (https://guides.interactivebrokers.com/cp/cp.htm) and then it is 
under `Performance & Statements` ->  `Reports` ->  `Flex Queries` . Which is baically where it is on the UI too.
 


## Pending Work

- Still working on ScalaJS side, 
  - getting HTTP4S Ember client to link (fallback to STTP if needed)
  - Trying to find a JS/JVM for usage, may have to write a facade. Now very simply need, but nice
    to be able to parse stuff and diplay all in web app.

- Data models, IBKR has a few and not too happy with OpenAPI Spec generation.
  - Custom OpenAPI generation
  - Centralize repo with hand written (and with Codecs), crowd-sourcing ideal!

