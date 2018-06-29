package com.arjun

object Configs {

  case class Database(
      username: String,
      password: String,
      url: String,
      driverClassName: String
  )

  case class Main(
      database: Database
  )

}
