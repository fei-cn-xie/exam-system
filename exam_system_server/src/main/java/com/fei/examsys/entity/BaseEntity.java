package com.fei.examsys.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "创建时间")
    private Date createTime;


    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // LEARN @JsonFormat()局部配置
    @JsonIgnore() // LEARN 添加@JsonIgnore()注解后，前端不会接受到此字段值，同时前端传入此值也会被忽略
    @Schema(description = "修改时间")
    private Date updateTime;

    @JsonIgnore()// LEARN 添加@JsonIgnore()注解后，前端不会接受到此字段值，同时前端传入此值也会被忽略
    @Schema(description = "逻辑删除")
    @TableField("is_deleted")
    private Byte isDeleted;

}