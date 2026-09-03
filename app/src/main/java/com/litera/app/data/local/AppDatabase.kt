package com.litera.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.litera.app.data.local.dao.ClubDao
import com.litera.app.data.local.dao.CommentDao
import com.litera.app.data.local.dao.PostDao
import com.litera.app.data.local.entity.ClubEntity
import com.litera.app.data.local.entity.CommentEntity
import com.litera.app.data.local.entity.PostEntity

// Shelf, reading goals, notes and focus sessions moved to per-user Firestore
// documents (see data/repository/*RepositoryImpl.kt) so they follow the
// signed-in account across devices instead of staying on one phone. Room is
// now only used for the Comunidade area, which is still local-only/mock.
@Database(
    entities = [
        PostEntity::class,
        CommentEntity::class,
        ClubEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun clubDao(): ClubDao
}
