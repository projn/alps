package com.projn.sample.alps.module.console.controller;

import com.alibaba.fastjson.JSONObject;
import com.projn.alps.exception.HttpException;
import com.projn.alps.tool.HttpControllerTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.projn.alps.define.CommonDefine.COLLECTION_INIT_SIZE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author : auto
 */
@Controller
public class ConsoleModuleController {
    @Autowired
    private HttpControllerTools httpControllerTools;

    /**
     * service bean :
     *
     * @see com.projn.sample.alps.module.console.service.impl.GetVerificationCodeInfoServiceImpl
     * request bean :
     * @see com.projn.sample.alps.module.console.msg.request.HttpGetVerificationCodeInfoRequestInfo
     * response bean :
     * @see null
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/user/verification-code"}, method = {GET})
    public DeferredResult<Object> getVerificationCodeInfo(HttpServletRequest request, HttpServletResponse response) throws HttpException {
        Map<String, String> pathParamMap = new HashMap<>(COLLECTION_INIT_SIZE);
        String url = "/user/verification-code";
        return httpControllerTools.deal(url, request, response, pathParamMap, null);
    }

    /**
     * service bean :
     *
     * @see com.projn.sample.alps.module.console.service.impl.LoginServiceImpl
     * request bean :
     * @see com.projn.sample.alps.module.console.msg.request.HttpLoginRequestInfo
     * response bean :
     * @see com.projn.sample.alps.module.console.msg.response.HttpLoginResponseInfo
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/user/login"}, method = {POST})
    public @ResponseBody
    DeferredResult<Object> login(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) JSONObject requestJson) throws HttpException {
        Map<String, String> pathParamMap = new HashMap<>(COLLECTION_INIT_SIZE);
        String url = "/user/login";
        return httpControllerTools.deal(url, request, response, pathParamMap, (Object) requestJson);
    }

    /**
     * service bean :
     *
     * @see com.projn.sample.alps.module.console.service.impl.LogoutServiceImpl
     * request bean :
     * @see com.projn.sample.alps.module.console.msg.request.HttpLogoutRequestInfo
     * response bean :
     * @see null
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/user/logout/{user_id}"}, method = {GET})
    public @ResponseBody
    DeferredResult<Object> logout(HttpServletRequest request, HttpServletResponse response, @PathVariable(name = "user_id") String userId) throws HttpException {
        Map<String, String> pathParamMap = new HashMap<>(COLLECTION_INIT_SIZE);
        pathParamMap.put("user_id", userId);
        String url = "/user/logout/{user_id}";
        return httpControllerTools.deal(url, request, response, pathParamMap, null);
    }

    /**
     * service bean :
     *
     * @see com.projn.sample.alps.module.console.service.impl.UploadUserHeadImageInfoServiceImpl
     * request bean :
     * @see com.projn.sample.alps.module.console.msg.request.HttpUploadUserHeadImageInfoRequestInfo
     * response bean :
     * @see null
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/user/headimage/{user_id}"}, method = {POST})
    public @ResponseBody
    DeferredResult<Object> uploadUserHeadImageInfo(HttpServletRequest request, HttpServletResponse response, @RequestParam("file") MultipartFile requestFile, @PathVariable(name = "user_id") String userId) throws HttpException {
        Map<String, String> pathParamMap = new HashMap<>(COLLECTION_INIT_SIZE);
        pathParamMap.put("user_id", userId);
        String url = "/user/headimage/{user_id}";
        return httpControllerTools.deal(url, request, response, pathParamMap, (Object) requestFile);
    }
}