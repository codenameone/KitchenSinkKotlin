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

package kotlin.reflect.jvm.internal.impl.load.kotlin

import kotlin.reflect.jvm.internal.impl.name.ClassId
import kotlin.reflect.jvm.internal.impl.serialization.ClassDataWithSource
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.ClassDataFinder
import kotlin.reflect.jvm.internal.impl.serialization.jvm.JvmProtoBufUtil

class JavaClassDataFinder(
        private val kotlinClassFinder: KotlinClassFinder,
        private val deserializedDescriptorResolver: DeserializedDescriptorResolver
) : ClassDataFinder {
    override fun findClassData(classId: ClassId): ClassDataWithSource? {
        val kotlinJvmBinaryClass = kotlinClassFinder.findKotlinClass(classId) ?: return null
        assert(kotlinJvmBinaryClass.classId == classId) {
            "Class with incorrect id found: expected $classId, actual ${kotlinJvmBinaryClass.classId}"
        }
        val data = deserializedDescriptorResolver.readData(kotlinJvmBinaryClass, DeserializedDescriptorResolver.KOTLIN_CLASS) ?: return null
        val strings = kotlinJvmBinaryClass.classHeader.strings ?: error("String table not found in $kotlinJvmBinaryClass")
        val classData = JvmProtoBufUtil.readClassDataFrom(data, strings)
        return ClassDataWithSource(classData, KotlinJvmBinarySourceElement(kotlinJvmBinaryClass))
    }
}
