package com.example.rugbyapp

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

object GestorSQLExternModern {
    private const val BASE_URL = "http://10.0.2.2/rugby_app_v2"
    private const val PLAYERS_IMG_PATH = "$BASE_URL/img/jugadors/"  // "jugadors" no "players"
    private const val TEAMS_IMG_PATH = "$BASE_URL/img/teams/"

    suspend fun testConnection(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val testUrl = URL("http://10.0.2.2/")
                val connection = testUrl.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 2000
                val responseCode = connection.responseCode
                connection.disconnect()
                responseCode in 200..499
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun getPlayers(): List<Player> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("${BASE_URL}/get_players.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = 10000
                    readTimeout = 10000
                }

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    parsePlayersJson(response)
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getPlayerHistory(playerId: Int): List<EtapaEquip> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("${BASE_URL}/get_player_history.php?player_id=$playerId")
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000
                }

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    parseHistoryJson(response)
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private fun parsePlayersJson(jsonString: String): List<Player> {
        return try {
            val players = mutableListOf<Player>()
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                val imageName = jsonObject.optString("image_name", "").ifEmpty {
                    jsonObject.optString("photo", "")
                }

                val player = Player(
                    id = jsonObject.optInt("id", 0),
                    name = jsonObject.optString("name", "").ifEmpty {
                        jsonObject.optString("nombre", "Jugador")
                    },
                    birthDate = jsonObject.optString("birthDate", "").ifEmpty {
                        jsonObject.optString("fecha_nacimiento", "")
                    },
                    position = jsonObject.optString("position", "").ifEmpty {
                        jsonObject.optString("posicion", "")
                    },
                    nationality = jsonObject.optString("nationality", "").ifEmpty {
                        jsonObject.optString("nacionalidad", "")
                    },
                    currentTeam = jsonObject.optString("currentTeam", "").ifEmpty {
                        jsonObject.optString("current_team", "")
                    },
                    imageName = imageName,
                    imageUrl = if (imageName.isNotBlank()) "$PLAYERS_IMG_PATH$imageName" else ""
                )

                if (player.id > 0 && player.name.isNotBlank()) {
                    players.add(player)
                }
            }
            players
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseHistoryJson(jsonString: String): List<EtapaEquip> {
        return try {
            val history = mutableListOf<EtapaEquip>()

            if (jsonString.trim() == "[]" || jsonString.contains("error") || jsonString == "null") {
                return emptyList()
            }

            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                val logoUrl = jsonObject.optString("logoUrl", "").ifEmpty {
                    val teamName = jsonObject.optString("teamName", "").ifEmpty {
                        jsonObject.optString("team_name", "")
                    }
                    getTeamLogoUrl(teamName)
                }

                val etapa = EtapaEquip(
                    team = jsonObject.optString("teamName", "").ifEmpty {
                        jsonObject.optString("team_name", "Equipo")
                    },
                    years = jsonObject.optString("years", "").ifEmpty {
                        val start = jsonObject.optString("start_year", "")
                        val end = jsonObject.optString("end_year", "")
                        if (start.isNotBlank() && end.isNotBlank()) "$start–$end"
                        else if (start.isNotBlank()) start
                        else ""
                    },
                    logoUrl = logoUrl
                )

                if (etapa.team.isNotBlank()) {
                    history.add(etapa)
                }
            }
            history
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun getTeamLogoUrl(teamName: String): String {
        val teamLower = teamName.lowercase()
        return when {
            teamLower.contains("newcastle") || teamLower.contains("falcon") ->
                "$TEAMS_IMG_PATH/newcastle_falcons.png"
            teamLower.contains("toulon") ->
                "$TEAMS_IMG_PATH/rc_toulon.png"
            teamLower.contains("crusader") ->
                "$TEAMS_IMG_PATH/crusaders.png"
            teamLower.contains("england") || teamLower.contains("inglaterra") ->
                "$TEAMS_IMG_PATH/england.png"
            teamLower.contains("new zealand") || teamLower.contains("nueva zelanda") || teamLower.contains("all blacks") ->
                "$TEAMS_IMG_PATH/new_zealand.png"
            teamLower.contains("stade") || teamLower.contains("français") || teamLower.contains("francais") ->
                "$TEAMS_IMG_PATH/stade_francais.png"
            teamLower.contains("italy") || teamLower.contains("italia") ->
                "$TEAMS_IMG_PATH/italy.png"
            else -> ""
        }
    }

    fun getBaseUrl(): String = BASE_URL
    fun getPlayersImgPath(): String = PLAYERS_IMG_PATH
    fun getTeamsImgPath(): String = TEAMS_IMG_PATH
}