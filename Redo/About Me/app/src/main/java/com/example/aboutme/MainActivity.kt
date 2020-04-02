package com.example.android.aboutme

import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.example.aboutme.MyName
import com.example.aboutme.R
import com.example.android.aboutme.databinding.ActivityMainBinding


/**
 * Main Activity of the AboutMe app. This app demonstrates:
 *     * LinearLayout with TextViews, ImageView, Button, EditText, and ScrollView
 *     * ScrollView to display scrollable text
 *     * Getting user input with an EditText.
 *     * Click handler for a Button to retrieve text from an EditText and set it in a TextView.
 *     * Setting the visibility status of a view.
 *     * Data binding between MainActivity and activity_main.xml. How to remove findViewById,
 *       and how to display data in views using the data binding object.
 */
class MainActivity : AppCompatActivity() {

    // Binding object for MainActivity.
    // Name of the object is derived from the name of the activity or fragment.
    private lateinit var binding: ActivityMainBinding

    // Instance of MyName data class.
    private val myName: MyName = MyName("Lindsey Wang")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.myName = myName

        binding.doneButton.setOnClickListener {
            addNickname(it)
        }
    }

    /**
     * Click handler for the Done button.
     * Demonstrates how data binding can be used to make code much more readable
     * by eliminating calls to findViewById and changing data in the binding object.
     */
    private fun addNickname(view: View) {
        binding.apply {
            // Set the text for nicknameText to the value in nicknameEdit.
            myName?.nickname = nicknameEdit.text.toString()
            // Invalidate all binding expressions and request a new rebind to refresh UI
            invalidateAll()
            // Change which views are visible.
            // Remove the EditText and the Button.
            // With GONE they are invisible and do not occupy space.
            nicknameEdit.visibility = View.GONE
            doneButton.visibility = View.GONE

            nicknameText.visibility = View.VISIBLE
        }
        // Hide the keyboard.
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}