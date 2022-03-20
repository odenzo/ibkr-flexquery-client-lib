# Flex Reporting Query API

+ Enable in Account Settings
+ Calls flex reporting queries that are already defined (e.g. via Portal website)
+ https://gdcdyn.interactivebrokers.com/Universal/servlet/FlexStatementService.SendRequest?t=TOKEN&q=QUERY_ID&v=3
This seems to be an older XML based system, not sure there is a new equivalent:
+ Once a flex request is made, XML returned with a pickup token
+ Pick up with:
+ ype the following URL in your browser’s Address field:
  https://gdcdyn.interactivebrokers.com/Universal/servlet/FlexStatement
  Service.GetStatement?q=REFERENCE_CODE&t=TOKEN
  &v=VERSION

Where:
REFERENCE_CODE is the code you received as part of the response when you placed the request
TOKEN is your current token
VERSION is the version of the Flex Web Service Version you are using. You can set this to 2 or 3. Note that if you do not specify a Version, the system will use Version 2.

Overview of Version 3 API is: https://www.interactivebrokers.com/en/software/am/am/reports/flex_web_service_version_3.htm



Programmatic access requires the User-Agent HTTP header to be set. Accepted values are: Blackberry or Java (LOL)

We get a key from setting it up in IBKR Portal, this key is sourced from environment variable IBKR_FLWX_KEY


org.http4s.scalaxml