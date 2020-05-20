package com.mt.bbdj;


import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FileTest {
    final String fromFolderPath = "F:\\my_work\\express\\拍照入库相关\\assets\\新建文件夹";

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
        String fromPath = fromFolderPath;
        String toPath = "E:\\t";


        String FILE_NAME_OLD_NEW[][] = new String[][]{

                {"icon_拍照", "ic_capture"},
                {"icon_提示", "ic_capture_tip"},
                {"修改", "ic_capture_edit"},

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




    @Test
    public void testLookupDomain() {
        String domainsh = "www.hao123.com";
        String ipsh = "220.181.107.181";
        //使用域名创建对象
        try {
            InetAddress inet1 = InetAddress.getByName(domainsh);
            System.out.println("domain: " + domainsh + ";ip: " + inet1.getHostAddress());
            //使用IP创建对象
            InetAddress inet2 = InetAddress.getByName(ipsh);
            System.out.println("ip: " + ipsh + ";domain: " + inet2.getHostAddress());
            //获得本机地址对象
            InetAddress inet3 = InetAddress.getLocalHost();
            System.out.println(inet3);
            //获得对象中存储的域名
            String host = inet3.getHostName();
            System.out.println("域名：" + host);
            //获得对象中存储的IP
            String ip = inet3.getHostAddress();
            System.out.println("IP:" + ip);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }

    }

    @Test
    public void testLookup() {
        String ip = "220.181.38.150";
        String domain = "www.baidu.com";
        String ipsh = "139.224.68.67";
        String domainsh = "www.shshcom.com";
        String[] args = {ipsh, domainsh};
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                lookup(args[i]);
            }
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter the domain names or IP addresses. Enter \"exit\" to quit.");
            try {
                while (true) {
                    String host = in.readLine();
                    if (host.equalsIgnoreCase("exit")) {
                        break;
                    }
                    lookup(host);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void lookup(String host) {
        if (isDomain(host)) {
            try {
                InetAddress address = InetAddress.getByName(host);
                System.out.print("Address: ");
                System.out.println(address.getHostAddress());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else {
            try {
                InetAddress address = InetAddress.getByName(host);
                String hostName = address.getHostName();
                if (hostName.equals(host)) {
                    System.out.println(host + "'s domain cant find");
                } else {
                    System.out.print("Domain: ");
                    System.out.println(hostName);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isDomain(String host) {
        String[] part = host.split("\\.");
        if (part.length == 4) {
            for (String pa : part) {
                if (!isNumeric(pa)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    @Test
    public void getDecimalFormat() {
        DecimalFormat decimalFormat = new DecimalFormat("\u00A5###,##0.00");
        String output = decimalFormat.format(BigDecimal.valueOf(0));
        System.out.println(output);
        System.out.println(decimalFormat.format(123456.34f));
        System.out.println(decimalFormat.format(123456.346));
        System.out.println(decimalFormat.format(1234567890.346));

        System.out.println(Integer.parseInt("1"));

    }

}