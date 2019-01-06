package com.projn.alps.alpsmicroservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.projn.alps.exception.HttpException;
import com.projn.alps.alpsmicroservice.property.RunTimeProperties;
import com.projn.alps.tool.HttpControllerTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.projn.alps.define.CommonDefine.*;

/**
 * http controller
 *
 * @author : sunyuecheng
 */
@Controller
@EnableConfigurationProperties(RunTimeProperties.class)
public class HttpController {
    @Autowired
    private HttpControllerTools httpControllerTools;

    /**
     * deal controller
     *
     * @param request      :
     * @param response     :
     * @param serviceName  :
     * @param requestJson  :
     * @return org.springframework.web.context.request.async.DeferredResult<java.lang.Object> :
     * @throws HttpException :
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/api/{service_name"}, method = {RequestMethod.POST})
    public @ResponseBody
    DeferredResult<Object>
    dealController(HttpServletRequest request, HttpServletResponse response,
                   @PathVariable(name = "service_name") String serviceName,
                   @RequestBody(required = false) JSONObject requestJson)
            throws HttpException {

        Map<String, String> pathParamMap = new HashMap<>(COLLECTION_INIT_SIZE);
        pathParamMap.put("serviceName", serviceName);

        String url= "/api/{service_name";

        return httpControllerTools.deal(url, request,response,pathParamMap, (Object)requestJson);
    }

}