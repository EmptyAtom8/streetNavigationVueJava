package com.groupwork.charchar.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.groupwork.charchar.dao.UsersDao;
import com.groupwork.charchar.entity.UsersEntity;
import com.groupwork.charchar.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author Eastman
 * @email 931654949@qq.com
 * @date 2023-05-02 15:33:03
 */

/**
 * The implementation of the UsersService interface.
 * Uses UsersDao for data access.
 */
@Service("usersService")
public class UsersServiceImpl extends ServiceImpl<UsersDao, UsersEntity> implements UsersService {
    @Autowired
    private UsersDao usersDao;

    /**
     * Attempts to log in the user and returns a Map with a response code and message.
     *
     * @param user The user to log in.
     * @return A Map containing the response code and message.
     */
    @Override
    public Map<String, Object> loginAccount(UsersEntity user) {
        //Create map record to output user entered account password registered or unregistered, incorrect password, etc.
        Map<String, Object> feedback = new ConcurrentHashMap<>();

        //Determine if the input is an email address
        if (!isEmail(user.getEmail())) {
            feedback.put("code", 400);
            feedback.put("message", "Please enter the correct email address");
            return feedback;
        }

        //Determine if a user exists in the database
        List<UsersEntity> usersEntityList = usersDao.selectEmail(user.getEmail());

        if (usersEntityList.size() > 1) {//Multiple accounts with the same name exist for users, determine account anomalies
            feedback.put("code", 400);
            feedback.put("message", "Account anomalies");
            return feedback;
        }
        if (usersEntityList.isEmpty()) {//In addition to preventing tampering with external inputs it also prevents tampering in the database（除了防止外部输入的篡改还能防止数据库中的篡改）
            feedback.put("code", 400);
            feedback.put("message", "The account does not exist");
            return feedback;
        }

        UsersEntity usersEntity2 = usersEntityList.get(0);//Query for a user and do a password comparison (an email has only one user so it's get (0))
        String md5Pwd = SecureUtil.md5(user.getPassword() + usersEntity2.getSalt());//Snowflake number encryption by adding salt to the password entered by the user(查询到的salt和密码编写的雪花数应该与database对应)
        //whether the account be actived
        if (usersEntity2.getIsValid() != 1) {
            feedback.put("code", 400);
            feedback.put("message", "The account is not active");
            return feedback;
        }

        if (!usersEntity2.getPassword().equals(md5Pwd)) {//Determine if the password entered is correct
            feedback.put("code", 400);
            feedback.put("message", "The password entered is incorrect");
            return feedback;
        }
        feedback.put("code", 200);
        feedback.put("message", "Login successful");
        feedback.put("data", user.getEmail());
        feedback.put("userId", usersEntityList.get(0).getUserId());
        feedback.put("username", usersEntityList.get(0).getUsername());
        return feedback;
    }

    /**
     * Changes the user's password and returns a Map with a response code and message.
     *
     * @param user The user whose password is to be changed.
     * @return A Map containing the response code and message.
     */
    @Override
    public Map<String, Object> updatePassword(UsersEntity user) {
        Map<String, Object> feedback = new ConcurrentHashMap<>();

        List<UsersEntity> usersEntityList = usersDao.selectEmail(user.getEmail());//Get the database information of the user by mailbox
        List<UsersEntity> usersEntityList2 = usersDao.findVerifiCode(user.getEmail());//Get the verification_code of verification_code

        if (usersEntityList == null || usersEntityList.isEmpty()) {
            feedback.put("code", 400);
            feedback.put("message", "The account does not exist");
            return feedback;
        }
        if (usersEntityList.size() > 1) {
            feedback.put("code", 400);
            feedback.put("message", "The account is abnormal");
            return feedback;
        }

        UsersEntity usersEntity2 = usersEntityList2.get(0);
        if (!((usersEntity2.getVerificationCode()).equals(user.getVerificationCode()))) {
            feedback.put("code", 400);
            feedback.put("message", "The verification code you entered is incorrect");
            return feedback;
        }
        String salt = RandomUtil.randomString(6);//Used as an encryption to generate a snowflake number with 6 random digits(用为加密，生成随机数位6位的雪花数)
        String md5Pwd = SecureUtil.md5(user.getPassword() + salt);
        //Generate a new salt and save it to the database along with the new encrypted password(生成新的盐和加密后的新密码一并保存到数据库)
        usersDao.updatePwd(user.getEmail(), md5Pwd, salt);
        feedback.put("code", 200);
        feedback.put("message", "Password change successful");
        return feedback;
    }

    /**
     * Changes the user's information and returns a Map with a response code and message.
     *
     * @param user The user whose information is to be changed.
     * @return A Map containing the response code and message.
     */

    @Override
    public Map<String, Object> updateOneUserInformation(UsersEntity user) {
        Map<String, Object> feedback = new ConcurrentHashMap<>();
        List<UsersEntity> usersEntityList = usersDao.selectEmail(user.getEmail());

        if (usersEntityList == null || usersEntityList.isEmpty()) {
            feedback.put("code", 400);
            feedback.put("message", "This user does not exist or is not registered");
            return feedback;
        }

        if (usersEntityList.size() > 1 || usersEntityList.get(0).getUsername().equals(user.getUsername())) {
            feedback.put("code", 400);
            feedback.put("message", "The account is renamed or abnormal");
            return feedback;
        }
        usersDao.updateUserInformation(user.getUsername(), user.getEmail());
        feedback.put("code", 200);
        feedback.put("message", "Change personal information successfully");
        return feedback;
    }

    /**
     * Register user method: Record user registration information, encrypt user passwords and send activation links to user email addresses.
     *
     * @param user the user to register
     * @return a map containing the result of the operation
     * The email sending functionality in this method is based on the code from the following video tutorial:
     * Reference: { @link: https//www.youtube.com/watch?v=ugIUObNHZdo}
     */
    @Override
    public Map<String, Object> register(UsersEntity user) {
        Map<String, Object> feedback = new ConcurrentHashMap<>();

        if (!isEmail(user.getEmail())) {
            feedback.put("code", 400);
            feedback.put("message", "Please enter the correct email address");
            return feedback;
        }

        List<UsersEntity> usersEntityList = usersDao.selectEmail(user.getEmail());
        if (null == usersEntityList || usersEntityList.size() == 0) {

            String confirmCode = IdUtil.getSnowflake(1, 1).nextIdStr();// (Snowflake algorithm to generate confirmation codes)雪花算法生成确认码
            String salt = RandomUtil.randomString(6);//Salt:used as an encryption to generate a snowflake number with 6 random digits((盐)用为加密，生成随机数位6位的雪花数)
            String md5Pwd = SecureUtil.md5(user.getPassword() + salt);//Encrypted password, original password + salt(加密密码，原始密码+盐)
            LocalDateTime ldt = LocalDateTime.now().plusDays(1);// Activation expiry time: 24 hours(激活失效时间：24小时)

            //Initialising account information(初始化账号信息)
            user.setSalt(salt);
            user.setPassword(md5Pwd);
            user.setConfirmCode(confirmCode);
            user.setActivationTime(ldt);
            user.setIsValid((byte) 0);
            user.setIsDelete((byte) 0);
            // Add an account
            int result = usersDao.save(user);
            if (result != 0) {
                String activationUrl = "http://localhost:9090/charchar/users/activation?confirmCode=" + confirmCode;
                sendMail(activationUrl, user.getEmail());
                feedback.put("code", 200);
                feedback.put("message", "Register successfully, please go to your mailbox to activate");
            } else {
                feedback.put("code", 400);
                feedback.put("message", "Registration failed");
            }
            return feedback;
        } else if (usersEntityList.size() == 1) {
            feedback.put("code", 400);
            feedback.put("message", "This email is already registered");
            return feedback;
        } else {
            feedback.put("code", 400);
            feedback.put("message", "The account is abnormal");
            return feedback;
        }
    }

    /**
     * Forget password method: Send a new password to the user by email
     *
     * @param user the user to reset the password for
     * @return a map containing the result of the operation
     * The email sending functionality in this method is based on the code from the following video tutorial:
     * Reference: { @link: https//www.youtube.com/watch?v=ugIUObNHZdo}
     */
    @Override
    public Map<String, Object> forgetPassword(UsersEntity user) {
        //Using Map to return the results of a run to the front end（使用Map将运行结果返回前端）
        Map<String, Object> feedback = new ConcurrentHashMap<>();
        //Get this user by email（通过邮箱获取该用户）
        List<UsersEntity> usersEntityList = usersDao.selectEmail(user.getEmail());
        List<UsersEntity> usersEntityList2 = usersDao.findVerifiCode(user.getEmail());
        //确认该用户是否存在
        if (usersEntityList == null || usersEntityList.isEmpty()) {
            feedback.put("code", 400);
            feedback.put("message", "The account does not exist");
            return feedback;
        }
        //User has multiple accounts with the same name and account anomalies（用户存在多个相同名字账号，账号异常）
        if (usersEntityList.size() > 1) {
            feedback.put("code", 400);
            feedback.put("message", "The account is abnormal");
            return feedback;
        }
        UsersEntity usersEntity2 = usersEntityList2.get(0);

        if (!((usersEntity2.getVerificationCode()).equals(user.getVerificationCode()))) {
            feedback.put("code", 400);
            feedback.put("message", "The verification code you entered is incorrect");
            return feedback;
        }
        // After passing the above determination, create a temporary password（通过上述判定后，创建临时密码）
        int num = (int) ((Math.random() * 9 + 1) * 100000);
        String salt = RandomUtil.randomString(6);
        String md5Pwd = SecureUtil.md5(num + salt);//Generate a new salt and save it to the database along with the new encrypted password(生成新的盐和加密后的新密码一并保存到数据库)

        usersDao.updatePwd(user.getEmail(), md5Pwd, salt);
        String activationUrl = "A new password has been created, please change it as soon as possible" + num;
        sendMail(activationUrl, user.getEmail());// Send an email to get a temporary password(发送邮件得到临时密码)
        feedback.put("code", 200);
        feedback.put("message", "Please go to your email address for a temporary password");
        return feedback;
    }


    /**
     * This method deletes a user account.
     *
     * @param user The user account to be deleted.
     * @return A map containing the result of the operation.
     */

    @Override
    public Map<String, Object> deleteUser(UsersEntity user) {//This method should be executed before the loginAccount call.(执行该方法前应该先执行loginAccount再调用)
        Map<String, Object> feedback = new ConcurrentHashMap<>();
        List<UsersEntity> usersEntityList = usersDao.selectEmail(user.getEmail());//Get the corresponding user name, encrypted password and salt(获取该用户名相应的用户名，加密后的密码 和 盐)

        if (usersEntityList == null || usersEntityList.isEmpty()) {
            feedback.put("code", 400);
            feedback.put("message", "This user does not exist or is not registered");
            return feedback;
        }
        if (usersEntityList.size() > 1) {
            feedback.put("code", 400);
            feedback.put("message", "The account is abnormal");
            return feedback;
        }

        UsersEntity usersEntity = usersEntityList.get(0);//Query for a user and do a password comparison (an email has only one user so it's get (0)) (查询到一个用户，进行密码对比(一个email只有一个用户所以是get（0）))
        String md5Pwd = SecureUtil.md5(user.getPassword() + usersEntity.getSalt());
        if (!usersEntity.getPassword().equals(md5Pwd)) { //The number of snowflakes written in the query for the salt and password should correspond to the database(查询到的salt和密码编写的雪花数应该与database对应)
            feedback.put("code", 400);
            feedback.put("message", "The password entered is incorrect");
            return feedback;
        }
        //注销账户
        usersDao.updateUserStatus(user.getEmail());
        feedback.put("code", 200);
        feedback.put("message", "The account has been cancelled");
        return feedback;
    }

    /**
     * This field holds the email address of the mail sender.
     */
    @Value("${spring.mail.username}")
    private String mailUsername;
    /**
     * This field holds an instance of the JavaMailSender class for sending emails.
     */
    @Resource
    private JavaMailSender javaMailSender;
    /**
     * This field holds an instance of the TemplateEngine class for creating email templates.
     */
    @Resource
    private TemplateEngine templateEngine;

    /**
     * This method sends an email to the specified email address.
     *
     * @param Url   The URL to be sent in the email.
     * @param email The email address to send the email to.
     *              The email sending functionality in this method is based on the code from the following video tutorial:
     *              Reference: { @link: https//www.youtube.com/watch?v=ugIUObNHZdo}
     */
    @Override
    public void sendMail(String Url, String email) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);

            message.setSubject("Welcome to CharChar Services");// Set email subject(设置邮件主题)
            message.setFrom(mailUsername);// Setting up email senders(设置邮件发送者)
            message.setTo(email);// Set up email recipients(设置邮件接受者，可以多个)
            message.setSentDate(new Date());//Set email delivery date(设置邮件发送日期)
            Context context = new Context(); // Creating a Contextual Environment(创建上下文环境)
            context.setVariable("Url", Url);//URL
            String text = templateEngine.process("activation-account.html", context);
            message.setText(text, true);// Setting the body of the email(设置邮件正文)
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        javaMailSender.send(mimeMessage);// Mailing
    }

    /**
     * This method activates a user account based on the confirmation code.
     *
     * @param confirmCode The confirmation code used to activate the account.
     * @return A map containing the result of the operation.
     */
    @Override
    public Map<String, Object> activationAccount(String confirmCode) {
        Map<String, Object> feedback = new ConcurrentHashMap<>();

        UsersEntity user = usersDao.selectUserByConfirmCode(confirmCode);// User search by confirmation code(根据确认码查询用户)
        if (user.getActivationTime() != null) {// Determine if the activation time has expired(判断激活时间是否超时)
            boolean after = LocalDateTime.now().isAfter(user.getActivationTime());

            if (after) {
                feedback.put("code", 400);
                feedback.put("message", "The link is no longer available, please re-register");
                return feedback;
            }
        } else {
            feedback.put("code", 400);
            feedback.put("message", "error,ActivationTime is null");
            return feedback;
        }

        int result = usersDao.updateUserByConfirmCode(confirmCode); // Look up the user according to the confirmation code and change the status value to 1 (available)(根据确认码查询用户并修改状态值为 1（可用）)
        if (result > 0) {
            feedback.put("code", 200);
            feedback.put("message", "Successful activation");
        } else {
            feedback.put("code", 400);
            feedback.put("message", "Activation failed");
        }
        return feedback;
    }

    /**
     * Determines if the given email address is valid.
     *
     * @param email The email address to be validated
     *              Returns true if the email address is valid, otherwise returns false.
     */
    @Override
    public boolean isEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    /**
     * Updates the verification code for the specified user and sends an email containing the code.
     *
     * @param user the user to update the verification code for
     * @return a map containing the result of the operation, including a code and message
     */
    @Override
    public Map<String, Object> updateVerificationCode(UsersEntity user) {
        Map<String, Object> feedback = new ConcurrentHashMap<>();

        List<UsersEntity> usersEntityList = usersDao.selectEmail(user.getEmail());
        if (usersEntityList == null || usersEntityList.isEmpty()) {
            feedback.put("code", 400);
            feedback.put("message", "This user does not exist or is not registered");
            return feedback;
        }
        if (usersEntityList.size() > 1) {
            feedback.put("code", 400);
            feedback.put("message", "The account is abnormal");
            return feedback;
        }

        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        String vertifi = String.valueOf(randomNumber);
        usersDao.updateVertificationCode(user.getEmail(), vertifi);
        String activationUrl = "This is your verification code, please keep it safe: " + vertifi + ". ";
        sendMail(activationUrl, user.getEmail());
        if (vertifi != null) {//Prevent database data from being changed to null(防止数据库数据被改为null)
            feedback.put("code", 200);
            feedback.put("message", "验证码发送成功");
        } else {
            feedback.put("code", 400);
            feedback.put("message", "验证码发送失败");
        }

        return feedback;
    }
}