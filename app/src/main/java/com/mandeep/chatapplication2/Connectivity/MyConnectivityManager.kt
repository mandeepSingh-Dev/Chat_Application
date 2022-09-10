package com.mandeep.chatapplication2.Connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.remote.ConnectivityMonitor
import com.google.firebase.firestore.util.Consumer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext


class MyConnectivityManager(context: Context) /*:NetworkConnectivityObserver*/{
    var connectivityManager:ConnectivityManager
    lateinit var state:Flow<State>

    init {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
     fun observe(): Flow<State> {

        return callbackFlow {
            val networkCallback = object:ConnectivityManager.NetworkCallback(){
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    Log.d("dogdg","AVAILABLE")
                    state = flow{emit(State.Available)}
                    launch {  send(State.Available) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    Log.d("dogdg","onUnavailable")
                    state = flow{emit(State.UnAvailable)}
                    launch {  send(State.UnAvailable) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Log.d("dogdg","onLost")
                    state = flow{emit(State.Lose)}
                    launch {  send(State.Lose) }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    Log.d("dogdg","onLosing")
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