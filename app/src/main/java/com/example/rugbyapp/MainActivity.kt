package com.example.rugbyapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rugbyapp.ui.theme.RugbyAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RugbyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerListScreen()
                }
            }
        }
    }
}

@Composable
fun PlayerListScreen() {
    val players = remember { mutableStateOf(emptyList<Player>()) }
    val isLoading = remember { mutableStateOf(true) }
    val serverConnected = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                serverConnected.value = GestorSQLExternModern.testConnection()

                if (serverConnected.value) {
                    players.value = GestorSQLExternModern.getPlayers()
                }
            } catch (e: Exception) {
                serverConnected.value = false
            } finally {
                isLoading.value = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Jugadores de Rugby",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (isLoading.value) {
            LoadingState()
        } else {
            if (serverConnected.value && players.value.isNotEmpty()) {
                PlayersList(players = players.value)
            } else if (!serverConnected.value) {
                ServerUnavailableState()
            } else {
                EmptyState()
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Conectando con el servidor...")
            Text(
                text = "URL: ${GestorSQLExternModern.getBaseUrl()}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun ServerUnavailableState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "SERVIDOR NO DISPONIBLE",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            Text(
                text = "No se puede conectar a:",
                fontSize = 16.sp
            )

            Text(
                text = GestorSQLExternModern.getBaseUrl(),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Verifica que:\n" +
                        "1. XAMPP esté ejecutándose (Apache)\n" +
                        "2. Los archivos PHP estén en la carpeta correcta\n" +
                        "3. La URL sea accesible desde el emulador",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Servidor disponible pero sin datos")
    }
}

@Composable
fun PlayersList(players: List<Player>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(players) { player ->
            PlayerCard(player = player)
        }
    }
}

@Composable
fun PlayerCard(player: Player) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("player_id", player.id)
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl = if (player.imageUrl.isNotEmpty()) {
                player.imageUrl
            } else {
                "${GestorSQLExternModern.getPlayersImgPath()}${player.imageName}"
            }

            AsyncImage(
                model = imageUrl,
                contentDescription = player.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.placeholder)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = player.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (player.position.isNotEmpty()) {
                    Text(
                        text = player.position,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}