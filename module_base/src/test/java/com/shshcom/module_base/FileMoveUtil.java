package com.shshcom.module_base;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FileMoveUtil {
    final String fromFolderPath = "C:\\Users\\Administrator\\Desktop\\UI";

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void modifyFilename() {
        String type = "drawable";// "drawable"
        traverseFolder(fromFolderPath, type);
    }

    /**
     * 修改 图片名称
     * 去掉 @2x,小写，并放到xhdpi文件夹下
     *
     * @param path
     * @param type "drawable" or "mipmap"
     */
    void traverseFolder(String path, String type) {

        File fileRoot = new File(path);
        if (fileRoot.exists()) {
            File[] files = fileRoot.listFiles();
            if (files.length == 0) {
                System.out.println("文件夹是空的!");
                return;
            } else {
                for (File fileTarget : files) {
                    if (fileTarget.isDirectory()) {
                        System.out.println("文件夹:" + fileTarget.getAbsolutePath());
                        traverseFolder(fileTarget.getAbsolutePath(), type);
                    } else {
                        System.out.println("文件:" + fileTarget.getAbsolutePath());


                        String filename = fileTarget.getName();
                        System.out.println(filename);
                        //这里可以反复使用replace替换,当然也可以使用正则表达式来替换了
                        String target = "";
                        String replacement = "";
                        String filePath = "";
                        if (filename.contains("1x") || !filename.contains("@")) {
                            target = "@1x";
                            filePath = fileTarget.getParentFile().getAbsolutePath() + "/" + type + "-mdpi";
                            File dest = new File(filePath);
                            if (!dest.exists()) {
                                dest.mkdirs();
                            }
                            filePath = filePath + "//" + filename.replace(target, replacement).toLowerCase();
                            dest = new File(filePath);

                            fileTarget.renameTo(dest);
                        }
                        if (filename.contains("1.5x")) {
                            target = "@1.5x";
                            filePath = fileTarget.getParentFile().getAbsolutePath() + "//" + type + "-hdpi";
                            File dest = new File(filePath);
                            if (!dest.exists()) {
                                dest.mkdirs();
                            }
                            filePath = filePath + "//" + filename.replace(target, replacement).toLowerCase();
                            dest = new File(filePath);

                            fileTarget.renameTo(dest);
                        }
                        if (filename.contains("2x")) {
                            target = "@2x";
                            filePath = fileTarget.getParentFile().getAbsolutePath() + "//" + type + "-xhdpi";
                            File dest = new File(filePath);
                            if (!dest.exists()) {
                                dest.mkdirs();
                            }
                            filePath = filePath + "//" + filename.replace(target, replacement).toLowerCase();
                            dest = new File(filePath);

                            fileTarget.renameTo(dest);
                        }
                        if (filename.contains("3x")) {
                            target = "@3x";
                            filePath = fileTarget.getParentFile().getAbsolutePath() + "//" + type + "-xxhdpi";
                            File dest = new File(filePath);
                            if (!dest.exists()) {
                                dest.mkdirs();
                            }
                            filePath = filePath + "//" + filename.replace(target, replacement).toLowerCase();
                            dest = new File(filePath);
                            System.out.println("文件:" + dest.getAbsolutePath());
                            fileTarget.renameTo(dest);
                        }
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
    }

    @Test
    public void moveAndRenamePicture() {
        String fromPath = "D:\\Android\\work2019\\Shihua\\app\\src\\main\\res";
        String toPath = "D:\\Android\\workKotlin\\BBDJ_ExpressMan\\app\\src\\main\\res";


        String FILE_NAME_OLD_NEW[][] = new String[][]{

                {"sh_call_mute_s", "sh_call_mute_s"},
                {"sh_call_mute", "sh_call_mute"},

        };


        for (String filename[] : FILE_NAME_OLD_NEW) {
            String oldName = filename[0];
            String newName = filename[1];
            System.out.println("oldName=============================================================" + oldName);
            System.out.println("newName=============================================================" + newName);
            moveAndRenamePictureAction(fromPath, toPath, oldName, newName);
        }


    }

    /**
     * 将图片资源从旧项目复制到新项目，并重命名
     */
    void moveAndRenamePictureAction(String fromPath, String toPath, String oldName, String newName) {
        File fromFile = new File(fromPath);
        File toFile = new File(toPath);
        if (!fromFile.exists()) {
            System.out.println("路径不存在!");
            return;
        }

        File[] files = fromFile.listFiles();
        if (files.length == 0) {
            System.out.println("文件夹是空的!");
            return;
        }

        for (File fileTarget : files) {
            if (fileTarget.isDirectory()) {
                //System.out.println("文件夹:" + fileTarget.getAbsolutePath());
                moveAndRenamePictureAction(fileTarget.getAbsolutePath(), toPath, oldName, newName);
            } else {
                //System.out.println("文件:" + fileTarget.getAbsolutePath());


                String filename = fileTarget.getName();
                //这里可以反复使用replace替换,当然也可以使用正则表达式来替换了
                String target = oldName;
                String filePath = "";

                String[] filenames = filename.split("\\.");
                filename = filenames[0];
                String replacement = newName + "." + filenames[1];

                if (filename.equals(target)) {
                    System.out.println("文件找到-------->" + fileTarget.getAbsolutePath());
                    String fileDir = fileTarget.getParentFile().getName();
                    filePath = toFile.getAbsolutePath() + "//" + fileDir;
                    File dest = new File(filePath);
                    if (!dest.exists()) {
                        dest.mkdirs();
                    }
                    filePath = filePath + "//" + filename.replace(target, replacement).toLowerCase();
                    dest = new File(filePath);

                    //fileTarget.renameTo(dest);

                    try {
                        Files.copy(fileTarget.toPath(), dest.toPath());
                    } catch (IOException e) {
                        try {
                            Files.copy(fileTarget.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                }
            }
        }


    }


}