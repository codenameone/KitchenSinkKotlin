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

package kotlin.reflect.jvm.internal.impl.resolve.calls.inference

import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations
import kotlin.reflect.jvm.internal.impl.types.*
import kotlin.reflect.jvm.internal.impl.types.Variance.IN_VARIANCE
import kotlin.reflect.jvm.internal.impl.types.Variance.OUT_VARIANCE
import kotlin.reflect.jvm.internal.impl.types.typeUtil.builtIns

class CapturedTypeConstructor(
        val typeProjection: TypeProjection
): TypeConstructor {
    init {
        assert(typeProjection.projectionKind != Variance.INVARIANT) {
            "Only nontrivial projections can be captured, not: $typeProjection"
        }
    }

    override fun getParameters(): List<TypeParameterDescriptor> = listOf()

    override fun getSupertypes(): Collection<KotlinType> {
        val superType = if (typeProjection.projectionKind == Variance.OUT_VARIANCE)
            typeProjection.type
        else
            builtIns.nullableAnyType
        return listOf(superType)
    }

    override fun isFinal() = true

    override fun isDenotable() = false

    override fun getDeclarationDescriptor() = null

    override fun toString() = "CapturedTypeConstructor($typeProjection)"

    override fun getBuiltIns(): KotlinBuiltIns = typeProjection.type.constructor.builtIns
}

class CapturedType(
        private val typeProjection: TypeProjection
): DelegatingType(), SubtypingRepresentatives {

    private val delegateType = run {
        val scope = ErrorUtils.createErrorScope(
                "No member resolution should be done on captured type, it used only during constraint system resolution", true)
        KotlinTypeImpl.create(Annotations.EMPTY, CapturedTypeConstructor(typeProjection), false, listOf(), scope)
    }

    override fun getDelegate(): KotlinType = delegateType

    override fun getCapabilities(): TypeCapabilities = object : TypeCapabilities {
        override fun <T : TypeCapability> getCapability(capabilityClass: Class<T>) =
            this@CapturedType.getCapability(capabilityClass)
    }

    override fun <T : TypeCapability> getCapability(capabilityClass: Class<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return if (capabilityClass == SubtypingRepresentatives::class.java) this as T
        else super.getCapability(capabilityClass)
    }

    override val subTypeRepresentative: KotlinType
        get() = representative(OUT_VARIANCE, builtIns.nullableAnyType)

    override val superTypeRepresentative: KotlinType
        get() = representative(IN_VARIANCE, builtIns.nothingType)

    private fun representative(variance: Variance, default: KotlinType) =
        if (typeProjection.projectionKind == variance) typeProjection.type else default

    override fun sameTypeConstructor(type: KotlinType) = delegateType.constructor === type.constructor

    override fun toString() = "Captured($typeProjection)"
}

fun createCapturedType(typeProjection: TypeProjection): KotlinType
        = CapturedType(typeProjection)

fun KotlinType.isCaptured(): Boolean = constructor is CapturedTypeConstructor

fun TypeSubstitution.wrapWithCapturingSubstitution(needApproximation: Boolean = true): TypeSubstitution =
    if (this is IndexedParametersSubstitution)
        IndexedParametersSubstitution(
                this.parameters,
                this.arguments.zip(this.parameters).map {
                    it.first.createCapturedIfNeeded(it.second)
                }.toTypedArray(),
                approximateCapturedTypes = needApproximation)
    else
        object : DelegatedTypeSubstitution(this@wrapWithCapturingSubstitution) {
            override fun approximateContravariantCapturedTypes() = needApproximation
            override fun get(key: KotlinType) = super.get(key)?.createCapturedIfNeeded(key.constructor.declarationDescriptor as? TypeParameterDescriptor)
        }

private fun TypeProjection.createCapturedIfNeeded(typeParameterDescriptor: TypeParameterDescriptor?): TypeProjection {
    if (typeParameterDescriptor == null || projectionKind == Variance.INVARIANT) return this

    // Treat consistent projections as invariant
    if (typeParameterDescriptor.variance == projectionKind) {
        // TODO: Make star projection type lazy
        return if (isStarProjection)
            TypeProjectionImpl(object : DelegatingType() {
                override fun getDelegate() = this@createCapturedIfNeeded.type
            })
        else
            TypeProjectionImpl(this@createCapturedIfNeeded.type)
    }

    return TypeProjectionImpl(createCapturedType(this))
}
