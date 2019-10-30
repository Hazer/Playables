package at.florianschuster.playables.core.android.provider

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import at.florianschuster.control.test.TestCoroutineScopeRule
import at.florianschuster.control.test.emissions
import at.florianschuster.control.test.expect
import at.florianschuster.control.test.test
import at.florianschuster.playables.core.provider.NetworkConnectivity
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

internal class AndroidNetworkConnectivityProviderTest {

    @get:Rule
    val scopeRule = TestCoroutineScopeRule()

    private val context = mockk<Context>()
    internal val connectivityManager = mockk<ConnectivityManager>()
    internal val network = mockk<Network>()
    internal val capabilities = mockk<NetworkCapabilities>()
    internal val networkCallback = slot<ConnectivityManager.NetworkCallback>()

    internal val sut = AndroidNetworkConnectivityProvider(context)

    @Before
    fun setup() {
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities

        every {
            connectivityManager.registerNetworkCallback(any(), capture(networkCallback))
        } just Runs
        every {
            connectivityManager.unregisterNetworkCallback(any<ConnectivityManager.NetworkCallback>())
        } just Runs
    }

    @Test
    fun `current network connectivity`() {
        givenNetworkCapabilities(cellular = false, wifi = false)
        thenNetworkConnectivity(NetworkConnectivity.Unavailable)

        givenNetworkCapabilities(cellular = true, wifi = false)
        thenNetworkConnectivity(NetworkConnectivity.Available)

        givenNetworkCapabilities(cellular = false, wifi = true)
        thenNetworkConnectivity(NetworkConnectivity.Available)

        givenNetworkCapabilities(cellular = true, wifi = true)
        thenNetworkConnectivity(NetworkConnectivity.Available)
    }

    @Test
    fun `network connectivity flow`() {
        val collector = sut.networkConnectivity.test(scopeRule)
        thenNetworkCallbackRegistered()

        whenNetworkCallbackOnAvailable()
        whenNetworkCallbackOnLost()
        whenNetworkCallbackOnAvailable()

        collector expect emissions(
            NetworkConnectivity.Unknown,
            NetworkConnectivity.Available,
            NetworkConnectivity.Unavailable,
            NetworkConnectivity.Available
        )

        collector.cancel()
        thenNetworkCallbackUnregistered()
    }
}

private fun AndroidNetworkConnectivityProviderTest.givenNetworkCapabilities(
    cellular: Boolean = false,
    wifi: Boolean = false
) {
    every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns cellular
    every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns wifi
}

private fun AndroidNetworkConnectivityProviderTest.whenNetworkCallbackOnAvailable() {
    networkCallback.captured.onAvailable(network)
}

private fun AndroidNetworkConnectivityProviderTest.whenNetworkCallbackOnLost() {
    networkCallback.captured.onLost(network)
}

private fun AndroidNetworkConnectivityProviderTest.thenNetworkConnectivity(connectivity: NetworkConnectivity) {
    assertEquals(connectivity, sut.currentNetworkConnectivity)
}

private fun AndroidNetworkConnectivityProviderTest.thenNetworkCallbackRegistered() {
    verify(exactly = 1) {
        connectivityManager.registerNetworkCallback(
            any(),
            any<ConnectivityManager.NetworkCallback>()
        )
    }
}

private fun AndroidNetworkConnectivityProviderTest.thenNetworkCallbackUnregistered() {
    verify(exactly = 1) {
        connectivityManager.unregisterNetworkCallback(any<ConnectivityManager.NetworkCallback>())
    }
}
