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

package kotlin.reflect.jvm.internal.impl.builtins

import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations
import kotlin.reflect.jvm.internal.impl.incremental.components.NoLookupLocation
import kotlin.reflect.jvm.internal.impl.name.FqName
import kotlin.reflect.jvm.internal.impl.name.FqNameUnsafe
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.findClassAcrossModuleDependencies
import kotlin.reflect.jvm.internal.impl.types.*
import java.util.*
import kotlin.reflect.KProperty

val KOTLIN_REFLECT_FQ_NAME = FqName("kotlin.reflect")

class ReflectionTypes(module: ModuleDescriptor) {
    private val kotlinReflectScope: MemberScope by lazy(LazyThreadSafetyMode.PUBLICATION) {
        module.getPackage(KOTLIN_REFLECT_FQ_NAME).memberScope
    }

    fun find(className: String): ClassDescriptor {
        val name = Name.identifier(className)
        return kotlinReflectScope.getContributedClassifier(name, NoLookupLocation.FROM_REFLECTION) as? ClassDescriptor
                ?: ErrorUtils.createErrorClass(KOTLIN_REFLECT_FQ_NAME.child(name).asString())
    }

    private object ClassLookup {
        operator fun getValue(types: ReflectionTypes, property: KProperty<*>): ClassDescriptor {
            return types.find(property.name.capitalize())
        }
    }

    fun getKFunction(n: Int): ClassDescriptor = find("KFunction$n")

    val kClass: ClassDescriptor by ClassLookup
    val kProperty0: ClassDescriptor by ClassLookup
    val kProperty1: ClassDescriptor by ClassLookup
    val kProperty2: ClassDescriptor by ClassLookup
    val kMutableProperty0: ClassDescriptor by ClassLookup
    val kMutableProperty1: ClassDescriptor by ClassLookup

    fun getKClassType(annotations: Annotations, type: KotlinType): KotlinType {
        val descriptor = kClass
        if (ErrorUtils.isError(descriptor)) {
            return descriptor.defaultType
        }

        val arguments = listOf(TypeProjectionImpl(Variance.INVARIANT, type))
        return KotlinTypeImpl.create(annotations, descriptor, false, arguments)
    }

    fun getKFunctionType(
            annotations: Annotations,
            receiverType: KotlinType?,
            parameterTypes: List<KotlinType>,
            returnType: KotlinType
    ): KotlinType {
        val arguments = getFunctionTypeArgumentProjections(receiverType, parameterTypes, returnType)

        val classDescriptor = getKFunction(arguments.size - 1 /* return type */)

        if (ErrorUtils.isError(classDescriptor)) {
            return classDescriptor.defaultType
        }

        return KotlinTypeImpl.create(annotations, classDescriptor, false, arguments)
    }

    fun getKPropertyType(annotations: Annotations, receiverType: KotlinType?, returnType: KotlinType, mutable: Boolean): KotlinType {
        val classDescriptor =
                when {
                    receiverType != null -> when {
                        mutable -> kMutableProperty1
                        else -> kProperty1
                    }
                    else -> when {
                        mutable -> kMutableProperty0
                        else -> kProperty0
                    }
                }

        if (ErrorUtils.isError(classDescriptor)) {
            return classDescriptor.defaultType
        }

        val arguments = ArrayList<TypeProjection>(2)
        if (receiverType != null) {
            arguments.add(TypeProjectionImpl(receiverType))
        }
        arguments.add(TypeProjectionImpl(returnType))
        return KotlinTypeImpl.create(annotations, classDescriptor, false, arguments)
    }

    companion object {
        fun isReflectionClass(descriptor: ClassDescriptor): Boolean {
            val containingPackage = DescriptorUtils.getParentOfType(descriptor, PackageFragmentDescriptor::class.java)
            return containingPackage != null && containingPackage.fqName == KOTLIN_REFLECT_FQ_NAME
        }

        fun isCallableType(type: KotlinType): Boolean =
                type.isFunctionTypeOrSubtype || isKCallableType(type)

        @JvmStatic
        fun isNumberedKPropertyOrKMutablePropertyType(type: KotlinType): Boolean =
                isNumberedKPropertyType(type) || isNumberedKMutablePropertyType(type)

        private fun isKCallableType(type: KotlinType): Boolean =
                hasFqName(type.constructor, KotlinBuiltIns.FQ_NAMES.kCallable) ||
                type.constructor.supertypes.any { isKCallableType(it) }

        fun isNumberedKMutablePropertyType(type: KotlinType): Boolean {
            val descriptor = type.constructor.declarationDescriptor as? ClassDescriptor ?: return false
            return hasFqName(descriptor, KotlinBuiltIns.FQ_NAMES.kMutableProperty0) ||
                   hasFqName(descriptor, KotlinBuiltIns.FQ_NAMES.kMutableProperty1) ||
                   hasFqName(descriptor, KotlinBuiltIns.FQ_NAMES.kMutableProperty2)
        }

        fun isNumberedKPropertyType(type: KotlinType): Boolean {
            val descriptor = type.constructor.declarationDescriptor as? ClassDescriptor ?: return false
            return hasFqName(descriptor, KotlinBuiltIns.FQ_NAMES.kProperty0) ||
                   hasFqName(descriptor, KotlinBuiltIns.FQ_NAMES.kProperty1) ||
                   hasFqName(descriptor, KotlinBuiltIns.FQ_NAMES.kProperty2)
        }

        private fun hasFqName(typeConstructor: TypeConstructor, fqName: FqNameUnsafe): Boolean {
            val descriptor = typeConstructor.declarationDescriptor
            return descriptor is ClassDescriptor && hasFqName(descriptor, fqName)
        }

        private fun hasFqName(descriptor: ClassDescriptor, fqName: FqNameUnsafe): Boolean {
            return descriptor.name == fqName.shortName() && DescriptorUtils.getFqName(descriptor) == fqName
        }

        fun createKPropertyStarType(module: ModuleDescriptor): KotlinType? {
            val kPropertyClass = module.findClassAcrossModuleDependencies(KotlinBuiltIns.FQ_NAMES.kProperty) ?: return null
            return KotlinTypeImpl.create(
                    Annotations.EMPTY, kPropertyClass, false,
                    listOf(StarProjectionImpl(kPropertyClass.typeConstructor.parameters.single()))
            )
        }
    }
}
