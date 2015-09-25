import com.github.kevinsawicki.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fuxiuyin on 15-9-24.
 */
public class Session
{
    private Map<String, String> cookie;

    public Session()
    {
        cookie = new HashMap<String, String>();
    }

    private String getCookie()
    {
        String cookies = "";
        for(Map.Entry<String, String> entry:cookie.entrySet())
        {
            if (cookies.isEmpty())
            {
                cookies = String.format("%s=%s", entry.getKey(), entry.getValue());
            }
            else
            {
                cookies = String.format("%s;%s=%s", cookies, entry.getKey(), entry.getValue());
            }
        }
        if (cookies.isEmpty())
        {
            return null;
        }
        return cookies;
    }

    private void setCookie(String set_cookie)
    {
        if (!(set_cookie == null))
        {
            String[] cookies = set_cookie.split(";");
            for (String cookie_value : cookies)
            {
                if ((cookie_value.indexOf("Path")) == -1)
                {
                    String[] keyAndValue = cookie_value.split("=");
                    cookie.put(keyAndValue[0], keyAndValue[1]);
                }
            }
        }
    }

    public Response get(String url) throws HttpRequest.HttpRequestException
    {
        String request_cookies = getCookie();
        HttpRequest request = HttpRequest.get(url).header("Cookie", request_cookies);
        Response response = new Response(request);
        String set_cookie = request.header("Set-Cookie");
        setCookie(set_cookie);
        response.requestCode = response.request.code();
        response.location = response.request.url().toString();
        return response;
    }

    public Response post(String url, Map<String, String> data) throws HttpRequest.HttpRequestException
    {
        String request_cookies = getCookie();
        HttpRequest request = HttpRequest.post(url).header("Cookie", request_cookies).form(data);
        Response response = new Response(request);
        // String set_cookie =  request.header("Set-Cookie");
        // setCookie(set_cookie);
        response.requestCode = response.request.code();
        response.location = response.request.url().toString();
        return response;
    }
}

class Response
{
    public HttpRequest request;
    public int requestCode;
    public String requestUrl;
    public String location;
    public String body;

    public Response(HttpRequest request_)
    {
        request = request_;
        requestUrl = request.url().toString();
    }
}
