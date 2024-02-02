<template>
  <div class="delete-account">
    <Ihead />
    <el-main>
      <div class="tt">Delete Account</div>
      <el-form ref="formRef" :model="form">
        <el-form-item label="Email" label-width="75px">
          <el-input v-model="form.email" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="Password" label-width="75px">
          <el-input
            v-model="form.password"
            autocomplete="off"
            type="password"
          />
        </el-form-item>
      </el-form>
      <span class="dialog-footer">
        <el-button @click="deleteAccount()" type="danger">
          Delete Account
        </el-button>
        <el-button @click="cancel()">Cancel</el-button>
      </span>
    </el-main>
  </div>
</template>
  
  <script lang="ts" setup>
import { reactive, ref } from "vue";
import { useStore } from "vuex";
import Ihead from "@/components/InfoHead.vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox, FormInstance } from "element-plus";
import { deleteUser } from "@/api";

const formRef = ref<FormInstance>();

let store = useStore();
let router = useRouter();
const form = reactive({
  email:
    localStorage.getItem("email") || store.state.HomeModules.userList.email,
  password: "",
});
localStorage.setItem("email", form.email);

const deleteAccount = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        ElMessageBox.confirm(
          "proxy will permanently delete the review. Continue?",
          "Warning",
          {
            confirmButtonText: "OK",
            cancelButtonText: "Cancel",
            type: "warning",
          }
        ).then(async () => {
          const res: any = await deleteUser({
            email: form.email,
            password: form.password,
          });
          if (res && res.code === 200) {
            ElMessage({
              message: "Account deleted successfully!",
              grouping: true,
              type: "success",
            });
            router.push("/");
          } else {
            ElMessage({
              showClose: true,
              message: "Sorry, failed to delete account.",
              type: "error",
            });
          }
        });
      } catch (error) {
        console.error("An error occurred:", error);
        ElMessage({
          showClose: true,
          message: "Sorry, failed to delete account.",
          type: "error",
        });
      }
    }
  });
};

const cancel = () => {
  router.push("/userinfo");
};
</script>
  
  <style>
.delete-account .el-main .tt {
  font-weight: bold;
  text-align: center;
  font-size: 21px;
  color: #1a4776;
  margin-bottom: 33px;
}

.delete-account span.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: center;
}

.delete-account button.el-button.el-button--danger {
  background: #f56c6c;
  border: none;
}

.delete-account span.dialog-footer button {
  flex: 1;
  max-width: 122px;
}
</style>