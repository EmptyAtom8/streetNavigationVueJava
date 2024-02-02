<template>
  <div class="login">
    <div class="head">
      <span class="cancel"><router-link to="/index">Cancel</router-link></span>
      <span class="head_tt">{{ model }}</span>
    </div>
    <div class="login-con">
      <el-form ref="ruleFormRef" :model="ruleForm" status-icon :rules="rules" class="demo-ruleForm">
        <el-form-item prop="username" v-if="model === 'Sign up'">
          <el-input v-model="ruleForm.username" type="username" autocomplete="off" placeholder="username" />
        </el-form-item>

        <el-form-item prop="Email">
          <el-row :gutter="model === 'Forgot Password' ? 10 : 0">
            <el-col :span="model === 'Forgot Password' ? 14 : 24">
              <el-input v-model="ruleForm.Email" type="Email" autocomplete="off" placeholder="Email" />
            </el-col>
            <el-col span="4" v-if="model === 'Forgot Password'">
              <el-button type="primary" @click="sendVerificationCodeImpl(ruleForm.Email)" :disabled="btnloop">{{
                sendBtnText() }}</el-button>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item prop="verificationCode" v-if="model === 'Forgot Password'">
          <el-input v-model="ruleForm.verificationCode" type="text" autocomplete="off" placeholder="Verification Code" />
        </el-form-item>
        <el-form-item prop="pass" v-if="model !== 'Forgot Password'">
          <el-input v-model="ruleForm.pass" type="password" autocomplete="off" placeholder="password" />
        </el-form-item>
        <el-form-item prop="checkPass" v-if="model !== 'Forgot Password'">
          <el-input v-model="ruleForm.checkPass" type="password" autocomplete="off" placeholder="Duplicate password"
            v-if="model === 'Sign up'" />
        </el-form-item>
        <el-form-item width="100%">
          <el-button type="primary" :disabled="btnloop" @click="submitForm(ruleFormRef)" width="100%">{{ formatBtnText()
          }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="forgot" @click="forgotPass">Forgot Password?</div>
      <div class="forgot" v-if="model === 'Log in'">Not a member?<a @click="signUp">Sign up</a></div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref, onMounted, watch } from "vue";
import { FormInstance, ElMessage } from "element-plus";
import * as check from "@/util/checkData";
import { useRouter } from "vue-router";
import { useStore } from "vuex"
import { forgetUserPassword, getUserInformation, login, register, sendVerificationCode, updateUserPassword } from '@/api'
import { dataType } from "element-plus/es/components/table-v2/src/common";
import { setAccount } from "@/util";

let store = useStore();
let router = useRouter();
let model = ref("Sign up");

onMounted(() => {
  let lurl = window.location.href;
  let cs = parseInt(lurl.split("?")[1].split("=")[1]);
  if (cs == 1) {
    model.value = "Sign up";
  } else {
    model.value = "Log in";
  }
});
const ruleFormRef = ref<FormInstance>();
const validateName = (rule: any, value: any, callback: any) => {
  if (value === "" && model.value === "Sign up") {
    callback(new Error("Please input a username！"));
  } else {
    callback();
  }
};
const validatePass = (rule: any, value: any, callback: any) => {
  if (value === "") {
    callback(new Error("Please input a password！"));
  } else {
    callback();
  }
};
const sendVerificationCodeImpl = async (email: string) => {
  // Disable the send button first
  btnloop.value = true
  try {

    // Sending a verification code request
    const res: any = await sendVerificationCode({ email })
    console.log('res', res);
    if (res.code === 200) {
      ElMessage.success('Verification code sent successfully!')
      // Send verification code successfully and enable countdown
      countDown()
    } else {
      ElMessage.error('Failed to send verification code!')
      // Failed to send captcha, enable send button
      btnloop.value = false
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('Failed to send verification code!')
    // Failed to send captcha, enable send button
    btnloop.value = false
  }
}
const validatePass2 = (rule: any, value: any, callback: any) => {
  if (model.value === "Log in") {
    callback();
  }
  if (value === "") {
    callback(new Error("Please enter a duplicate password！"));
  } else if (value !== ruleForm.pass) {
    callback(new Error("The two passwords do not match!"));
  } else {
    callback();
  }
};
const validateEmail = (rule: any, value: any, callback: any) => {
  if (value === "") {
    callback(new Error("Please enter the email address!"));
  } else if (!check.checkEmail(value)) {
    callback(new Error("Please enter the email address!"));
  } else {
    callback();
  }
};

const ruleForm = reactive({
  username: "",
  pass: "123456",
  checkPass: "",
  Email: "1754817404@qq.com",
  verificationCode: "",

});
let btnloop = ref(false);
watch(ruleForm, (newsDate: any, oldDate: any) => {
  if (model.value === "Log in") {
    if (newsDate.Email != "" && newsDate.pass != "") {
      btnloop.value = false;
    } else {
      btnloop.value = true;
    }
  } else if (model.value === "Sign up") {
    if (newsDate.username != "" && newsDate.Email != "" && newsDate.pass != "" && newsDate.checkPass != "") {
      btnloop.value = false;
    } else {
      btnloop.value = true;
    }
  } else {
    if (newsDate.Email != "") {
      btnloop.value = false;
    } else {
      btnloop.value = true;
    }
  }
});
const rules = reactive({
  username: [{ validator: validateName, trigger: "blur" }],
  pass: [{ validator: validatePass, trigger: "blur" }],
  checkPass: [{ validator: validatePass2, trigger: "blur" }],
  Email: [{ validator: validateEmail, trigger: "blur" }],
});

const submitForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  let registerData: any = { username: ruleForm.username, email: ruleForm.Email, password: ruleForm.pass, verificationCode: ruleForm.verificationCode };
  formEl.validate(async (valid) => {
    console.log('167', valid);

    if (valid) {
      if (model.value === "Log in") {
        const params = { email: ruleForm.Email, password: ruleForm.pass }
        const res: any = await login(params)
        console.log('res', res);
        if (res.code === 200) {
          const userData = { email: res.data, userId: res.userId, userName: res.username }
          setAccount(userData)
          store.commit("USER_INFO", userData)//user to get email from Java 
          ElMessage({
            message: "success！",
            grouping: true,
            type: "success",
          });
          router.push("/map");
        } else {
          ElMessage({
            message: "Login failed",
            grouping: true,
            type: "error",
          });
        }
      } else if (model.value === "Sign up") {
        const res: any = await register(registerData)
        console.log('res', res);
        if (res.code == 200) {
          ElMessage({
            message: "Please go to your mailbox to activate your account",
            grouping: true,
            type: "success",
          });
          model.value = "Log in";
        } else {
          ElMessage({
            showClose: true,
            message: "Registration failed",
            type: "error",
          });
        }
      } else {
        console.log('registerData', registerData);

        const res: any = await forgetUserPassword({ email: registerData.email, verificationCode: registerData.verificationCode })
        console.log('res', res);
        if (res.code == 200) {
          ElMessage({
            message: "Successfully modified!",
            grouping: true,
            type: "success",
          });
          model.value = "Log in";
        } else {
          ElMessage({
            showClose: true,
            message: "error",
            type: "error",
          });
        }
      }
    }
  });
};
const forgotPass = () => {
  model.value = "Forgot Password";
  ruleForm.username = "";
  ruleForm.Email = "";
  ruleForm.pass = "";
  ruleForm.checkPass = "";
  ruleForm.verificationCode = "";
};
const MAX_COUNT = 60 // Maximum number of seconds to countdown
let count = ref(0) // Current countdown seconds
let timer = ref(null) // Countdown timer

const countDown = () => {
  count.value = MAX_COUNT
  // Enable timer to update countdown seconds per second
  timer.value = setInterval(() => {
    count.value -= 1
    if (count.value <= 0) {
      // End of countdown, clear timer and enable send button
      clearInterval(timer.value)
      btnloop.value = false
    }
  }, 1000)
}

const sendBtnText = () => {
  if (count.value > 0) {
    return `${count.value}s`;
  }
  return model.value === "Forgot Password" ? "Send Code" : model.value;
};
const formatBtnText = () => {
  if (model.value === 'Forgot Password') {
    return 'Send new password'
  }
  return model.value
}
const signUp = () => {
  model.value = "Sign up";
}

</script>
<style></style>

<style scoped>
.el-row {
  align-items: center;
  width: 100%;
}


.login {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  flex-direction: column;
}

.head {
  line-height: 45px;
  background: #1a4776;
  width: 100%;
  color: #fff;
  padding: 0 8px;
  box-sizing: border-box;
  position: relative;
  margin-bottom: 0.8rem;
  text-align: center;
}

span.cancel {
  position: absolute;
  left: 8px;
}

span.head_tt {
  font-size: 16px;
}

form {
  width: 100%;
}

.login-con {
  width: 100%;
  padding: 0 8px;
  box-sizing: border-box;
}

.forgot {
  text-align: center;
  cursor: pointer;
  line-height: 0.4rem;
}

.forgot a {
  color: #1a4776;
}

button.el-button.el-button--primary {
  width: 100%;
}

span.cancel a {
  color: #fff;
}

button.el-button.el-button--primary {
  background: #1a4776;
}
</style>