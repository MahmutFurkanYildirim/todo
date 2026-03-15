package com.furkanyildirim.learningcompose.di

import android.content.Context
import com.furkanyildirim.learningcompose.data.local.TodoDao
import com.furkanyildirim.learningcompose.data.local.TodoDatabase
import com.furkanyildirim.learningcompose.data.repository.FirebaseRepository
import com.furkanyildirim.learningcompose.data.repository.TodoRepository
import com.furkanyildirim.learningcompose.domain.repository.LocalTodoRepository
import com.furkanyildirim.learningcompose.domain.repository.RemoteTodoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TodoDatabase {
        return TodoDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTodoDao(database: TodoDatabase): TodoDao {
        return database.todoDao()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(todoDao: TodoDao): LocalTodoRepository {
        return TodoRepository(todoDao)
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(): RemoteTodoRepository {
        return FirebaseRepository()
    }
}
