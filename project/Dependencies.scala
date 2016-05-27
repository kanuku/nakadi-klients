import sbt._

object Dependencies {

	val akkaVersion = "2.4.4"

	val apiDeps = {
		Seq(
		"com.fasterxml.jackson.module" %% "jackson-module-scala"   						% "2.6.3",
		"com.fasterxml.jackson.core"   % "jackson-core"           						% "2.7.0")
	}

	val clientDeps = {
		Seq(
			"com.typesafe"                 % "config"                  						% "1.3.0",
		  "com.google.guava"             % "guava"                   						% "19.0",
		  "com.typesafe.scala-logging"   %% "scala-logging"          						% "3.1.0",
		  "com.fasterxml.jackson.core"   % "jackson-core"           						% "2.7.3",
		  "com.fasterxml.jackson.module" %% "jackson-module-scala"   						% "2.7.3",
		  "ch.qos.logback"               % "logback-classic"         						% "1.1.3",
			"com.typesafe.akka"            %% "akka-actor"            	 					% akkaVersion,
			"com.typesafe.akka"            %% "akka-http-experimental" 						% akkaVersion,
			"com.typesafe.akka"            %% "akka-stream" 											% akkaVersion,
			"com.typesafe.akka"            %% "akka-testkit"           						% akkaVersion % "test",
		  "org.scalatest"                %% "scalatest"              						% "2.2.6" % "test",
		  "com.google.code.findbugs"     % "jsr305"                  						% "1.3.9" % "test",
		  "junit"                        % "junit"                   						% "4.12" % "test",
			"org.mockito" 								 % "mockito-core" 											% "1.10.19" % "test"
		)
	}
}
