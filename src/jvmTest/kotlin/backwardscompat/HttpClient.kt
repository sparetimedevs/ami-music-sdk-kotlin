/*
 * Copyright (c) 2024 sparetimedevs and respective authors and developers.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package backwardscompat

import arrow.core.Either
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

data class TestError(val reason: String)

inline fun <reified A, reified B> postJsonRequest(
    apiUrl: String,
    client: OkHttpClient,
    jsonParser: Json,
    requestObject: A,
): Either<TestError, B> =
    Either.catch {
        val requestBodyJson = jsonParser.encodeToString(requestObject)

        val request =
            Request.Builder()
                .url(apiUrl)
                .post(requestBodyJson.toRequestBody(MEDIA_TYPE_JSON))
                .build()

        val responseBody: String =
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                response.body!!.string()
            }
        jsonParser.decodeFromString<B>(responseBody)
    }
        .mapLeft { TestError(reason = it.message ?: "An exception was thrown.") }

val MEDIA_TYPE_JSON = "application/json".toMediaType()
