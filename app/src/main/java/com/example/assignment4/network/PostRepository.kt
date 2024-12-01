package com.example.assignment4.network

class PostRepository(private val apiService: ApiService) {

    suspend fun fetchPosts(): List<Post> {
        return apiService.getPosts()
    }
}
