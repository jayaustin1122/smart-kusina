package com.dev.smartkusina.di

import jakarta.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SpoonacularRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EdamamRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ThemeAlDBRetrofit