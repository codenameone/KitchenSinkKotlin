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

import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement
import kotlin.reflect.jvm.internal.impl.descriptors.SourceFile
import kotlin.reflect.jvm.internal.impl.name.ClassId
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.resolve.jvm.JvmClassName

class JvmPackagePartSource(val className: JvmClassName, val facadeClassName: JvmClassName?) : SourceElement {
    constructor(kotlinClass: KotlinJvmBinaryClass) : this(
            JvmClassName.byClassId(kotlinClass.classId),
            kotlinClass.classHeader.multifileClassName?.let {
                if (it.isNotEmpty()) JvmClassName.byInternalName(it) else null
            }
    )

    val simpleName: Name get() = Name.identifier(className.internalName.substringAfterLast('/'))

    val classId: ClassId get() = ClassId(className.packageFqName, simpleName)

    override fun toString() = "${javaClass.simpleName}: $className"

    override fun getContainingFile(): SourceFile = SourceFile.NO_SOURCE_FILE
}
