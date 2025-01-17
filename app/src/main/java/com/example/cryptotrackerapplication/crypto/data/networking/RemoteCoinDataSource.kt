package com.example.cryptotrackerapplication.crypto.data.networking


import android.annotation.SuppressLint
import com.example.cryptotrackerapplication.core.data.networking.constructUrl
import com.example.cryptotrackerapplication.core.data.networking.safeCall
import com.example.cryptotrackerapplication.core.domain.util.NetworkError
import com.example.cryptotrackerapplication.core.domain.util.Result
import com.example.cryptotrackerapplication.core.domain.util.map
import com.example.cryptotrackerapplication.crypto.data.mappers.toCoin
import com.example.cryptotrackerapplication.crypto.data.mappers.toCoinPrice
import com.example.cryptotrackerapplication.crypto.data.networking.dto.CoinHistoryDto
import com.example.cryptotrackerapplication.crypto.data.networking.dto.CoinResponseDto
import com.example.cryptotrackerapplication.crypto.domain.Coin
import com.example.cryptotrackerapplication.crypto.domain.CoinDataSource
import com.example.cryptotrackerapplication.crypto.domain.CoinPrice
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.ZoneId
import java.time.ZonedDateTime

class RemoteCoinDataSource(
    private val httpClient: HttpClient
) : CoinDataSource {
    override suspend fun getCoins(): Result<List<Coin>, NetworkError> {
        return safeCall<CoinResponseDto> {
            httpClient.get(
                urlString = constructUrl("/assets")
            )
        }.map { response ->
            response.data.map { it.toCoin() }
        }
    }

    @SuppressLint("NewApi")
    override suspend fun getCoinHistory(
        coinId: String, start: ZonedDateTime, end: ZonedDateTime
    ): Result<List<CoinPrice>, NetworkError> {
        val startMillis = start.withZoneSameInstant(ZoneId.of("UTC")).toInstant().toEpochMilli()
        val endMillis = end.withZoneSameInstant(ZoneId.of("UTC")).toInstant().toEpochMilli()

        return safeCall<CoinHistoryDto> {
            httpClient.get(
                urlString = constructUrl("/assets/$coinId/history")
            ) {
                parameter("interval", "h6")
                parameter("start", startMillis)
                parameter("end", endMillis)
            }
        }.map { response ->
            response.data.map { it.toCoinPrice() }
        }
    }

}