package com.cursosandroidant.forecastweather.mainModule.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cursosandroidant.forecastweather.MainCoroutineRule
import com.cursosandroidant.forecastweather.common.dataAccess.WeatherForecastService
import com.cursosandroidant.forecastweather.common.data_access.JsonFileLoader
import com.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutinesRule = MainCoroutineRule()

    private lateinit var mainViewModel: MainViewModel
    private lateinit var service: WeatherForecastService

    companion object {
        private lateinit var retrofit: Retrofit

        @BeforeClass
        @JvmStatic
        fun setUpCommon() {
            retrofit = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    @Before
    fun setUp() {
        mainViewModel = MainViewModel()
        service = retrofit.create(WeatherForecastService::class.java)
    }

    @Test
    fun checkCurrentWeatherIsNotNullTest() {
        runBlocking {
            val result = service.getWeatherForecastByCoordinates(
                7.1521,
                -73.1328,
                "6364546cb00c113bff0065ac8aea2438",
                "metric",
                "en"
            )
            assertThat(result.current, `is`(notNullValue()))
        }
    }

    @Test
    fun checkTimezoneReturnsBogotaTest() {
        runBlocking {
            val result = service.getWeatherForecastByCoordinates(
                7.1521,
                -73.1328,
                "6364546cb00c113bff0065ac8aea2438",
                "metric",
                "en"
            )
            assertThat(result.timezone, `is`("America/Bogota"))
        }
    }

    @Test
    fun checkErrorResponseWithOnlyCoordinatesTest() {
        runBlocking {
            try {
                service.getWeatherForecastByCoordinates(
                    7.1521,
                    -73.1328,
                    "",
                    "",
                    ""
                )
            } catch (e: Exception) {
                assertThat(e.localizedMessage, `is`("HTTP 401 Unauthorized"))
            }
        }
    }

    @Test
    fun checkHourlySizeTest() {
        runBlocking {
            mainViewModel.getWeatherAndForecast(
                7.1521,
                -73.1328,
                "6364546cb00c113bff0065ac8aea2438",
                "metric",
                "en"
            )
            val result = mainViewModel.getResult().getOrAwaitValue()
            assertThat(result.hourly.size, `is`(48))
        }
    }

    @Test
    fun checkRemoteResponseWithLocalResponse() {
        runBlocking {
            val remoteResponse = service.getWeatherForecastByCoordinates(
                7.1521,
                -73.1328,
                "6364546cb00c113bff0065ac8aea2438",
                "metric",
                "en"
            )
            val localResponse =
                JsonFileLoader().loadWeatherForecastEntity("weather_forecast_response_success")

            assertThat(remoteResponse.hourly.size, `is`(localResponse?.hourly?.size))
            assertThat(remoteResponse.timezone, `is`(localResponse?.timezone))
        }
    }
}