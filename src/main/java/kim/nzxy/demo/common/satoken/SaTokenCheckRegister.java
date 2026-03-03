package kim.nzxy.demo.common.satoken;

import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 自定义一个注册器, 覆盖SaTokenPluginForSign注册结果
 * todo: 临时用 runner 注册, 自己写的时候需要注意注册时机
 */
@Component
@RequiredArgsConstructor
public class SaTokenCheckRegister implements CommandLineRunner {
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) {
        // 注册成自己的, 以覆盖原来的注册结果
        SaAnnotationStrategy.instance.registerAnnotationHandler(new SaCheckSignHandler(objectMapper));
    }
}
