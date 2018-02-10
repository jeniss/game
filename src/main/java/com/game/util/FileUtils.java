package com.game.util;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by jeniss on 18/2/10.
 */
public class FileUtils {
    private final static Logger logger = Logger.getLogger(FileUtils.class);

    /**
     * 移动文件到指定目录
     * @param oldPath String  如：c:/fqf.txt
     * @param newPath String  如：d:/fqf.txt
     */
    public static void moveFile(String oldPath, String newPath) {
        copyFile(oldPath, newPath);
        delFile(oldPath);

    }

    /**
     * 复制单个文件
     * @param oldPath String  原文件路径  如：c:/fqf.txt
     * @param newPath String  复制后路径  如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {  //文件存在时
                InputStream inStream = new FileInputStream(oldPath);  //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            logger.error(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
        }
    }

    /**
     * 删除文件
     * @param filePathAndName String  文件路径及名称  如c:/fqf.txt
     */
    public static void delFile(String filePathAndName) {
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            java.io.File myDelFile = new java.io.File(filePath);
            myDelFile.delete();
        } catch (Exception e) {
            logger.error(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
        }
    }


}
