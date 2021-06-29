package com.epa.coroutines

import android.app.Application
import com.epa.coroutines.data.LocalStorageImpl
import com.epa.coroutines.data.remote.FcApi
import com.epa.coroutines.data.remote.RemoteApiImpl
import com.epa.coroutines.domain.LocalStorage
import com.epa.coroutines.domain.RemoteApi
import com.google.gson.*
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(module {
                single<FcApi> {
                    Retrofit.Builder()
                        .baseUrl("https://fcsapi.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(buildHttpClient())
                        .build()
                        .create(FcApi::class.java)
                }
                single<RemoteApi> { RemoteApiImpl(get()) }
                factory<LocalStorage> { LocalStorageImpl(get()) }
            })
        }
    }

    private fun buildHttpClient(): OkHttpClient {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) = Unit

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) = Unit

                    override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> =
                trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + trustManagers.contentToString()
            }

            val trustManager = trustManagers[0] as X509TrustManager
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
            builder.addInterceptor { chain ->
                val newUrl = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("access_key", "JGSPWml6e3bzi6Cpr0IiadRL")
                    .build()
                chain.proceed(chain.request().newBuilder().url(newUrl).build())
            }
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}