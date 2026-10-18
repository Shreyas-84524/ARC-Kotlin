package com.arc.arc.core.di

import android.content.Context
import com.arc.arc.core.auth.DevFakeAuthRepository
import com.arc.arc.core.auth.IAuthRepository
import com.arc.arc.core.database.ArcDatabase
import com.arc.arc.core.database.RoomTaskDataSource
import com.arc.arc.core.datastore.AppPreferencesDataStore
import com.arc.arc.core.datastore.arcDataStore

/**
 * Dependency injection container for ARC.
 * Provides singleton data sources, database, and repository instances.
 */
interface AppContainer {
    val database: ArcDatabase
    val taskDataSource: RoomTaskDataSource
    val preferencesDataStore: AppPreferencesDataStore
    val authRepository: IAuthRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val database: ArcDatabase by lazy {
        ArcDatabase.getInstance(context)
    }

    override val taskDataSource: RoomTaskDataSource by lazy {
        RoomTaskDataSource(database)
    }

    override val preferencesDataStore: AppPreferencesDataStore by lazy {
        AppPreferencesDataStore(context.arcDataStore)
    }

    override val authRepository: IAuthRepository by lazy {
        // In Phase 1 dev builds without production credentials, use explicitly labeled DevFakeAuthRepository
        DevFakeAuthRepository()
    }
}
