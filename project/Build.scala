import sbt.Keys._
import sbt.{Resolver, _}

//noinspection ScalaFileName
object Common {

  def projectSettings = Seq(
    scalaVersion := "2.12.8",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-numeric-widen",
    ),
    resolvers ++= Seq(
      "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots"),
      Resolver.sonatypeRepo("staging")),
    libraryDependencies ++= Seq(
      "javax.inject" % "javax.inject" % "1",
      "biz.enef" %% "slogging-slf4j" % "0.6.1",
      "org.slf4j" % "slf4j-simple" % "1.7.+",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "org.scalamock" %% "scalamock" % "4.1.0" % Test,
    ),
    scalacOptions in Test ++= Seq("-Yrangepos")
  )
}
