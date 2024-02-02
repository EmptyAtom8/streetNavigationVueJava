<template>
  <div class="box">
    <div class="context">
      <h1>Char Char</h1>
      <span>Charting for your next adventure</span>
    </div>
    <div class="anniu">
      <button class="Sign" @click="jump(1)">Sign up</button>
      <button class="Log" @click="jump(2)">Log in</button>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { useRouter } from "vue-router";
let router = useRouter();
const jump = (e:any) => {
  router.push("/login?type="+e);
};
</script>

<style>
.box {
  height: 100vh;
  position: relative;
  background: url(../../assets/loginbg.jpg) no-repeat center;
  background-size: cover;
}
.box:after {
  content: "";
  display: block;
  width: 100%;
  height: 100%;
  position: absolute;
  background: #333;
  opacity: 0.3;
}

.anniu {
  position: absolute;
  z-index: 1;
  bottom: 0;
  width: 100%;
}

.anniu button {
  display: block;
  width: 100%;
  line-height: 45px;
  border: none;
  color: #fff;
  font-size: 16px;
}

.anniu button.Sign {
  background: #22a519;
}

.anniu button.Log {
  background: #0e8ee2;
}
.context {
  position: absolute;
  top: 30%;
  color: #fff;
  text-align: center;
  width: 100%;
  z-index: 1;
  font-family: fangsong;
  padding: 0.5rem;
  box-sizing: border-box;
}

.context h1 {
  font-size: 0.7rem;
  margin-bottom: 0.2rem;
}

.context span {
  font-size: 0.4rem;
}

</style>