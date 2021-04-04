package com.example.observableswithkotlindelegates

import java.util.*
import kotlin.collections.ArrayList

typealias ItemListener<T> = (Int, T) -> Unit

interface Movable<T> {
    fun move(element: T, destIndex: Int)

    fun addMoveListener(listener: (from: Int, to: Int) -> Unit): Boolean

    fun removeMoveListener(listener: (from: Int, to: Int) -> Unit): Boolean
}

interface ObservableList<T> : MutableList<T>, Movable<T> {

    fun addInsertListener(listener: ItemListener<T>): Boolean

    fun addRemoveListener(listener: ItemListener<T>): Boolean

    fun removeInsertListener(listener: ItemListener<T>): Boolean

    fun removeRemoveListener(listener: ItemListener<T>): Boolean

    fun clearInsertListeners()

    fun clearRemoveListeners()

    fun clearMoveListeners()
}

private class ObservableArrayList<T> : ArrayList<T>, ObservableList<T> {

    constructor() : super()
    constructor(collection: Collection<T>) : super(collection)

    val insertListeners = mutableListOf<ItemListener<T>>()
    val removeListeners = mutableListOf<ItemListener<T>>()
    val moveListeners = mutableListOf<(Int, Int) -> Unit>()

    override fun add(element: T): Boolean {
        val isAdded = super.add(element)
        if (isAdded) insertListeners.forEach { it(size - 1, element) }
        return isAdded
    }

    override fun add(index: Int, element: T) {
        super.add(index, element)
        insertListeners.forEach { it(index, element) }
    }

    override fun remove(element: T): Boolean {
        val index = indexOf(element)
        val isRemoved = super.remove(element)
        if (isRemoved) removeListeners.forEach { it(index, element) }
        return isRemoved
    }

    override fun removeAt(index: Int): T {
        val element = super.removeAt(index)
        removeListeners.forEach { it(index, element) }
        return element
    }

    //TODO: Add addAll and removeAll methods new implementation

    override fun clear() {
        while (size != 0) removeAt(0)
    }

    override fun move(element: T, destIndex: Int) {
        val currentIndex = indexOf(element)
        if (currentIndex == -1) throw IllegalAccessException("No such element in list")
        if (destIndex > lastIndex || destIndex < 0) return
        Collections.swap(this, currentIndex, destIndex)
        moveListeners.forEach { it(currentIndex, destIndex) }
    }

    override fun addMoveListener(listener: (from: Int, to: Int) -> Unit) =
        moveListeners.add(listener)

    override fun removeMoveListener(listener: (from: Int, to: Int) -> Unit) =
        moveListeners.remove(listener)

    override fun addInsertListener(listener: ItemListener<T>) = insertListeners.add(listener)

    override fun addRemoveListener(listener: ItemListener<T>) = removeListeners.add(listener)

    override fun removeInsertListener(listener: ItemListener<T>) = insertListeners.remove(listener)

    override fun removeRemoveListener(listener: ItemListener<T>) = removeListeners.remove(listener)

    override fun clearInsertListeners() = insertListeners.clear()

    override fun clearRemoveListeners() = removeListeners.clear()

    override fun clearMoveListeners() = moveListeners.clear()
}

fun <T> observableListOf(): ObservableList<T> = ObservableArrayList()
fun <T> observableListOf(collection: Collection<T>): ObservableList<T> = ObservableArrayList(
    collection
)

fun <E, T> List<E>.mapToObservableList(transform: (E) -> T): ObservableList<T> = mapTo(
    observableListOf()
) { transform(it) }

fun <T> List<T>.toObservableList(): ObservableList<T> = observableListOf(this)
