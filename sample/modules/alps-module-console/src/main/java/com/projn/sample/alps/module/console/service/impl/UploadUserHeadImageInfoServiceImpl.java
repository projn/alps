package com.projn.sample.alps.module.console.service.impl;

import com.projn.alps.exception.HttpException;
import com.projn.alps.service.IComponentsHttpService;
import com.projn.alps.struct.HttpRequestInfo;
import com.projn.alps.struct.HttpResponseInfo;
import com.projn.alps.util.FileUtils;
import com.projn.sample.alps.module.console.msg.request.HttpUploadUserHeadImageInfoRequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static com.projn.alps.exception.code.CommonErrorCode.RESULT_INVAILD_PARAM_ERROR;
import static com.projn.alps.exception.code.CommonErrorCode.RESULT_SYSTEM_INTER_ERROR;

/**
 * 提交用户头像
 *
 * @author : auto
 */
@Component("UploadUserHeadImageInfoServiceImpl")
public class UploadUserHeadImageInfoServiceImpl implements IComponentsHttpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadUserHeadImageInfoServiceImpl.class);

    @Override
    public HttpResponseInfo execute(HttpRequestInfo httpRequestInfo) throws HttpException {
        HttpResponseInfo httpResponseInfo = new HttpResponseInfo();

        if (httpRequestInfo == null || httpRequestInfo.getParamObj() == null) {
            LOGGER.error("Error param.");
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_INVAILD_PARAM_ERROR);
        }

        HttpUploadUserHeadImageInfoRequestInfo httpUploadUserHeadImageInfoRequestInfo =
                (HttpUploadUserHeadImageInfoRequestInfo) httpRequestInfo.getParamObj();
        MultipartFile file = httpUploadUserHeadImageInfoRequestInfo.getFile();
        String fileName = file.getOriginalFilename();

        String filePath = "./" + fileName + System.currentTimeMillis();
        try {
            FileUtils.writeFileByByte(filePath, file.getBytes(), false);
        } catch (Exception e) {
            LOGGER.error("Save file error, file name({}), error info({}).",
                    fileName, e.getMessage());
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR.value(), RESULT_SYSTEM_INTER_ERROR);
        }

        return httpResponseInfo;
    }
}