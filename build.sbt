name := "HelloV1"

version := "0.1"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.6.10",
  "com.typesafe.akka" %% "akka-persistence" % "2.6.10",
  "com.typesafe.akka"           %% "akka-persistence-typed" % "2.6.10",
  "org.iq80.leveldb"  % "leveldb" % "0.7",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
"com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.6.10",
"com.typesafe.akka" % "akka-stream-experimental_2.11" % "1.0"
)