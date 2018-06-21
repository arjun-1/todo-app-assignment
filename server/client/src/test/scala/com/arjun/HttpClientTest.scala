package com.arjun

import argonaut.Parse
import de.heikoseeberger.akkahttpargonaut.ArgonautSupport

import scala.concurrent.Await
import scala.concurrent.duration._
object HttpClientTest extends App  {
  val client = HttpClient

    val task = Task(None, None, true, "")

  val result1 = client.addTask(task)
  println(Await.result(result1.value, 10 seconds))

//  val string =
//  """
//    |{"text":"sdfsdfs","isDone":true,"userId":"bbec2d34-e097-4294-9c35-6653caf1024a","id":"b689cc42-1f77-4c24-8ad3-514efc949cf1"}
//  """.stripMargin
//
//
//  import argonaut._, Argonaut._, ArgonautShapeless._
//
//
//  val task = Task(None, None, true, "")
//  val encode = EncodeJson.of[Task]
//  val json: Json = encode(task)
//  println(json.nospaces)
//
//  val decode = DecodeJson.of[Task]
//  val result = decode.decodeJson(json)
//  println(result)



}
