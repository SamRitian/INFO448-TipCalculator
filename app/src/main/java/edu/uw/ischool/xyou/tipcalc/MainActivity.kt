package edu.uw.ischool.xyou.tipcalc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private var current = "" // The current value of the EditText
    private var percentage = 0.0 // The current tip percentage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tipInput: EditText = findViewById(R.id.amount)
        val tipSelection: RadioGroup = findViewById(R.id.tipGroup)
        val tipButton: Button = findViewById(R.id.tip)

        // Create a textWatcher to format the EditText as a dollar amount
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    tipInput.removeTextChangedListener(this)
                    tipInput.hint = "" // Remove the hint

                    val cleanString = s.toString().replace("\\D".toRegex(), "") // Remove all non-digit characters
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble() // Convert to a double
                        val formatted = String.format("$%.2f", parsed / 100) // Format as a dollar amount

                        current = formatted
                        tipInput.setText(formatted)
                        tipInput.setSelection(formatted.length) // Move the cursor to the end
                    } else {
                        tipInput.setText("")
                    }

                    tipInput.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Enable the tip button if the input is valid
                enableTipButton()
            }
        }

        // Add the textWatcher to the EditText
        tipInput.addTextChangedListener(textWatcher)

        // Add a click listener to the RadioGroup
        tipSelection.setOnCheckedChangeListener { group, checkedId ->
            // Hide the keyboard
            val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            percentage = when (checkedId) {
                R.id.ten -> 0.10
                R.id.fifteen -> 0.15
                R.id.eighteen -> 0.18
                else -> 0.20
            }

            // Enable the tip button if the input is valid
            enableTipButton()
        }

        // Add a click listener to the button
        tipButton.setOnClickListener {
            calculateTip(tipInput.text.toString(), percentage)
        }
    }

    private fun calculateTip(amount: String, percentage: Double) {
        val parsed = amount.replace("\\D".toRegex(), "").toDouble() / 100 // Remove all non-digit characters and convert to a double
        val tip = parsed * percentage
        val formatted = String.format("$%.2f", tip) // Format as a dollar amount

        // Display the tip
        Toast.makeText(this, "Tip: $formatted", Toast.LENGTH_LONG).show()
    }

    private fun enableTipButton() {
        val tipButton: Button = findViewById(R.id.tip)
        val tipInput: EditText = findViewById(R.id.amount)
        tipButton.isEnabled = tipInput.hint.toString() != "Amount" && current != "$0.00" && percentage > 0
    }
}