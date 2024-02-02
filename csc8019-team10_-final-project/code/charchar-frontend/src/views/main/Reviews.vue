<template>
  <div class="review">
    <Ihead />
    <div class="comment-wrap">
      <div v-for="item, idx in commentList" :key="idx" class="comment-item">
        <div class="top">
          <el-avatar :size="50" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
          <div class="user-name">
            <div>{{ account.userName }}</div>
            <div>{{ formatDate(item.createTime) }}</div>
          </div>
        </div>
        <div class="content">{{ item.reviewText }}</div>
        <el-icon @click="deleteReview(item.reviewId)"><Delete /></el-icon>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, onMounted, ref } from "vue";
import Ihead from "@/components/InfoHead.vue";
import { Timer, ChatDotSquare, StarFilled, Delete } from "@element-plus/icons-vue";
import { deleteUserReviews, getUserReviewsList } from '@/api/place'
import moment from 'moment'
import { formatDate, getAccount } from "@/util";
import { ElMessage, ElMessageBox } from "element-plus";

const account = getAccount()

const commentList = ref([])
console.log('commentList', commentList);

const deleteReview = async (id) => {
   ElMessageBox.confirm(
    'proxy will permanently delete the review. Continue?',
    'Warning',
    {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
      type: 'warning',
    }
  )
    .then( async () => {
        const res = await deleteUserReviews([id])
        ElMessage.success('success')
        getUserReviewsListImpl()
    })
}

const getUserReviewsListImpl = async () => {
  const res: any = await getUserReviewsList({ userId: account.userId })
  // if (res.code == 200) {
  commentList.value = res
  // }
}
onMounted(() => {
  getUserReviewsListImpl()
});
</script>
   
<style lang="scss" scoped>
.comment-wrap {
  width: 90%;
  margin: auto;
  max-height: 800px;
  overflow: auto;

  &::-webkit-scrollbar {
    width: 0px !important;
    height: 0px !important;
  }

  .comment-item {
    padding: 16px;
    border-bottom: 1px solid #dcdfe6;

    .top {
      display: flex;
      align-items: center;

      .user-name {
        margin-left: 20px;

        div:nth-child(1) {
          margin-bottom: 8px;
          color: #000;
          font-weight: 700;
          font-size: 16px;
        }
      }
    }

    .content {
      margin-top: 10px;
      line-height: 20px;
      color: #414141;
      font-size: 14px;
    }
  }
}
</style>