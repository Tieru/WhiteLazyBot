libraryDependencies += "org.flywaydb" % "flyway-core" % "6.3.2"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.12"
libraryDependencies += "com.typesafe" % "config" % "1.2.1"

TaskKey[Unit]("migrate") := (runMain in Compile).toTask(" migrations.MigrationsLauncher").value

