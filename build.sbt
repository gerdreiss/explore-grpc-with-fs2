ThisBuild / scalaVersion := "3.3.1"

lazy val protobuf =
  project
    .in(file("modules/protobuf"))
    .settings(
      name := "protobuf"
    )
    .enablePlugins(Fs2Grpc)

lazy val root =
  project
    .in(file("modules/server"))
    .settings(
      name := "grpc-with-fs2",
      libraryDependencies ++= Seq(
        "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion
        // "org.http4s"          %% "http4s-ember-server" % "0.23.24",
        // "org.http4s"          %% "http4s-dsl"          % "0.23.24",
        // "org.http4s"          %% "http4s-circe"        % "0.23.24",
        // "com.disneystreaming" %% "weaver-cats"         % "0.8.3" % Test,
      ),
      testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    )
    .dependsOn(protobuf)
