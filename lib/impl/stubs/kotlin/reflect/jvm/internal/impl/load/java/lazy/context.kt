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

package kotlin.reflect.jvm.internal.impl.load.java.lazy

import kotlin.reflect.jvm.internal.impl.builtins.ReflectionTypes
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.PackagePartProvider
import kotlin.reflect.jvm.internal.impl.descriptors.SupertypeLoopChecker
import kotlin.reflect.jvm.internal.impl.incremental.components.LookupTracker
import kotlin.reflect.jvm.internal.impl.load.java.JavaClassFinder
import kotlin.reflect.jvm.internal.impl.load.java.components.*
import kotlin.reflect.jvm.internal.impl.load.java.lazy.types.LazyJavaTypeResolver
import kotlin.reflect.jvm.internal.impl.load.java.sources.JavaSourceElementFactory
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaTypeParameterListOwner
import kotlin.reflect.jvm.internal.impl.load.kotlin.DeserializedDescriptorResolver
import kotlin.reflect.jvm.internal.impl.load.kotlin.KotlinClassFinder
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.ErrorReporter
import kotlin.reflect.jvm.internal.impl.storage.StorageManager

class JavaResolverComponents(
        val storageManager: StorageManager,
        val finder: JavaClassFinder,
        val kotlinClassFinder: KotlinClassFinder,
        val deserializedDescriptorResolver: DeserializedDescriptorResolver,
        val externalAnnotationResolver: ExternalAnnotationResolver,
        val signaturePropagator: SignaturePropagator,
        val errorReporter: ErrorReporter,
        val javaResolverCache: JavaResolverCache,
        val javaPropertyInitializerEvaluator: JavaPropertyInitializerEvaluator,
        val samConversionResolver: SamConversionResolver,
        val sourceElementFactory: JavaSourceElementFactory,
        val moduleClassResolver: ModuleClassResolver,
        val packageMapper: PackagePartProvider,
        val supertypeLoopChecker: SupertypeLoopChecker,
        val lookupTracker: LookupTracker
)

class LazyJavaResolverContext(
        val components: JavaResolverComponents,
        val packageFragmentProvider: LazyJavaPackageFragmentProvider,
        val javaClassResolver: LazyJavaClassResolver,
        val module: ModuleDescriptor,
        val reflectionTypes: ReflectionTypes,
        val typeParameterResolver: TypeParameterResolver
) {
    val typeResolver = LazyJavaTypeResolver(this, typeParameterResolver)

    val storageManager: StorageManager
        get() = components.storageManager
}

fun LazyJavaResolverContext.child(
        typeParameterResolver: TypeParameterResolver
) = LazyJavaResolverContext(components, packageFragmentProvider, javaClassResolver, module, reflectionTypes, typeParameterResolver)


fun LazyJavaResolverContext.child(
        containingDeclaration: DeclarationDescriptor,
        typeParameterOwner: JavaTypeParameterListOwner,
        typeParametersIndexOffset: Int = 0
) = this.child(LazyJavaTypeParameterResolver(this, containingDeclaration, typeParameterOwner, typeParametersIndexOffset))
