package com.litera.app.core.di

import com.litera.app.data.repository.AuthRepositoryImpl
import com.litera.app.data.repository.AuthorRepositoryImpl
import com.litera.app.data.repository.BookRepositoryImpl
import com.litera.app.data.repository.CommunityRepositoryImpl
import com.litera.app.data.repository.FocusPreferencesRepositoryImpl
import com.litera.app.data.repository.FocusSessionRepositoryImpl
import com.litera.app.data.repository.NoteRepositoryImpl
import com.litera.app.data.repository.PreferencesRepositoryImpl
import com.litera.app.data.repository.ReadingGoalRepositoryImpl
import com.litera.app.data.repository.ShelfRepositoryImpl
import com.litera.app.domain.repository.AuthRepository
import com.litera.app.domain.repository.AuthorRepository
import com.litera.app.domain.repository.BookRepository
import com.litera.app.domain.repository.CommunityRepository
import com.litera.app.domain.repository.FocusPreferencesRepository
import com.litera.app.domain.repository.FocusSessionRepository
import com.litera.app.domain.repository.NoteRepository
import com.litera.app.domain.repository.PreferencesRepository
import com.litera.app.domain.repository.ReadingGoalRepository
import com.litera.app.domain.repository.ShelfRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBookRepository(impl: BookRepositoryImpl): BookRepository

    @Binds
    @Singleton
    abstract fun bindShelfRepository(impl: ShelfRepositoryImpl): ShelfRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindFocusSessionRepository(impl: FocusSessionRepositoryImpl): FocusSessionRepository

    @Binds
    @Singleton
    abstract fun bindReadingGoalRepository(impl: ReadingGoalRepositoryImpl): ReadingGoalRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindCommunityRepository(impl: CommunityRepositoryImpl): CommunityRepository

    @Binds
    @Singleton
    abstract fun bindAuthorRepository(impl: AuthorRepositoryImpl): AuthorRepository

    @Binds
    @Singleton
    abstract fun bindFocusPreferencesRepository(impl: FocusPreferencesRepositoryImpl): FocusPreferencesRepository
}
