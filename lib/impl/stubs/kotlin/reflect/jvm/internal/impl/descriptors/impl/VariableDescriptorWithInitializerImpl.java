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
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.constants.ConstantValue;
import kotlin.reflect.jvm.internal.impl.storage.NullableLazyValue;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.LazyType;

public abstract class VariableDescriptorWithInitializerImpl extends VariableDescriptorImpl {
    private final boolean isVar;

    protected NullableLazyValue<ConstantValue<?>> compileTimeInitializer;

    public VariableDescriptorWithInitializerImpl(
            @NotNull DeclarationDescriptor containingDeclaration,
            @NotNull Annotations annotations,
            @NotNull Name name,
            @Nullable KotlinType outType,
            boolean isVar,
            @NotNull SourceElement source
    ) {
        super(containingDeclaration, annotations, name, outType, source);

        this.isVar = isVar;
    }

    @Override
    public boolean isVar() {
        return isVar;
    }

    @Nullable
    @Override
    public ConstantValue<?> getCompileTimeInitializer() {
        // Force computation and setting of compileTimeInitializer, if needed
        if (compileTimeInitializer == null && outType instanceof LazyType) {
            outType.getConstructor();
        }

        if (compileTimeInitializer != null) {
            return compileTimeInitializer.invoke();
        }
        return null;
    }

    public void setCompileTimeInitializer(@NotNull NullableLazyValue<ConstantValue<?>> compileTimeInitializer) {
        assert !isVar() : "Constant value for variable initializer should be recorded only for final variables: " + getName();
        this.compileTimeInitializer = compileTimeInitializer;
    }
}
