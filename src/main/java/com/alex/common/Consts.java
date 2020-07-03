package com.alex.common;

import lombok.Data;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author wsh
 * @date 2020-07-02
 */
@Data
@Component
public class Consts {

    @Value("${file.upload.dir}")
    private String uploadDir;
}
