package com.example.observableswithkotlindelegates

import com.example.observableswithkotlindelegates.ObservablePropertyTest.PropTestClass.Companion.ID_CHANGED
import com.example.observableswithkotlindelegates.ObservablePropertyTest.PropTestClass.Companion.VALUE_CHANGED
import com.example.observableswithkotlindelegates.ObservablePropertyTest.PropTestClass.Companion.ID_DEFAULT
import com.example.observableswithkotlindelegates.ObservablePropertyTest.PropTestClass.Companion.VALUE_DEFAULT
import com.example.observableswithkotlindelegates.utels.TestDummy
import com.example.observableswithkotlindelegates.utels.getTestDummy
import io.mockk.Called
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ObservablePropertyTest {

    // region Constants-----------------------------------------------------------------------------

    // endregion Constants--------------------------------------------------------------------------

    // region Helper fields-------------------------------------------------------------------------
    private val prop1Listener1 = spyk<PropChangeListener<Int>>()
    private val prop1Listener2 = spyk<PropChangeListener<Int>>()
    private val prop2Listener1 = spyk<PropChangeListener<TestDummy>>()
    private val prop2Listener2 = spyk<PropChangeListener<TestDummy>>()
    // endregion Helper fields----------------------------------------------------------------------

    lateinit var SUT: PropTestClass

    @Before
    fun setup() {
        SUT = PropTestClass()
    }

    @Test
    fun `when property changed all listeners notified with correct values`() {
        //Arrange
        addListeners()
        //Act
        SUT.prop1 = VALUE_CHANGED
        SUT.prop2 = getTestDummy(ID_CHANGED)
        //Assert
        verify(exactly = 1) { prop1Listener1(VALUE_DEFAULT, VALUE_CHANGED) }
        verify(exactly = 1) { prop1Listener2(VALUE_DEFAULT, VALUE_CHANGED) }
        verify(exactly = 1) { prop2Listener1(getTestDummy(ID_DEFAULT), getTestDummy(ID_CHANGED)) }
        verify(exactly = 1) { prop2Listener2(getTestDummy(ID_DEFAULT), getTestDummy(ID_CHANGED)) }
    }

    @Test
    fun `when property changed removed listeners don't get notified`() {
        //Arrange
        addListeners()
        SUT.removeListener(PropTestClass::prop1, prop1Listener2)
        SUT.removeListener(PropTestClass::prop2, prop2Listener2)
        //Act
        SUT.prop1 = VALUE_CHANGED
        SUT.prop2 = getTestDummy(ID_CHANGED)
        //Assert
        verify { prop1Listener2(any(), any()) wasNot Called }
        verify { prop2Listener2(any(), any()) wasNot Called }
    }

    @Test
    fun `when all listeners cleared none is notified`() {
        //Arrange
        addListeners()
        SUT.clearAllListeners(PropTestClass::prop1)
        SUT.clearAllListeners(PropTestClass::prop2)
        //Act
        SUT.prop1 = VALUE_CHANGED
        SUT.prop2 = getTestDummy(ID_CHANGED)
        //Assert
        verify { prop1Listener1(any(), any()) wasNot Called }
        verify { prop1Listener2(any(), any()) wasNot Called }
        verify { prop2Listener1(any(), any()) wasNot Called }
        verify { prop2Listener2(any(), any()) wasNot Called }
    }

    // region Helper methods------------------------------------------------------------------------
    private fun addListeners() {
        SUT.addListener(PropTestClass::prop1, prop1Listener1)
        SUT.addListener(PropTestClass::prop1, prop1Listener2)
        SUT.addListener(PropTestClass::prop2, prop2Listener1)
        SUT.addListener(PropTestClass::prop2, prop2Listener2)
    }
    // endregion Helper methods----------------------------------------------------------------------

    // region Helper classes------------------------------------------------------------------------
    class PropTestClass: ObservableProperty by ObservablePropertyImpl() {
        companion object {
            const val VALUE_DEFAULT = 1
            const val ID_DEFAULT = "idDef"
            const val VALUE_CHANGED = 2
            const val ID_CHANGED = "changedId"
        }
        var prop1: Int by observable(VALUE_DEFAULT)
        var prop2: TestDummy by observable(getTestDummy(ID_DEFAULT))
    }
    // endregion Helper classes---------------------------------------------------------------------

}