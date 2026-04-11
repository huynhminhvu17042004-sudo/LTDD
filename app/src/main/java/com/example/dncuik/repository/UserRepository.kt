package com.example.dncuik.repository

import com.example.dncuik.data.UserDao
import com.example.dncuik.data.UserEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun getUserByUsername(username: String) = userDao.getUserByUsername(username)
    suspend fun registerUser(user: UserEntity) = userDao.registerUser(user)
}
