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

package kotlin.reflect.jvm.internal.impl.descriptors.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import kotlin.reflect.jvm.internal.impl.descriptors.*;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope;
import kotlin.reflect.jvm.internal.impl.storage.LockBasedStorageManager;
import kotlin.reflect.jvm.internal.impl.types.ClassTypeConstructorImpl;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ClassDescriptorImpl extends ClassDescriptorBase {
    private final Modality modality;
    private final ClassKind kind;
    private final TypeConstructor typeConstructor;

    private MemberScope unsubstitutedMemberScope;
    private Set<ConstructorDescriptor> constructors;
    private ConstructorDescriptor primaryConstructor;

    public ClassDescriptorImpl(
            @NotNull DeclarationDescriptor containingDeclaration,
            @NotNull Name name,
            @NotNull Modality modality,
            @NotNull ClassKind kind,
            @NotNull Collection<KotlinType> supertypes,
            @NotNull SourceElement source
    ) {
        super(LockBasedStorageManager.NO_LOCKS, containingDeclaration, name, source);
        this.modality = modality;
        this.kind = kind;

        this.typeConstructor = new ClassTypeConstructorImpl(this, false, Collections.<TypeParameterDescriptor>emptyList(), supertypes);
    }

    public final void initialize(
            @NotNull MemberScope unsubstitutedMemberScope,
            @NotNull Set<ConstructorDescriptor> constructors,
            @Nullable ConstructorDescriptor primaryConstructor
    ) {
        this.unsubstitutedMemberScope = unsubstitutedMemberScope;
        this.constructors = constructors;
        this.primaryConstructor = primaryConstructor;
    }

    @NotNull
    @Override
    public Annotations getAnnotations() {
        return Annotations.Companion.getEMPTY();
    }

    @Override
    @NotNull
    public TypeConstructor getTypeConstructor() {
        return typeConstructor;
    }

    @NotNull
    @Override
    public Collection<ConstructorDescriptor> getConstructors() {
        return constructors;
    }

    @NotNull
    @Override
    public MemberScope getUnsubstitutedMemberScope() {
        return unsubstitutedMemberScope;
    }

    @NotNull
    @Override
    public MemberScope getStaticScope() {
        return MemberScope.Empty.INSTANCE;
    }

    @Nullable
    @Override
    public ClassDescriptor getCompanionObjectDescriptor() {
        return null;
    }

    @NotNull
    @Override
    public ClassKind getKind() {
        return kind;
    }

    @Override
    public boolean isCompanionObject() {
        return false;
    }

    @Override
    public ConstructorDescriptor getUnsubstitutedPrimaryConstructor() {
        return primaryConstructor;
    }

    @Override
    @NotNull
    public Modality getModality() {
        return modality;
    }

    @NotNull
    @Override
    public Visibility getVisibility() {
        return Visibilities.PUBLIC;
    }

    @Override
    public boolean isData() {
        return false;
    }

    @Override
    public boolean isInner() {
        return false;
    }

    @Override
    public String toString() {
        return "class " + getName();
    }

    @NotNull
    @Override
    public List<TypeParameterDescriptor> getDeclaredTypeParameters() {
        return Collections.emptyList();
    }
}