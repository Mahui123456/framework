//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.krb.guaranty.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krb.guaranty.common.extention.mybatisplus.page.IFCPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

public class RequestUtil {
    private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);

    public RequestUtil() {
    }

    public static String getContextPath(HttpServletRequest request, boolean endStr) {
        String contextPath = request.getContextPath();
        contextPath = contextPath.equals("/") ? "" : contextPath;
        if (endStr) {
            contextPath = contextPath + "/";
        }

        return contextPath;
    }

    public static String getPath4Method(HttpServletRequest request) {
        String contextPath = getContextPath(request, false);
        String uri = request.getRequestURI();
        if (StringUtils.isNotEmpty(contextPath)) {
            uri = uri.replace(contextPath, "");
        }

        String method = getString(request, "method", "index");
        uri = uri + "?method=" + method;
        return uri;
    }

    public static String getPath4Params(HttpServletRequest request) {
        String contextPath = getContextPath(request, false);
        String uri = request.getRequestURI();
        if (StringUtils.isNotEmpty(contextPath)) {
            uri = uri.replace(contextPath + "/", "");
            uri = uri.replace(contextPath, "");
        } else if (uri.substring(0, 1).equals("/")) {
            uri = uri.substring(1, uri.length());
        }

        String params = request.getQueryString();
        if (StringUtils.isEmpty(params)) {
            uri = uri + "?method=index";
        } else {
            String method = getString(request, "method", null);
            if (method == null) {
                uri = uri + "?method=index&" + request.getQueryString();
            } else {
                uri = uri + "?" + request.getQueryString();
            }
        }

        return uri;
    }

    public static String encodeUrl(String msg) {
        String ret = "";

        try {
            ret = URLEncoder.encode(msg,"UTF-8");
        } catch (UnsupportedEncodingException var3) {
            logger.error("error...",var3);
        }

        return ret;
    }

    public static String decodeUrl(String msg) {
        String ret = "";

        try {
            ret = URLDecoder.decode(msg,"UTF-8");
        } catch (UnsupportedEncodingException var3) {
            logger.error("error...",var3);
        }

        return ret;
    }

    public static String getRequestContent(HttpServletRequest request) {
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer("");
        String content = "";

        try {
            br = request.getReader();

            for(String line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line + "\n");
            }

            content = sb.toString();
        } catch (IOException var13) {
            ;
        } finally {
            try {
                br.close();
            } catch (IOException var12) {
                logger.error("error...",var12);
            }

        }

        return content;
    }


    public static String appendParam(String url, String param) {
        String ret;
        if (url.indexOf("?") >= 0) {
            ret = url + "&" + param;
        } else {
            ret = url + "?" + param;
        }

        return ret;
    }

    public static String appendParams(String url, Map<String, String> params) {
        String key;
        if (params != null) {
            for(Iterator var2 = params.keySet().iterator(); var2.hasNext(); url = appendParam(key, (String)params.get(key))) {
                key = (String)var2.next();
            }
        }

        return url;
    }

    public static String removeParams(String url, String... params) {
        String reg = null;

        for(int i = 0; i < params.length; ++i) {
            reg = "(?<=[\\?&])" + params[i] + "=[^&]*&?";
            url = url.replaceAll(reg, "");
        }

        url = url.replaceAll("&+$", "");
        return url;
    }

    public static String updateParam(String url, String key, String value) {
        if (StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(key)) {
            url = url.replaceAll("(" + key + "=[^&]*)", key + "=" + value);
        }

        return url;
    }

    public static String putParam(String url, String key, String value) {
        return url.contains(key + "=") ? updateParam(url, key, value) : appendParam(url, key + "=" + value);
    }

    public static String getCookieValue(HttpServletRequest request, String cookieKey) {
        String cookieValue = null;
        Cookie cookie = getCookie(request, cookieKey);
        if (cookie != null) {
            cookieValue = cookie.getValue();
        }

        return cookieValue;
    }

    public static Cookie getCookie(HttpServletRequest request, String cookieKey) {
        Cookie cookieValue = null;
        Cookie[] cookies = request.getCookies();
        if (null == cookies) {
            return null;
        } else {
            for(int i = 0; i < cookies.length; ++i) {
                Cookie cookie = cookies[i];

                try {
                    String key = URLDecoder.decode(cookie.getName(), "UTF8");
                    if (cookieKey.equals(key)) {
                        cookieValue = cookie;
                        break;
                    }
                } catch (UnsupportedEncodingException var7) {
                    logger.warn("Unsupported Encoding Exception", var7);
                }
            }

            return cookieValue;
        }
    }

    public static void setCookieValue(HttpServletResponse response, String cookieKey, String value, int expire) {
        Cookie cookie = new Cookie(cookieKey, value);
        cookie.setPath("/");
        if (expire > 0) {
            cookie.setMaxAge(expire);
        }

        response.addCookie(cookie);
    }

    public static void removeCookie(HttpServletResponse response, String cookieKey) {
        setCookieValue(response, cookieKey, "", 0);
    }

    public static String getString(HttpServletRequest request, String name, Object defaultValue, boolean emptyThrowException) {
        String str = null;

        String param = request.getParameter(name);
        if (StringUtils.isNotBlank(param)) {
            str = param;
        } else if (emptyThrowException) {
            String errorMsg = request.getRequestURL() + "中的" + name + "参数为空NULL";
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }else if(defaultValue != null){
            str = String.valueOf(defaultValue);
        }

        return str;
    }

    public static boolean getBoolean(HttpServletRequest request, String name, boolean defaultValue, boolean emptyThrowException) {
        boolean ret = false;
        String str = getString(request, name, String.valueOf(defaultValue), emptyThrowException);

        try {
            ret = Boolean.parseBoolean(str);
        } catch (NumberFormatException var) {
            String errorMsg = request.getRequestURL() + "中的" + name + "参数值类型不合法";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        return ret;
    }

    public static Integer getInt(HttpServletRequest request, String name, Integer defaultValue, boolean emptyThrowException) {
        Integer i = null;
        String str = getString(request, name, defaultValue, emptyThrowException);

        if(str != null){
            try {
                i = Integer.parseInt(str);
            } catch (NumberFormatException var) {
                String errorMsg = request.getRequestURL() + "中的" + name + "参数值类型不合法";
                logger.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }
        }

        return i;
    }

    public static Long getLong(HttpServletRequest request, String name, Long defaultValue, boolean emptyThrowException) {
        Long l = null;
        String str = getString(request, name, defaultValue, emptyThrowException);

        if(l != null){
            try {
                l = Long.parseLong(str);
            } catch (NumberFormatException var) {
                String errorMsg = request.getRequestURL() + "中的" + name + "参数值类型不合法";
                logger.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }
        }

        return l;
    }

    public static Double getDouble(HttpServletRequest request, String name, double defaultValue, boolean emptyThrowException) {
        Double d = null;
        String str = getString(request, name, defaultValue, emptyThrowException);

        if(str != null){
            try {
                d = Double.parseDouble(str);
            } catch (NumberFormatException var) {
                String errorMsg = request.getRequestURL() + "中的" + name + "参数值类型不合法";
                logger.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }
        }

        return d;
    }
    
    public static BigDecimal getDecimal(HttpServletRequest request, String name, boolean emptyThrowException) {
    	BigDecimal bd = null;
        String str = getString(request, name, emptyThrowException);
        
        if(str != null){
        	try {
				bd = new BigDecimal(str);
			} catch (Exception e) {
				String errorMsg = request.getRequestURL() + "中的" + name + "参数值类型不合法";
                logger.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
			}
        }

        return bd;
    }

    public static String[] getArray(HttpServletRequest request, String name, boolean emptyThrowException) {
        String[] str = request.getParameterValues(name);
        if (str == null && emptyThrowException) {
            String errorMsg = request.getRequestURL() + "中的" + name + "参数为空NULL";
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        } else {
            return str;
        }
    }

    public static Date getDate(HttpServletRequest request, String name, Date defaultValue) {
        String dateStr = getString(request, name, "");
        if (dateStr != null && !dateStr.equals("")) {
            Date date = null;
            try {
                date = DateUtils.parseDate(dateStr,"yyyy-MM-dd HH:mm:ss");
            } catch (ParseException e) {
                String errorMsg = request.getRequestURL() + "中的" + name + "参数值类型不合法";
                logger.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }
            return date == null ? defaultValue : date;
        } else {
            return defaultValue;
        }
    }

    public static Date getDate(HttpServletRequest request, String name) {
        return getDate(request, name, (Date)null);
    }

    public static boolean getBoolean(HttpServletRequest request, String name) {
        return getBoolean(request, name, false, false);
    }

    public static boolean getBoolean(HttpServletRequest request, String name, boolean defaultValue) {
        return getBoolean(request, name, defaultValue, false);
    }

    public static Long getLong(HttpServletRequest request, String name) {
        return getLong(request, name, null, false);
    }

    public static Long getLong(HttpServletRequest request, String name, long defaultValue) {
        return getLong(request, name, defaultValue, false);
    }

    public static String getString(HttpServletRequest request, String name) {
        return getString(request, name, null, false);
    }

    public static String getString(HttpServletRequest request, String name, String defaultValue) {
        return getString(request, name, defaultValue, false);
    }

    public static String getString(HttpServletRequest request, String name, boolean emptyThrowException) {
        return getString(request, name, "", emptyThrowException);
    }

    public static String getAjaxString(HttpServletRequest request, String name, boolean emptyThrowException) {
        String str = getString(request, name, "", emptyThrowException);

        try {
            str = new String(str.getBytes("GBK"), "utf-8");
        } catch (Exception var5) {
            request.setAttribute("", "");
        }

        return str;
    }

    public static Integer getInt(HttpServletRequest request, String name) {
        return getInt(request, name, null, false);
    }

    public static Integer getInt(HttpServletRequest request, String name, Integer defaultValue) {
        return getInt(request, name, defaultValue, false);
    }

    public static Integer getInt(HttpServletRequest request, String name, boolean emptyThrowException) {
        return getInt(request, name, null, emptyThrowException);
    }

    public static String[] getArray(HttpServletRequest request, String name) {
        String[] str = request.getParameterValues(name);
        return str;
    }

    public static String getRequestAtt(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer("");
        Enumeration e = request.getAttributeNames();

        while(e.hasMoreElements()) {
            String name = (String)e.nextElement();
            sb.append(name + ":" + request.getAttribute(name).toString() + ";");
        }

        return sb.toString();
    }

    public static String getRequestParam(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer("");
        Enumeration e = request.getParameterNames();

        while(e.hasMoreElements()) {
            String name = (String)e.nextElement();
            sb.append(name + ":" + request.getParameter(name).toString() + ";");
        }

        return sb.toString();
    }

    public static Map<String, String> getRequestParam2Map(HttpServletRequest request) {
        Map<String, String> params = new HashMap();
        Enumeration e = request.getParameterNames();

        while(e.hasMoreElements()) {
            String name = (String)e.nextElement();
            params.put(name, request.getParameter(name).toString());
        }

        return params;
    }

    public static String getSessionAtt(HttpSession s) {
        StringBuffer sb = new StringBuffer("");
        Enumeration e = s.getAttributeNames();

        while(e.hasMoreElements()) {
            String name = (String)e.nextElement();
            sb.append(name + ":" + s.getAttribute(name).toString() + ";");
        }

        return sb.toString();
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        return (request.getHeader("X-Requested-With") != null && "XMLHttpRequest"
                .equals(request.getHeader("X-Requested-With")));

    }

    public static void sendJSON(HttpServletResponse response, Object obj){
        PrintWriter output = null;
        try {
            response.setContentType("application/json;charset=UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), obj);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(output!=null)output.close();
        }
    }

    public static <T> IFCPage<T> getRequestPage(HttpServletRequest request, Class<T> cla) {
    	return new IFCPage<T>(request);
    }

    public static <T> IFCPage<T> getRequestPage(HttpServletRequest request) {
        return new IFCPage<T>(request);
    }

    public static boolean isGetMethod(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase("GET");
    }

    public static boolean isPostMethod(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase("POST");
    }

    public static void sendRedirect(HttpServletResponse response, String url) {
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
