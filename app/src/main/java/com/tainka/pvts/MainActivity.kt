package com.tainka.pvts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.viewbinding.ViewBinding
import com.tainka.pvts.databinding.ActivityMainBinding
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {

    private var _activityBinding : ActivityMainBinding? = null
    private val binding get() = _activityBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityBinding = ActivityMainBinding.inflate(layoutInflater)


        binding.buttonLogin.setOnClickListener{

            val emailInput = binding.emailEditText.text.toString()
            val passwordInput = binding.passwordEditText.text.toString()
            if (emailInput.equals("admin", ignoreCase = true) && passwordInput == "admin")
            {

                val intent = Intent(this@MainActivity, MainMenuActivity::class.java)
                intent.putExtra("AccountName", "Admin")
                startActivity(intent)
            }
            else
            {
                val failedLoginMessage = binding.loginErrorMessage
                val errorIcon = ResourcesCompat.getDrawable(this@MainActivity.resources, R.drawable.ic_error, null)
                val theMessage = this@MainActivity.getString(R.string.wrong_accout)
                val errorColor = ResourcesCompat.getColor(this@MainActivity.resources, R.color.redError, null)

                errorIcon?.setTint(errorColor)
                failedLoginMessage.setCompoundDrawablesWithIntrinsicBounds(errorIcon, null, null, null)
                failedLoginMessage.text = theMessage
            }
        }

        setContentView(binding.root)

    }
}