package com.example.cryptotrackerapplication.crypto.data.mappers

import android.annotation.SuppressLint
import com.example.cryptotrackerapplication.crypto.data.networking.dto.CoinDto
import com.example.cryptotrackerapplication.crypto.data.networking.dto.CoinPriceDto
import com.example.cryptotrackerapplication.crypto.domain.Coin
import com.example.cryptotrackerapplication.crypto.domain.CoinPrice
import java.time.Instant
import java.time.ZoneId

fun CoinDto.toCoin(): Coin {
    return Coin(
        id = id,
        name = name,
        symbol = symbol,
        rank = rank,
        marketCapUsd = marketCapUsd,
        priceUsd = priceUsd,
        changePercent24Hr = changePercent24Hr
    )
}

@SuppressLint("NewApi")
fun CoinPriceDto.toCoinPrice(): CoinPrice {
    return CoinPrice(
        dateTime = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()),
        priceUsd = priceUsd
    )
}