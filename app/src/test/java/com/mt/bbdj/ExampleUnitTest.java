package com.mt.bbdj;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shshcom.station.storage.http.bean.BaseResult;
import com.shshcom.station.storage.http.bean.StationOrcResult;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
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
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * 183 1057 6535 123456
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    final String fromFolderPath = "C:\\Users\\Administrator\\Desktop\\UI";

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        String tailNumber = "1234";

        if(tailNumber!= null && tailNumber.length()>4){
            int length = tailNumber.length();
            tailNumber = tailNumber.substring(length-4, length);
        }

        System.out.println(tailNumber);

        System.out.println(isMatch("^[A-Za-z0-9]+$","SH12345*"));



    }

    public static boolean isMatch(final String regex, final CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }


    @Test
    public void json(){
        Gson gson = new Gson();
        String json = "{\"code\":5001,\"msg\":\"请求成功\",\"data\":{\"succeed\":1,\"fail\":7,\"succeed_lists\":[{\"pie_id\":145121,\"number\":\"77111187755872\",\"code\":\"Y700940\",\"mobile\":\"18618203265\",\"msg\":\"\",\"express_id\":100101,\"express_name\":\"中通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/1c10e35e31062840ea909e3288b60e41.jpg\"}],\"fail_lists\":[{\"pie_id\":144528,\"number\":\"7711118775587223\",\"code\":\"Y7009402\",\"mobile\":\"18618203265\",\"msg\":\"取件码重复\",\"express_id\":100101,\"express_name\":\"中通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/74391167756b4f963f23f5c07488e76c.jpg\"},{\"pie_id\":145120,\"number\":\"77111187755872243\",\"code\":\"33333\",\"mobile\":\"18618203265\",\"msg\":\"取件码重复\",\"express_id\":100101,\"express_name\":\"中通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/3fa492d5c06ebcceb648e563c680181b.jpg\"},{\"pie_id\":145233,\"number\":\"YT4295748861510\",\"code\":\"1000\",\"mobile\":\"\",\"msg\":\"手机号识别失败\",\"express_id\":100102,\"express_name\":\"圆通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/b594b926d2a3a9cd7710a7f23da444c8.jpg\"},{\"pie_id\":145234,\"number\":\"YT4295748861510\",\"code\":\"1000\",\"mobile\":\"\",\"msg\":\"手机号识别失败\",\"express_id\":100102,\"express_name\":\"圆通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/f6e7d3d2bda5b8699477196d9942e1b6.jpg\"},{\"pie_id\":145240,\"number\":\"YT4295748861510\",\"code\":\"1000\",\"mobile\":\"\",\"msg\":\"手机号识别失败\",\"express_id\":100102,\"express_name\":\"圆通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/9facaf1ae8dbe0c024e685cda8194439.jpg\"},{\"pie_id\":145241,\"number\":\"YT4295748861510\",\"code\":\"1000\",\"mobile\":\"\",\"msg\":\"手机号识别失败\",\"express_id\":100102,\"express_name\":\"圆通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/c9efee6867eb6225a20f51c92f97c870.jpg\"},{\"pie_id\":145242,\"number\":\"YT4295748861510\",\"code\":\"1000\",\"mobile\":\"\",\"msg\":\"手机号识别失败\",\"express_id\":100102,\"express_name\":\"圆通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/1ee4301d5381cac47144909ee96608e6.jpg\"}]}}";
//        ResponseResult<List<UserInfo>> responseResult1 = JSON.parseObject(str, new TypeReference<ResponseResult<List<UserInfo>>>() {});
//        BaseResult<List<String>> responseResult2 = gson.fromJson(json , new TypeToken<BaseResult<List<String>>>(){}.getType());
//        BaseResult<List<String>> responseResult2 = gson.fromJson(json , new TypeToken<BaseResult<StationOrcResult>>(){}.getType());

        BaseResult result = JSON.parseObject(json, new TypeReference<BaseResult<StationOrcResult>>(){});


        BaseResult responseResult2 = gson.fromJson(json , new TypeToken<BaseResult<StationOrcResult>>(){}.getType());


        System.out.println(1);
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
        String toPath = "E:\\bingbingdaojia\\BBDJ_ExpressStation\\app\\src\\main\\res";


        String FILE_NAME_OLD_NEW[][] = new String[][]{

                {"关闭", "ic_close_tran"},

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
            // TODO Auto-generated method stub
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
            // TODO Auto-generated method stub
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