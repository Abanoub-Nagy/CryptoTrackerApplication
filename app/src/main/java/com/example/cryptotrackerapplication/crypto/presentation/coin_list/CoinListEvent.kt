package com.example.cryptotrackerapplication.crypto.presentation.coin_list

import com.example.cryptotrackerapplication.core.domain.util.NetworkError

sealed interface CoinListEvent {
    data class Error(val error: NetworkError) : CoinListEvent
}