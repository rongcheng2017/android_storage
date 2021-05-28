package com.rong.cheng.storage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.rong.cheng.storage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private final val TAG: String = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(
                this, R.layout.activity_main
            )

        binding.innerDedicatedStorageSpace.setOnClickListener(this)
        binding.outDedicatedStorageSpace.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.innerDedicatedStorageSpace -> toInnerDedicatedStorageSpace()
            binding.outDedicatedStorageSpace -> toOutDedicatedStorageSpace()

        }
    }

    private fun toOutDedicatedStorageSpace() {
        startActivity(Intent(this, InnerDedicatedStorageSpaceActivity::class.java))
    }

    private fun toInnerDedicatedStorageSpace() {
        startActivity(Intent(this, InnerDedicatedStorageSpaceActivity::class.java))
    }
}