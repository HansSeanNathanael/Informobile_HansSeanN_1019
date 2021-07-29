package com.tainka.pvts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.tainka.pvts.databinding.ActivityMainBinding
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private var _activityBinding : ActivityMainBinding? = null
    private val binding get() = _activityBinding!!

    private var loginButtonDisabled = false
    private var loginSuccess = false
    private var loginResult = emptyList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityBinding = ActivityMainBinding.inflate(layoutInflater)

        binding.buttonLogin.setOnClickListener{

            /*
            val emailInput = binding.emailEditText.text.toString()
            val passwordInput = binding.passwordEditText.text.toString()

            var result : List<String> = emptyList()

            thread {
                val url = "http://192.168.100.9/PVTS/login.php?" + "email=" + emailInput + "&password=" + passwordInput
                var connection = URL(url)
                a = connection.readText()
            }
            */


            thread {
                login()
            }

            this@MainActivity.runOnUiThread {
                while(true)
                {
                    if (loginButtonDisabled && binding.buttonLogin.isClickable)
                    {
                        binding.buttonLogin.isClickable = false
                        binding.buttonLogin.text = "..."
                    }
                    else if (!loginButtonDisabled && !binding.buttonLogin.isClickable)
                    {
                        binding.buttonLogin.isClickable = true
                        binding.buttonLogin.text = "LOGIN"

                        val failedLoginMessage = binding.loginErrorMessage
                        val errorIcon = ResourcesCompat.getDrawable(this@MainActivity.resources, R.drawable.ic_error, null)
                        val theMessage = this@MainActivity.getString(R.string.wrong_account)
                        val errorColor = ResourcesCompat.getColor(this@MainActivity.resources, R.color.redError, null)

                        errorIcon?.setTint(errorColor)
                        failedLoginMessage.setCompoundDrawablesWithIntrinsicBounds(errorIcon, null, null, null)
                        failedLoginMessage.text = theMessage

                        break
                    }
                    else if (loginSuccess)
                    {
                        break
                    }
                }
                if (loginSuccess)
                {
                    val intent = Intent(this@MainActivity, MainMenuActivity::class.java)
                    intent.putExtra("AccountName", loginResult[2])
                    startActivity(intent)
                }
            }

            /*
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
                val theMessage = this@MainActivity.getString(R.string.wrong_account)
                val errorColor = ResourcesCompat.getColor(this@MainActivity.resources, R.color.redError, null)

                errorIcon?.setTint(errorColor)
                failedLoginMessage.setCompoundDrawablesWithIntrinsicBounds(errorIcon, null, null, null)
                //failedLoginMessage.text = theMessage
            }*/
        }
        setContentView(binding.root)
    }

    fun ParseJSON(JSON : String) : List<String>
    {
        var returnVal : MutableList<String> = mutableListOf()

        var i = 0
        var keyval = false
        while (i < JSON.length)
        {
            if (JSON[i] == '{')
            {
                var str = ""
                var line = 0
                while (i < JSON.length)
                {
                    if (JSON[i] == ':')
                    {
                        str = ""
                        keyval = true;
                    }
                    else if (JSON[i] == ',' || JSON[i] == '}')
                    {
                        returnVal.add(str)
                        keyval = false
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

    fun login()
    {
        loginButtonDisabled = true

        val emailInput = binding.emailEditText.text.toString()
        val passwordInput = binding.passwordEditText.text.toString()

        val url = "http://192.168.100.9/PVTS/login.php?" + "email=" + emailInput + "&password=" + passwordInput

        loginResult = ParseJSON(URL(url).readText())

        if (loginResult[0] == "success")
        {
            loginSuccess = true
        }
        else
        {
            loginButtonDisabled = false
        }
    }
}