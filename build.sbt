import com.typesafe.sbt.site.util.SiteHelpers
import com.typesafe.sbt.SbtGhPages.GhPagesKeys._

// **************************************
// Tutorial: edit the following variables

val USER = "denisrosset"
val REPO = "fizzmono"
val NAME = "fizzmono"
val ORG = "org.fizzorg"

val APIDIR = "latest/api"
val TUTDIR = "_tut"

// derived info
lazy val GITURL = s"git@github.com:$USER/$REPO.git"
lazy val SCMINFO = ScmInfo(url(s"https://github.com/$USER/$REPO"), s"scm:git:$GITURL")
lazy val APIURL = s"http://$USER.github.io/$REPO/$APIDIR"

// **************************************

val tutorialSubDirName = settingKey[String]("Website tutorial directory")
val apiSubDirName = settingKey[String]("Scaladoc API directory")

name := NAME
organization := ORG
scalaVersion := "2.11.8"
scmInfo := Some(SCMINFO)
apiURL := Some(url(APIURL)) // enable external projects to link to our Scaladoc

// ****************************************
// Sbt-site and sbt-ghpages, general config

ghpages.settings
siteMappings ++= Seq(
  file("CONTRIBUTING.md") -> "contributing.md"
)
ghpagesNoJekyll := false
git.remoteRepo := GITURL
includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.yml" | "*.md"

// **************
// To use Doctest
// Customize: the Scalatest version

doctestSettings

doctestTestFramework := DoctestTestFramework.ScalaTest // opinion: we default to Scalatest

// the following two lines specify an explicit Scalatest version and tell sbt-doctest to
// avoid importing new dependencies
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.0-M7" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
)

doctestWithDependencies := false

// ***************
// To use Scaladoc
// Customize: remove hierarchy diagram generation if Graphviz is not installed

// enable automatic linking to the external Scaladoc of our own managed dependencies
autoAPIMappings := true

scalacOptions in (Compile, doc) ++= Seq(
  // we want warnings to be fatal (on broken links for example)
  "-Xfatal-warnings",
  // link to source code, yes that's an euro symbol
  "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
  "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath,
  // generate type hierarchy diagrams, runs graphviz
  "-diagrams"
)

apiSubDirName := APIDIR

addMappingsToSiteDir(mappings in (Compile, packageDoc), apiSubDirName)

// **********
// To use Tut

tutSettings

tutorialSubDirName := TUTDIR
addMappingsToSiteDir(tut, tutorialSubDirName)
tutScalacOptions ~= (_.filterNot(Set("-Ywarn-unused-import", "-Ywarn-dead-code")))
