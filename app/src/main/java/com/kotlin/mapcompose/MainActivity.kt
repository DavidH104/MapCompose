
package com.kotlin.mapcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        Configuration.getInstance().userAgentValue =
            "MapComposePractica/1.0 ($packageName)"

        setContent {
            MaterialTheme {
                MyOpenStreetMap()
            }
        }
    }
}

data class Lugar(
    val nombre: String,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double
)

@Composable
fun MyOpenStreetMap() {

    val context = LocalContext.current

    val lugares = remember {
        listOf(
            Lugar(
                "Volcan del Teide",
                "El pico mas alto de Espana",
                28.2724, -16.6425
            ),
            Lugar(
                "Observatorio del Teide",
                "Centro de investigacion astronomica",
                28.3004, -16.5097
            ),
            Lugar(
                "Los Gigantes",
                "Impresionantes acantilados",
                28.2430, -16.8410
            ),
            Lugar(
                "Playa de las Teresitas",
                "Playa de arena dorada",
                28.5084, -16.1855
            ),
            Lugar(
                "Auditorio de Tenerife",
                "Edificio cultural y musical",
                28.4568, -16.2518
            ),
            Lugar(
                "Loro Parque",
                "Parque zoologico en Puerto de la Cruz",
                28.4086, -16.5655
            ),
            Lugar(
                "Parque Rural de Anaga",
                "Reserva natural de Tenerife",
                28.5320, -16.2800
            ),
            Lugar(
                "Playa de las Americas",
                "Zona turistica del sur de Tenerife",
                28.0605, -16.7295
            )
        )
    }

    val mapView = remember(context) {
        MapView(context).apply {

            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)

            zoomController.setVisibility(
                CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT
            )

            // Vista inicial de toda la isla
            controller.setZoom(9.0)
            controller.setCenter(
                GeoPoint(28.2916, -16.6291)
            )

            // Crear todos los marcadores
            lugares.forEach { lugar ->

                val marcador = Marker(this)

                marcador.position = GeoPoint(
                    lugar.latitud,
                    lugar.longitud
                )

                marcador.title = lugar.nombre
                marcador.snippet = lugar.descripcion

                marcador.setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                )

                overlays.add(marcador)
            }

            invalidate()
        }
    }

    DisposableEffect(mapView) {
        mapView.onResume()

        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = "© OpenStreetMap contributors",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .background(
                    Color.White.copy(alpha = 0.85f)
                )
                .padding(4.dp),
            color = Color.Black,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
