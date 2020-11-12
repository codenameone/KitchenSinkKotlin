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

import kotlin.reflect.jvm.internal.impl.builtins.functions.BuiltInFictitiousFunctionClassFactory
import kotlin.reflect.jvm.internal.impl.name.FqNameUnsafe
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.fqNameUnsafe
import kotlin.reflect.jvm.internal.impl.types.KotlinType
import kotlin.reflect.jvm.internal.impl.types.TypeProjection
import kotlin.reflect.jvm.internal.impl.types.TypeProjectionImpl
import kotlin.reflect.jvm.internal.impl.types.Variance
import kotlin.reflect.jvm.internal.impl.utils.DFS
import java.util.*

val KotlinType.isFunctionTypeOrSubtype: Boolean
    get() = isFunctionType || DFS.dfsFromNode(
            this,
            DFS.Neighbors { it.constructor.supertypes },
            DFS.VisitedWithSet(),
            object : DFS.AbstractNodeHandler<KotlinType, Boolean>() {
                private var result = false

                override fun beforeChildren(current: KotlinType): Boolean {
                    if (current.isFunctionType) {
                        result = true
                    }
                    return !result
                }

                override fun result() = result
            }
    )

val KotlinType.isFunctionType: Boolean
    get() {
        val descriptor = constructor.declarationDescriptor
        return descriptor != null && isNumberedFunctionClassFqName(descriptor.fqNameUnsafe)
    }

val KotlinType.isNonExtensionFunctionType: Boolean
    get() = isFunctionType && !isTypeAnnotatedWithExtensionFunctionType

val KotlinType.isExtensionFunctionType: Boolean
    get() = isFunctionType && isTypeAnnotatedWithExtensionFunctionType

private val KotlinType.isTypeAnnotatedWithExtensionFunctionType: Boolean
    get() = annotations.findAnnotation(KotlinBuiltIns.FQ_NAMES.extensionFunctionType) != null

/**
 * @return true if this is an FQ name of a fictitious class representing the function type,
 * e.g. kotlin.Function1 (but NOT kotlin.reflect.KFunction1)
 */
fun isNumberedFunctionClassFqName(fqName: FqNameUnsafe): Boolean {
    val segments = fqName.pathSegments()
    if (segments.size != 2) return false

    if (KotlinBuiltIns.BUILT_INS_PACKAGE_NAME != segments.first()) return false

    val shortName = segments.last().asString()
    return BuiltInFictitiousFunctionClassFactory.isFunctionClassName(shortName, KotlinBuiltIns.BUILT_INS_PACKAGE_FQ_NAME)
}

fun getReceiverTypeFromFunctionType(type: KotlinType): KotlinType? {
    assert(type.isFunctionType) { "Not a function type: $type" }
    return if (type.isTypeAnnotatedWithExtensionFunctionType) type.arguments.first().type else null
}

fun getReturnTypeFromFunctionType(type: KotlinType): KotlinType {
    assert(type.isFunctionType) { "Not a function type: $type" }
    return type.arguments.last().type
}

fun getValueParameterTypesFromFunctionType(type: KotlinType): List<TypeProjection> {
    assert(type.isFunctionType) { "Not a function type: $type" }
    val arguments = type.arguments
    val first = if (type.isExtensionFunctionType) 1 else 0
    val last = arguments.size - 1
    assert(first <= last) { "Not an exact function type: $type" }
    return arguments.subList(first, last)
}

fun getFunctionTypeArgumentProjections(
        receiverType: KotlinType?,
        parameterTypes: List<KotlinType>,
        returnType: KotlinType
): List<TypeProjection> {
    fun KotlinType.defaultProjection() = TypeProjectionImpl(Variance.INVARIANT, this)

    val arguments = ArrayList<TypeProjection>(parameterTypes.size + (if (receiverType != null) 1 else 0) + 1)
    receiverType?.let { arguments.add(it.defaultProjection()) }
    parameterTypes.mapTo(arguments, KotlinType::defaultProjection)
    arguments.add(returnType.defaultProjection())
    return arguments
}
