package com.hummer.rest.monitor;

import com.google.common.base.Strings;
import com.hummer.common.SysConstant;
import com.hummer.common.exceptions.AppException;
import com.hummer.common.exceptions.BusinessIdempotentException;
import com.hummer.common.exceptions.ErrorRequestException;
import com.hummer.rest.model.ResourceResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * global exception handle,response friendly message.
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/20 17:51
 **/
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @Autowired(required = false)
    private CustomerExceptionHandler handler;
    private static final Map<Class<?>, Integer> EXCEPTIONS = new HashMap<>(16);

    @Value("${hummer.response.error.code.ratio:100}")
    private Integer codeRatio;

    @Value("${hummer.response.error.SysException.code:500}")
    private Integer sysExceptionCode;

    @Value("${hummer.response.error.SysException.code:400}")
    private Integer errorRequestExceptionCode;

    static {
        EXCEPTIONS.put(HttpRequestMethodNotSupportedException.class, HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        EXCEPTIONS.put(HttpMediaTypeNotSupportedException.class, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        EXCEPTIONS.put(HttpMediaTypeNotAcceptableException.class, HttpServletResponse.SC_NOT_ACCEPTABLE);
        EXCEPTIONS.put(MissingPathVariableException.class, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        EXCEPTIONS.put(MissingServletRequestParameterException.class, HttpServletResponse.SC_BAD_REQUEST);
        EXCEPTIONS.put(ServletRequestBindingException.class, HttpServletResponse.SC_BAD_REQUEST);
        EXCEPTIONS.put(ConversionNotSupportedException.class, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        EXCEPTIONS.put(TypeMismatchException.class, HttpServletResponse.SC_BAD_REQUEST);
        EXCEPTIONS.put(HttpMessageNotReadableException.class, HttpServletResponse.SC_BAD_REQUEST);
        EXCEPTIONS.put(HttpMessageNotWritableException.class, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        EXCEPTIONS.put(MethodArgumentNotValidException.class, HttpServletResponse.SC_BAD_REQUEST);
        EXCEPTIONS.put(MissingServletRequestPartException.class, HttpServletResponse.SC_BAD_REQUEST);
        EXCEPTIONS.put(BindException.class, HttpServletResponse.SC_BAD_REQUEST);
        EXCEPTIONS.put(NoHandlerFoundException.class, HttpServletResponse.SC_NOT_FOUND);
        EXCEPTIONS.put(IllegalArgumentException.class, HttpServletResponse.SC_BAD_REQUEST);
        EXCEPTIONS.put(MethodArgumentTypeMismatchException.class, HttpServletResponse.SC_BAD_REQUEST);
        EXCEPTIONS.put(ErrorRequestException.class, HttpServletResponse.SC_BAD_REQUEST);
        EXCEPTIONS.put(SQLIntegrityConstraintViolationException.class, SysConstant.BUSINESS_IDEMPOTENT_ERROR_CODE);
        EXCEPTIONS.put(org.springframework.dao.DuplicateKeyException.class, SysConstant.BUSINESS_IDEMPOTENT_ERROR_CODE);
    }

    @ExceptionHandler(value = Throwable.class)
    @ResponseBody
    public ResourceResponse handleException(HttpServletRequest request
            , HttpServletResponse response, Throwable e) {
        //parse request body if exists.
        String bodyString = readBody(request);
        String id = MDC.get(SysConstant.REQUEST_ID);

        ResourceResponse<Object> rep = new ResourceResponse<>();
        Integer status;
        StringBuilder sb = new StringBuilder();
        if (e instanceof ConstraintViolationException) {
            String errorMessage = ((ConstraintViolationException) e).getConstraintViolations()
                    .iterator()
                    .next()
                    .getMessage();
            status = HttpServletResponse.SC_BAD_REQUEST * codeRatio;
            rep.setCode(status);
            rep.setMessage(errorMessage);
        } else if ((e instanceof ErrorRequestException) || !(e instanceof AppException)) {
            sb.append("current request failed :")
                    .append(StringUtils.LF)
                    .append("url:")
                    .append(requestUrl(request))
                    .append(StringUtils.LF)
                    .append("request body:")
                    .append(bodyString)
                    .append(StringUtils.LF)
                    .append(String.format("request id:%s > stack: %s", id, ExceptionUtils.getStackTrace(e)));
            LOGGER.error(sb.toString());
            status = EXCEPTIONS.get(e.getClass());
            rep.setCode(status == null ? sysExceptionCode * codeRatio : status * codeRatio);
            rep.setMessage(e.getMessage());


        } else if (e instanceof BusinessIdempotentException) {
            // ignore business idempotent exception
            rep.setCode(SysConstant.BUSINESS_IDEMPOTENT_ERROR_CODE);
            rep.setSubCode(SysConstant.BUSINESS_IDEMPOTENT_SUB_CODE);
            rep.setMessage(e.getMessage());
            rep.setData(((AppException) e).getReturnObj());

        } else {
            //output
            rep.setCode(((AppException) e).getCode());
            rep.setMessage(e.getMessage());
            rep.setData(((AppException) e).getReturnObj());
            sb.append("current request app exception failed :")
                    .append(StringUtils.LF)
                    .append("url:")
                    .append(requestUrl(request))
                    .append(StringUtils.LF)
                    .append("request body:")
                    .append(bodyString)
                    .append(StringUtils.LF)
                    .append(String.format("response code:%s > msg: %s", rep.getCode(), rep.getMessage()));

            LOGGER.warn(sb.toString());
        }

        //if exists customer exception handle
        if (handler != null) {
            handler.hande(GlobalExceptionContext
                    .builder()
                    .throwable(e)
                    .url(requestUrl(request))
                    .param(bodyString)
                    .build());
        }

        //set response http status
        rep.setTrackId(id);
        response.setHeader("Track_Id", id);
        response.setHeader("Service_Id", "127.0.0.1");
        return rep;
    }

    private String requestUrl(HttpServletRequest request) {
        if (!Strings.isNullOrEmpty(request.getQueryString())) {
            return String.format("%s?%s", request.getRequestURL(), request.getQueryString());
        }
        return request.getRequestURL().toString();
    }

    private String readBody(final HttpServletRequest request) {
        InputStreamReader inputStream = null;
        BufferedReader reader = null;
        try {
            HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
            if (requestWrapper.getInputStream() == null || requestWrapper.getInputStream().isFinished()) {
                return null;
            }
            inputStream = new InputStreamReader(requestWrapper.getInputStream());
            reader = new BufferedReader(inputStream);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            LOGGER.warn("global exception handle read body failed,", e);
            return "read body failed";
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //ignore
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
    }
}
