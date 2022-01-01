ThisBuild / scalaVersion := "2.13.1"

lazy val root = project.in(file(".")).
  aggregate(qucore.js, qucore.jvm).
  settings(
    publish := {},
    publishLocal := {},
  )

lazy val qucore = crossProject(JSPlatform, JVMPlatform).in(file(".")).
  settings(
    name := "qucore",
    version := "0.1-SNAPSHOT",
  ).
  jvmSettings(
    // Add JVM-specific settings here

    //https://alvinalexander.com/scala/how-to-use-junit-testing-with-scala/
    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.8" % "test->default",
      "com.lihaoyi" %%% "upickle" % "1.1.0",
      "com.github.tototoshi" %% "scala-csv" % "1.3.6",
      "junit" % "junit" % "4.13")  //not sure why intellisense wants it explicit
  ).
  jsSettings(
    // Add JS-specific settings here
    scalaJSUseMainModuleInitializer := true,

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0"
    )

  )

