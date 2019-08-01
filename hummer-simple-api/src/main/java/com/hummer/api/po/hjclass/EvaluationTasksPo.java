package com.hummer.api.po.hjclass;

import lombok.Data;

import java.util.Date;

@Data
public class EvaluationTasksPo {
    private Integer taskId;
    private Integer companyId;
    private Integer organizationId;
    private Integer menteeId;
    private Integer intentionProject;
    private Integer scheduleId;
    private Date beginTime;
    private Date endTime;
    private Integer roomId;
    private Integer eventId;
    private Integer teacherId;
    private Integer taskStatus;
    private Integer createUser;
    private Date createTime;
    private Integer editUser;
    private Date editTime;
    private Boolean isActive;
    private Boolean isDeleted;
    private String evaluationMsg;
}
