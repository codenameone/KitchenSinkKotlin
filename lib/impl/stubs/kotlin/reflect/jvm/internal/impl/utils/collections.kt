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

package kotlin.reflect.jvm.internal.impl.utils

import java.util.*

fun <K, V> Iterable<K>.keysToMap(value: (K) -> V): Map<K, V> {
    return associateBy({ it }, value)
}

fun <K, V: Any> Iterable<K>.keysToMapExceptNulls(value: (K) -> V?): Map<K, V> {
    val map = LinkedHashMap<K, V>()
    for (k in this) {
        val v = value(k)
        if (v != null) {
            map[k] = v
        }
    }
    return map
}

fun <K> Iterable<K>.mapToIndex(): Map<K, Int> {
    val map = LinkedHashMap<K, Int>()
    for ((index, k) in this.withIndex()) {
        map[k] = index
    }
    return map
}

inline fun <K, V> MutableMap<K, V>.getOrPutNullable(key: K, defaultValue: () -> V): V {
    return if (!containsKey(key)) {
        val answer = defaultValue()
        put(key, answer)
        answer
    }
    else {
        get(key) as V
    }
}

inline fun <T, C: Collection<T>> C.ifEmpty(body: () -> C): C = if (isEmpty()) body() else this

inline fun <T> Array<out T>.ifEmpty(body: () -> Array<out T>): Array<out T> = if (isEmpty()) body() else this

fun <T: Any> emptyOrSingletonList(item: T?): List<T> = listOfNotNull(item)

fun <T: Any> MutableCollection<T>.addIfNotNull(t: T?) {
    if (t != null) add(t)
}

fun <K, V> newHashMapWithExpectedSize(expectedSize: Int): HashMap<K, V> {
    return HashMap(if (expectedSize < 3) 3 else expectedSize + expectedSize / 3 + 1)
}

fun <E> newHashSetWithExpectedSize(expectedSize: Int): HashSet<E> {
    return HashSet(if (expectedSize < 3) 3 else expectedSize + expectedSize / 3 + 1)
}

fun <E> newLinkedHashSetWithExpectedSize(expectedSize: Int): LinkedHashSet<E> {
    return LinkedHashSet(if (expectedSize < 3) 3 else expectedSize + expectedSize / 3 + 1)
}

fun <T> Collection<T>.toReadOnlyList(): List<T> =
        when (size) {
            0 -> emptyList()
            1 -> listOf(first())
            else -> ArrayList(this)
        }

fun <T: Any> T?.singletonOrEmptyList(): List<T> =
        if (this != null) listOf(this) else emptyList()

fun <T> List<T>.indexOfFirst(startFrom: Int, predicate: (T) -> Boolean): Int {
    for (index in startFrom..lastIndex) {
        if (predicate(this[index])) return index
    }
    return -1
}