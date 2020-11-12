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

package kotlin.reflect.jvm.internal.impl.descriptors.annotations

import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement
import kotlin.reflect.jvm.internal.impl.descriptors.ValueParameterDescriptor
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.resolve.constants.AnnotationValue
import kotlin.reflect.jvm.internal.impl.resolve.constants.ArrayValue
import kotlin.reflect.jvm.internal.impl.resolve.constants.EnumValue
import kotlin.reflect.jvm.internal.impl.resolve.constants.StringValue
import kotlin.reflect.jvm.internal.impl.types.Variance

// This function may be useful if we deprecate something not via the annotation but with special code in the compiler
@Suppress("unused")
fun KotlinBuiltIns.createDeprecatedAnnotation(
        message: String,
        replaceWith: String,
        level: String = "WARNING"
): AnnotationDescriptor {
    val deprecatedAnnotation = deprecatedAnnotation
    val parameters = deprecatedAnnotation.unsubstitutedPrimaryConstructor!!.valueParameters

    val replaceWithClass = getBuiltInClassByName(Name.identifier("ReplaceWith"))

    val replaceWithParameters = replaceWithClass.unsubstitutedPrimaryConstructor!!.valueParameters
    return AnnotationDescriptorImpl(
            deprecatedAnnotation.defaultType,
            mapOf(
                    parameters["message"] to StringValue(message, this),
                    parameters["replaceWith"] to AnnotationValue(
                            AnnotationDescriptorImpl(
                                    replaceWithClass.defaultType,
                                    mapOf(
                                            replaceWithParameters["expression"] to StringValue(replaceWith, this),
                                            replaceWithParameters["imports"]    to ArrayValue(
                                                    emptyList(), getArrayType(Variance.INVARIANT, stringType), this)
                                    ),
                                    SourceElement.NO_SOURCE
                            )
                    ),
                    parameters["level"] to EnumValue(getDeprecationLevelEnumEntry(level) ?: error("Deprecation level $level not found"))
            ),
            SourceElement.NO_SOURCE)
}

private operator fun Collection<ValueParameterDescriptor>.get(parameterName: String) = single { it.name.asString() == parameterName }
