package com.example.thechefbot

import android.app.Application
import com.example.thechefbot.di.dbModule.provideDatabaseModule
import com.example.thechefbot.di.generativeModule.provideGenerativeAI
import com.example.thechefbot.di.dbModule.provideRepositoryModule
import com.example.thechefbot.di.dbModule.provideSessionPrefsModule
import com.example.thechefbot.di.dbModule.provideViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
    startKoin{
        androidContext(this@Application)
        modules(
            provideGenerativeAI,
            provideDatabaseModule,
            provideSessionPrefsModule,
//            provideDataSourceModule,
            provideRepositoryModule,
//            provideUseCaseModule,
            provideViewModelModule
        )
    }
}
}