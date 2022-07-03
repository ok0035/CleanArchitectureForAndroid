package com.zerodeg.data

import com.zerodeg.data.GithubRepo

interface GithubRepository {
    suspend fun getRepos(owner: String): List<GithubRepo>
}