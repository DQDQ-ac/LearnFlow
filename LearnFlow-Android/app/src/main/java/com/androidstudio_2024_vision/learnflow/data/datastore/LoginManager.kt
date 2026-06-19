package com.androidstudio_2024_vision.learnflow.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user")

class LoginManager(
    private val context: Context
) {

    companion object {

        val USER_ID =
            longPreferencesKey("user_id")

        val USERNAME =
            stringPreferencesKey("username")

        val NICKNAME =
            stringPreferencesKey("nickname")

        val PASSWORD =
            stringPreferencesKey("password")

        val IS_LOGIN =
            booleanPreferencesKey("is_login")
    }

    val userFlow: Flow<UserSession> =
        context.dataStore.data.map {

            UserSession(
                userId = it[USER_ID] ?: -1L,
                username = it[USERNAME] ?: "",
                nickname = it[NICKNAME] ?: "",
                isLogin = it[IS_LOGIN] ?: false
            )
        }

    suspend fun register(
        username: String,
        password: String,
        nickname: String
    ) {
        context.dataStore.edit {

            it[USER_ID] = System.currentTimeMillis()
            it[USERNAME] = username
            it[NICKNAME] = nickname
            it[PASSWORD] = password
            it[IS_LOGIN] = true
        }
    }


    suspend fun login(
        username: String,
        password: String
    ): Boolean {
        val current = context.dataStore.data
            .map { it }
            .map {
                val savedUser = it[USERNAME]
                val savedPass = it[PASSWORD]

                savedUser == username && savedPass == password
            }
            .first()

        if (current) {
            context.dataStore.edit {
                it[IS_LOGIN] = true
            }
        }
        return current
    }

    suspend fun saveUser(
        userId: Long,
        username: String,
        nickname: String
    ) {
        context.dataStore.edit {

            it[USER_ID] = userId
            it[USERNAME] = username
            it[NICKNAME] = nickname
            it[IS_LOGIN] = true
        }
    }

    suspend fun logout() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
