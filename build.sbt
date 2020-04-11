name := "WhiteBumBot"

version := "0.1"

lazy val global = project
  .in(file("."))
  .settings(Common.projectSettings)
  .enablePlugins(CodegenPlugin)
  .aggregate(
    flyway,
  )

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "4.2.2",
  "net.codingwell" %% "scala-guice" % "4.2.2",
  "com.osinka.i18n" %% "scala-i18n" % "1.0.2",

  "com.bot4s" %% "telegram-core" % "4.0.0-RC2",
  "com.bot4s" %% "telegram-akka" % "4.0.0-RC2",

  "org.parboiled" %% "parboiled" % "2.1.5",

  "eu.timepit" %% "fs2-cron-core" % "0.2.2",
  "org.typelevel" %% "cats-core" % "1.6.1",
  "org.typelevel" %% "cats-effect" % "1.4.0",

  "com.typesafe.akka" %% "akka-actor" % "2.5.23",
)

// Database
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
  "org.postgresql" % "postgresql" % "42.2.5",
  "com.github.tminglei" %% "slick-pg" % "0.16.3",
  "com.typesafe" % "config" % "1.2.1",
  "com.novocode" % "junit-interface" % "0.10" % Test,
)

lazy val flyway = (project in file("modules/flyway"))
  .settings(Common.projectSettings)

lazy val databaseUrl = sys.env.getOrElse("DB_DEFAULT_URL", "jdbc:postgresql://localhost/white_bum_bot")
lazy val databaseUser = sys.env.getOrElse("DB_DEFAULT_USER", "slavik")
lazy val databasePassword = sys.env.getOrElse("DB_DEFAULT_PASSWORD", "1234")

slickCodegenDatabaseUrl := databaseUrl
slickCodegenDatabaseUser := databaseUser
slickCodegenDatabasePassword := databasePassword
slickCodegenDriver := slick.jdbc.PostgresProfile
slickCodegenJdbcDriver := "org.postgresql.Driver"
slickCodegenOutputPackage := "schema.data"
slickCodegenExcludedTables := Seq("flyway_schema_history")

sourceGenerators in Compile += slickCodegen.taskValue
