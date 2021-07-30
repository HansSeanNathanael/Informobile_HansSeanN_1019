package com.tainka.pvts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.tainka.pvts.databinding.ActivityMainBinding
import java.io.IOException
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private var _activityBinding : ActivityMainBinding? = null
    private val binding get() = _activityBinding!!

    private var loginResult = emptyList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityBinding = ActivityMainBinding.inflate(layoutInflater)

        binding.buttonLogin.setOnClickListener{

            thread {
                login()
            }

        }

        setContentView(binding.root)
    }

    private fun ParseJSON(JSON : String) : List<String>
    {
        var returnVal : MutableList<String> = mutableListOf()

        var i = 0
        while (i < JSON.length)
        {
            if (JSON[i] == '{')
            {
                var str = ""
                while (i < JSON.length)
                {
                    if (JSON[i] == ':')
                    {
                        str = ""
                    }
                    else if (JSON[i] == ',' || JSON[i] == '}')
                    {
                        returnVal.add(str)
                    }
                    else if (JSON[i] != '"')
                    {
                        str += JSON[i]
                    }
                    i++
                }
                break
            }
            i++
        }

        return returnVal
    }

    private fun login()
    {

        this@MainActivity.runOnUiThread {
            binding.buttonLogin.isClickable = false
            binding.buttonLogin.text = "..."
        }

        val emailInput = binding.emailEditText.text.toString()
        val passwordInput = binding.passwordEditText.text.toString()

        val url = "http://192.168.100.9/PVTS/login.php?" + "email=" + emailInput + "&password=" + passwordInput

        val connection = URL(url)

        try {
            loginResult = ParseJSON(connection.readText())

            this@MainActivity.runOnUiThread {
                if (loginResult[0] == "success")
                {
                    binding.buttonLogin.isClickable = true
                    binding.loginErrorMessage.text = ""
                    binding.loginErrorMessage.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                    val loginMessage = this@MainActivity.getString(R.string.login)
                    binding.buttonLogin.text = loginMessage

                    val intent = Intent(this@MainActivity, MainMenuActivity::class.java)
                    intent.putExtra("AccountName", loginResult[2])
                    startActivity(intent)
                }
                else
                {
                    val loginMessage = this@MainActivity.getString(R.string.login)
                    binding.buttonLogin.text = loginMessage

                    val failedLoginMessage = binding.loginErrorMessage
                    val errorIcon = ResourcesCompat.getDrawable(this@MainActivity.resources, R.drawable.ic_error, null)
                    val theMessage = this@MainActivity.getString(R.string.wrong_account)
                    val errorColor = ResourcesCompat.getColor(this@MainActivity.resources, R.color.redError, null)

                    errorIcon?.setTint(errorColor)
                    failedLoginMessage.setCompoundDrawablesWithIntrinsicBounds(errorIcon, null, null, null)
                    failedLoginMessage.text = theMessage
                }
            }
        }
        catch (e: IOException)
        {
            this@MainActivity.runOnUiThread {
                val failedLoginMessage = binding.loginErrorMessage
                val errorIcon = ResourcesCompat.getDrawable(this@MainActivity.resources, R.drawable.ic_error, null)
                val theMessage = this@MainActivity.getString(R.string.connection_timeout)
                val errorColor = ResourcesCompat.getColor(this@MainActivity.resources, R.color.redError, null)

                errorIcon?.setTint(errorColor)
                failedLoginMessage.setCompoundDrawablesWithIntrinsicBounds(errorIcon, null, null, null)
                failedLoginMessage.text = theMessage

                val loginMessage = this@MainActivity.getString(R.string.login)
                binding.buttonLogin.text = loginMessage
            }
        }

        this@MainActivity.runOnUiThread {
            binding.buttonLogin.isClickable = true
        }
    }
}