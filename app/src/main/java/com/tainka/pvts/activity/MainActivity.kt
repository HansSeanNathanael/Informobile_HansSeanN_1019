package com.tainka.pvts.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.tainka.pvts.R
import com.tainka.pvts.databinding.ActivityMainBinding
import java.io.IOException
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private var _activityBinding : ActivityMainBinding? = null
    private val binding get() = _activityBinding!!

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

    private fun parseJSON(JSON : String) : List<String>
    {
        var returnVal : ArrayList<String> = arrayListOf()

        var i = 0
        while (i < JSON.length)
        {
            if (JSON[i] == '"')
            {
                var str = ""
                i++;

                while(JSON[i] != '"')
                {
                    str += JSON[i]
                    i++;
                }
                returnVal.add(str)
            }
            i++
        }

        return returnVal
    }

    private fun checkPassword(password : String) : Boolean
    {

        for (i in 0..(password.length-1))
        {
            if ( !((password[i].code >= '0'.code && password[i].code <= '9'.code) ||
                  (password[i].code >= 'A'.code && password[i].code <= 'Z'.code) ||
                  (password[i].code >= 'a'.code && password[i].code <= 'z'.code)) )
            {
                return false
            }
        }

        return true
    }

    private fun getNetworkText(url : String) : String
    {
        var returnValue : String = ""
        val connection : URL = URL(url)

        try
        {
            val text = connection.readText()
            var firstIndex = text.indexOf("<body>", 0,true)
            while(text[firstIndex] != '"' && text[firstIndex] != '[' && firstIndex < text.length)
            {
                firstIndex += 1
            }
            var lastIndex = text.indexOf("</body>", firstIndex, true)
            while(text[lastIndex] != '"' && text[lastIndex] != ']' && lastIndex > 0)
            {
                lastIndex -= 1
            }
            returnValue = text.substring(firstIndex, lastIndex+1)
        }
        catch (e : IOException)
        {
            this@MainActivity.runOnUiThread {
                val failedLoginMessage = binding.loginErrorMessage
                val errorIcon = ResourcesCompat.getDrawable(this@MainActivity.resources,
                    R.drawable.ic_error, null)
                val theMessage = this@MainActivity.getString(R.string.connection_timeout)
                val errorColor = ResourcesCompat.getColor(this@MainActivity.resources,
                    R.color.redError, null)

                errorIcon?.setTint(errorColor)
                failedLoginMessage.setCompoundDrawablesWithIntrinsicBounds(errorIcon, null, null, null)
                failedLoginMessage.text = theMessage
            }

            returnValue = "ERROR"
        }

        return returnValue
    }

    private fun login()
    {

        this@MainActivity.runOnUiThread {
            binding.buttonLogin.isClickable = false
            binding.buttonLogin.text = "..."
        }

        val emailInput = binding.emailEditText.text.toString()
        var passwordInput = binding.passwordEditText.text.toString()

        if (checkPassword(passwordInput))
        {

            // Encrypt password through URL
            var url = "http://192.168.100.8/PVTS/encryption.php?text=" + passwordInput

            // Trim the text from URL (just take json_encode)
            var jsonText = getNetworkText(url)

            if (jsonText != "ERROR")
            {
                // if not connection timeout (jsonText will "ERROR" if connection timeout)

                var parsedJSON : List<String> = parseJSON(jsonText) // Parse JSON from URL to get encrypted password

                // accessing login url
                url = "http://192.168.100.8/PVTS/login.php?" + "email=" + emailInput + "&password=" + parsedJSON[0]
                jsonText = getNetworkText(url)

                if (jsonText != "ERROR")
                {
                    // if successfully connect to login URL

                    parsedJSON = parseJSON(jsonText)

                    this@MainActivity.runOnUiThread {
                        if (parsedJSON[0] == "success")
                        {
                            binding.buttonLogin.isClickable = true
                            binding.loginErrorMessage.text = ""
                            binding.loginErrorMessage.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                            val intent = Intent(this@MainActivity, MainMenuActivity::class.java)
                            intent.putExtra("AccountName", parsedJSON[2])
                            startActivity(intent)
                        }
                        else
                        {
                            val failedLoginMessage = binding.loginErrorMessage
                            val errorIcon = ResourcesCompat.getDrawable(this@MainActivity.resources,
                                R.drawable.ic_error, null)
                            val theMessage = this@MainActivity.getString(R.string.wrong_account)
                            val errorColor = ResourcesCompat.getColor(this@MainActivity.resources,
                                R.color.redError, null)

                            errorIcon?.setTint(errorColor)
                            failedLoginMessage.setCompoundDrawablesWithIntrinsicBounds(errorIcon, null, null, null)
                            failedLoginMessage.text = theMessage
                        }
                    }
                }

            }


        }
        else
        {
            this@MainActivity.runOnUiThread {
                val failedLoginMessage = binding.loginErrorMessage

                val passwordNotCompatibleMessage = this@MainActivity.getString(R.string.password_not_compatible)
                val errorIcon = ResourcesCompat.getDrawable(this@MainActivity.resources,
                    R.drawable.ic_error, null)
                val errorColor = ResourcesCompat.getColor(this@MainActivity.resources,
                    R.color.redError, null)

                errorIcon?.setTint(errorColor)
                failedLoginMessage.setCompoundDrawablesWithIntrinsicBounds(errorIcon, null, null, null)
                failedLoginMessage.text = passwordNotCompatibleMessage
            }
        }

        this@MainActivity.runOnUiThread {
            binding.buttonLogin.isClickable = true

            val loginMessage = this@MainActivity.getString(R.string.login)
            binding.buttonLogin.text = loginMessage
        }
    }
}