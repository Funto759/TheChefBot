package com.example.thechefbot.di.generativeAiModule

import androidx.room.Room
import com.example.thechefbot.dao.MIGRATION_1_2
import com.example.thechefbot.dao.RecipeDatabase

import com.example.thechefbot.model.ChatRepository
import com.example.thechefbot.model.RecipeViewModel
import com.example.thechefbot.model.SessionPrefs
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.generationConfig
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val provideGenerativeAI = module {
    single {
        GenerativeModel(
            modelName = "gemini-2.5-flash", // Use the appropriate model for your use case
            apiKey = "AIzaSyAduYh2j8BKYSb6zsoojB-KiadMzQQ6-cI" ,// Store your API key securely
                    generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = Int.MAX_VALUE
            },
//            safetySettings = listOf(
//                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
//                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
//            ),
            requestOptions = RequestOptions(
                timeout = 60_000 // 60 seconds timeout
            )
        )
    }
}

val provideDatabaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            RecipeDatabase::class.java,
            "recipe_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    single { get<RecipeDatabase>().chatDao() }
    single { get<RecipeDatabase>().chatSessionDao() }
}

val provideRepositoryModule = module {
    singleOf(::ChatRepository)
}

val provideSessionPrefsModule = module {
    single { SessionPrefs(get()) }
}

val provideViewModelModule = module {
    viewModelOf(::RecipeViewModel)
//    viewModelOf(::HistoryViewModel)
//    viewModelOf(::HistoryDetailViewModel)
}
