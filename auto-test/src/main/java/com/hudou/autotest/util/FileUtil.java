package com.hudou.autotest.util;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * FileUtil
 */
public class FileUtil {

    /**
     * 递归删除文件和文件夹
     *
     * @param file  要删除的根目录
     * @param isAll 是否删除根目录
     */
    public static void deleteFile(File file, boolean isAll) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                if (isAll) {
                    file.delete();
                }
                return;
            }
            for (File f : childFile) {
                deleteFile(f, true);
            }
            if (isAll) {
                file.delete();
            }
        }
    }

    /**
     * 当文件夹的文件数量为限制的数量时，删除文件夹的所有文件
     *
     * @param limitFileNum 文件限制数量
     * @param dirPath      文件夹路径
     */

    public static void fileNumAboveDelete(int limitFileNum, String dirPath) {
        File folder = new File(dirPath);

        int fileCount = 0;
        File[] files = folder.listFiles();

        if (files != null) {
            fileCount = files.length;
        }

        if (fileCount >= limitFileNum) {
            FileUtil.deleteFile(folder, false);
        }


    }

    /**
     * 获取assets目录下的所有文件夹名称
     *
     * @param context   上下文对象
     * @param directory assets目录下的子目录名称，传入""获取根目录下的文件夹
     * @return 包含所有文件夹名称的列表
     */
    public static List<String> listFoldersFromAssets(Context context, String directory) {
        List<String> folders = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try {
            String[] list = assetManager.list(directory);
            if (list != null) {
                for (String item : list) {
                    // 检查是否为文件夹
                    if (isFolder(context, directory, item)) {
                        folders.add(item);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folders;
    }


    /**
     * 检查assets目录下的指定路径是否为文件夹
     *
     * @param context   上下文对象
     * @param parentDir 父目录
     * @param fileName  文件或文件夹名称
     * @return 如果是文件夹返回true，否则返回false
     */
    private static boolean isFolder(Context context, String parentDir, String fileName) {
        AssetManager assetManager = context.getAssets();
        try {
            String path = "".equals(parentDir) ? fileName : parentDir + "/" + fileName;
            String[] list = assetManager.list(path);
            return list != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Append content to a text file
     *
     * @param fileName the path of file
     * @param content  the content of you append
     * @param append   true: append ; false: clean and append
     * @return result true: success ; false: fail
     */
    public boolean updateContent(String fileName, String content, boolean append) {
        boolean res = true;
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, append);
            if (append) {
                content = System.getProperty("line.separator") + content;
            }
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            res = false;
            ex.printStackTrace();
        }
        return res;
    }

    public static void loadAssetsFolder(Activity activity, String folderName, String loadedDirectory) throws IOException {
        String[] filesArray = activity.getAssets().list(folderName);
        if (filesArray != null) {
            for (String file : filesArray) {
                FileUtil.loadAssetsFiles(activity, file, folderName, loadedDirectory);
            }
        }
    }

    public static void loadAssetsFiles(Activity activity, String fileName, String folderName, String path) throws IOException {
        fileName = folderName + "/" + fileName;
        InputStream inputStream = activity.getAssets().open(fileName);
        File file = new File(path + folderName);
        if (!file.exists()) {
            file.mkdirs();
        }
        fileName = fileName.replace(folderName + "/", "");
        FileOutputStream fileOutputStream = new FileOutputStream(file + File.separator + fileName);

        int len = -1;
        byte[] buffer = new byte[1024];
        while ((len = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, len);
        }
        fileOutputStream.close();
        inputStream.close();
    }


}
