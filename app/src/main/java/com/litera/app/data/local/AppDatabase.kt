package com.litera.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.litera.app.data.local.dao.ClubDao
import com.litera.app.data.local.dao.CommentDao
import com.litera.app.data.local.dao.FocusSessionDao
import com.litera.app.data.local.dao.NoteDao
import com.litera.app.data.local.dao.PostDao
import com.litera.app.data.local.dao.ReadingGoalDao
import com.litera.app.data.local.dao.ShelfDao
import com.litera.app.data.local.entity.ClubEntity
import com.litera.app.data.local.entity.CommentEntity
import com.litera.app.data.local.entity.FocusSessionEntity
import com.litera.app.data.local.entity.NoteEntity
import com.litera.app.data.local.entity.PostEntity
import com.litera.app.data.local.entity.ReadingGoalEntity
import com.litera.app.data.local.entity.ShelfBookEntity

@Database(
    entities = [
        ShelfBookEntity::class,
        FocusSessionEntity::class,
        ReadingGoalEntity::class,
        NoteEntity::class,
        PostEntity::class,
        CommentEntity::class,
        ClubEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shelfDao(): ShelfDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun readingGoalDao(): ReadingGoalDao
    abstract fun noteDao(): NoteDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun clubDao(): ClubDao
}
