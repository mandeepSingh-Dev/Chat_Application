package com.mandeep.chatapplication4.Connectivity

import kotlinx.coroutines.flow.Flow

interface NetworkConnectivityObserver {
    fun observe(): Flow<State>
}