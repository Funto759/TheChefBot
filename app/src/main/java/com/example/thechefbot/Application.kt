package com.example.thechefbot

import android.app.Application
import com.example.thechefbot.di.generativeAiModule.provideDatabaseModule
import com.example.thechefbot.di.generativeAiModule.provideGenerativeAI
import com.example.thechefbot.di.generativeAiModule.provideRepositoryModule
import com.example.thechefbot.di.generativeAiModule.provideViewModelModule
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
//            provideDataSourceModule,
            provideRepositoryModule,
//            provideUseCaseModule,
            provideViewModelModule
        )
    }
}
}