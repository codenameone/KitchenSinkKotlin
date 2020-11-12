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

import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns
import kotlin.reflect.jvm.internal.impl.descriptors.ClassKind
import kotlin.reflect.jvm.internal.impl.descriptors.Modality
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement
import kotlin.reflect.jvm.internal.impl.descriptors.impl.ClassDescriptorImpl
import kotlin.reflect.jvm.internal.impl.descriptors.impl.PackageFragmentDescriptorImpl
import kotlin.reflect.jvm.internal.impl.name.FqName
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.platform.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.platform.JvmBuiltIns
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.fqNameSafe
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.AdditionalSupertypes
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedClassDescriptor
import kotlin.reflect.jvm.internal.impl.types.DelegatingType
import kotlin.reflect.jvm.internal.impl.types.KotlinType
import java.io.Serializable

class BuiltInClassesAreSerializableOnJvm(
        private val moduleDescriptor: ModuleDescriptor
) : AdditionalSupertypes {

    private val mockSerializableType = createMockJavaIoSerializableType()

    private fun createMockJavaIoSerializableType(): KotlinType {
        val mockJavaIoPackageFragment = object : PackageFragmentDescriptorImpl(moduleDescriptor, FqName("java.io")) {
            override fun getMemberScope() = MemberScope.Empty
        }

        //NOTE: can't reference anyType right away, because this is sometimes called when JvmBuiltIns are initializing
        val superTypes = listOf(object : DelegatingType() {
            override fun getDelegate(): KotlinType {
                return JvmBuiltIns.Instance.anyType
            }
        })

        val mockSerializableClass = ClassDescriptorImpl(
                mockJavaIoPackageFragment, Name.identifier("Serializable"), Modality.ABSTRACT, ClassKind.INTERFACE, superTypes, SourceElement.NO_SOURCE
        )

        mockSerializableClass.initialize(MemberScope.Empty, emptySet(), null)
        return mockSerializableClass.defaultType
    }

    override fun forClass(classDescriptor: DeserializedClassDescriptor): Collection<KotlinType> {
        if (isSerializableInJava(classDescriptor.fqNameSafe)) {
            return listOf(mockSerializableType)
        }
        else return listOf()
    }

    companion object {
        fun isSerializableInJava(classFqName: FqName): Boolean {
            val fqNameUnsafe = classFqName.toUnsafe()
            if (fqNameUnsafe == KotlinBuiltIns.FQ_NAMES.array || KotlinBuiltIns.isPrimitiveArray(fqNameUnsafe)) {
                return true
            }
            val javaClassId = JavaToKotlinClassMap.INSTANCE.mapKotlinToJava(fqNameUnsafe) ?: return false
            val classViaReflection = try {
                Class.forName(javaClassId.asSingleFqName().asString())
            }
            catch (e: ClassNotFoundException) {
                return false
            }
            return Serializable::class.java.isAssignableFrom(classViaReflection)
        }

    }
}