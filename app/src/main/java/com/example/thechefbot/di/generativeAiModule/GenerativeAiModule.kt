package com.example.thechefbot.di.generativeAiModule

val provideGenerativeAI = module {
    single {
        GenerativeModel(
            modelName = "gemini-1.5-flash", // Use the appropriate model for your use case
            apiKey = BuildConfig.API_KEY // Store your API key securely
        )
    }
}