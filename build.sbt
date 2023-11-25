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
      ),
    )
    .dependsOn(protobuf)
