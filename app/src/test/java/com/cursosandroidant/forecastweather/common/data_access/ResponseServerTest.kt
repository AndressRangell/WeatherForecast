package com.cursosandroidant.forecastweather.common.data_access

import com.cursosandroidant.forecastweather.entities.WeatherForecastEntity
import com.google.gson.Gson
import java.net.HttpURLConnection
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ResponseServerTest {

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun readJsonFileSuccess() {
        val reader = JsonFileLoader().loadJsonString("weather_forecast_response_success")
        assertThat(reader, `is`(notNullValue()))
        assertThat(reader, containsString("America/Bogota"))
    }

    @Test
    fun getWeatherForecastCheckTimezoneExist() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(
                JsonFileLoader().loadJsonString("weather_forecast_response_success")
                    ?: "{errorCode:34}"
            )
        mockWebServer.enqueue(response)
        assertThat(response.getBody()?.readUtf8(), containsString("\"timezone\""))
    }

    @Test
    fun getWeatherForecastCheckFailResponse() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(
                JsonFileLoader().loadJsonString("weather_forecast_response_fail")
                    ?: "{errorCode:34}"
            )
        mockWebServer.enqueue(response)
        assertThat(
            response.getBody()?.readUtf8(),
            containsString("Invalid API key. Please see https://openweathermap.org/faq#error401 for more info.")
        )
    }

    @Test
    fun getWeatherForecastCheckContainsHourly() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(
                JsonFileLoader().loadJsonString("weather_forecast_response_success")
                    ?: "{errorCode:34}"
            )
        mockWebServer.enqueue(response)
        assertThat(response.getBody()?.readUtf8(), containsString("hourly"))

        val json = Gson().fromJson(
            response.getBody()?.readUtf8() ?: "",
            WeatherForecastEntity::class.java
        )
        assertThat(json.hourly.isEmpty(), `is`(false))
    }

}