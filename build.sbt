import sbt.Keys.libraryDependencies

ThisBuild / githubOwner       := "odenzo"
ThisBuild / githubRepository  := "ibkr-flexquery-lib"
ThisBuild / githubTokenSource := TokenSource.Or(TokenSource.GitConfig("github.token"), TokenSource.Environment("GITHUB_TOKEN"))

ThisBuild / publishMavenStyle := true
ThisBuild / bspEnabled        := false
ThisBuild / scalaVersion      := "3.1.1"
ThisBuild / test / fork       := false
ThisBuild / versionScheme     := Some("early-semver")

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

// Seq("-versions-dictionary-url", "https://odenzo.github.com/ibkr-portal/versions.json") ++
//   Seq("-groups") ++
//    Seq("-external-mappings:.*scala.*::scaladoc3::https://scala-lang.org/api/3.x/,.*java.*::javadoc::https://docs.oracle")
// .com/javase/8/docs/api/

//

lazy val root = project.in(file("."))
  .aggregate(models.jvm, models.js, ibkr.jvm, ibkr.js)
  .settings(
    name           := "ibkr",
    description    := "A minimal IBKR Portal and Flex Query Web API Library",
    startYear      := Some(2022),
    publish / skip := true,
    scalacOptions  := myScalacOptions
  )

// Do I really want to run ScalaJS Tests ore JV< Test good enough?
lazy val models = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("./modules/models"))
  .settings(
    name        := "ibkr-flexquery-models",
    description := "Models for use in ScalaJS and Scala",
    scalacOptions ++= myScalacOptions,
    test / fork := false,
    libraryDependencies ++= Seq(
      XLib.scribe.value,
      XLib.cats.value,
      XLib.catsEffect.value,
      XLib.circeCore.value,
      XLib.circeGeneric.value,
      XLib.munit.value,
      XLib.munitCats.value,
      XLib.pprint.value,
      XLib.monocle.value,
      XLib.http4sCore.value,
      XLib.http4sEmber.value,
      XLib.scalaXML.value
    )
  )

lazy val ibkr = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("./modules/ibkr"))
  .dependsOn(models)
  .settings(
    name          := "ibkr-flexquery",
    description   := "A minimal Flex Query API Library",
    libraryDependencies ++= Seq(
      XLib.http4sCore.value,
      XLib.http4sDsl.value,
      XLib.http4sCirce.value,
      XLib.http4sEmber.value,
      XLib.catsRetry.value,
      XLib.fs2.value,
      XLib.fs2DataXml.value
    ),
    libraryDependencies ++= Seq(XLib.munit.value, XLib.munitCats.value, XLib.scribe.value),
    scalacOptions := myScalacOptions,
    Test / javaOptions += "-DCI=true",
    test / fork   := false
  ).jvmSettings(
    libraryDependencies ++= Libs.scribeSLF4J
  )
