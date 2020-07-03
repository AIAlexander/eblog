package com.alex.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alex.common.Consts;
import com.alex.common.Result;
import com.alex.shiro.AccountProfile;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author wsh
 * @date 2020-07-02
 */
@Slf4j
@Component
public class UploadUtil {

    @Autowired
    Consts consts;

    public final static String TYPE_AVATAR = "avatar";

    public Result upload(String type, MultipartFile file) throws IOException {

        if (StrUtil.isBlank(type) || file.isEmpty()){
            return Result.fail("上传失败");
        }

        //获取文件名
        String fileName = file.getOriginalFilename();
        log.info("上传文件名为：" + fileName);
        //获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        log.info("上传的后缀名为：" + suffixName);
        //文件上传后的路径
        String filePath = consts.getUploadDir();

        if("avatar".equalsIgnoreCase(type)){
            AccountProfile profile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
            fileName = "/res/images/avatar/avatar_" + System.currentTimeMillis() + suffixName;
        }else if ("post".equalsIgnoreCase(type)){
            fileName = "/res/images/post/post_" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN) + suffixName;
        }

        try{
            File dest = new File(filePath + fileName);
            //检测是否存在目录
            if(!dest.getParentFile().exists()){
                dest.getParentFile().mkdir();
            }
            file.transferTo(dest);
            log.info("上传成功后的文件路径末：" + filePath + fileName);
            String path = filePath + fileName;
            String url = fileName;
            log.info("url ----> {}", url);

            return Result.success(url);
        } catch (IllegalStateException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return Result.success(null);
    }
}
