package com.example.rugbyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
                    RugbyPlayerScreen(onExitClick = { finish() })
                }
            }
        }
    }
}

@Composable
fun RugbyPlayerScreen(onExitClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = stringResource(id = R.string.title),
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Imagen del jugador
        Image(
            painter = painterResource(id = R.drawable.jonny_wilkinson),
            contentDescription = "Jonny Wilkinson",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 16.dp)
        )

        // Nombre
        Text(
            text = stringResource(id = R.string.player_name),
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // País
        Text(
            text = stringResource(id = R.string.country),
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 5.dp)
        )

        // Posición
        Text(
            text = stringResource(id = R.string.position),
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Historial
        Text(
            text = stringResource(id = R.string.team_history),
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 15.dp)
        )

        Text(
            text = stringResource(id = R.string.team1),
            fontSize = 16.sp
        )

        Text(
            text = stringResource(id = R.string.team2),
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = stringResource(id = R.string.team3),
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Botón
        Button(
            onClick = onExitClick,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.exit_button),
                fontSize = 18.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RugbyPlayerScreenPreview() {
    RugbyAppTheme {
        RugbyPlayerScreen(onExitClick = {})
    }
}