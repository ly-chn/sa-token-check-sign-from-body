package kim.nzxy.demo.controller;

import cn.dev33.satoken.sign.annotation.SaCheckSign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("demo")
public class DemoController {
    @PostMapping("json-body")
    @SaCheckSign
    public String jsonBody(@RequestBody HashMap<String, Object> body) {
        log.info("body:{}", body);
        return "ok";
    }

    @PostMapping("json-body-some-params")
    @SaCheckSign(verifyParams = {"name", "age"})
    public String jsonBodySomeParams(@RequestBody HashMap<String, Object> body) {
        log.info("aaa body:{}", body);
        return "ok";
    }

    @PostMapping("form-body")
    @SaCheckSign
    public String formBody(String name, String age) {
        log.info("name:{}, age:{}", name, age);
        return "ok";
    }

    @GetMapping("create-demo")
    public String demo() {
        String name = "nzxy";
        int age = 18;
        // 生成签名
        Map<String, ? extends Serializable> paramsMap = Map.of(
                "name", name,
                "age", age
        );
        String sign = cn.dev33.satoken.sign.SaSignManager.getSaSignTemplate().createSign(paramsMap);
        var timeMillis = System.currentTimeMillis();
        var nonce = UUID.randomUUID().toString().replace("-", "");
        return String.format("""
                        ### json 请求示例
                        POST http://localhost:8080/demo/json-body
                        timestamp:%s
                        nonce:%s
                        sign:%s
                        Content-Type: application/json
                        
                        {
                          "name": "%s",
                          "age": %s
                        }
                        
                        ### json 请求示例（部分参数参与签名验证）
                        POST http://localhost:8080/demo/json-body-some-params
                        timestamp:%s
                        nonce:%s
                        sign:%s
                        Content-Type: application/json
                        
                        {
                          "name": "%s",
                          "age": %s,
                          "otherParam": "xxx"
                        }
                        
                        ### form 请求示例
                        POST http://localhost:8080/demo/form-body
                        timestamp:%s
                        nonce:%s
                        sign:%s
                        Content-Type: application/x-www-form-urlencoded
                        
                        name=%s&age=%s
                        
                        ### query 请求示例
                        POST http://localhost:8080/demo/form-body?name=%s&age=%s
                        timestamp:%s
                        nonce:%s
                        sign:%s
                        Content-Type: application/x-www-form-urlencoded
                        """, timeMillis, nonce, sign, name, age,
                timeMillis, nonce, sign, name, age,
                timeMillis, nonce, sign, name, age,
                name, age, timeMillis, nonce, sign);
    }
}
