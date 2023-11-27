package com.zerodeg.domain.interfaces

interface GithubRepository {
    suspend fun getRepos(owner: String): List<GithubRepo>
}