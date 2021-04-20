// SPDX-License-Identifier: BSD-3-Clause

organization := "edu.berkeley.cs"
name := "maltese-smt"
version := "0.5-SNAPSHOT"

// scala settings
scalaVersion := "2.12.13"
crossScalaVersions := Seq("2.12.13", "2.13.5")
scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")
// Scala 2.12 requires Java 8.
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")


// JNA for SMT Solver bindings
libraryDependencies += "net.java.dev.jna" % "jna" % "5.4.0"
libraryDependencies += "net.java.dev.jna" % "jna-platform" % "5.4.0"
libraryDependencies += "com.github.com-github-javabdd" % "com.github.javabdd" % "1.0.1"
// test dependencies
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.6" % "test"
// for now we depend on treadle for its VCD library
resolvers += Resolver.sonatypeRepo("snapshots")
libraryDependencies += "edu.berkeley.cs" %% "treadle" % "1.5-SNAPSHOT"

// source layout
scalaSource in Compile := baseDirectory.value / "src"
resourceDirectory in Compile := baseDirectory.value / "src" / "resources"
scalaSource in Test := baseDirectory.value / "test"
resourceDirectory in Test := baseDirectory.value / "test" / "resources"

// publishing settings
publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { x => false }

// scm is set by sbt-ci-release
pomExtra := (
<url>http://chisel.eecs.berkeley.edu/</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://www.opensource.org/licenses/bsd-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
<developers>
  <developer>
    <id>ekiwi</id>
    <name>Kevin Laeufer</name>
  </developer>
</developers>
)

publishTo := {
  val v = version.value
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) {
    Some("snapshots".at(nexus + "content/repositories/snapshots"))
  } else {
    Some("releases".at(nexus + "service/local/staging/deploy/maven2"))
  }
}
