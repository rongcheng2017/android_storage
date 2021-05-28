package com.rong.cheng.storage

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.rong.cheng.storage.databinding.ActivityInnerDedicatedStorageSpaceBinding
import java.io.File

/**
 * 内部专属空间
 */
class InnerDedicatedStorageSpaceActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityInnerDedicatedStorageSpaceBinding

    private val fileName = "file1.txt"
    private val cachedFileName = "cached_file"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_inner_dedicated_storage_space
        )
        binding.getFileDir.setOnClickListener(this)
        binding.createNewFile.setOnClickListener(this)
        binding.createCachedFile.setOnClickListener(this)
        binding.writeWordsToFile.setOnClickListener(this)
        binding.readWordsFromFile.setOnClickListener(this)
        binding.getAllocatableBytes.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.getFileDir -> getFileDir()
            binding.createNewFile -> createNewFile(fileName)
            binding.createCachedFile -> createCachedFile(cachedFileName)
            binding.writeWordsToFile -> writeWordsToFile("Hello , test FileOutPutStream", fileName)
            binding.readWordsFromFile -> readWordsFromFile(fileName)
            binding.getAllocatableBytes -> getAllocatableBytes()
        }
    }

    private fun getAllocatableBytes() {

        showText("当前应用可用空间：" + StorageQueryUtil.queryWithStorageManager(this).free + " B")

//        val storageManager: StorageManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            applicationContext.getSystemService(StorageManager::class.java)
//        } else ({
//            applicationContext.getSystemService(STORAGE_SERVICE)
//        }) as StorageManager
//
//        var allocatableBytes:Long=0L
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val appSpecificInternalDirUuid = storageManager.getUuidForPath(filesDir)
//             allocatableBytes = storageManager.getAllocatableBytes(appSpecificInternalDirUuid)
//            if (allocatableBytes>NUM_BYTES_NEEDED_FOR_MY_APP){
//                storageManager.allocateBytes(appSpecificInternalDirUuid, NUM_BYTES_NEEDED_FOR_MY_APP)
//            }
//        }
    }


    /**
     * 只能写入filesDir目录下的文件
     */
    private fun writeWordsToFile(s: String, fileName: String) {
        val file = File(this.filesDir, fileName)
        if (file.exists()) {
            openFileOutput(fileName, MODE_PRIVATE)
        } else {
            file.createNewFile()
            openFileOutput(fileName, MODE_PRIVATE)
        }.use { fileOutPutStream ->
            fileOutPutStream.write(s.toByteArray())
        }
        showText("文件写入完成！")

    }

    /**
     * 只能读取filesDir目录下的文件
     */
    private fun readWordsFromFile(fileName: String) {
        val file = File(this.filesDir, fileName)
        if (file.exists()) {
            openFileInput(fileName).bufferedReader().useLines { lines ->
                val sb = StringBuffer()
                sb.append("文件中读取到的内容：")
                lines.forEach {
                    sb.append(it)
                }
                showText(sb.toString())
            }
        } else {
            showText("文件不存在")
        }
    }


    private fun createNewFile(file_name: String) {
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

    companion object {
        const val NUM_BYTES_NEEDED_FOR_MY_APP = 1024 * 1024 * 10L
    }
}