package com.alex.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author wsh
 * @date 2020-06-15
 */
@Data
public class BaseEntity {
    private Long id;
    private Date created;
    private Date modified;
}
