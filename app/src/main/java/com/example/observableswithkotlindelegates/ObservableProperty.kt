package com.example.observableswithkotlindelegates

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

typealias PropChangeListener<PROPERTY> = (old: PROPERTY?, new: PROPERTY?) -> Unit

interface ObservableProperty {
    fun <CLASS, PROPERTY> addListener(
        field: KProperty1<CLASS, PROPERTY>,
        listener: PropChangeListener<PROPERTY>
    )

    fun <CLASS, PROPERTY> removeListener(
        field: KProperty1<CLASS, PROPERTY>,
        listener: PropChangeListener<PROPERTY>
    )

    fun <CLASS, PROPERTY> clearAllListeners(field: KProperty1<CLASS, PROPERTY>)

    fun notify(name: String, old: Any?, new: Any?)
}

class ObservablePropertyImpl : ObservableProperty {

    private val listeners = mutableMapOf<String, MutableList<PropChangeListener<Any>>>()

    @Suppress("UNCHECKED_CAST")
    override fun <CLASS, PROPERTY> addListener(
        field: KProperty1<CLASS, PROPERTY>,
        listener: (old: PROPERTY?, new: PROPERTY?) -> Unit
    ) {
        listeners.getOrPut(field.name, ::mutableListOf).add(listener as PropChangeListener<Any>)
    }

    override fun <CLASS, PROPERTY> removeListener(
        field: KProperty1<CLASS, PROPERTY>,
        listener: (old: PROPERTY?, new: PROPERTY?) -> Unit
    ) {
        listeners[field.name]?.remove(listener)
    }

    override fun <CLASS, PROPERTY> clearAllListeners(field: KProperty1<CLASS, PROPERTY>) {
        listeners[field.name]?.clear()
    }

    override fun notify(name: String, old: Any?, new: Any?) {
        listeners[name]?.forEach { listener -> listener(old, new) }
    }
}

class ObservableDelegate<CLASS : ObservableProperty, PROPERTY : Any?>(initialValue: PROPERTY) {
    private var value: PROPERTY = initialValue

    operator fun getValue(thisRef: CLASS, property: KProperty<*>): PROPERTY = value

    operator fun setValue(thisRef: CLASS, property: KProperty<*>, newValue: PROPERTY) {
        val oldValue = value
        value = newValue
        thisRef.notify(property.name, oldValue, newValue)
    }
}

fun <CLASS : ObservableProperty, PROPERTY : Any?> observable(initialValue: PROPERTY) =
    ObservableDelegate<CLASS, PROPERTY>(initialValue)
