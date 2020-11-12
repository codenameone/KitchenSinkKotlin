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

package kotlin.reflect.jvm.internal.impl.serialization.deserialization

import kotlin.reflect.jvm.internal.impl.descriptors.annotations.AnnotationWithTarget
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations
import kotlin.reflect.jvm.internal.impl.serialization.ProtoBuf
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedAnnotationsWithPossibleTargets
import kotlin.reflect.jvm.internal.impl.types.AbstractLazyType
import kotlin.reflect.jvm.internal.impl.types.ErrorUtils
import kotlin.reflect.jvm.internal.impl.types.LazyType
import kotlin.reflect.jvm.internal.impl.utils.toReadOnlyList

class DeserializedType(
        private val c: DeserializationContext,
        private val typeProto: ProtoBuf.Type,
        private val additionalAnnotations: Annotations = Annotations.EMPTY
) : AbstractLazyType(c.storageManager), LazyType {
    override fun computeTypeConstructor() = c.typeDeserializer.typeConstructor(typeProto)

    override fun computeArguments() =
            typeProto.collectAllArguments().mapIndexed {
                index, proto ->
                c.typeDeserializer.typeArgument(constructor.parameters.getOrNull(index), proto)
            }.toReadOnlyList()

    private fun ProtoBuf.Type.collectAllArguments(): List<ProtoBuf.Type.Argument> =
            argumentList + outerType(c.typeTable)?.collectAllArguments().orEmpty()

    private val annotations = DeserializedAnnotationsWithPossibleTargets(c.storageManager) {
        c.components.annotationAndConstantLoader
                .loadTypeAnnotations(typeProto, c.nameResolver)
                .map { AnnotationWithTarget(it, null) } + additionalAnnotations.getAllAnnotations()
    }

    override fun isMarkedNullable(): Boolean = typeProto.nullable

    override fun isError(): Boolean {
        val descriptor = constructor.declarationDescriptor
        return descriptor != null && ErrorUtils.isError(descriptor)
    }

    override fun getAnnotations(): Annotations = annotations

    override fun getCapabilities() = typeCapabilities()

    private val typeCapabilities = c.storageManager.createLazyValue {
        c.components.typeCapabilitiesLoader.loadCapabilities(typeProto)
    }
}
