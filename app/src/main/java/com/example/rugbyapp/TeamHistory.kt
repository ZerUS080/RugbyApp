package com.example.rugbyapp

data class TeamHistory(
    val id: Int,
    val playerId: Int,
    val teamName: String,
    val years: String,
    val matchesPlayed: Int
)