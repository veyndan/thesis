package com.veyndan.thesis

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

fun main() {
    val driver = JdbcSqliteDriver(name = "jdbc:sqlite:/Users/veyndan/IdeaProjects/thesis/src/main/thesis.db")
    Database.Schema.create(driver)
    val database = Database(driver)

//    val playerQueries: PlayerQueries = database.playerQueries
//
//    println(playerQueries.selectAll().executeAsList())
//    // Prints [HockeyPlayer.Impl(15, "Ryan Getzlaf")]
//
//    playerQueries.insert(player_number = 10, full_name = "Corey Perry")
//    println(playerQueries.selectAll().executeAsList())
//    // Prints [HockeyPlayer.Impl(15, "Ryan Getzlaf"), HockeyPlayer.Impl(10, "Corey Perry")]
}
