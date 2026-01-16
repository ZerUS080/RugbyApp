package com.example.rugbyapp

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object NetworkManager {
    private const val BASE_URL = "http://10.0.2.2/rugby_app_v2/api/"
        suspend fun getPlayers(): List<Player> {
            return withContext(Dispatchers.IO) {
                try {
                    val url = URL("${BASE_URL}get_players.php")
                    Log.d("NetworkManager", "🔄 Conectando a: $url")

                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000

                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream = connection.inputStream
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val response = StringBuilder()
                        var line: String?

                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }

                        reader.close()
                        inputStream.close()

                        val jsonResponse = response.toString()
                        Log.d("NetworkManager", " Respuesta: ${jsonResponse.take(100)}...")

                        connection.disconnect()
                        parsePlayersJson(jsonResponse)
                    } else {
                        Log.e("NetworkManager", " Error HTTP: ${connection.responseCode}")
                        emptyList()
                    }
                } catch (e: Exception) {
                    Log.e("NetworkManager", " Error: ${e.message}")
                    e.printStackTrace()
                    emptyList()
                }
            }
        }

        suspend fun getPlayerHistory(playerId: Int): List<TeamHistory> {
            return withContext(Dispatchers.IO) {
                try {
                    val url = URL("${BASE_URL}get_player_history.php?player_id=$playerId")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 5000

                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream = connection.inputStream
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val response = StringBuilder()
                        var line: String?

                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }

                        reader.close()
                        inputStream.close()
                        connection.disconnect()

                        parseHistoryJson(response.toString())
                    } else {
                        Log.e("NetworkManager", "Error: ${connection.responseCode}")
                        emptyList()
                    }
                } catch (e: Exception) {
                    Log.e("NetworkManager", "Error: ${e.message}")
                    emptyList()
                }
            }
        }
        private fun parsePlayersJson(jsonString: String): List<Player> {
            return try {
                val playersList = mutableListOf<Player>()
                val jsonArray = JSONArray(jsonString)

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    val player = Player(
                        id = jsonObject.getInt("id"),
                        name = jsonObject.getString("name"),
                        birthDate = jsonObject.getString("birthDate"),
                        position = jsonObject.getString("position"),
                        nationality = jsonObject.getString("nationality"),
                        currentTeam = jsonObject.getString("currentTeam"),
                        imageName = jsonObject.getString("imageName")
                    )
                    playersList.add(player)
                }

                Log.d("NetworkManager", "Parseados ${playersList.size} jugadores")
                playersList
            } catch (e: Exception) {
                Log.e("NetworkManager", "Error parseando JSON: ${e.message}")
                e.printStackTrace()
                emptyList()
            }
        }

        private fun parseHistoryJson(jsonString: String): List<TeamHistory> {
            return try {
                val historyList = mutableListOf<TeamHistory>()
                val jsonArray = JSONArray(jsonString)

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    val history = TeamHistory(
                        id = jsonObject.getInt("id"),
                        playerId = jsonObject.getInt("playerId"),
                        teamName = jsonObject.getString("teamName"),
                        years = jsonObject.getString("years"),
                        matchesPlayed = jsonObject.getInt("matchesPlayed")
                    )

                    historyList.add(history)
                }

                historyList
            } catch (e: Exception) {
                Log.e("NetworkManager", "Error parseando historial: ${e.message}")
                emptyList()
            }
        }
}