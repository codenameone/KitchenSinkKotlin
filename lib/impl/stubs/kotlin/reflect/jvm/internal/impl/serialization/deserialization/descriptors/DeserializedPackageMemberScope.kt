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

import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement
import kotlin.reflect.jvm.internal.impl.incremental.components.LookupLocation
import kotlin.reflect.jvm.internal.impl.incremental.components.NoLookupLocation
import kotlin.reflect.jvm.internal.impl.name.ClassId
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.resolve.scopes.DescriptorKindFilter
import kotlin.reflect.jvm.internal.impl.serialization.ProtoBuf
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializationComponents
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.NameResolver
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.TypeTable

open class DeserializedPackageMemberScope(
        packageDescriptor: PackageFragmentDescriptor,
        proto: ProtoBuf.Package,
        nameResolver: NameResolver,
        containerSource: SourceElement?,
        components: DeserializationComponents,
        classNames: () -> Collection<Name>
) : DeserializedMemberScope(
        components.createContext(packageDescriptor, nameResolver, TypeTable(proto.typeTable), containerSource),
        proto.functionList, proto.propertyList, classNames
) {
    private val packageFqName = packageDescriptor.fqName

    override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean)
            = computeDescriptors(kindFilter, nameFilter, NoLookupLocation.WHEN_GET_ALL_DESCRIPTORS)

    override fun hasClass(name: Name) =
            super.hasClass(name) || c.components.fictitiousClassDescriptorFactory.shouldCreateClass(packageFqName, name)

    override fun createClassId(name: Name) = ClassId(packageFqName, name)

    override fun addNonDeclaredDescriptors(result: MutableCollection<DeclarationDescriptor>, location: LookupLocation) {
        // Do nothing
    }

    override fun addEnumEntryDescriptors(result: MutableCollection<DeclarationDescriptor>, nameFilter: (Name) -> Boolean) {
        // Do nothing
    }
}
