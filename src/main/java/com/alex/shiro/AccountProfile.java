package com.alex.shiro;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wsh
 * @date 2020-06-27
 */
@Data
public class AccountProfile implements Serializable {

    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String sign;
    private String gender;
    private Date created;

    public String getSex(){
        return "0".equals(gender) ? "女" : "男";
    }
}

