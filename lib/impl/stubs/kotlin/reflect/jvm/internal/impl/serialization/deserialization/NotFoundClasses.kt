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

package kotlin.reflect.jvm.internal.impl.serialization.deserialization

import kotlin.reflect.jvm.internal.impl.descriptors.*
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations
import kotlin.reflect.jvm.internal.impl.descriptors.impl.ClassDescriptorBase
import kotlin.reflect.jvm.internal.impl.descriptors.impl.EmptyPackageFragmentDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.impl.TypeParameterDescriptorImpl
import kotlin.reflect.jvm.internal.impl.name.ClassId
import kotlin.reflect.jvm.internal.impl.name.FqName
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.module
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope
import kotlin.reflect.jvm.internal.impl.serialization.ProtoBuf
import kotlin.reflect.jvm.internal.impl.storage.StorageManager
import kotlin.reflect.jvm.internal.impl.types.ClassTypeConstructorImpl
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor
import kotlin.reflect.jvm.internal.impl.types.Variance
import kotlin.reflect.jvm.internal.impl.utils.toReadOnlyList

class NotFoundClasses(private val storageManager: StorageManager, private val module: ModuleDescriptor) {
    /**
     * @param typeParametersCount list of numbers of type parameters in this class and all its outer classes, starting from this class
     */
    private data class ClassRequest(val classId: ClassId, val typeParametersCount: List<Int>)

    private val packageFragments = storageManager.createMemoizedFunction<FqName, PackageFragmentDescriptor> { fqName ->
        EmptyPackageFragmentDescriptor(module, fqName)
    }

    private val classes = storageManager.createMemoizedFunction<ClassRequest, ClassDescriptor> { request ->
        val (classId, typeParametersCount) = request

        if (classId.isLocal) {
            throw UnsupportedOperationException("Unresolved local class: $classId")
        }

        val container =
                if (classId.isNestedClass) getOrCreateClass(classId.outerClassId, typeParametersCount.drop(1))
                else packageFragments(classId.packageFqName)

        // Treat a class with a nested ClassId as inner for simplicity, otherwise the outer type cannot have generic arguments
        val isInner = classId.isNestedClass

        MockClassDescriptor(storageManager, container, classId.shortClassName, isInner, typeParametersCount.firstOrNull() ?: 0)
    }

    class MockClassDescriptor(
            storageManager: StorageManager,
            container: DeclarationDescriptor,
            name: Name,
            private val isInner: Boolean,
            numberOfDeclaredTypeParameters: Int
    ) : ClassDescriptorBase(storageManager, container, name, SourceElement.NO_SOURCE) {
        private val typeParameters = (1..numberOfDeclaredTypeParameters).map { index ->
            TypeParameterDescriptorImpl.createWithDefaultBound(
                    this, Annotations.EMPTY, false, Variance.INVARIANT, Name.identifier("T$index"), index
            )
        }

        private val typeConstructor = ClassTypeConstructorImpl(this, /* isFinal = */ true, typeParameters, setOf(module.builtIns.anyType))

        override fun getKind() = ClassKind.CLASS
        override fun getModality() = Modality.FINAL
        override fun getVisibility() = Visibilities.PUBLIC
        override fun getTypeConstructor() = typeConstructor
        override fun getDeclaredTypeParameters() = typeParameters
        override fun isInner() = isInner

        override fun isCompanionObject() = false
        override fun isData() = false
        override fun getAnnotations() = Annotations.EMPTY

        override fun getUnsubstitutedMemberScope() = MemberScope.Empty
        override fun getStaticScope() = MemberScope.Empty
        override fun getConstructors(): Collection<ConstructorDescriptor> = emptySet()
        override fun getUnsubstitutedPrimaryConstructor(): ConstructorDescriptor? = null
        override fun getCompanionObjectDescriptor(): ClassDescriptor? = null

        override fun toString() = "class $name (not found)"
    }

    // We create different ClassDescriptor instances for types with the same ClassId but different number of type arguments.
    // (This may happen when a class with the same FQ name is instantiated with different type arguments in different modules.)
    // It's better than creating just one descriptor because otherwise would fail in multiple places where it's asserted that
    // the number of type arguments in a type must be equal to the number of the type parameters of the class
    private fun getOrCreateClass(classId: ClassId, typeParametersCount: List<Int>): ClassDescriptor {
        return classes(ClassRequest(classId, typeParametersCount))
    }

    fun get(proto: ProtoBuf.Type, nameResolver: NameResolver, typeTable: TypeTable): TypeConstructor {
        val classId = nameResolver.getClassId(proto.className)
        val typeParametersCount = generateSequence(proto) { it.outerType(typeTable) }.map { it.argumentCount }.toMutableList()
        val classNestingLevel = generateSequence(classId) { if (it.isNestedClass) it.outerClassId else null }.count()
        while (typeParametersCount.size < classNestingLevel) {
            typeParametersCount.add(0)
        }
        return getOrCreateClass(classId, typeParametersCount.toReadOnlyList()).typeConstructor
    }

    fun get(classId: ClassId, typeParametersCount: List<Int>): TypeConstructor {
        return getOrCreateClass(classId, typeParametersCount).typeConstructor
    }
}
