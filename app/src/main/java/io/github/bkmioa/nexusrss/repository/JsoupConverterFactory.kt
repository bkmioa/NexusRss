package io.github.bkmioa.nexusrss.repository

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class JsoupConverterFactory : Converter.Factory() {
    companion object {
        @JvmStatic
        fun create(): JsoupConverterFactory {
            return JsoupConverterFactory()
        }
    }

    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        return JsoupResponseBodyConverter<Any>(type)
    }
}