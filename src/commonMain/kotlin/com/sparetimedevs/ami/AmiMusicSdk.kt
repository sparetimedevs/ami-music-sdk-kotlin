/*
 * Copyright (c) 2024-2025 sparetimedevs and respective authors and developers.
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

package com.sparetimedevs.ami

import kotlinx.serialization.json.Json

/**
 * Entry point for SDK-wide configuration and metadata.
 */
public object AmiMusicSdk {
    /**
     * The SDK's own [Json] configuration. It defines the canonical JSON form.
     *
     * - `ignoreUnknownKeys = true`: JSON written by a newer SDK version can be read, unknown fields are dropped.
     * - `encodeDefaults = true` and `explicitNulls = false`: defaults are encoded, nulls are left out.
     */
    public val json: Json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = false
        }

    /**
     * The version of this SDK, generated from `libraryVersion` in `gradle.properties` at build time.
     */
    public const val VERSION: String = GENERATED_SDK_VERSION
}
