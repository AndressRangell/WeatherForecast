package com.cursosandroidant.forecastweather.common.data_access

import com.cursosandroidant.forecastweather.entities.WeatherForecastEntity
import com.google.gson.Gson
import java.io.InputStreamReader

class JsonFileLoader {

    private var jsonString: String? = null

    fun loadJsonString(file: String): String? {
        val loader = InputStreamReader(this.javaClass.classLoader?.getResourceAsStream(file))
        jsonString = loader.readText()
        loader.close()
        return jsonString
    }

    fun loadWeatherForecastEntity(file: String): WeatherForecastEntity? {
        val loader = InputStreamReader(this.javaClass.classLoader?.getResourceAsStream(file))
        jsonString = loader.readText()
        loader.close()
        return Gson().fromJson(jsonString, WeatherForecastEntity::class.java)
    }

}