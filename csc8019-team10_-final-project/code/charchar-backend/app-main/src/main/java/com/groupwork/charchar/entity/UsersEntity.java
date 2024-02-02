package com.groupwork.charchar.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Eastman
 * @email 931654949@qq.com
 * @date 2023-05-24 15:33:03
 */
@Data
@NoArgsConstructor//有参无参构造器
@AllArgsConstructor
@TableName("users")
public class UsersEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier for the user.
     */
    @TableId(type = IdType.AUTO)
    private Integer userId;
    /**
     * The username of the user.
     */
    private String username;
    /**
     * The email address of the user.
     */
    private String email;
    /**
     * The password of the user.
     */
    private String password;
    /**
     * Salt is used to encrypt the user's password
     */
    private String salt;
    /**
     * The creation time of the user.
     */
    private Date createdTime;
    /**
     * The last update time of the user.
     */
    private Date updatedTime;
    /**
     * The confirmation code for user activation.
     */
    @TableField("confirm_code")
    private String confirmCode;
    /**
     * Activation expiry time
     */
    @TableField("activation_time")
    private LocalDateTime activationTime; // 激活失效时间
    /**
     * Determining whether an account is active or not
     */
    @TableField("is_valid")
    private Byte isValid; // 是否可用
    /**
     * The verification code associated with the user.
     */
    @TableField("verification_code")
    private String verificationCode;
    /**
     * Used to determine if a user has deleted.
     */
    @TableField("is_delete")
    private Byte isDelete; // 是否可用

}
