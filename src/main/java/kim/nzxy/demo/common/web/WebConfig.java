package kim.nzxy.demo.common.web;


import java.util.ArrayList;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<RequestBodyReaderFilter> fequestBodyReaderFilter() {
        FilterRegistrationBean<RequestBodyReaderFilter> registrationBean = new FilterRegistrationBean<>();
        RequestBodyReaderFilter filter = new RequestBodyReaderFilter();
        registrationBean.setFilter(filter);
        ArrayList<String> urls = new ArrayList<>();
        urls.add("/*");//配置过滤规则
        registrationBean.setUrlPatterns(urls);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

}