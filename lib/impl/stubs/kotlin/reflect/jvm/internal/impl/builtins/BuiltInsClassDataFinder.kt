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

package kotlin.reflect.jvm.internal.impl.builtins

import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement
import kotlin.reflect.jvm.internal.impl.name.ClassId
import kotlin.reflect.jvm.internal.impl.serialization.ClassData
import kotlin.reflect.jvm.internal.impl.serialization.ClassDataWithSource
import kotlin.reflect.jvm.internal.impl.serialization.builtins.BuiltInsProtoBuf
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.ClassDataFinder
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.NameResolver

class BuiltInsClassDataFinder(
        proto: BuiltInsProtoBuf.BuiltIns,
        private val nameResolver: NameResolver
) : ClassDataFinder {
    private val classIdToProto =
            proto.class_List.associateBy { klass ->
                nameResolver.getClassId(klass.fqName)
            }

    internal val allClassIds: Collection<ClassId> get() = classIdToProto.keys

    override fun findClassData(classId: ClassId): ClassDataWithSource? {
        val classProto = classIdToProto[classId] ?: return null
        return ClassDataWithSource(ClassData(nameResolver, classProto), SourceElement.NO_SOURCE)
    }
}
