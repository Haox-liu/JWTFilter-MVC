package mvc.token.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Map<String, Object> testGet() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "test get");

        return result;
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public void testPost(@RequestBody Map<String, Object> body) {
        System.out.println(body);
    }

}
