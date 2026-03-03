/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kim.nzxy.demo.common.satoken;

import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.sign.annotation.SaCheckSign;
import cn.dev33.satoken.sign.exception.SaSignException;
import cn.dev33.satoken.sign.template.SaSignMany;
import cn.dev33.satoken.sign.template.SaSignTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kim.nzxy.demo.common.web.RequestWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 抄的 sa-token 的, 具体区别查看同名源码对比
 */
@Slf4j
@AllArgsConstructor
public class SaCheckSignHandler implements SaAnnotationHandlerInterface<SaCheckSign> {
    private final ObjectMapper objectMapper;

    @Override
    public Class<SaCheckSign> getHandlerAnnotationClass() {
        return SaCheckSign.class;
    }

    @Override
    public void checkMethod(SaCheckSign at, AnnotatedElement element) {
        _checkMethod(at.appid(), at.verifyParams());
    }

    public void _checkMethod(String appid, String[] verifyParams) {
        SaRequest req = SaHolder.getRequest();
        // 如果 appid 为 #{} 格式，则从请求参数中获取
        if(appid.startsWith("#{") && appid.endsWith("}")) {
            String reqParamName = appid.substring(2, appid.length() - 1);
            appid = req.getParam(reqParamName);
        }
        // 上方维持原样, 下方为调整信息
        // SaSignMany.getSignTemplate(appid).checkRequest(req, verifyParams); 这是原来的, 下面是调整后的, 主要是为了兼容 json 请求体的验签

        // 为参数添加 timestamp、nonce、sign 三个参数, 约定从 header 读取
        String timestampValue = req.getHeader(SaSignTemplate.timestamp);
        String nonceValue = req.getHeader(SaSignTemplate.nonce);
        String signValue = req.getHeader(SaSignTemplate.sign);
        SaSignException.notEmpty(timestampValue, "缺少 timestamp 字段");
        SaSignException.notEmpty(nonceValue, "缺少 nonce 字段");
        SaSignException.notEmpty(signValue, "缺少 sign 字段");

        SaSignMany.getSignTemplate(appid).checkTimestamp(Long.parseLong(timestampValue));
        SaSignMany.getSignTemplate(appid).checkNonce(nonceValue);
        SaSignMany.getSignTemplate(appid).checkSign(getFullSignParam(req, verifyParams), signValue);
    }

    public Map<String, String> getFullSignParam(SaRequest req, String[] verifyParams) {
        var signParam = getSignParam(req);
        var res = new HashMap<>(signParam);
        // 如果 verifyParams 不为空, 则只保留 verifyParams 中的参数
        if (Objects.nonNull(verifyParams) && verifyParams.length > 0) {
            res.entrySet().removeIf(entry -> {
                for (String verifyParam : verifyParams) {
                    if (verifyParam.equals(entry.getKey())) {
                        return false;
                    }
                }
                return true;
            });
        }
        return res;
    }
    public Map<String, String> getSignParam(SaRequest req) {
        var request = (RequestWrapper) req.getSource();
        var contentType = request.getContentType();
        // json body, 取 body 参数
        if (Objects.isNull(contentType) || !contentType.contains("application/json")) {
            return req.getParamMap();
        }
        // 其他类型, 取 form 参数
        try {
            return objectMapper.readValue(request.getInputStream(), new TypeReference<>() {
            });
        } catch (IOException e) {
            // todo: 这里异常需要手动处理
            throw new RuntimeException(e);
        }
    }



}