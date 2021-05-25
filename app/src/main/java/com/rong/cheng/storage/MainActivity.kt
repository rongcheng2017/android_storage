package com.rong.cheng.storage

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.rong.cheng.storage.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private final val TAG: String = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(
                this, R.layout.activity_main
            )

        binding.dedicatedStorageSpace.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.dedicatedStorageSpace -> dedicatedStorageSpace()

        }
    }

    private fun dedicatedStorageSpace() {

        startActivity(Intent(this, DedicatedStorageSpaceActivity::class.java))
        return

        val fileName = "dedicated_storage_space.txt"
        val dirName = "second_level"
        val fileContent = "this  is  dedicated storage space"
        val file = File(this.filesDir, fileName)
        if (file.exists()) {
            Log.e(TAG, "file is exit :${file.absoluteFile}")
        } else {
            file.createNewFile()
            Log.e(TAG, "file  not is exit :${file.absoluteFile}")
        }
        this.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(fileContent.toByteArray())
        }

        this.openFileInput(fileName).bufferedReader().useLines { lines ->
//            lines.fold(""){ some,text->
//                "$some\n$text"
//            }
            lines.forEach {
                Log.e(TAG, "file  content is  : $it  \n")
            }
        }

        val dir = this.getDir(dirName, Context.MODE_PRIVATE)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val secondLevelDirFile = File(dir, "second_level_file")
        if (!secondLevelDirFile.exists()) {
            secondLevelDirFile.createNewFile()
        }
        val inlineSecondLevelDirFile = File(this.filesDir, "second_level_file")
        if (!inlineSecondLevelDirFile.exists()) {
            inlineSecondLevelDirFile.createNewFile()
        }
        this.fileList().forEach {
            Log.e(TAG, "file list :$it")
        }

        Log.e(TAG, "file dirs is  :${this.filesDir}")

        val tempFile = File.createTempFile(fileName, null, this.cacheDir)
        Log.e(TAG, "temp file is : ${tempFile.path}")

    }
}