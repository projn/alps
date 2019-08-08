package com.projn.sample.alps.module.console.service.impl;

import com.projn.alps.exception.HttpException;
import com.projn.alps.exception.code.CommonErrorCode;
import com.projn.alps.service.IComponentsHttpService;
import com.projn.alps.struct.HttpRequestInfo;
import com.projn.alps.struct.HttpResponseInfo;
import com.projn.sample.alps.module.console.util.VerificationCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取登录验证码
 *
 * @author : auto
 */
@Component("GetVerificationCodeInfoServiceImpl")
public class GetVerificationCodeInfoServiceImpl implements IComponentsHttpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetVerificationCodeInfoServiceImpl.class);

    private static final int VERIFICATION_CODE_PICTURE_WIDTH = 90;

    private static final int VERIFICATION_CODE_PICTURE_HEIGHT = 20;

    private int verificationCodeCount = 4;

    private static final int VERIFICATION_CODE_FONT_HEIGHT = 18;

    public int getVerificationCodeCount() {
        return verificationCodeCount;
    }

    public void setVerificationCodeCount(int verificationCodeCount) {
        verificationCodeCount = verificationCodeCount;
    }

    @Override
    public HttpResponseInfo execute(HttpRequestInfo httpRequestInfo) throws HttpException {
        HttpResponseInfo httpResponseInfo = new HttpResponseInfo();

        String verificationCode = null;
        byte[] verificationCodeImg = null;
        try {
            Map<String, Object> verificationCodeMap = VerificationCodeUtils.generateCodeAndPicture(VERIFICATION_CODE_PICTURE_WIDTH,
                    VERIFICATION_CODE_PICTURE_HEIGHT, verificationCodeCount, VERIFICATION_CODE_FONT_HEIGHT);
            if (verificationCodeMap != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                ImageIO.write((RenderedImage) verificationCodeMap.get("codePic"), "jpeg", out);
                verificationCodeImg = out.toByteArray();
                out.close();

                verificationCode = ((StringBuffer) verificationCodeMap.get("code")).toString();
            }

        } catch (Exception e) {
            LOGGER.error("Create verification code error,error info(" + e.getMessage() + ")!");
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    CommonErrorCode.RESULT_SYSTEM_INTER_ERROR);
        }

        Map<String, String> headerInfoMap = new HashMap<>();
        headerInfoMap.put("Content-Type", "application/x-jpg");
        httpResponseInfo.setHeaderInfoMap(headerInfoMap);
        httpResponseInfo.setMsg(verificationCodeImg);
        return httpResponseInfo;
    }
}