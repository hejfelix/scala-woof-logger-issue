val versionHttp4S = "1.0.0-M38"

lazy val root = (project in file("."))
  .settings(
    name := "scala-woof-logger-issue",
    version := "0.1",
    organization := "com.may",
    scalaVersion := "3.4.1",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.10.0",
      "org.typelevel" %% "cats-effect" % "3.5.1",
      "org.legogroup" %% "woof-core" % "0.7.0",
      "org.legogroup" %% "woof-slf4j-2" % "0.7.0",
      "org.http4s" %% s"http4s-ember-client" % versionHttp4S,
      "org.http4s" %% s"http4s-ember-server" % versionHttp4S,
      "org.http4s" %% s"http4s-dsl" % versionHttp4S,
      "org.http4s" %% s"http4s-circe" % versionHttp4S,
      "co.fs2" %% "fs2-core" % "3.5.0",
    )
  )
