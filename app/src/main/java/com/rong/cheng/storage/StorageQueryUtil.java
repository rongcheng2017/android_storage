package com.rong.cheng.storage;

/**
 * @author: frc
 * @description:
 * @date: 2021/5/27 9:56 上午
 */


import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class StorageQueryUtil {
    /**
     * 单位B
     */
    static class StorageInfo {
        private long total;
        private long free;
        private long available;

        public long getTotal() {
            return total;
        }

        public void setTotal(long iTotal) {
            total = iTotal;
        }

        public long getFree() {
            return free;
        }

        public void setFree(long iFree) {
            free = iFree;
        }

        public long getAvailable() {
            return available;
        }

        public void setAvailable(long iAvailable) {
            available = iAvailable;
        }
    }

    private final static String TAG = "storage";

    public static StorageInfo queryWithStorageManager(Context context) {
        StorageInfo storageInfo = new StorageInfo();
        //5.0 查外置存储
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        float unit = 1024, unit2 = 1000;
        int version = Build.VERSION.SDK_INT;
        if (version < Build.VERSION_CODES.M) {//小于6.0
            try {
                Method getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList");
                StorageVolume[] volumeList = (StorageVolume[]) getVolumeList.invoke(storageManager);
                long totalSize = 0, availableSize = 0;
                if (volumeList != null) {
                    Method getPathFile = null;
                    for (StorageVolume volume : volumeList) {
                        if (getPathFile == null) {
                            getPathFile = volume.getClass().getDeclaredMethod("getPathFile");
                        }
                        File file = (File) getPathFile.invoke(volume);
                        totalSize += file.getTotalSpace();
                        availableSize += file.getUsableSpace();
                        storageInfo.setTotal(totalSize);
                        storageInfo.setAvailable(availableSize);
                        storageInfo.setFree(file.getFreeSpace());
                    }
                }
                Log.d(TAG, "totalSize = " + getUnit(totalSize, unit) + " ,availableSize = " + getUnit(availableSize, unit));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {

            try {
                Method getVolumes = StorageManager.class.getDeclaredMethod("getVolumes");//6.0
                List<Object> getVolumeInfo = (List<Object>) getVolumes.invoke(storageManager);
                long total = 0L, used = 0L, systemSize = 0L;
                for (Object obj : getVolumeInfo) {

                    Field getType = obj.getClass().getField("type");
                    int type = getType.getInt(obj);
                    Log.d(TAG, "type: " + type);
                    if (type == 1) {//TYPE_PRIVATE

                        long totalSize = 0L;

                        //获取内置内存总大小
                        if (version >= Build.VERSION_CODES.O) {//8.0
                            Method getFsUuid = obj.getClass().getDeclaredMethod("getFsUuid");
                            String fsUuid = (String) getFsUuid.invoke(obj);
                            totalSize = getTotalSize(context, fsUuid);//8.0 以后使用
                        } else if (version >= Build.VERSION_CODES.N_MR1) {//7.1.1
                            Method getPrimaryStorageSize = StorageManager.class.getMethod("getPrimaryStorageSize");//5.0 6.0 7.0没有
                            totalSize = (long) getPrimaryStorageSize.invoke(storageManager);
                        }

                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                        boolean readable = (boolean) isMountedReadable.invoke(obj);
                        if (readable) {
                            Method file = obj.getClass().getDeclaredMethod("getPath");
                            File f = (File) file.invoke(obj);

                            if (totalSize == 0) {
                                totalSize = f.getTotalSpace();
                            }
                            systemSize = totalSize - f.getTotalSpace();
                            used += totalSize - f.getFreeSpace();
                            total += totalSize;

                        }
                        Log.d(TAG, "type = " + type + "totalSize = " + getUnit(totalSize, unit)
                                + " ,used(with system) = " + getUnit(used, unit)
                                + " ,free = " + getUnit(totalSize - used, unit));

                    } else if (type == 0) {//TYPE_PUBLIC
                        //外置存储
                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                        boolean readable = (boolean) isMountedReadable.invoke(obj);
                        if (readable) {
                            Method file = obj.getClass().getDeclaredMethod("getPath");
                            File f = (File) file.invoke(obj);
                            used += f.getTotalSpace() - f.getFreeSpace();
                            total += f.getTotalSpace();

                        }
                    } else if (type == 2) {//TYPE_EMULATED

                    }
                }
                storageInfo.setTotal(total);
                storageInfo.setAvailable(used);
                storageInfo.setFree(total - used);
                Log.d(TAG, "总内存 total = " + getUnit(total, unit) + "\n已用 used(with system) = " + getUnit(used, unit)
                        + "可用 available = " + getUnit(total - used, unit) + "系统大小：" + getUnit(systemSize, unit));

                Log.d(TAG, "总内存 total = " + getUnit(total, unit2) + "\n已用 used(with system) = " + getUnit(used, 1000)
                        + "可用 available = " + getUnit(total - used, unit2) + "系统大小：" + getUnit(systemSize, unit2));

            } catch (SecurityException e) {
                Log.e(TAG, "缺少权限：permission.PACKAGE_USAGE_STATS");
            } catch (Exception e) {
                e.printStackTrace();
                return queryWithStatFs();
            }
        }
        return storageInfo;
    }

    public static StorageInfo queryWithStatFs() {
        StorageInfo storageInfo = new StorageInfo();
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());

        //存储块
        long blockCount = statFs.getBlockCount();
        //块大小
        long blockSize = statFs.getBlockSize();
        //可用块数量
        long availableCount = statFs.getAvailableBlocks();
        //剩余块数量，注：这个包含保留块（including reserved blocks）即应用无法使用的空间
        long freeBlocks = statFs.getFreeBlocks();

        //level 18
//        long totalSize = statFs.getTotalBytes();
//        long availableSize = statFs.getAvailableBytes();

        storageInfo.setTotal(blockSize * blockCount);
        storageInfo.setAvailable(blockSize * availableCount);
        storageInfo.setFree(blockSize * freeBlocks);
        Log.d(TAG, "=========");
        Log.d(TAG, "total = " + getUnit(blockSize * blockCount, 1024));
        Log.d(TAG, "available = " + getUnit(blockSize * availableCount, 1024));
        Log.d(TAG, "free = " + getUnit(blockSize * freeBlocks, 1024));
        return storageInfo;
    }

    private static String[] units = {"B", "KB", "MB", "GB", "TB"};

    /**
     * 进制转换
     */
    public static String getUnit(float size, float base) {
        int index = 0;
        while (size > base && index < 4) {
            size = size / base;
            index++;
        }
        return String.format(Locale.getDefault(), " %.2f %s ", size, units[index]);
    }

    /**
     * API 26 android O
     * 获取总共容量大小，包括系统大小
     */
    @RequiresApi(Build.VERSION_CODES.O)
    public static long getTotalSize(Context context, String fsUuid) {
        try {
            UUID id;
            if (fsUuid == null) {
                id = StorageManager.UUID_DEFAULT;
            } else {
                id = UUID.fromString(fsUuid);
            }
            StorageStatsManager stats = context.getSystemService(StorageStatsManager.class);
            return stats.getTotalBytes(id);
        } catch (NoSuchFieldError | NoClassDefFoundError | NullPointerException | IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Checks if a volume containing external storage is available
    // for read and write.
    public static boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // Checks if a volume containing external storage is available to at least read.
    public static boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    public static File getAppSpecificAlbumStorageDir(Context context, String albumName) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), albumName);
        if (file != null || !file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }
}