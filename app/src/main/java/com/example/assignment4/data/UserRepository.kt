package com.example.assignment4.data

class UserRepository(private val userDao: UserDao) {

    fun getAllUsers(): List<User> {
        return userDao.getAll()
    }

    fun addUsers(vararg users: User) {
        userDao.insertAll(*users)
    }

    fun deleteUser(user: User) {
        userDao.delete(user)
    }
}
