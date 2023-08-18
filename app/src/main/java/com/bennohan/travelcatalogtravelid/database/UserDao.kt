package com.bennohan.travelcatalogtravelid.database

import androidx.room.Dao
import androidx.room.Query
import com.crocodic.core.data.CoreDao
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao : CoreDao<User> {
    @Query("DELETE FROM User")
    suspend fun deleteAll()

    @Query("SELECT * FROM User WHERE idRoom = 1 ")
    fun getUser(): Flow<User>

    @Query("SELECT EXISTS (SELECT 1 FROM User WHERE idRoom = 1)")
    suspend fun isLogin(): Boolean

}