package com.ledger.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ledger.app.data.db.dao.AccountDao
import com.ledger.app.data.db.dao.CategoryDao
import com.ledger.app.data.db.dao.RecurringConfigDao
import com.ledger.app.data.db.dao.TransactionDao
import com.ledger.app.data.db.entity.AccountEntity
import com.ledger.app.data.db.entity.CategoryEntity
import com.ledger.app.data.db.entity.RecurringConfigEntity
import com.ledger.app.data.db.entity.TransactionEntity

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        RecurringConfigEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class LedgerDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun recurringConfigDao(): RecurringConfigDao

    companion object {
        @Volatile private var INSTANCE: LedgerDatabase? = null

        fun getInstance(context: Context): LedgerDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    LedgerDatabase::class.java,
                    "ledger.db"
                )
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }
    }
}
