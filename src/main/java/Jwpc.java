/**
 * Created by fuxiuyin on 15-9-24.
 */
import com.github.kevinsawicki.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Jwpc
{
    /*
        在执行完login()之后session成员存放的是带有教务验证的cookie,此后可以使用
        session.get()
        session.post()
        方法请求教务的各种信息
     */
    private String host;
    private String imageUrl;
    private String loginUrl;
    private String imagePath;
    private String imageName;
    private String userId;
    private String userPwd;
    private Session session;  // 用来管理cookie的小类
    private String scoreUrl;
    private boolean isLogin;  // 用来判断是否执行过login的

    public Jwpc()
    {
        isLogin = false;
        session = new Session();
        host = "http://210.42.121.241";
        imageUrl = "/servlet/GenImg";
        loginUrl = "/servlet/Login";
        imagePath = "./image";
        imageName = "yzm.jpeg";
        scoreUrl = "/servlet/Svlt_QueryStuScore?year=0&term=&learnType=&scoreFlag=0";
        checkAndCreatePath();
    }

    private boolean isDigit(String str)
    {
        for (int i = 0; i < str.length(); ++i)
        {
            if(!Character.isDigit(str.charAt(i)))
            {
                return false;
            }
        }
        return true;
    }

    public boolean login()
    {
        Scanner in = new Scanner(new BufferedInputStream(System.in));
//        System.out.println("请输入学号:");
//        userId = in.next();
        userId = "2013302580159";
        userPwd = "690911sKy";
        while(userId.length() == 13 && !isDigit(userId))
        {
            System.out.println("输入的学号格式不正确,请重新输入入学号");
            userId = in.next();
        }
//        System.out.println("请输入密码:");
//        userPwd = in.next();
        while(userPwd.isEmpty())
        {
            System.out.println("输入的密码为空,请重新输入密码");
            userPwd = in.next();
        }
        if (getImage())
        {
            System.out.println(String.format("验证码图片存放在:\n%s%s/%s\n当中,请查看后输入图片上的字符:",
                    System.getProperty("user.dir"), imagePath, imageName));
            String yzm = in.next();
            Map<String, String> data = new HashMap<String, String>();
            data.put("id", userId);
            data.put("pwd", userPwd);
            data.put("xdvfb", yzm);
            Response response = session.post(String.format("%s%s", host, loginUrl), data);
            if (response.location.contains("stu"))
            {
                isLogin = true;
                System.out.println("登陆成功");
                return true;
            }
            else
            {
                System.out.println("登录失败");
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public boolean getClasses()
    {
        if (!isLogin)
        {
            if(!login())
            {
                return false;
            }
        }
        Response response = session.get(String.format("%s%s", host, scoreUrl));
        // 判断成功不成功的方法是靠HttpRequest自动处理302这个特性工作的,如果登录失败他会跳转的login
        // url里就找不到Svlt
        if (!response.location.contains("Svlt"))
        {
            System.out.println("没有登陆");
            isLogin = false;
            return false;
        }
        else
        {
            System.out.println(response.request.body());
            return true;
        }
    }

    private boolean getImage()
    {

        String imgFile = imagePath + "/" + imageName;
        File file = new File(imgFile);
        if (!file.exists())
        {
            try
            {
                if(!file.createNewFile())
                {
                    System.out.println("创建图片文件失败");
                    return false;
                }
            }
            catch (IOException exception)
            {
                System.out.println("创建图片文件失败");
                return false;
            }
        }

        Response response;
        try
        {
            response = session.get(host + imageUrl);
            if (response.requestCode == 200)
            {
                response.request.receive(file);
                return true;
            }
            else
            {
                System.out.println(String.format("获取验证码时教务系统返回%d", response.requestCode));
                return false;
            }
        }
        catch (HttpRequest.HttpRequestException exception)
        {
            System.out.println("连接不上教务");
            return false;
        }
    }

    private void checkAndCreatePath()
    {
        File file = new File(imagePath);
        if (!file.exists())
        {
            file.mkdir();
        }
        else if (!file.isDirectory())
        {
            file.delete();
            file.mkdir();
        }
    }
}
