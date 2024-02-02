package com.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping()
public class ConfigController {

    private final ConfigService configService;

    private final ConfigServletInner configServletInner;

    private final ConfigModificationVerifyServiceChoose configModificationVerifyServiceChoose;

    @SneakyThrows
    @PostMapping("/listener")
    public void listener (HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        String probeModify = request.getParameter(Constants.LISTENING_CONFIGS);
        if (StringUtils.isEmpty(probeModify)) {
            throw new IllegalArgumentException("invalid probeModify");
        }
        probeModify = URLDecoder.decode(probeModify, Constants.ENCODE);
        Map<String, String> clientMd5Map;
        try {
            clientMd5Map = Md5ConfigUtil.getClientMd5Map(probeModify);
        } catch (Throwable e) {
            throw new IllegalArgumentException("invalid probeModify");
        }
        configServletInner.doPollingConfig(request, response, clientMd5Map, probeModify.length());
    }
}
