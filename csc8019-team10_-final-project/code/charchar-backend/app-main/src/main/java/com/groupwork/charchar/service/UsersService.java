package com.groupwork.charchar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.groupwork.charchar.entity.UsersEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @author Eastman
 * @email 931654949@qq.com
 * @date 2023-05-02 15:33:03
 */
@Transactional
public interface UsersService extends IService<UsersEntity> {
    /**
     * loginAccount
     *
     * @param user
     * @return
     */
    Map<String, Object> loginAccount(UsersEntity user);

    /**
     * register
     *
     * @param user
     * @return
     */
    Map<String, Object> register(UsersEntity user);

    /**
     * updatePassword
     *
     * @param user
     * @return
     */
    Map<String, Object> updatePassword(UsersEntity user);

    /**
     * updateOneUserInformation
     *
     * @param user
     * @return
     */
    Map<String, Object> updateOneUserInformation(UsersEntity user);

    /**
     * forgetPassword
     *
     * @param user
     * @return
     */
    Map<String, Object> forgetPassword(UsersEntity user);

    /**
     * deleteUser
     *
     * @param user
     * @return
     */
    Map<String, Object> deleteUser(UsersEntity user);

    /**
     * sendMail
     *
     * @param Url
     * @param email
     */
    void sendMail(String Url, String email);

    /**
     * activationAccont
     *
     * @param confirmCode
     * @return
     */
    Map<String, Object> activationAccount(String confirmCode);

    /**
     * isEmail
     *
     * @param email
     * @return
     */
    boolean isEmail(String email);

    /**
     * updateVerificationCode
     *
     * @param users
     * @return
     */
    Map<String, Object> updateVerificationCode(UsersEntity users);

}

