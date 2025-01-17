package com.example.cryptotrackerapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cryptotrackerapplication.core.navigation.AdaptiveCoinListDetailPane
import com.example.cryptotrackerapplication.crypto.presentation.coin_detail.CoinDetailsScreen
import com.example.cryptotrackerapplication.crypto.presentation.coin_list.CoinListScreen
import com.example.cryptotrackerapplication.crypto.presentation.coin_list.CoinListViewModel
import com.example.cryptotrackerapplication.ui.theme.CryptoTrackerApplicationTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoTrackerApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AdaptiveCoinListDetailPane(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

