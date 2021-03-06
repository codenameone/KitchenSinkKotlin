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

package kotlin.reflect.jvm.internal.impl.load.java.reflect

import kotlin.reflect.jvm.internal.impl.load.java.JavaClassFinder
import kotlin.reflect.jvm.internal.impl.name.FqName
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaClass
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaPackage
import kotlin.reflect.jvm.internal.impl.load.java.structure.reflect.ReflectJavaClass
import kotlin.reflect.jvm.internal.impl.load.java.structure.reflect.ReflectJavaPackage
import kotlin.reflect.jvm.internal.impl.name.ClassId

class ReflectJavaClassFinder(private val classLoader: ClassLoader) : JavaClassFinder {
    override fun findClass(classId: ClassId): JavaClass? {
        val packageFqName = classId.packageFqName
        val relativeClassName = classId.relativeClassName.asString().replace('.', '$')
        val name =
                if (packageFqName.isRoot) relativeClassName
                else packageFqName.asString() + "." + relativeClassName

        val klass = classLoader.tryLoadClass(name)
        return if (klass != null) ReflectJavaClass(klass) else null
    }

    override fun findPackage(fqName: FqName): JavaPackage? {
        // We don't know which packages our class loader has and has not, so we behave as if it contains any package in the world
        return ReflectJavaPackage(fqName)
    }

    override fun knownClassNamesInPackage(packageFqName: FqName): Set<String>? = null
}

fun ClassLoader.tryLoadClass(fqName: String) =
        try {
            loadClass(fqName)
        }
        catch (e: ClassNotFoundException) {
            null
        }
