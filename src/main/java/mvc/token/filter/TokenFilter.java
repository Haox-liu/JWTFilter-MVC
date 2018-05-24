package mvc.token.filter;


import com.alibaba.fastjson.JSONReader;
import mvc.token.utils.TokenUtils;
import org.springframework.util.ResourceUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static mvc.token.utils.TokenUtils.tokenUtils;

public class TokenFilter implements Filter {



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("============ do init method ============");
        initFilterUris();
        tokenUtils();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String authToken = request.getHeader("Authorization");


//        System.out.println("uri: " + request.getRequestURI());
//        System.out.println("url: " + request.getRequestURL());
//        System.out.println("s_path: " + request.getServletPath());
//        System.out.println("c_path: " + request.getContextPath());



        if (TokenUtils.filterUris.contains(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }


        if (authToken != null && tokenUtils().validateToken(authToken)) {
            System.out.println("===== chain filter====");
            chain.doFilter(request, response);
        } else {
            System.out.println("===== 403 ====");
            response.setStatus(403);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"code\":\"403\",\"data\":\"\",\"message\":\"账号未认证！\"}");
        }

    }

    @Override
    public void destroy() {

    }

    public void initFilterUris() {
        JSONReader reader = null;
        try {
            File uriJsonFile = ResourceUtils.getFile("classpath:filter-uri.json");
            reader = new JSONReader(new FileReader(uriJsonFile));
            reader.startObject();
            while (reader.hasNext()) {
                String key = reader.readString();
                if ("filterUri".equals(key)) {
                    reader.startArray();
                    while (reader.hasNext()) {
                        reader.startObject();
                        while (reader.hasNext()) {
                            String uri = reader.readString();
                            String comment = reader.readObject().toString();
                            System.out.println(uri + ": " + comment);
                            TokenUtils.filterUris.add(uri);
                        }
                        reader.endObject();
                    }
                    reader.endArray();
                }
            }
            reader.endObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
