package com.example.rugbyapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rugbyapp.ui.theme.RugbyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RugbyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerListScreen(onExitClick = { finish() })
                }
            }
        }
    }
}

fun getSamplePlayers(): List<Player> {
    return listOf(
        Player(
            id = 1,
            name = "Jonny Wilkinson",
            birthDate = "1979-05-25",
            position = "Fly-half",
            nationality = "Inglaterra",
            currentTeam = "Retirado",
            imageName = "jonny_wilkinson.jpg"
        ),
        Player(
            id = 2,
            name = "Sergio Parisse",
            birthDate = "1983-09-12",
            position = "Number 8",
            nationality = "Italia",
            currentTeam = "Retirado",
            imageName = "sergio_parisse.jpg"
        ),
        Player(
            id = 3,
            name = "Dan Carter",
            birthDate = "1982-03-05",
            position = "Fly-half",
            nationality = "Nueva Zelanda",
            currentTeam = "Retirado",
            imageName = "dan_carter.jpg"
        )
    )
}

@Composable
fun PlayerListScreen(onExitClick: () -> Unit) {
    val players = remember { mutableStateOf(emptyList<Player>()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Log.d("MainActivity", " Iniciando carga de jugadores desde servidor...")

        try {
            val serverPlayers = NetworkManager.getPlayers()
            Log.d("MainActivity", " Jugadores obtenidos del servidor: ${serverPlayers.size}")

            val nombres = serverPlayers.joinToString { it.name }
            Log.d("MainActivity", " Orden recibido: $nombres")

            if (serverPlayers.isNotEmpty()) {
                players.value = serverPlayers
                errorMessage.value = null
                Log.d("MainActivity", " Usando datos DEL SERVIDOR")

                serverPlayers.forEach { player ->
                    Log.d("MainActivity", "    ${player.name} (ID: ${player.id})")
                }
            } else {
                players.value = getSamplePlayers()
                errorMessage.value = " Usando datos locales (servidor vacío)"
                Log.d("MainActivity", " Servidor vacío, usando datos LOCALES")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", " Error: ${e.message}")
            players.value = getSamplePlayers()
            errorMessage.value = " Error: ${e.message}. Usando datos locales."
            Log.d("MainActivity", " Error de conexión, usando datos LOCALES")
        }

        isLoading.value = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🏉 Jugadores de Rugby",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (errorMessage.value != null) {
            Text(
                text = errorMessage.value!!,
                color = if (errorMessage.value!!.contains("")) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (isLoading.value) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(" Conectando con el servidor...")
                Text(" URL: http://10.0.2.2/rugby_app_v2/api/get_players.php", fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(players.value) { player ->
                    PlayerCard(player = player)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onExitClick,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text(" Salir", fontSize = 18.sp)
            }

            Text(
                text = "Total jugadores: ${players.value.size}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}


@Composable
fun PlayerCard(player: Player) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = player.name,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(" Posición: ${player.position}", fontSize = 16.sp)
                    Text(" Nacionalidad: ${player.nationality}", fontSize = 16.sp)
                    Text(" Fecha nacimiento: ${player.birthDate}", fontSize = 16.sp)
                    Text(" Equipo actual: ${player.currentTeam}", fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(" Información técnica:", fontWeight = FontWeight.Bold)
                    Text("   • ID en base de datos: ${player.id}", fontSize = 14.sp)
                    Text("   • Nombre imagen: ${player.imageName}", fontSize = 14.sp)
                    Text("   • Origen: Servidor MySQL", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)

                    Spacer(modifier = Modifier.height(8.dp))


                    Text(
                        text = "Estos datos vienen de la base de datos rugby_app_v2",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showDialog = true
                Log.d("PlayerCard", " Clic en: ${player.name} (ID: ${player.id})")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageResId = when (player.imageName) {
                "jonny_wilkinson.jpg" -> R.drawable.jonny_wilkinson
                "sergio_parisse.jpg" -> R.drawable.sergio_parisse
                "dan_carter.jpg" -> R.drawable.dan_carter
                else -> R.drawable.jonny_wilkinson
            }

            Image(
                painter = painterResource(id = imageResId),
                contentDescription = player.name,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.size(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.name,
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = player.position,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Row {
                    Text(
                        text = player.nationality,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "• ${player.currentTeam}",
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = "ID: ${player.id}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "",
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}