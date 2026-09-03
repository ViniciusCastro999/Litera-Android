package com.litera.app.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.litera.app.core.common.Constants
import com.litera.app.data.local.AppDatabase
import com.litera.app.data.local.SeedData
import com.litera.app.data.local.dao.ClubDao
import com.litera.app.data.local.dao.CommentDao
import com.litera.app.data.local.dao.FocusSessionDao
import com.litera.app.data.local.dao.NoteDao
import com.litera.app.data.local.dao.PostDao
import com.litera.app.data.local.dao.ReadingGoalDao
import com.litera.app.data.local.dao.ShelfDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        databaseProvider: Provider<AppDatabase>
    ): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Seed the local-only community area so it isn't empty on
                    // first launch. Provider defers resolution past this
                    // builder call, avoiding a circular dependency on the
                    // AppDatabase instance being constructed right now.
                    CoroutineScope(Dispatchers.IO).launch {
                        val database = databaseProvider.get()
                        val now = System.currentTimeMillis()
                        database.clubDao().insertAll(SeedData.seedClubs())
                        SeedData.seedPosts(now).forEach { database.postDao().insert(it) }
                    }
                }
            })
            .build()

    @Provides
    fun provideShelfDao(database: AppDatabase): ShelfDao = database.shelfDao()

    @Provides
    fun provideFocusSessionDao(database: AppDatabase): FocusSessionDao = database.focusSessionDao()

    @Provides
    fun provideReadingGoalDao(database: AppDatabase): ReadingGoalDao = database.readingGoalDao()

    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao = database.noteDao()

    @Provides
    fun providePostDao(database: AppDatabase): PostDao = database.postDao()

    @Provides
    fun provideCommentDao(database: AppDatabase): CommentDao = database.commentDao()

    @Provides
    fun provideClubDao(database: AppDatabase): ClubDao = database.clubDao()
}
