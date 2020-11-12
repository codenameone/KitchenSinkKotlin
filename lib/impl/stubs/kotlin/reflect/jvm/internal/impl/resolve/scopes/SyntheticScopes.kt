/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kotlin.reflect.jvm.internal.impl.resolve.scopes

import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor
import kotlin.reflect.jvm.internal.impl.incremental.components.LookupLocation
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.types.KotlinType


interface SyntheticScope {
    fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<PropertyDescriptor>
    fun getSyntheticExtensionFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor>

    fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>): Collection<PropertyDescriptor>
    fun getSyntheticExtensionFunctions(receiverTypes: Collection<KotlinType>): Collection<FunctionDescriptor>
}

interface SyntheticScopes {
    val scopes: Collection<SyntheticScope>

    object Empty : SyntheticScopes {
        override val scopes: Collection<SyntheticScope> = emptyList()
    }
}


fun SyntheticScopes.collectSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation)
        = scopes.flatMap { it.getSyntheticExtensionProperties(receiverTypes, name, location) }

fun SyntheticScopes.collectSyntheticExtensionFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation)
        = scopes.flatMap { it.getSyntheticExtensionFunctions(receiverTypes, name, location) }

fun SyntheticScopes.collectSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>)
        = scopes.flatMap { it.getSyntheticExtensionProperties(receiverTypes) }

fun SyntheticScopes.collectSyntheticExtensionFunctions(receiverTypes: Collection<KotlinType>)
        = scopes.flatMap { it.getSyntheticExtensionFunctions(receiverTypes) }