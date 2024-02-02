<template>
  <div class="ihead">
    <el-header style="height: 60px;
    align-items: center;
    display: flex;
    box-shadow: 0 0 10px rgb(51 51 51 / 20%);">
      <el-page-header :icon="ArrowLeft" @back="onBack">
        <template #content>
          <div class="flex items-center">
            <el-avatar :size="32" class="mr-3"
              src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
            <span class="text-large font-600 mr-3">
              {{ $store.state.HomeModules.userList.email }}
            </span>
          </div>
        </template>
      </el-page-header>
    </el-header>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
let router = useRouter();
const onBack = () => {
  router.push("/userinfo");
};
</script>

<style>
.flex.items-center {
  display: flex;
  font-size: 15px;
  font-weight: bold;
  display: flex;
  align-items: center;
}

.el-page-header__left .el-divider--vertical {
  margin: 0 5px;
}
</style>