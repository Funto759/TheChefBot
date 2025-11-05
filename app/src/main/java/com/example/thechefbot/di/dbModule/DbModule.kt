package com.example.thechefbot.di.dbModule

import androidx.room.Room
import com.example.thechefbot.database.MIGRATION_1_2
import com.example.thechefbot.database.RecipeDatabase
import com.example.thechefbot.presentation.ChatBotFeat.model.ChatRepository
import com.example.thechefbot.presentation.ChatBotFeat.model.RecipeViewModel
import com.example.thechefbot.presentation.ChatBotFeat.model.SessionPrefs
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

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