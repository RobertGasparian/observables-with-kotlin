package com.example.observableswithkotlindelegates

import com.example.observableswithkotlindelegates.utels.TestDummy
import com.example.observableswithkotlindelegates.utels.getTestDummy
import io.mockk.Called
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class ObservableArrayListTest {

    // region Constants-----------------------------------------------------------------------------
    companion object {
        private const val ID_1 = "ID_1"
        private const val ID_2 = "ID_12"
    }
    // endregion Constants--------------------------------------------------------------------------

    // region Helper fields-------------------------------------------------------------------------
    private val listener1 = spyk<ItemListener<TestDummy>>()
    private val listener2 = spyk<ItemListener<TestDummy>>()
    private val listener3 = spyk<ItemListener<TestDummy>>()
    private val listener4 = spyk<ItemListener<TestDummy>>()
    private val moveListener1 = spyk<(Int, Int) -> Unit>()
    private val moveListener2 = spyk<(Int, Int) -> Unit>()
    // endregion Helper fields----------------------------------------------------------------------

    lateinit var SUT: ObservableList<TestDummy>

    @Before
    fun setup() {
        SUT = ObservableArrayList()
    }

    //ADD

    @Test
    fun `new element added to list`() {
        //Arrange
        //Act
        SUT.add(getTestDummy())
        //Assert
        assertEquals(1, SUT.size)
    }

    @Test
    fun `when new element is added all insert listeners are notified with correct index and element`() {
        //Arrange
        SUT.addInsertListener(listener1)
        SUT.addInsertListener(listener2)
        //Act
        SUT.add(getTestDummy())
        //Assert
        verify(exactly = 1) { listener1.invoke(0, getTestDummy()) }
        verify(exactly = 1) { listener2.invoke(0, getTestDummy()) }
    }

    @Test
    fun `removed insert listeners don't get insert event`() {
        //Arrange
        SUT.addInsertListener(listener1)
        SUT.addInsertListener(listener2)
        SUT.removeInsertListener(listener2)
        //Act
        SUT.add(getTestDummy())
        //Assert
        verify(exactly = 1) { listener1.invoke(0, getTestDummy()) }
        verify { listener2.invoke(any(), any()) wasNot Called }
    }

    @Test
    fun `when new element is added at particular index all insert listeners are notified with correct index and element`() {
        //Arrange
        SUT.add(getTestDummy(ID_1))
        SUT.addInsertListener(listener1)
        SUT.addInsertListener(listener2)
        //Act
        SUT.add(0, getTestDummy(ID_2))
        //Assert
        assertEquals(2, SUT.size)
        verify(exactly = 1) { listener1.invoke(0, getTestDummy(ID_2)) }
        verify(exactly = 1) { listener2.invoke(0, getTestDummy(ID_2)) }
    }

    @Test
    fun `when adding to invalid index IndexOutOfBoundsException must be thrown`() {
        //Arrange
        //Act
        try {
            SUT.add(-1, getTestDummy())
            fail("IndexOutOfBoundsException needed to be thrown")
        } catch (ex: IndexOutOfBoundsException) {
            //Assert
            //pass
        }
    }

    @Test
    fun `when adding to index that is greater than size IndexOutOfBoundsException must be thrown`() {
        //Arrange
        //Act
        try {
            SUT.add(10, getTestDummy())
            fail("IndexOutOfBoundsException needed to be thrown")
        } catch (ex: IndexOutOfBoundsException) {
            //Assert
            //pass
        }
    }

    @Test
    fun `after clearing insert listeners no insert listener is notified`() {
        //Arrange
        SUT.addInsertListener(listener1)
        SUT.addInsertListener(listener2)
        //Act
        SUT.clearInsertListeners()
        SUT.add(getTestDummy(ID_1))
        SUT.add(0, getTestDummy(ID_2))
        //Assert
        assertEquals(2, SUT.size)
        verify { listener1(any(), any()) wasNot Called }
        verify { listener2(any(), any()) wasNot Called }
    }

    @Test
    fun `when new element is added other event listeners were not notified`() {
        //Arrange
        SUT.addInsertListener(listener1)
        SUT.addInsertListener(listener2)
        SUT.addRemoveListener(listener3)
        SUT.addRemoveListener(listener4)
        SUT.addMoveListener(moveListener1)
        SUT.addMoveListener(moveListener2)
        //Act
        SUT.add(getTestDummy(ID_1))
        SUT.add(0, getTestDummy(ID_2))
        //Assert
        verify { listener3(any(), any()) wasNot Called }
        verify { listener4(any(), any()) wasNot Called }
        verify { moveListener1(any(), any()) wasNot Called }
        verify { moveListener2(any(), any()) wasNot Called }
    }

    //REMOVE

    @Test
    fun `element removed from the list`() {
        //Arrange
        SUT.add(getTestDummy())
        //Act
        SUT.remove(getTestDummy())
        //Assert
        assertEquals(0, SUT.size)
    }

    @Test
    fun `when element is removed all remove listeners are notified with correct index and element`() {
        //Arrange
        SUT.addRemoveListener(listener1)
        SUT.addRemoveListener(listener2)
        SUT.add(getTestDummy(ID_1))
        SUT.add(getTestDummy(ID_2))
        //Act
        SUT.remove(getTestDummy(ID_2))
        //Assert
        verify(exactly = 1) { listener1.invoke(1, getTestDummy(ID_2)) }
        verify(exactly = 1) { listener2.invoke(1, getTestDummy(ID_2)) }
    }

    @Test
    fun `removed remove listeners don't get remove event`() {
        //Arrange
        SUT.addRemoveListener(listener1)
        SUT.addRemoveListener(listener2)
        SUT.removeRemoveListener(listener2)
        SUT.add(getTestDummy(ID_1))
        SUT.add(getTestDummy(ID_2))
        //Act
        SUT.remove(getTestDummy(ID_2))
        //Assert
        verify(exactly = 1) { listener1.invoke(1, getTestDummy(ID_2)) }
        verify { listener2.invoke(any(), any()) wasNot Called }
    }

    @Test
    fun `when element is removed at particular index all remove listeners are notified with correct index and element`() {
        //Arrange
        SUT.addRemoveListener(listener1)
        SUT.addRemoveListener(listener2)
        SUT.add(getTestDummy(ID_1))
        SUT.add(getTestDummy(ID_2))
        //Act
        SUT.removeAt(0)
        //Assert
        verify(exactly = 1) { listener1.invoke(0, getTestDummy(ID_1)) }
        verify(exactly = 1) { listener2.invoke(0, getTestDummy(ID_1)) }
    }

    @Test
    fun `when removing at invalid index IndexOutOfBoundsException must be thrown`() {
        //Arrange
        SUT.add(getTestDummy())
        //Act
        try {
            SUT.removeAt(-1)
            fail("IndexOutOfBoundsException needed to be thrown")
        } catch (ex: IndexOutOfBoundsException) {
            //Assert
            //pass
        }
    }

    @Test
    fun `when removing at index that is greater then size IndexOutOfBoundsException must be thrown`() {
        //Arrange
        SUT.add(getTestDummy())
        //Act
        try {
            SUT.removeAt(32)
            fail("IndexOutOfBoundsException needed to be thrown")
        } catch (ex: IndexOutOfBoundsException) {
            //Assert
            //pass
        }
    }

    @Test
    fun `when removing non-existing element false must be returned`() {
        //Arrange
        SUT.add(getTestDummy(ID_1))
        //Act
        val result = SUT.remove(getTestDummy(ID_2))
        //Assert
        assertFalse(result)
        assertEquals(1, SUT.size)
    }

    @Test
    fun `when removing non-existing element no listener must be notified`() {
        //Arrange
        SUT.addRemoveListener(listener1)
        SUT.addRemoveListener(listener2)
        SUT.add(getTestDummy(ID_1))
        //Act
        SUT.remove(getTestDummy(ID_2))
        //Assert
        verify { listener1(any(), any()) wasNot Called }
        verify { listener2(any(), any()) wasNot Called }
    }

    @Test
    fun `after clearing remove listeners no listener is notified`() {
        //Arrange
        SUT.add(getTestDummy(ID_1))
        SUT.add(getTestDummy(ID_2))
        SUT.addRemoveListener(listener1)
        SUT.addRemoveListener(listener2)
        //Act
        SUT.clearRemoveListeners()
        SUT.removeAt(0)
        SUT.remove(getTestDummy(ID_2))

        //Assert
        assertEquals(0, SUT.size)
        verify { listener1(any(), any()) wasNot Called }
        verify { listener2(any(), any()) wasNot Called }
    }

    @Test
    fun `when removing element other event listeners were not notified`() {
        //Arrange
        SUT.addInsertListener(listener1)
        SUT.addInsertListener(listener2)
        SUT.addRemoveListener(listener3)
        SUT.addRemoveListener(listener4)
        SUT.addMoveListener(moveListener1)
        SUT.addMoveListener(moveListener2)
        SUT.add(getTestDummy(ID_1))
        SUT.add(getTestDummy(ID_2))
        //Act
        SUT.removeAt(0)
        SUT.remove(getTestDummy(ID_2))
        //Assert
        verify { listener1(any(), any()) wasNot Called }
        verify { listener2(any(), any()) wasNot Called }
        verify { moveListener1(any(), any()) wasNot Called }
        verify { moveListener2(any(), any()) wasNot Called }
    }

    //MOVE

    @Test
    fun `when moving elements are swapped`() {
        //Arrange
        SUT.add(getTestDummy(ID_1))
        SUT.add(getTestDummy(ID_2))
        //Act
        SUT.move(getTestDummy(ID_1), 1)
        //Assert
        assertEquals(2, SUT.size)
        assertEquals(getTestDummy(ID_2), SUT[0])
        assertEquals(getTestDummy(ID_1), SUT[1])
    }

    @Test
    fun `when moving to same spot nothing changes`() {
        //Arrange
        SUT.add(getTestDummy(ID_1))
        SUT.add(getTestDummy(ID_2))
        //Act
        SUT.move(getTestDummy(ID_1), 0)
        //Assert
        assertEquals(2, SUT.size)
        assertEquals(getTestDummy(ID_1), SUT[0])
        assertEquals(getTestDummy(ID_2), SUT[1])
    }

    @Test
    fun `when element is moved all move listeners notified`() {
        //Arrange
        SUT.add(getTestDummy(ID_1))
        SUT.add(getTestDummy(ID_2))
        SUT.addMoveListener(moveListener1)
        SUT.addMoveListener(moveListener2)
        //Act
        SUT.move(getTestDummy(ID_1), 1)
        //Assert
        verify(exactly = 1) { moveListener1(0, 1) }
        verify(exactly = 1) { moveListener2(0, 1) }
    }

    @Test
    fun `removed move listeners don't get move event`() {
        //Arrange
        SUT.add(getTestDummy(ID_1))
        SUT.add(getTestDummy(ID_2))
        SUT.addMoveListener(moveListener1)
        SUT.addMoveListener(moveListener2)
        SUT.addMoveListener(moveListener2)
        //Act
        SUT.move(getTestDummy(ID_1), 1)
        //Assert
        verify(exactly = 1) { moveListener1(0, 1) }
        verify { moveListener2(any(), any()) wasNot Called }
    }

    @Test
    fun `when moving to invalid index IndexOutOfBoundsException must be thrown`() {
        //Arrange
        SUT.add(getTestDummy(ID_1))
        //Act
        try {
            SUT.move(getTestDummy(ID_1), -1)
            fail("IndexOutOfBoundsException needed to be thrown")
        } catch (ex: IndexOutOfBoundsException) {
            //Assert
            //pass
        }
    }

    @Test
    fun `when element is moved to index that is greater then size IndexOutOfBoundsException must be thrown`() {
        SUT.add(getTestDummy(ID_1))
        //Act
        try {
            SUT.move(getTestDummy(ID_1), 21)
            fail("IndexOutOfBoundsException needed to be thrown")
        } catch (ex: IndexOutOfBoundsException) {
            //Assert
            //pass
        }
    }

    @Test
    fun `when moving non-existing element IllegalAccessException must be thrown`() {
        //Arrange
        SUT.add(getTestDummy(ID_1))
        //Act
        try {
            SUT.move(getTestDummy(ID_2), 0)
            fail("IllegalAccessException needed to be thrown")
        } catch (ex: IllegalAccessException) {
            //Assert
            //pass
        }
    }

    @Test
    fun `after clearing move listeners no listener is notified`() {
        //Arrange
        SUT.add(getTestDummy(ID_1))
        SUT.add(getTestDummy(ID_2))
        SUT.addMoveListener(moveListener1)
        SUT.addMoveListener(moveListener2)
        //Act
        SUT.clearMoveListeners()
        SUT.move(getTestDummy(ID_1), 1)

        //Assert
        assertEquals(2, SUT.size)
        verify { moveListener1(any(), any()) wasNot Called }
        verify { moveListener2(any(), any()) wasNot Called }
    }

    @Test
    fun `when element is moved other event listeners were not notified`() {
        //Arrange
        SUT.addInsertListener(listener1)
        SUT.addInsertListener(listener2)
        SUT.addRemoveListener(listener3)
        SUT.addRemoveListener(listener4)
        SUT.addMoveListener(moveListener1)
        SUT.addMoveListener(moveListener2)
        SUT.add(getTestDummy(ID_1))
        SUT.add(getTestDummy(ID_2))
        //Act
        SUT.move(getTestDummy(ID_1), 1)
        //Assert
        verify { listener1(any(), any()) wasNot Called }
        verify { listener2(any(), any()) wasNot Called }
        verify { listener3(any(), any()) wasNot Called }
        verify { listener4(any(), any()) wasNot Called }
    }

    // region Helper methods------------------------------------------------------------------------

    // endregion Helper methods----------------------------------------------------------------------

    // region Helper classes------------------------------------------------------------------------

    // endregion Helper classes---------------------------------------------------------------------

}