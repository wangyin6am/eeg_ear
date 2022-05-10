package com.example.testversion;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * 文件工具类
 * Created by zst on 2018/2/1.
 */
public class FileUtil {

    private RandomAccessFile raf;
    private File file;

    /**
     * 文件保存路径
     */
    private String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "脑电";

    public FileUtil(String fileName) {
        fileName = fileName + ".txt";
        createFile(FILE_PATH, fileName);
    }

    public void write(byte[] bytes) {
        try {
            if (raf == null) {
                Log.i(TAG, "write: ~~");
                raf = new RandomAccessFile(file, "rw");
            }
            raf.seek(file.length());
            raf.write(bytes);
            raf.write("\r\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeFile() {
        try {
            if (raf != null) {
                raf.close();
                Log.i(TAG, "closeFile: ~~");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //生成文件
    private void createFile(String filePath, String fileName) {
        try {
            file = new File(filePath + "/" + fileName);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
