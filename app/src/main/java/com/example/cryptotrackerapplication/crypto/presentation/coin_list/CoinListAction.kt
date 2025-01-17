package com.example.cryptotrackerapplication.crypto.presentation.coin_list

import com.example.cryptotrackerapplication.crypto.presentation.model.CoinUi

sealed interface CoinListAction {
    data class OnCoinClick(val coinUi: CoinUi): CoinListAction
}