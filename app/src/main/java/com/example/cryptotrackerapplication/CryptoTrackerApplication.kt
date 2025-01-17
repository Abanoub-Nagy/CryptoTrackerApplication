package com.example.cryptotrackerapplication

import android.app.Application
import com.example.cryptotrackerapplication.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class CryptoTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CryptoTrackerApplication)
            androidLogger()
            modules(appModule)
        }
    }
}