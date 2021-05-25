package com.rong.cheng.storage

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.storage.StorageManager
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.rong.cheng.storage.databinding.ActivityDedicatedStorageSpaceBinding
import java.io.File

/**
 * 内部专属空间
 */
class DedicatedStorageSpaceActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityDedicatedStorageSpaceBinding

    private val fileName = "file1.txt"
    private val cachedFileName = "cached_file"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_dedicated_storage_space
        )
        binding.getFileDir.setOnClickListener(this)
        binding.createNewFile.setOnClickListener(this)
        binding.createCachedFile.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.getFileDir -> getFileDir()
            binding.createNewFile -> createNewFile(fileName)
            binding.createCachedFile -> createCachedFile(cachedFileName)
        }
    }

    private fun createNewFile( file_name: String) {
        val file = File(this.filesDir, file_name)
        if (file.exists()) {
            showText("file exists , path is ${file.path}")
        } else {
            val exists = file.createNewFile()
            showText("file no exits ,create new file and create success ? : $exists , path is ${file.path}")
        }
    }

    private fun createCachedFile(file_name: String) {
        val file = File.createTempFile(file_name, null, this.cacheDir)
        val systemService = this.getSystemService(Context.STORAGE_SERVICE)
        if (file.exists()) {
            showText("file exists , path is ${file.path}")
        } else {
            val exists = file.createNewFile()
            showText("file no exits ")
        }
    }

    private fun getFileDir() {
        binding.showSpace.text = "file dir is :${this.filesDir}"
    }

    private fun showText(content: String) {
        binding.showSpace.text = content
    }
}