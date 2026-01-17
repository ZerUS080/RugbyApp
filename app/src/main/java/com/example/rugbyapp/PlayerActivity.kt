package com.example.rugbyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rugbyapp.ui.theme.RugbyAppTheme
import kotlinx.coroutines.launch

class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerId = intent.getIntExtra("player_id", -1)

        setContent {
            RugbyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerDetailScreen(playerId = playerId)
                }
            }
        }
    }
}

@Composable
fun PlayerDetailScreen(playerId: Int) {
    val player = remember { mutableStateOf<Player?>(null) }
    val historial = remember { mutableStateOf(emptyList<EtapaEquip>()) }
    val isLoading = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(playerId) {
        coroutineScope.launch {
            try {
                val allPlayers = GestorSQLExternModern.getPlayers()
                player.value = allPlayers.find { it.id == playerId }

                historial.value = GestorSQLExternModern.getPlayerHistory(playerId)
            } catch (e: Exception) {
            } finally {
                isLoading.value = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading.value) {
            CircularProgressIndicator()
        } else if (player.value == null) {
            Text("Jugador no encontrado")
        } else {
            val imageUrl = if (player.value!!.imageUrl.isNotEmpty()) {
                player.value!!.imageUrl
            } else {
                "${GestorSQLExternModern.getPlayersImgPath()}${player.value!!.imageName}"
            }

            AsyncImage(
                model = imageUrl,
                contentDescription = player.value!!.name,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.placeholder)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = player.value!!.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            PlayerInfoRow(title = "Posición", value = player.value!!.position)
            PlayerInfoRow(title = "Nacionalidad", value = player.value!!.nationality)
            PlayerInfoRow(title = "Fecha Nacimiento", value = player.value!!.birthDate)
            PlayerInfoRow(title = "Equipo Actual", value = player.value!!.currentTeam)

            Spacer(modifier = Modifier.height(24.dp))

            if (historial.value.isNotEmpty()) {
                Text(
                    text = "Historial de Equipos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                historial.value.forEach { etapa ->
                    HistorialItem(etapa = etapa)
                }
            } else {
                Text(
                    text = "No hay historial disponible",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PlayerInfoRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontWeight = FontWeight.SemiBold)
        Text(value)
    }
}
@Composable
fun HistorialItem(etapa: EtapaEquip) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (etapa.logoUrl.isNotEmpty()) {
                AsyncImage(
                    model = etapa.logoUrl,
                    contentDescription = "Logo ${etapa.team}",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = etapa.team,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = etapa.years,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}