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

package kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors

import kotlin.reflect.jvm.internal.impl.descriptors.*
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations
import kotlin.reflect.jvm.internal.impl.descriptors.impl.ConstructorDescriptorImpl
import kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl
import kotlin.reflect.jvm.internal.impl.descriptors.impl.PropertyDescriptorImpl
import kotlin.reflect.jvm.internal.impl.descriptors.impl.SimpleFunctionDescriptorImpl
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.protobuf.MessageLite
import kotlin.reflect.jvm.internal.impl.serialization.ProtoBuf
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.NameResolver
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.TypeTable

interface DeserializedCallableMemberDescriptor : CallableMemberDescriptor {
    val proto: MessageLite

    val nameResolver: NameResolver

    val typeTable: TypeTable

    // Information about the origin of this callable's container (class or package part on JVM) or null if there's no such information.
    val containerSource: SourceElement?
}

class DeserializedSimpleFunctionDescriptor(
        containingDeclaration: DeclarationDescriptor,
        original: SimpleFunctionDescriptor?,
        annotations: Annotations,
        name: Name,
        kind: CallableMemberDescriptor.Kind,
        override val proto: ProtoBuf.Function,
        override val nameResolver: NameResolver,
        override val typeTable: TypeTable,
        override val containerSource: SourceElement?
) : DeserializedCallableMemberDescriptor,
        SimpleFunctionDescriptorImpl(containingDeclaration, original, annotations, name, kind, SourceElement.NO_SOURCE) {

    override fun createSubstitutedCopy(
            newOwner: DeclarationDescriptor,
            original: FunctionDescriptor?,
            kind: CallableMemberDescriptor.Kind,
            newName: Name?,
            preserveSource: Boolean
    ): FunctionDescriptorImpl {
        return DeserializedSimpleFunctionDescriptor(
                newOwner, original as SimpleFunctionDescriptor?, annotations, newName ?: name, kind,
                proto, nameResolver, typeTable, containerSource
        )
    }
}

class DeserializedPropertyDescriptor(
        containingDeclaration: DeclarationDescriptor,
        original: PropertyDescriptor?,
        annotations: Annotations,
        modality: Modality,
        visibility: Visibility,
        isVar: Boolean,
        name: Name,
        kind: CallableMemberDescriptor.Kind,
        isLateInit: Boolean,
        isConst: Boolean,
        override val proto: ProtoBuf.Property,
        override val nameResolver: NameResolver,
        override val typeTable: TypeTable,
        override val containerSource: SourceElement?
) : DeserializedCallableMemberDescriptor,
        PropertyDescriptorImpl(containingDeclaration, original, annotations,
                               modality, visibility, isVar, name, kind, SourceElement.NO_SOURCE, isLateInit, isConst) {

    override fun createSubstitutedCopy(
            newOwner: DeclarationDescriptor,
            newModality: Modality,
            newVisibility: Visibility,
            original: PropertyDescriptor?,
            kind: CallableMemberDescriptor.Kind
    ): PropertyDescriptorImpl {
        return DeserializedPropertyDescriptor(
                newOwner, original, annotations, newModality, newVisibility, isVar, name, kind, isLateInit, isConst,
                proto, nameResolver, typeTable, containerSource
        )
    }
}

class DeserializedConstructorDescriptor(
        containingDeclaration: ClassDescriptor,
        original: ConstructorDescriptor?,
        annotations: Annotations,
        isPrimary: Boolean,
        kind: CallableMemberDescriptor.Kind,
        override val proto: ProtoBuf.Constructor,
        override val nameResolver: NameResolver,
        override val typeTable: TypeTable,
        override val containerSource: SourceElement?
) : DeserializedCallableMemberDescriptor,
        ConstructorDescriptorImpl(containingDeclaration, original, annotations, isPrimary, kind, SourceElement.NO_SOURCE) {

    override fun createSubstitutedCopy(
            newOwner: DeclarationDescriptor,
            original: FunctionDescriptor?,
            kind: CallableMemberDescriptor.Kind,
            newName: Name?,
            preserveSource: Boolean
    ): DeserializedConstructorDescriptor {
        return DeserializedConstructorDescriptor(
                newOwner as ClassDescriptor, original as ConstructorDescriptor?, annotations, isPrimary, kind,
                proto, nameResolver, typeTable, containerSource
        )
    }

    override fun isExternal(): Boolean = false

    override fun isInline(): Boolean = false

    override fun isTailrec(): Boolean = false
}
