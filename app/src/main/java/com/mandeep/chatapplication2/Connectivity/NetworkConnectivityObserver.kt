package com.mandeep.chatapplication2.Connectivity

import kotlinx.coroutines.flow.Flow

interface NetworkConnectivityObserver {

    fun observe(): Flow<State>

}