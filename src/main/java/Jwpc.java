/**
 * Created by fuxiuyin on 15-9-24.
 */
import com.github.kevinsawicki.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.io.*;

public class Jwpc
{
    private String host;
    private String imageUrl;
    private String loginUrl;
    private String imagePath;
    private String imageName;

    public Jwpc()
    {
        host = "http://210.42.121.241";
        imageUrl = "/servlet/GenImg";
        loginUrl = "/servlet/login";
        imagePath = "./image";
        imageName = "yzm.png";
        checkAndCreatePath();
    }

    public void test()
    {
        getImage();
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
                    System.out.println("穿件图片文件失败");
                    return false;
                }
            }
            catch (IOException exception)
            {
                System.out.println("创建图片文件失败");
                return false;
            }
        }
        HttpRequest request;
        try
        {
            request = HttpRequest.get(host + imageUrl);
        }
        catch (HttpRequest.HttpRequestException exception)
        {
            System.out.println("连接不上教务");
            return false;
        }
        if (request.ok())
        {
            String imageContent = request.body();
            try
            {
                FileWriter fw = new FileWriter(file);
                fw.write(imageContent);
                fw.close();
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
                return false;
            }
            return true;
        }
        else
        {
            System.out.println(String.format("获取验证码时教务系统返回%d", request.code()));
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
