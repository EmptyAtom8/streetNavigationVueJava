<template>
  <div class="reset-password">
    <Ihead />
    <el-main>
      <div class="tt">Reset Your Password</div>
      <el-form ref="formRef" :model="form" label-width="130px">
        <el-form-item label="Email" >

          <el-input v-model="form.email" :disabled="true">

            <template #append>
              <el-button type="primary" @click="sendCode()" :disabled="countdown"
                style="width: 100px; height: 42px; background-color: #1a4776; color: #fff">{{ countdown > 0 ? countdown +
                  's' : 'Send Code' }}</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="Verification Code">
          <el-input v-model="form.verificationCode" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="New Password">
          <el-input v-model="form.password" autocomplete="off" type="password" />
        </el-form-item>
        <el-form-item label="Confirm Password">
          <el-input v-model="form.confirmPassword" autocomplete="off" type="password" />
        </el-form-item>
      </el-form>
      <span class="dialog-footer">
        <el-button @click="resetPassword()" type="primary"> Reset Password </el-button>
        <el-button @click="exit()">Cancel</el-button>
      </span>
    </el-main>
  </div>
</template>
  
<script lang="ts" setup>
import { reactive, ref } from "vue";
import { useStore } from "vuex";
import Ihead from "@/components/InfoHead.vue";
import { useRouter } from "vue-router";
import { ElMessage, FormInstance } from 'element-plus'
import { updateUserPassword, sendVerificationCode } from '@/api'

const formRef = ref<FormInstance>()
let store = useStore();
let router = useRouter();



const form = reactive({
  email: localStorage.getItem('email') || store.state.HomeModules.userList.email,
  verificationCode: '',
  password: '',
  confirmPassword: '',
});
localStorage.setItem('email', form.email);//use to protect data disappeared from update web page
const countdown = ref(0);
const sendCode = async () => {
  if (countdown.value === 0) {
    try {
      const res: any = await sendVerificationCode({ email: form.email });
      console.log('响应结果：', res);
      if (res.code === 200) {
        ElMessage({
          message: "Verification code sent successfully!",
          type: "success",
        });
        timeCount();
      } else {
        ElMessage({
          showClose: true,
          message: "Failed to send verification code.",
          type: "error",
        });
      }
    } catch (error) {
      console.error('发生错误：', error);
      ElMessage({
        showClose: true,
        message: "Failed to send verification code.",
        type: "error",
      });
    }
  }
};
const timeCount = () => {
  let timer = 60;
  countdown.value = timer;
  const interval = setInterval(() => {
    if (timer === 0) {
      clearInterval(interval);
      countdown.value = 0;
    } else {
      timer--;
      countdown.value = timer;
    }
  }, 1000);
};

const resetPassword = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.password === form.confirmPassword) {
        try {
          const res: any = await updateUserPassword({
            email: form.email,
            password: form.password,
            verificationCode: form.verificationCode
          })
          if (res && res.code === 200) {
            ElMessage({
              message: "Password reset successfully!",
              grouping: true,
              type: "success",
            });
            router.push("/userinfo");
          } else {
            ElMessage({
              showClose: true,
              message: "Sorry, failed to reset password.",
              type: "error",
            });
          }
        } catch (error) {
          console.error('发生错误：', error);
          ElMessage({
            showClose: true,
            message: "Sorry, failed to reset password.",
            type: "error",
          });
        }
      } else {
        ElMessage({
          showClose: true,
          message: "Password does not match the confirm password.",
          type: "error",
        });
      }
    }
  })
};

const exit = () => {
  router.push("/userinfo");
};
</script>
  
<style>
.reset-password .el-main .tt {
  font-weight: bold;
  text-align: center;
  font-size: 21px;
  color: #1a4776;
  margin-bottom: 33px;
}

.reset-password span.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: center;
}

.reset-password button.el-button.el-button--primary {
  background: #1a4776;
  border: none;
}

.el-button:after {
  color: white;
}
</style>