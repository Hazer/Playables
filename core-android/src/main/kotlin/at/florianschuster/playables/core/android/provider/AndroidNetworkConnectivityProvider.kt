package at.florianschuster.playables.core.android.provider

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import at.florianschuster.playables.core.provider.NetworkConnectivity
import at.florianschuster.playables.core.provider.NetworkConnectivityProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

class AndroidNetworkConnectivityProvider(context: Context) : NetworkConnectivityProvider {

    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override val currentNetworkConnectivity: NetworkConnectivity
        get() {
            val network = connectivityManager.activeNetwork
                ?: return NetworkConnectivity.Unknown
            val capabilities = connectivityManager.getNetworkCapabilities(network)
                ?: return NetworkConnectivity.Unknown

            val active = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)

            return if (active) NetworkConnectivity.Available else NetworkConnectivity.Unavailable
        }

    @ExperimentalCoroutinesApi
    override val networkConnectivity: Flow<NetworkConnectivity>
        get() = callbackFlow {
            offer(NetworkConnectivity.Unknown)

            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    offer(NetworkConnectivity.Unavailable)
                }

                override fun onAvailable(network: Network) {
                    offer(NetworkConnectivity.Available)
                }
            }

            val networkRequest = NetworkRequest.Builder().apply {
                addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            }.build()

            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

            awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
        }.conflate().distinctUntilChanged()
}