package com.mandeep.chatapplication2.Connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.remote.ConnectivityMonitor
import com.google.firebase.firestore.util.Consumer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext


class MyConnectivityManager(context: Context) /*:NetworkConnectivityObserver*/{
    var connectivityManager:ConnectivityManager
    lateinit var state:Flow<State>
    init {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
   // @RequiresApi(Build.VERSION_CODES.N)
     fun observe(): Flow<State> {

        return callbackFlow {
            val networkCallback = object:ConnectivityManager.NetworkCallback(){
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    state = flow{emit(State.Available)}
                    launch {  send(State.Available) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    state = flow{emit(State.UnAvailable)}
                    launch {  send(State.UnAvailable) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    state = flow{emit(State.Lose)}
                    launch {  send(State.Lose) }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    state = flow{emit(State.Losing)}
                    launch {  send(State.Losing) }
                }
            }
            connectivityManager.registerDefaultNetworkCallback(networkCallback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(networkCallback)
            }
        }

    }
}