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

package kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors

import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.SimpleFunctionDescriptor
import kotlin.reflect.jvm.internal.impl.incremental.components.LookupLocation
import kotlin.reflect.jvm.internal.impl.incremental.components.NoLookupLocation
import kotlin.reflect.jvm.internal.impl.load.java.components.DescriptorResolverUtils.resolveOverridesForStaticMembers
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.getParentJavaStaticClassScope
import kotlin.reflect.jvm.internal.impl.load.java.lazy.LazyJavaResolverContext
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaClass
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorFactory.createEnumValueOfMethod
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorFactory.createEnumValuesMethod
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils
import kotlin.reflect.jvm.internal.impl.resolve.scopes.DescriptorKindFilter
import kotlin.reflect.jvm.internal.impl.types.KotlinType

class LazyJavaStaticClassScope(
        c: LazyJavaResolverContext,
        private val jClass: JavaClass,
        override val ownerDescriptor: LazyJavaClassDescriptor
) : LazyJavaStaticScope(c) {

    override fun computeMemberIndex(): MemberIndex {
        val delegate = ClassMemberIndex(jClass) { it.isStatic }
        return object : MemberIndex by delegate {
            override fun getMethodNames(nameFilter: (Name) -> Boolean): Collection<Name> {
                // Should be a super call, but KT-2860
                return delegate.getMethodNames(nameFilter) +
                       // For SAM-constructors
                       jClass.innerClasses.map { it.name }
            }
        }
    }

    override fun getFunctionNames(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): Collection<Name> {
        if (jClass.isEnum) {
            return super.getFunctionNames(kindFilter, nameFilter) + listOf(DescriptorUtils.ENUM_VALUE_OF, DescriptorUtils.ENUM_VALUES)
        }
        return super.getFunctionNames(kindFilter, nameFilter)
    }

    override fun getPropertyNames(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): Collection<Name> =
            memberIndex().getAllFieldNames()

    override fun getClassNames(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): Collection<Name> = listOf()

    override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor? {
        // We don't need to track lookups here because we find nested/inner classes in LazyJavaClassMemberScope
        return null
    }

    override fun computeNonDeclaredFunctions(result: MutableCollection<SimpleFunctionDescriptor>, name: Name) {
        c.components.samConversionResolver.resolveSamConstructor(ownerDescriptor) {
            ownerDescriptor.unsubstitutedInnerClassesScope.getContributedClassifier(name, NoLookupLocation.FOR_ALREADY_TRACKED)
        }?.let { result.add(it) }

        val functionsFromSupertypes = getStaticFunctionsFromJavaSuperClasses(name, ownerDescriptor)
        result.addAll(resolveOverridesForStaticMembers(name, functionsFromSupertypes, result, ownerDescriptor, c.components.errorReporter))

        if (jClass.isEnum) {
            when (name) {
                DescriptorUtils.ENUM_VALUE_OF -> result.add(createEnumValueOfMethod(ownerDescriptor))
                DescriptorUtils.ENUM_VALUES -> result.add(createEnumValuesMethod(ownerDescriptor))
            }
        }
    }

    override fun computeNonDeclaredProperties(name: Name, result: MutableCollection<PropertyDescriptor>) {
        val propertiesFromSupertypes = getStaticPropertiesFromJavaSupertypes(name, ownerDescriptor)

        if (result.isNotEmpty()) {
            result.addAll(resolveOverridesForStaticMembers(
                    name, propertiesFromSupertypes, result, ownerDescriptor, c.components.errorReporter
            ))
        }
        else {
            result.addAll(propertiesFromSupertypes.groupBy {
                it.realOriginal
            }.flatMap {
                resolveOverridesForStaticMembers(name, it.value, result, ownerDescriptor, c.components.errorReporter)
            })
        }
    }

    private fun getStaticFunctionsFromJavaSuperClasses(name: Name, descriptor: ClassDescriptor): Set<SimpleFunctionDescriptor> {
        val staticScope = descriptor.getParentJavaStaticClassScope() ?: return emptySet()
        return staticScope.getContributedFunctions(name, NoLookupLocation.WHEN_GET_SUPER_MEMBERS).toSet()
    }

    private fun getStaticPropertiesFromJavaSupertypes(name: Name, descriptor: ClassDescriptor): Set<PropertyDescriptor> {

        fun getStaticProperties(supertype: KotlinType): Iterable<PropertyDescriptor> {
            val superTypeDescriptor = supertype.constructor.declarationDescriptor as? ClassDescriptor ?: return emptyList()

            val staticScope = superTypeDescriptor.staticScope

            if (staticScope !is LazyJavaStaticClassScope) return getStaticPropertiesFromJavaSupertypes(name, superTypeDescriptor)

            return staticScope.getContributedVariables(name, NoLookupLocation.WHEN_GET_SUPER_MEMBERS)
        }

        return descriptor.typeConstructor.supertypes.flatMap(::getStaticProperties).toSet()
    }

    private val PropertyDescriptor.realOriginal: PropertyDescriptor
        get() {
            if (this.kind.isReal) return this

            return this.overriddenDescriptors.map { it.realOriginal }.distinct().single()
        }
}
