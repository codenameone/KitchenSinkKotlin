/*
 * Copyright 2010-2016 JetBrains s.r.o.
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

package kotlin.reflect.jvm.internal.impl.load.java.components

import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaField
import kotlin.reflect.jvm.internal.impl.resolve.constants.ConstantValue

interface JavaPropertyInitializerEvaluator {
    fun getInitializerConstant(field: JavaField, descriptor: PropertyDescriptor): ConstantValue<*>?

    fun isNotNullCompileTimeConstant(field: JavaField): Boolean

    object DoNothing : JavaPropertyInitializerEvaluator {
        override fun getInitializerConstant(field: JavaField, descriptor: PropertyDescriptor) = null

        override fun isNotNullCompileTimeConstant(field: JavaField) = false
    }
}
