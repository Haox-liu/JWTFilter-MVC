package mvc.token.filter;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import mvc.token.utils.MapSortUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SignatureFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @SuppressWarnings("unchecked")
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        SignatureHttpServletRequestWrapper signatureRequest = new SignatureHttpServletRequestWrapper(request);

        System.out.println("requestBody: " + signatureRequest.getRequestBody());
        Map<String, Object> data = new Gson().fromJson(signatureRequest.getRequestBody(), Map.class);
        String signature = (String) data.get("signature");
        data.remove("signature");

        if (MapSortUtils.sortLetter(data).equals(signature)) {
            chain.doFilter(signatureRequest, response);
        } else {
            response.setStatus(400);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"code\":\"400\",\"data\":\"\",\"message\":\"数据校验异常！\"}");
        }

    }

    @Override
    public void destroy() {

    }


//    public static void main(String[] args) {
//        StringBuilder str = new StringBuilder();
//        BufferedReader reader = request.getReader();
//        try {
//            while (reader.readLine() != null) {
//                str.append(reader.readLine());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            reader.close();
//        }
//        System.out.println("params: " + str.toString());
//    }
}
