import sbt.Keys.libraryDependencies

ThisBuild / organization      := "com.odenzo"
ThisBuild / githubOwner       := "odenzo"
ThisBuild / githubRepository  := "ibkr-flexquery-lib"
ThisBuild / githubTokenSource := TokenSource.Or(TokenSource.GitConfig("github.token"), TokenSource.Environment("GITHUB_TOKEN"))

ThisBuild / publishMavenStyle := true
ThisBuild / bspEnabled        := false
ThisBuild / scalaVersion      := "3.1.1"
ThisBuild / Test / fork       := false
ThisBuild / versionScheme     := Some("early-semver")

ThisBuild / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }

val myScalacOptions = Seq(
  "-source",
  "3.1",
  "-Xmax-inlines",
  "100",
  // "-rewrite",
  "-new-syntax",
  "-language:implicitConversions",
  "-deprecation",     // emit warning and location for usages of deprecated APIs
  "-explain",         // explain errors in more detail
  "-explain-types",   // explain type errors in more detail
  "-feature",         // emit warning and location for usages of features that should be imported explicitly
  "-indent",          // allow significant indentation.
  "-print-lines",     // show source code line numbers.
  "-unchecked",       // enable additional warnings where generated code depends on assumptions
  "-Ykind-projector", // allow `*` as wildcard to be compatible with kind projector
  // "-Xfatal-warnings"  // fail the compilation if there are any warnings
  "-Xmigration"       // warn about constructs whose behavior may have changed since version
)

//---------- Failed Experiments in Site Generation with Scala 3 scaladoc. API Docs OK, unidoc style them
//Compile / doc / ta  rget ++= Seq("-d", "output-docs")
Compile / doc / scalacOptions ++= Seq("-project", "my-project")
ThisBuild / Compile / doc / scalacOptions ++=
  Seq("-project", "IBKR Web Portal and Flex Queries", "-project-version", "0.0.2") ++
    Seq("-revision", "1.0")

ThisBuild / apiURL          := Some(url("https://example.org/api/"))
ThisBuild / autoAPIMappings := true

lazy val root = project.in(file("."))
  .aggregate(flexquery.jvm, flexquery.js)
  .settings(
    name           := "ibkr-flexquery-root",
    publish / skip := true,
    scalacOptions  := myScalacOptions
  )

lazy val flexquery = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("./modules/flexquery"))
  .settings(
    name        := "ibkr-flexquery",
    description := "A minimal Flex Query API Library",
    libraryDependencies ++= Seq(
      XLib.cats.value,
      XLib.catsEffect.value,
      XLib.catsRetry.value,
      XLib.fs2.value,
      XLib.fs2DataXml.value,
      XLib.http4sCore.value,
      XLib.http4sDsl.value,
      XLib.http4sEmber.value,
      XLib.monocle.value,
      XLib.pprint.value,
      XLib.scalaXML.value,
      XLib.scribe.value
    ),
    libraryDependencies ++= Seq(XLib.munit.value, XLib.munitCats.value, XLib.scribe.value),
    scalacOptions ++= myScalacOptions,
    Test / javaOptions += "-DinCI=true"
  ).jvmSettings(
    libraryDependencies ++= Libs.scribeSLF4J
  )
