<template>
 <div class="common-layout">
    <el-container>
      <el-header><Uhead/></el-header>
      <el-main><EDit/></el-main>
    </el-container>
  </div>
</template>
<script setup>
import Uhead from "@/components/UserHead.vue"
import EDit from "@/components/UserEdit.vue"
</script>

<style>
.common-layout:before {
    content: "";
    display: block;
    width: 100%;
    height: 250px;
    background: #1a4776;
    position: absolute;
}

.common-layout {
    position: relative;
}

.el-main {
}



.common-layout .el-header {
    height: auto;
}
.common-layout .el-container {
    position: relative;
    z-index: 1;
}

.common-layout .el-container.is-vertical {
    position: relative;
}
</style>