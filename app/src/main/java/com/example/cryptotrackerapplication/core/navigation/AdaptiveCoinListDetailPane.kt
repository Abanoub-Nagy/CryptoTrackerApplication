@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.example.cryptotrackerapplication.core.navigation

import android.widget.Toast
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cryptotrackerapplication.core.presentation.util.ObserveAsEvent
import com.example.cryptotrackerapplication.core.presentation.util.toString
import com.example.cryptotrackerapplication.crypto.presentation.coin_detail.CoinDetailsScreen
import com.example.cryptotrackerapplication.crypto.presentation.coin_list.CoinListAction
import com.example.cryptotrackerapplication.crypto.presentation.coin_list.CoinListEvent
import com.example.cryptotrackerapplication.crypto.presentation.coin_list.CoinListScreen
import com.example.cryptotrackerapplication.crypto.presentation.coin_list.CoinListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AdaptiveCoinListDetailPane(
    modifier: Modifier = Modifier,
    viewModel: CoinListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    ObserveAsEvent(event = viewModel.events) { event ->
        when (event) {
            is CoinListEvent.Error -> {
                Toast.makeText(
                    context, event.error.toString(context), Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()
    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane {
                CoinListScreen(state = state, modifier = modifier, onAction = { action ->
                    viewModel.onAction(action)
                    when (action) {
                        is CoinListAction.OnCoinClick -> {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail
                            )
                        }

                        else -> Unit
                    }
                })
            }
        },
        detailPane = {
            AnimatedPane {
                CoinDetailsScreen(
                    state = state, modifier = modifier
                )
            }
        },
        modifier = modifier
    )
}