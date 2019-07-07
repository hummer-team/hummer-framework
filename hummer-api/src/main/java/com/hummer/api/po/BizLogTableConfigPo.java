package com.hummer.api.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/18 16:00
 **/
@Data
public class BizLogTableConfigPo {
    private int id;
    private String tableName;
    private String splitType;
    private int splitTypeUnit;
    private String bizOwnerGroup;
    private String bizOwnerGroupName;
    private boolean isValid;
    private Date createTime;
    private String columnNames;
    private int insertBatch;
}
