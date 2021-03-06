package com.security.jwt.ExceptionHandle;

import com.security.jwt.ImportInfoConfig;
import com.security.jwt.common_const.CommonEnum;
import com.security.jwt.exception.LogicException;
import com.security.jwt.exception.ResourceException;
import com.security.jwt.model.RestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description 异常统一处理
 * @Author cjy
 * @Date 2019/2/14
 **/
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private ImportInfoConfig importInfoConfig;

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object exceptionHandler(HttpServletRequest request, Exception e, HttpServletResponse response) {

        //系统级异常，错误码固定为-1，提示语固定为系统繁忙，请稍后再试
        RestResult result = new RestResult(false, CommonEnum.ReturnCode.SystemCode.sys_err_exception.getValue(),
                null, CommonEnum.ReturnMsg.SystemMsg.sys_err_exception.getValue());
        //如果是业务逻辑异常，返回具体的错误码与提示信息
        if (e instanceof LogicException) {
            LogicException logicException = (LogicException) e;
            result.setCode(logicException.getCode());
            result.setErrorMessage(logicException.getErrorMsg());
        } else if (e instanceof ResourceException){
            ResourceException resourceException = (ResourceException) e;
            result.setCode(resourceException.getRtnCode());
            result.setErrorMessage(resourceException.getMsg());
        } else if (e instanceof MaxUploadSizeExceededException){
            long gByte = importInfoConfig.getMaxUploadSize()/1024/1024;
            result.setCode(CommonEnum.ReturnCode.SystemCode.sys_err_uploadFile.getValue());
            result.setErrorMessage(String.format("文件上传失败:文件大小超过%dM", gByte));
        } else if (e instanceof MissingServletRequestParameterException){
            result.setCode(CommonEnum.ReturnCode.SystemCode.sys_err_paramerror.getValue());
            result.setErrorMessage("请求参数错误");
        } else if (e instanceof HttpRequestMethodNotSupportedException){
            result.setCode(CommonEnum.ReturnCode.SystemCode.sys_err_paramerror.getValue());
            result.setErrorMessage("请求方法类型错误");
        } else if (e instanceof MethodArgumentTypeMismatchException){
            result.setCode(CommonEnum.ReturnCode.SystemCode.sys_err_argumenttype.getValue());
            result.setErrorMessage("参数类型错误");
        } else if (e instanceof NoHandlerFoundException) {
            result.setCode(CommonEnum.ReturnCode.SystemCode.sys_err_resourcenotfound.getValue());
            result.setErrorMessage("访问的资源不存在");
        }
        else
            //对系统级异常进行日志记录
            logger.error("系统异常:" + e.getMessage(), e);
//        return JSON.toJSONString(result);
        return result;
    }
}