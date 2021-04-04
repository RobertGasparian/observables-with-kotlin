package com.example.observableswithkotlindelegates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "observable_checker"
    }

    private lateinit var user: User

    private val contacts = observableListOf<Contact>()

    private val hobbyListener: (Hobby?, Hobby?) -> Unit = { old, new ->
        Log.d(TAG, "Hobby has changed from $old to $new")
    }

    private val contactsRemoveListener:(Int, Contact) -> Unit = { index, contact ->
        Log.d(TAG, "Removed $contact from $index index")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        user = User("John")
        user.addListener(User::age) { old, new ->
            Log.d(TAG, "Age has changed from $old to $new")
        }
        user.addListener(User::hobby, hobbyListener)
        user.age = 21
        user.age = 46
        user.hobby = Hobby("Hunting")
        user.hobby = Hobby("Hunting")
        user.hobby = Hobby("Dancing")
        contacts.addInsertListener { index, contact ->
            Log.d(TAG, "Added new $contact at $index index")
        }
        contacts.addRemoveListener(contactsRemoveListener)
        contacts.addMoveListener { from, to ->
            Log.d(TAG, "Moved from $from to $to")
        }
        contacts.add(Contact("Jim", "123456"))
        contacts.add(Contact("Jack", "345123"))
        contacts.add(1, Contact("Steve", "9876554"))
        contacts.remove(Contact("Steve", "9876554"))
        contacts.move(Contact("Jim", "123456"), 1)
    }

    override fun onDestroy() {
        user.clearAllListeners(User::age)
        user.removeListener(User::hobby, hobbyListener)
        contacts.clearInsertListeners()
        contacts.removeRemoveListener(contactsRemoveListener)
        contacts.clearMoveListeners()
        super.onDestroy()
    }
}

data class User(
        val name: String
) : ObservableProperty by ObservablePropertyImpl() {
    var age: Int by observable(20)
    var hobby: Hobby by observable(Hobby("Skydiving"))
}

data class Hobby(val name: String) {
    override fun toString(): String {
        return "Hobby: $name"
    }
}

data class Contact(
        val name: String,
        val phone: String
) {
    override fun toString(): String {
        return "Contact: name - $name, phone: $phone"
    }
}