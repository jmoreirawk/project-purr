package pro.moreira.projectpurr.data.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pro.moreira.projectpurr.data.BuildConfig
import pro.moreira.projectpurr.data.remote.CatApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val builder = chain.request().newBuilder()
            builder.addHeader("x-api-key", BuildConfig.CATS_API_KEY)
            chain.proceed(builder.build())
        }
    }

    @Provides
    fun moshi(): Moshi {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    @Provides
    fun retrofit(interceptor: Interceptor, moshi: Moshi): Retrofit {
        val httpClientBuilder = OkHttpClient.Builder().addInterceptor(interceptor)
        httpClientBuilder.addInterceptor(loggingInterceptor)
        val httpClient = httpClientBuilder.build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.CATS_API_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    fun provideCatApi(retrofit: Retrofit): CatApi = retrofit.create(CatApi::class.java)

    private val loggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
}