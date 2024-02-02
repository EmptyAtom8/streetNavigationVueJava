<template>
  <div>
    <el-menu router>
      <template v-for="v in linklist" :key="v.path">
        <el-menu-item
          :index="v.path"
          style="border-bottom: 1px solid #eee; height: 70px"
        >
          <el-icon style="font-size: 30px; margin-right: 13px"
            ><component :is="v.meta.icon"></component
          ></el-icon>
          <span style="font-weight: bold">{{ v.meta.title }}</span>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>
  <script setup>
import { onMounted, reactive } from "vue";
import { useRouter } from "vue-router";
import { UserFilled, Comment, QuestionFilled, DeleteFilled, Edit} from "@element-plus/icons-vue";
let router = useRouter();
const linklist = reactive([
  {
    path: "/infoedit",
    name: "infoedit",
    meta: {
      title: "Manage your account",
      icon: UserFilled,
    },
  },
  {
    path: "/reviews",
    name: "reviews",
    meta: {
      title: "Reviews",
      icon: Comment,
    },
  },
  {
    path: "/reset-password",
    name: "reset-password",
    meta: {
      title: "Reset Password",
      icon: Edit,
    },
  },
  {
    path: "/delete-account",
    name: "delete-account",
    meta: {
      title: "Delete Account",
      icon: DeleteFilled,
    },
  },
  {
    path: "/contact",
    name: "contact",
    meta: {
      title: "Contact Customer Service",
      icon: QuestionFilled,
    },
  },
]);

const handleResetPassword = () => {
  router.push("/reset-password");
}
const handDeleteAccount = () => {
  router.push("/delete-account");
}

</script>

  <style>
</style>