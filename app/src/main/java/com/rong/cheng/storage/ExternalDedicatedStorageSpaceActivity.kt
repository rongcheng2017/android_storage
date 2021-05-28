package com.rong.cheng.storage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.rong.cheng.storage.databinding.ActivityExternalDedicatedStorageBinding
import java.io.File

/**
 * 外部存储空间
 */
class ExternalDedicatedStorageSpaceActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "OutDedicatedStorageSpaceActivity"
    private lateinit var mBinding: ActivityExternalDedicatedStorageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView<ActivityExternalDedicatedStorageBinding>(
            this,
            R.layout.activity_external_dedicated_storage
        )
        mBinding.isExternalStorageCanUse.setOnClickListener(this)
        mBinding.getExternalStoragePath.setOnClickListener(this)
        mBinding.createPersistentFile.setOnClickListener(this)
        mBinding.createCacheFile.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v) {
            mBinding.isExternalStorageCanUse -> isExternalStorageCanUse()
            mBinding.getExternalStoragePath -> getExternalStoragePath()
            mBinding.createPersistentFile -> createPersistentFile()
            mBinding.createCacheFile -> createCacheFile()
        }
    }

    private fun createCacheFile() {
        val cacheFile = File(externalCacheDir, "cache_file.txt")
        if (cacheFile.exists()) {
            showContent("创建缓存文件成功，path ： ${cacheFile.path}")
        } else {
            cacheFile.createNewFile()
            showContent("创建缓存文件成功，path ： ${cacheFile.path}")
        }

    }

    /**
     * 创建持久文件
     */
    private fun createPersistentFile() {
        /**
         * 通过这个Type可以指定更细的目录
         *          {@link android.os.Environment#DIRECTORY_MUSIC},
         *            {@link android.os.Environment#DIRECTORY_PODCASTS},
         *            {@link android.os.Environment#DIRECTORY_RINGTONES},
         *            {@link android.os.Environment#DIRECTORY_ALARMS},
         *            {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
         *            {@link android.os.Environment#DIRECTORY_PICTURES}, or
         *            {@link android.os.Environment#DIRECTORY_MOVIES}.
         */
        val persistentFile = File(this.getExternalFilesDir(null), "persistent_file.txt")
        if (persistentFile.exists()) {
            showContent("创建持久文件成功，path ： ${persistentFile.path}")
        } else {
            persistentFile.createNewFile()
            showContent("创建持久文件成功，path ： ${persistentFile.path}")
        }
        //可以创造更细分的目录，比如图片。视频
//        val albumFile =StorageQueryUtil.getAppSpecificAlbumStorageDir(this,"albumFile")
    }

    private fun getExternalStoragePath() {
        if (isExternalStorageCanUse()) {
            val externalFilesDirs = ContextCompat.getExternalFilesDirs(this, null)
            val externalCacheDirs = ContextCompat.getExternalCacheDirs(this)
            val sb = StringBuffer()
            sb.append("external file path : ")
            for (externalFilesDir in externalFilesDirs) {
                sb.append(externalFilesDir.path)
            }
            sb.append("\n")
            sb.append("external cache path : ")
            for (externalCacheDir in externalCacheDirs) {
                sb.append(externalCacheDir.path)
            }
            showContent(sb.toString())
        }
    }

    private fun isExternalStorageCanUse(): Boolean {
        return if (StorageQueryUtil.isExternalStorageWritable()) {

            showContent("外部存储空间可用")
            true
        } else {
            showContent("无外部存储空间可用")
            false
        }
    }

    private fun showContent(content: String) {

        mBinding.showSpace.text = content
    }
}