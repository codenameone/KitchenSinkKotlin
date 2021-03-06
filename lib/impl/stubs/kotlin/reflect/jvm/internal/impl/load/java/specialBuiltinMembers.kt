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

@file:JvmName("SpecialBuiltinMembers")
package kotlin.reflect.jvm.internal.impl.load.java

import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns
import kotlin.reflect.jvm.internal.impl.descriptors.*
import kotlin.reflect.jvm.internal.impl.load.java.BuiltinMethodsWithSpecialGenericSignature.getSpecialSignatureInfo
import kotlin.reflect.jvm.internal.impl.load.java.BuiltinMethodsWithSpecialGenericSignature.sameAsBuiltinMethodWithErasedValueParameters
import kotlin.reflect.jvm.internal.impl.load.java.BuiltinSpecialProperties.getBuiltinSpecialPropertyGetterName
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.JavaCallableMemberDescriptor
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.JavaClassDescriptor
import kotlin.reflect.jvm.internal.impl.load.kotlin.SignatureBuildingComponents
import kotlin.reflect.jvm.internal.impl.load.kotlin.computeJvmSignature
import kotlin.reflect.jvm.internal.impl.load.kotlin.signatures
import kotlin.reflect.jvm.internal.impl.name.FqName
import kotlin.reflect.jvm.internal.impl.name.FqNameUnsafe
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.*
import kotlin.reflect.jvm.internal.impl.resolve.jvm.JvmPrimitiveType
import kotlin.reflect.jvm.internal.impl.types.checker.TypeCheckingProcedure
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns.FQ_NAMES as BUILTIN_NAMES

private fun FqName.child(name: String): FqName = child(Name.identifier(name))
private fun FqNameUnsafe.childSafe(name: String): FqName = child(Name.identifier(name)).toSafe()

private data class NameAndSignature(val name: Name, val signature: String)

private fun String.method(name: String, parameters: String, returnType: String) =
        NameAndSignature(
                Name.identifier(name),
                SignatureBuildingComponents.signature(this@method, "$name($parameters)$returnType"))

object BuiltinSpecialProperties {
    private val PROPERTY_FQ_NAME_TO_JVM_GETTER_NAME_MAP = mapOf(
            BUILTIN_NAMES._enum.childSafe("name") to Name.identifier("name"),
            BUILTIN_NAMES._enum.childSafe("ordinal") to Name.identifier("ordinal"),
            BUILTIN_NAMES.collection.child("size") to Name.identifier("size"),
            BUILTIN_NAMES.map.child("size") to Name.identifier("size"),
            BUILTIN_NAMES.charSequence.childSafe("length") to Name.identifier("length"),
            BUILTIN_NAMES.map.child("keys") to Name.identifier("keySet"),
            BUILTIN_NAMES.map.child("values") to Name.identifier("values"),
            BUILTIN_NAMES.map.child("entries") to Name.identifier("entrySet")
    )

    private val GETTER_JVM_NAME_TO_PROPERTIES_SHORT_NAME_MAP: Map<Name, List<Name>> =
            PROPERTY_FQ_NAME_TO_JVM_GETTER_NAME_MAP.entries
                    .map { Pair(it.key.shortName(), it.value) }
                    .groupBy({ it.second }, { it.first })

    private val SPECIAL_FQ_NAMES = PROPERTY_FQ_NAME_TO_JVM_GETTER_NAME_MAP.keys
    internal val SPECIAL_SHORT_NAMES = SPECIAL_FQ_NAMES.map { it.shortName() }.toSet()

    fun hasBuiltinSpecialPropertyFqName(callableMemberDescriptor: CallableMemberDescriptor): Boolean {
        if (callableMemberDescriptor.name !in SPECIAL_SHORT_NAMES) return false

        return callableMemberDescriptor.hasBuiltinSpecialPropertyFqNameImpl()
    }

    private fun CallableMemberDescriptor.hasBuiltinSpecialPropertyFqNameImpl(): Boolean {
        if (fqNameOrNull() in SPECIAL_FQ_NAMES && valueParameters.isEmpty()) return true
        if (!isFromBuiltins()) return false

        return overriddenDescriptors.any { hasBuiltinSpecialPropertyFqName(it) }
    }

    fun getPropertyNameCandidatesBySpecialGetterName(name1: Name): List<Name> =
            GETTER_JVM_NAME_TO_PROPERTIES_SHORT_NAME_MAP[name1] ?: emptyList()

    fun CallableMemberDescriptor.getBuiltinSpecialPropertyGetterName(): String? {
        assert(isFromBuiltins()) { "This method is defined only for builtin members, but $this found" }

        val descriptor = propertyIfAccessor.firstOverridden { hasBuiltinSpecialPropertyFqName(it) } ?: return null
        return PROPERTY_FQ_NAME_TO_JVM_GETTER_NAME_MAP[descriptor.fqNameSafe]?.asString()
    }
}

object BuiltinMethodsWithSpecialGenericSignature {
    private val ERASED_COLLECTION_PARAMETER_NAME_AND_SIGNATURES = setOf(
            "containsAll", "removeAll", "retainAll"
    ).map { "java/util/Collection".method(it, "Ljava/util/Collection;", JvmPrimitiveType.BOOLEAN.desc) }

    private val ERASED_COLLECTION_PARAMETER_SIGNATURES = ERASED_COLLECTION_PARAMETER_NAME_AND_SIGNATURES.map { it.signature }

    enum class DefaultValue(val value: Any?) {
        NULL(null), INDEX(-1), FALSE(false)
    }

    private val GENERIC_PARAMETERS_METHODS_TO_DEFAULT_VALUES_MAP =
            signatures {
                mapOf(
                    javaUtil("Collection")
                            .method("contains", "Ljava/lang/Object;", JvmPrimitiveType.BOOLEAN.desc)             to DefaultValue.FALSE,
                    javaUtil("Collection")
                            .method("remove", "Ljava/lang/Object;", JvmPrimitiveType.BOOLEAN.desc)               to DefaultValue.FALSE,
                    javaUtil("Map")
                            .method("containsKey", "Ljava/lang/Object;", JvmPrimitiveType.BOOLEAN.desc)          to DefaultValue.FALSE,
                    javaUtil("Map")
                            .method("containsValue", "Ljava/lang/Object;", JvmPrimitiveType.BOOLEAN.desc)        to DefaultValue.FALSE,

                    javaUtil("Map")
                            .method("get", "Ljava/lang/Object;", "Ljava/lang/Object;")                           to DefaultValue.NULL,
                    javaUtil("Map")
                            .method("remove", "Ljava/lang/Object;", "Ljava/lang/Object;")                        to DefaultValue.NULL,

                    javaUtil("List")
                            .method("indexOf", "Ljava/lang/Object;", JvmPrimitiveType.INT.desc)                  to DefaultValue.INDEX,
                    javaUtil("List")
                            .method("lastIndexOf", "Ljava/lang/Object;", JvmPrimitiveType.INT.desc)              to DefaultValue.INDEX
                )
            }

    private val SIGNATURE_TO_DEFAULT_VALUES_MAP = GENERIC_PARAMETERS_METHODS_TO_DEFAULT_VALUES_MAP.mapKeys { it.key.signature }
    private val ERASED_VALUE_PARAMETERS_SHORT_NAMES: Set<Name>
    private val ERASED_VALUE_PARAMETERS_SIGNATURES: Set<String>

    init {
        val allMethods = GENERIC_PARAMETERS_METHODS_TO_DEFAULT_VALUES_MAP.keys + ERASED_COLLECTION_PARAMETER_NAME_AND_SIGNATURES
        ERASED_VALUE_PARAMETERS_SHORT_NAMES = allMethods.map { it.name }.toSet()
        ERASED_VALUE_PARAMETERS_SIGNATURES = allMethods.map { it.signature }.toSet()
    }

    private val CallableMemberDescriptor.hasErasedValueParametersInJava: Boolean
        get() = computeJvmSignature() in ERASED_VALUE_PARAMETERS_SIGNATURES

    @JvmStatic
    fun getOverriddenBuiltinFunctionWithErasedValueParametersInJava(
            functionDescriptor: FunctionDescriptor
    ): FunctionDescriptor? {
        if (!functionDescriptor.name.sameAsBuiltinMethodWithErasedValueParameters) return null
        return functionDescriptor.firstOverridden { it.hasErasedValueParametersInJava } as FunctionDescriptor?
    }

    @JvmStatic
    fun getDefaultValueForOverriddenBuiltinFunction(functionDescriptor: FunctionDescriptor): DefaultValue? {
        if (functionDescriptor.name !in ERASED_VALUE_PARAMETERS_SHORT_NAMES) return null
        return functionDescriptor.firstOverridden {
            it.computeJvmSignature() in SIGNATURE_TO_DEFAULT_VALUES_MAP.keys
        }?.let { SIGNATURE_TO_DEFAULT_VALUES_MAP[it.computeJvmSignature()] }
    }

    val Name.sameAsBuiltinMethodWithErasedValueParameters: Boolean
        get () = this in ERASED_VALUE_PARAMETERS_SHORT_NAMES

    enum class SpecialSignatureInfo(val valueParametersSignature: String?, val isObjectReplacedWithTypeParameter: Boolean) {
        ONE_COLLECTION_PARAMETER("Ljava/util/Collection<+Ljava/lang/Object;>;", false),
        OBJECT_PARAMETER_NON_GENERIC(null, true),
        OBJECT_PARAMETER_GENERIC("Ljava/lang/Object;", true)
    }

    fun CallableMemberDescriptor.isBuiltinWithSpecialDescriptorInJvm(): Boolean {
        if (!isFromBuiltins()) return false
        return getSpecialSignatureInfo()?.isObjectReplacedWithTypeParameter ?: false || doesOverrideBuiltinWithDifferentJvmName()
    }

    @JvmStatic
    fun CallableMemberDescriptor.getSpecialSignatureInfo(): SpecialSignatureInfo? {
        if (name !in ERASED_VALUE_PARAMETERS_SHORT_NAMES) return null

        val builtinSignature = firstOverridden { it is FunctionDescriptor && it.hasErasedValueParametersInJava }?.computeJvmSignature()
                ?: return null

        if (builtinSignature in ERASED_COLLECTION_PARAMETER_SIGNATURES) return SpecialSignatureInfo.ONE_COLLECTION_PARAMETER

        val defaultValue = SIGNATURE_TO_DEFAULT_VALUES_MAP[builtinSignature]!!

        return if (defaultValue == DefaultValue.NULL)
                    // return type is some generic type as 'Map.get'
                    SpecialSignatureInfo.OBJECT_PARAMETER_GENERIC
                else
                    SpecialSignatureInfo.OBJECT_PARAMETER_NON_GENERIC
    }
}

object BuiltinMethodsWithDifferentJvmName {
    // Note that signatures here are not real,
    // e.g. 'java/lang/CharSequence.get(I)C' does not actually exist in JDK
    // But it doesn't matter here, because signatures are only used to distinguish overloaded built-in definitions
    private val REMOVE_AT_NAME_AND_SIGNATURE =
            "java/util/List".method("removeAt", JvmPrimitiveType.INT.desc, "Ljava/lang/Object;")

    private val NAME_AND_SIGNATURE_TO_JVM_REPRESENTATION_NAME_MAP: Map<NameAndSignature, Name> = signatures {
        mapOf(
            javaLang("Number").method("toByte", "", JvmPrimitiveType.BYTE.desc)           to Name.identifier("byteValue"),
            javaLang("Number").method("toShort", "", JvmPrimitiveType.SHORT.desc)         to Name.identifier("shortValue"),
            javaLang("Number").method("toInt", "", JvmPrimitiveType.INT.desc)             to Name.identifier("intValue"),
            javaLang("Number").method("toLong", "", JvmPrimitiveType.LONG.desc)           to Name.identifier("longValue"),
            javaLang("Number").method("toFloat", "", JvmPrimitiveType.FLOAT.desc)         to Name.identifier("floatValue"),
            javaLang("Number").method("toDouble", "", JvmPrimitiveType.DOUBLE.desc)       to Name.identifier("doubleValue"),
            REMOVE_AT_NAME_AND_SIGNATURE                                                  to Name.identifier("remove"),
            javaLang("CharSequence")
                    .method("get", JvmPrimitiveType.INT.desc, JvmPrimitiveType.CHAR.desc) to Name.identifier("charAt")
        )
    }

    private val SIGNATURE_TO_JVM_REPRESENTATION_NAME: Map<String, Name> =
            NAME_AND_SIGNATURE_TO_JVM_REPRESENTATION_NAME_MAP.mapKeys { it.key.signature }

    val ORIGINAL_SHORT_NAMES: List<Name> = NAME_AND_SIGNATURE_TO_JVM_REPRESENTATION_NAME_MAP.keys.map { it.name }

    private val JVM_SHORT_NAME_TO_BUILTIN_SHORT_NAMES_MAP: Map<Name, List<Name>> =
            NAME_AND_SIGNATURE_TO_JVM_REPRESENTATION_NAME_MAP.entries
                    .map { Pair(it.key.name, it.value) }
                    .groupBy({ it.second }, { it.first })

    val Name.sameAsRenamedInJvmBuiltin: Boolean
        get() = this in ORIGINAL_SHORT_NAMES

    fun getJvmName(functionDescriptor: SimpleFunctionDescriptor): Name? {
        return SIGNATURE_TO_JVM_REPRESENTATION_NAME[functionDescriptor.computeJvmSignature() ?: return null]
    }

    fun isBuiltinFunctionWithDifferentNameInJvm(functionDescriptor: SimpleFunctionDescriptor): Boolean {
        if (!functionDescriptor.isFromBuiltins()) return false
        return functionDescriptor.firstOverridden {
            SIGNATURE_TO_JVM_REPRESENTATION_NAME.containsKey(functionDescriptor.computeJvmSignature())
        } != null
    }

    fun getBuiltinFunctionNamesByJvmName(name: Name): List<Name> =
            JVM_SHORT_NAME_TO_BUILTIN_SHORT_NAMES_MAP[name] ?: emptyList()


    val SimpleFunctionDescriptor.isRemoveAtByIndex: Boolean
        get() = name.asString() == "removeAt" && computeJvmSignature() == REMOVE_AT_NAME_AND_SIGNATURE.signature
}

@Suppress("UNCHECKED_CAST")
fun <T : CallableMemberDescriptor> T.getOverriddenBuiltinWithDifferentJvmName(): T? {
    if (name !in BuiltinMethodsWithDifferentJvmName.ORIGINAL_SHORT_NAMES
            && propertyIfAccessor.name !in BuiltinSpecialProperties.SPECIAL_SHORT_NAMES) return null

    return when (this) {
        is PropertyDescriptor, is PropertyAccessorDescriptor ->
            firstOverridden { BuiltinSpecialProperties.hasBuiltinSpecialPropertyFqName(it.propertyIfAccessor) } as T?
        is SimpleFunctionDescriptor ->
            firstOverridden {
                BuiltinMethodsWithDifferentJvmName.isBuiltinFunctionWithDifferentNameInJvm(it as SimpleFunctionDescriptor)
            } as T?
        else -> null
    }
}

fun CallableMemberDescriptor.doesOverrideBuiltinWithDifferentJvmName(): Boolean = getOverriddenBuiltinWithDifferentJvmName() != null

@Suppress("UNCHECKED_CAST")
fun <T : CallableMemberDescriptor> T.getOverriddenSpecialBuiltin(): T? {
    getOverriddenBuiltinWithDifferentJvmName()?.let { return it }

    if (!name.sameAsBuiltinMethodWithErasedValueParameters) return null

    return firstOverridden {
        it.isFromBuiltins() && it.getSpecialSignatureInfo() != null
    } as T?
}

// The subtle difference between getOverriddenBuiltinReflectingJvmDescriptor and getOverriddenSpecialBuiltin
// is that first one return descriptor reflecting JVM signature (JVM descriptor)
// E.g. it returns `contains(e: E): Boolean` instead of `contains(e: String): Boolean` for implementation of Collection<String>.contains
// Implementation differs by getting 'original' for collection methods with erased value parameters
// Also it ignores Collection<String>.containsAll overrides because they have the same JVM descriptor
@Suppress("UNCHECKED_CAST")
fun <T : CallableMemberDescriptor> T.getOverriddenBuiltinReflectingJvmDescriptor(): T? {
    getOverriddenBuiltinWithDifferentJvmName()?.let { return it }

    if (!name.sameAsBuiltinMethodWithErasedValueParameters) return null

    return firstOverridden {
        it.isFromBuiltins() && it.getSpecialSignatureInfo()?.isObjectReplacedWithTypeParameter ?: false
    }?.original as T?
}

fun getJvmMethodNameIfSpecial(callableMemberDescriptor: CallableMemberDescriptor): String? {
    val overriddenBuiltin = getOverriddenBuiltinThatAffectsJvmName(callableMemberDescriptor)?.propertyIfAccessor
                            ?: return null
    return when (overriddenBuiltin) {
        is PropertyDescriptor -> overriddenBuiltin.getBuiltinSpecialPropertyGetterName()
        is SimpleFunctionDescriptor -> BuiltinMethodsWithDifferentJvmName.getJvmName(overriddenBuiltin)?.asString()
        else -> null
    }
}

private fun getOverriddenBuiltinThatAffectsJvmName(
        callableMemberDescriptor: CallableMemberDescriptor
): CallableMemberDescriptor? {
    val overriddenBuiltin = callableMemberDescriptor.getOverriddenBuiltinWithDifferentJvmName() ?: return null

    if (callableMemberDescriptor.isFromBuiltins()) return overriddenBuiltin

    return null
}

fun ClassDescriptor.hasRealKotlinSuperClassWithOverrideOf(
        specialCallableDescriptor: CallableDescriptor
): Boolean {
    val builtinContainerDefaultType = (specialCallableDescriptor.containingDeclaration as ClassDescriptor).defaultType

    var superClassDescriptor = DescriptorUtils.getSuperClassDescriptor(this)

    while (superClassDescriptor != null) {
        if (superClassDescriptor !is JavaClassDescriptor) {
            // Kotlin class

            val doesOverrideBuiltinDeclaration =
                    TypeCheckingProcedure.findCorrespondingSupertype(superClassDescriptor.defaultType, builtinContainerDefaultType) != null

            if (doesOverrideBuiltinDeclaration) {
                val containingPackageFragment = DescriptorUtils.getParentOfType(superClassDescriptor, PackageFragmentDescriptor::class.java)
                if (superClassDescriptor.builtIns.isBuiltInPackageFragment(containingPackageFragment)) return false
                return true
            }
        }

        superClassDescriptor = DescriptorUtils.getSuperClassDescriptor(superClassDescriptor)
    }

    return false
}

// Util methods
val CallableMemberDescriptor.isFromJava: Boolean
    get() = propertyIfAccessor is JavaCallableMemberDescriptor && propertyIfAccessor.containingDeclaration is JavaClassDescriptor

fun CallableMemberDescriptor.isFromBuiltins(): Boolean {
    val fqName = propertyIfAccessor.fqNameOrNull() ?: return false
    return fqName.toUnsafe().startsWith(KotlinBuiltIns.BUILT_INS_PACKAGE_NAME) &&
            this.module == this.builtIns.builtInsModule
}

fun CallableMemberDescriptor.isFromJavaOrBuiltins() = isFromJava || isFromBuiltins()
