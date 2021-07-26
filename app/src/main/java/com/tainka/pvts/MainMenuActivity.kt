package com.tainka.pvts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.tainka.pvts.databinding.ActivityMainMenuBinding
import java.lang.NullPointerException
import java.util.zip.Inflater
import kotlin.system.exitProcess

class MainMenuActivity : AppCompatActivity() {

    private var accountName : String? = null

    private var _activityBinding : ActivityMainMenuBinding? = null
    private val binding get() = _activityBinding!!

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        _activityBinding = ActivityMainMenuBinding.inflate(layoutInflater)

        accountName = intent.getStringExtra("AccountName")
        setAccountName(accountName)
        print(accountName)

        binding.buttonOpenSetting.setOnClickListener()
        {
            val settingMenu = binding.settingMenu
            settingMenu.visibility = View.VISIBLE
        }

        binding.buttonCloseSetting.setOnClickListener()
        {
            val settingMenu = binding.settingMenu
            settingMenu.visibility = View.GONE
        }

        binding.buttonCloseSettingOut.setOnClickListener()
        {
            val settingMenu = binding.settingMenu
            settingMenu.visibility = View.GONE
        }

        binding.buttonLogOut.setOnClickListener()
        {
            val intent = Intent(this@MainMenuActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        setContentView(binding.root)
    }


    fun setAccountName(name : String?)
    {
        if (name == null)
        {
            throw IllegalArgumentException("Account Name Error")
        }

        val textAccountName = binding.textViewAccountName
        textAccountName.text = name
    }
}