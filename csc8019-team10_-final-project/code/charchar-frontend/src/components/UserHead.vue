<template>
  <div>
    <el-icon @click="back" color="#ffffff" style="font-size: 26px; float: left; margin-top: 0.5rem">
      <ArrowLeft />
    </el-icon>
    <el-icon @click="logout" color="#ffffff" style="font-size: 26px; float: right; margin-top: 0.5rem">
      <!-- <HomeFilled /> -->
      <SwitchButton />
    </el-icon>
    <div class="infobox">
      <el-icon style="
          font-size: 32px;
          color: #fff;
          border: 1px solid #fff;
          width: 52px;
          height: 52px;
          border-radius: 50%;
          margin-bottom: 24px;
        ">
        <UserFilled />
      </el-icon>
      <div class="email">{{ account.email }}</div>
    </div>
  </div>
</template>
   
<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { HomeFilled, UserFilled, SwitchButton, ArrowLeft } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import { getAccount, removeAccount } from '@/util'

const router = useRouter()
const account = getAccount()

const back = () => {
  router.push('/map')
}

const logout = () => {
  ElMessageBox.confirm(
    'Logout. Continue?',
    'Warning',
    {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
      type: 'warning',
    }
  )
    .then(() => {
      removeAccount()
      router.push('/')
    })
}

</script>
   
<style>
.infobox {
  width: 100%;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
}

.email {
  color: #fff;
  font-weight: bold;
  font-size: 18px;

}

.el-menu {
  border-radius: 4px;
}
</style>