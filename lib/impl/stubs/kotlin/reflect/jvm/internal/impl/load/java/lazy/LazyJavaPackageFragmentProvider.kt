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
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentProvider
import kotlin.reflect.jvm.internal.impl.incremental.components.NoLookupLocation
import kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaPackageFragment
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaClass
import kotlin.reflect.jvm.internal.impl.load.java.structure.LightClassOriginKind
import kotlin.reflect.jvm.internal.impl.name.FqName
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.storage.MemoizedFunctionToNullable
import kotlin.reflect.jvm.internal.impl.utils.emptyOrSingletonList

class LazyJavaPackageFragmentProvider(
        components: JavaResolverComponents,
        module: ModuleDescriptor,
        reflectionTypes: ReflectionTypes
) : PackageFragmentProvider {

    private val c =
            LazyJavaResolverContext(components, this, FragmentClassResolver(), module, reflectionTypes, TypeParameterResolver.EMPTY)

    private val packageFragments: MemoizedFunctionToNullable<FqName, LazyJavaPackageFragment> =
            c.storageManager.createMemoizedFunctionWithNullableValues {
                fqName ->
                val jPackage = c.components.finder.findPackage(fqName)
                if (jPackage != null) {
                    LazyJavaPackageFragment(c, jPackage)
                }
                else null
            }

    private fun getPackageFragment(fqName: FqName) = packageFragments(fqName)

    override fun getPackageFragments(fqName: FqName) = emptyOrSingletonList(getPackageFragment(fqName))

    override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean) =
            getPackageFragment(fqName)?.getSubPackageFqNames().orEmpty()

    fun getClass(javaClass: JavaClass): ClassDescriptor? = c.javaClassResolver.resolveClass(javaClass)

    private inner class FragmentClassResolver : LazyJavaClassResolver {
        override fun resolveClass(javaClass: JavaClass): ClassDescriptor? {
            val fqName = javaClass.fqName
            if (fqName != null && javaClass.lightClassOriginKind == LightClassOriginKind.SOURCE) {
                return c.components.javaResolverCache.getClassResolvedFromSource(fqName)
            }

            javaClass.outerClass?.let { outerClass ->
                val outerClassScope = resolveClass(outerClass)?.unsubstitutedInnerClassesScope
                return outerClassScope?.getContributedClassifier(javaClass.name, NoLookupLocation.FROM_JAVA_LOADER) as? ClassDescriptor
            }

            if (fqName == null) return null

            return getPackageFragment(fqName.parent())?.findClassifierByJavaClass(javaClass)
        }
    }
}
