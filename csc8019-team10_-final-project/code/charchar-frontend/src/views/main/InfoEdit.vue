<template>
  <div class="edit">
    <Ihead />
    <el-main>
      <div class="tt">Manage your account</div>
      <el-form ref="formRef" :model="form">
        <el-form-item label="username" label-width="75px">
          <el-input v-model="form.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="Email" label-width="75px">
          <!-- <el-input disabled v-model="form.email" autocomplete="off" /> -->
          <el-input v-model="form.email" :disabled="true"></el-input>
           <!-- <el-input  v-model="form.email" autocomplete="off" /> -->
        </el-form-item>
      </el-form>
      <span class="dialog-footer">
        <el-button @click="submit()" type="primary"> confirm </el-button>
        <el-button @click="exit()">cancel</el-button>
      </span>
    </el-main>
  </div>
</template>
<script lang="ts" setup>
// onMounted
import { reactive, ref } from "vue";
import { useStore } from "vuex";
import Ihead from "@/components/InfoHead.vue";
import { useRouter } from "vue-router";
import { ElMessage, FormInstance } from 'element-plus'
import { updateOneUserInfomation } from '@/api'
import { getAccount } from "@/util";

const formRef = ref<FormInstance>()
const account = getAccount();

let store = useStore();
let router = useRouter();
const form = reactive({
  email: account.email,
});
localStorage.setItem('email', form.email);//use to protect data disappeared from update web page
const submit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res: any = await updateOneUserInfomation(form)
        console.log(res)
        if (res && res.code == 200) {
          ElMessage({
            message: "Successfully!",
            grouping: true,
            type: "success",
          });
          if (res.data && res.data.length > 0) {
            store.commit("USER_INFO", res.data[0]);
          }
        } else {
          ElMessage({
            showClose: true,
            message: "Sorry, duplicate name please try again",
            type: "error",
          });
        }
      } catch (error) {
        console.error('发生错误：', error);
        ElMessage({
          showClose: true,
          message: "Sorry, failed to update user information.",
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

.edit .el-main .tt {
    font-weight: bold;
    text-align: center;
    font-size: 21px;
    color: #1a4776;
    margin-bottom: 33px;
}
.edit span.dialog-footer {
    display: flex;
    align-items: center;
    justify-content: center;
}

.edit button.el-button.el-button--primary {
    background: #fd9c23;
    border: none;
}
.edit span.dialog-footer button {flex: 1;max-width: 100px;}
</style>