package com.example.thechefbot.di.firebaseModule

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val provideFirebaseModule = module {
    single<com.google.firebase.auth.FirebaseAuth> { com.google.firebase.auth.FirebaseAuth.getInstance() }
    single<FirebaseFirestore> { FirebaseFirestore.getInstance() }
}