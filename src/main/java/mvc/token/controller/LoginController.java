package mvc.token.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static mvc.token.utils.TokenUtils.tokenUtils;

@Controller
public class LoginController {

//    @Autowired
//    private TokenUtils tokenUtils;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String, Object> login(HttpServletResponse response, @RequestBody Map<String, Object> body){
        String username = (String) body.get("username");
        String password = (String) body.get("password");

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", body.get("username"));

        Map<String, Object> result = new HashMap<>();
        if ("admin".equals(username) && "admin".equals(password)) {
            result.put("Authorization", tokenUtils().generateToken(claims));
        } else {
            result.put("message", "账号或密码错误！");
        }

        return result;
    }

    @RequestMapping(value = "/testGet", method = RequestMethod.GET)
    public Map<String, Object> testGet() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "test get");

        return result;
    }
}
