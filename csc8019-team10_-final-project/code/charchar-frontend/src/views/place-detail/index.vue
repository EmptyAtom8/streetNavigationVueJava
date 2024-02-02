<template>
    <div class="place-detail">
        <div class="header-wrap">
            <el-button @click="toMapDetail" round>Back</el-button>
            <el-button @click="toProfile" round>Profile</el-button>
        </div>
        <div class="carousel-wrap">
            <el-carousel :interval="4000" type="card" height="200px">
                <el-carousel-item v-for="item in detailInfo.imageUrl" :key="item">
                    <!--<h3 text="2xl" justify="center">{{ item }}</h3>-->
                    <img style="height: 100%; width: 100%" :src="item" alt="" srcset="">
                </el-carousel-item>
            </el-carousel>
        </div>
        <div class="detail-wrap">
            <div class="title">{{ detailInfo.attractionName }}</div>
            <div class="detail-item">
                <span>{{ detailInfo.attrRating }}</span>
                <el-rate v-model="detailInfo.attrRating" disabled text-color="#ff9900" />
                <!-- <span>({{ detailInfo.user_ratings_total }})</span> -->
            </div>
            <div class="detail-item">
                <span :class="detailInfo?.openingHours != 0 ? 'active' : ''">Open</span>
                <span class="point">·</span>
                <span :class="detailInfo?.openingHours == 0 ? 'active' : ''">Closes</span>
            </div>
            <div class="detail-item">{{ detailInfo.address }}</div>
            <div class="detail-item">£ {{ detailInfo.ticketPrice }}</div>
            <!-- <div class="detail-item">category: {{ detailInfo.category }}</div> -->
            <div class="detail-item"><el-tag type="primary">{{ detailInfo.category }}</el-tag></div>
        </div>
        <div class="divider"><el-divider /></div>
        <h3 class="comment-title">
            <span>Comments</span>
            <div>
                <el-button @click="addComment" :icon="Plus" type="primary">Add</el-button>
            </div>
        </h3>
        <div class="comment-wrap">
            <div v-for="item, idx in commentList" :key="idx" class="comment-item">
                <div class="top">
                    <!-- <el-avatar :size="50" :src="item.avatar" /> -->
                    <el-avatar :size="50" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
                    <div class="user-name">
                        <!-- <div>{{ item.userId }}</div> -->
                        <div>{{ item.username }}</div>
                        <div style="width: 100%; display: flex; justify-content: space-between; align-items: center;">
                            <span>{{ formatDate(item.createTime) }}</span>
                            <span>Rating: {{item.rating?.toFixed(1) }} /5.0</span>
                        </div>
                    </div>
                </div>
                <div class="content">{{ item.reviewText }}</div>
                <div class="like">
                    <el-icon @click="() => giveLike(item)" size="18px">
                        <!-- <StarFilled :color="item.likeStatus ? '#409eff' : ''" /> -->
                        <StarFilled :color="item.likeStatus ? 'red' : ''" />
                    </el-icon>
                    <span>({{ item.likeCount }})</span>
                </div>
            </div>
        </div>
        <el-dialog v-model="dialogFormVisible" title="Send Comment" width="80%" >
            <el-form label-width="80" ref="formRef" :rules="rules" :model="form">
                <el-form-item label="Rating" prop="rating">
                    <el-rate v-model="form.rating" />
                </el-form-item>
                <el-form-item label="Comment" prop="comment">
                    <el-input type="textarea" v-model="form.comment" placeholder="Please input Comment" :autosize="{minRows: 5, maxRows: 10}"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogFormVisible = false">Cancel</el-button>
                    <el-button type="primary" @click="confirm">
                        Confirm
                    </el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>
  
<script lang="ts" setup>
import { Notebook, Memo, Phone, StarFilled, Plus } from '@element-plus/icons-vue'
import { reactive, ref, onMounted, watch } from "vue";
import { ElMessage } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import { getAttractionInfo, getAttractionReviewsList, saveReviews, giveLikeOfReviews } from "@/api/place";
import { getAccount, formatDate } from '@/util';
const route = useRoute()
const { placeId, attractionId } = route.query

const router = useRouter();
const account = getAccount()

const toMapDetail = () => {
    router.push('/map-detail')
}

const toProfile = () => {
    router.push("/userinfo");
};

const detailInfo = ref({
    "attractionId": 1202,
    "attractionName": "Life Science Centre",
    "description": "{\"overview\":\"Interactive, state-of-the-art science village with a planetarium, themed shows & 4D Motion Ride.\",\"language\":\"en\"}",
    "category": "food",
    "latitude": 54.96701700,
    "longitude": -1.62059920,
    "openingHours": 1,
    "ticketPrice": 998,
    // "imageUrl":'[\"https://maps.googleapis.com/maps/api/place/photo?maxwidth\=400\&photoreference\=AZose0n97l6ICieo4GCpCFM06iZ8cxrOM2ZMQmUiYD4IxjwQUe-6gwTXtoJx0BOoeb-1UWkJ4yyScRANPKzq1jmxiSAb08vb9OGSy8lPBv5patT8P0QdbowKh1GFNhKgL2htz92tFf6MaGhZl3h4d20eWUZ_qnewnbayY2I0l7e97eoo_XpF\&key\=AIzaSyAH2t_DBBb8y7O3IY9wOcrrLfIOC842JyE\"]',
    "attrRating": 4.4,
    "wheelchairAllow": 1,
    "pramAllow": null,
    "hearingAllow": null,
    "address": "Life Science Centre International Centre for Life Times Square, Newcastle upon Tyne",
    "placeId": "ChIJR9W6XEt3fkgROSWmQQqG4BQ"
})

const formRef = ref(null)
const dialogFormVisible = ref(false)
const form = reactive({
    comment: '',
    rating: null
})
const rules = reactive({
    comment: [
        { required: true, message: 'Please input Comment', trigger: 'blur' },
    ],
    rating: [
        { required: true, message: 'Please Rating', trigger: 'blur' },
    ],
})

// 添加评论
const addComment = () => {
    dialogFormVisible.value = true
}

// 评论
const confirm = async () => {
    await formRef.value.validate(async (valid, fields) => {
        if (valid) {
            console.log('submit!')
            // commentList.value.unshift({
            //     id: new Date().getTime(),
            //     isLike: false,
            //     avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
            //     // name: 'new user member',
            //     // date: 'August 25,2021',
            //     date: moment().format('MMMM DD，YYYY'),
            //     name: account.username || 'new user member',
            //     comment: form.comment
            // })
            const addData = {
                attractionId,
                userId: account.userId,
                rating: form.rating,
                reviewText: form.comment
            }
            await saveReviews(addData)
            ElMessage.success('success!')
            getAttractionReviewsListImpl()
            formRef.value.resetFields()
            dialogFormVisible.value = false
        } else {
            console.log('error submit!', fields)
        }
    })
}
// const commentList = ref([...new Array(3)].map((item, idx) => ({ id: idx + 1, isLike: true, avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png', name: 'Former member', date: 'August 25,2021', comment: '52,000 fans roaring fans every week towatched premier league team NewcastleUnited FC.' })))
const commentList = ref([
    {
        "reviewId": 6,
        "attractionId": 991,
        "userId": 1,
        "userName": 'abc',
        "rating": 5,
        "reviewText": "good",
        "likeCount": 4,
        "likeStatus": 1,
        "createTime": "2023-04-30T05:10:24.000+00:00",
        "updateTime": "2023-05-07T23:55:28.000+00:00"
    }
])
console.log('commentList', commentList);

// function like
const giveLike = async (item) => {
    const likeData = {
        reviewId: item.reviewId,
        userId: account.userId
    }
    const res: any = await giveLikeOfReviews(likeData)
    // item.isLike = !item.isLike
    // likeStatus
    ElMessage.warning(res.likeStatus ? 'Liked' : 'Cancel Likes')
    getAttractionReviewsListImpl()
}

// Get details of the attractions
const getAttractionInfoImpl = async () => {
    const res: any = await getAttractionInfo({ placeId })
    res.imageUrl = JSON.parse(res.imageUrl || '[]')

    console.log('/save/AttractionList/', res.imageUrl);
    
    detailInfo.value = res
}

// Get a list of comments from the current user
const getAttractionReviewsListImpl = async () => {
    const res: any = await getAttractionReviewsList({attractionId, userId: account.userId})
    commentList.value = res
}

onMounted(() => {
    getAttractionInfoImpl()
    getAttractionReviewsListImpl()

});
</script>
  
<style lang="scss" scoped>
.place-detail {
    width: 100%;
    padding-bottom: 80px;

    .header-wrap {
        height: 60px;
        line-height: 60px;
        padding: 10px 0;
        width: 90%;
        margin: auto;
        box-sizing: border-box;
        display: flex;
        align-items: center;
        justify-content: space-between;
    }

    .carousel-wrap {
        width: 90%;
        margin: auto;

        .el-carousel__item h3 {
            color: #475669;
            opacity: 0.75;
            line-height: 200px;
            margin: 0;
            text-align: center;
        }

        .el-carousel__item:nth-child(2n) {
            background-color: #99a9bf;
        }

        .el-carousel__item:nth-child(2n + 1) {
            background-color: #d3dce6;
        }
    }

    .detail-wrap {
        width: 90%;
        margin: auto;

        .title {
            font-size: 22px;
            color: #000;
            font-weight: 700;
        }

        .detail-item {
            display: flex;
            align-items: center;
            color: #9C9FA3;
            line-height: 30px;
            font-size: 14px;

            .active {
                color: #3E9458;
            }

            .point {
                font-size: 20px;
                color: #000;
                margin: 0 5px;
            }

            .el-tag+.el-tag {
                margin-left: 16px;
            }

            .btns {
                display: flex;
                margin-top: 16px;
            }

            .el-button {
                border-radius: 10px;
            }

            .el-button+.el-button {
                margin-left: 16px;
            }
        }
    }

    .divider {
        width: 90%;
        margin: auto;
    }

    .comment-title {
        width: 90%;
        margin: auto;
        background-color: #d3dce6;
        padding: 20px 10px;
        font-size: 24px;
        border-radius: 10px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .comment-wrap {
        width: 90%;
        margin: auto;
        max-height: 500px;
        overflow: auto;

        &::-webkit-scrollbar {
            width: 0px !important;
            height: 0px !important;
            // background-color: #F5F5F5;
        }

        .comment-item {
            padding: 16px;
            border-bottom: 1px solid #dcdfe6;

            .top {
                display: flex;
                align-items: center;

                .user-name {
                    flex: 1;
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

            .like {
                display: flex;
                align-items: center;
            }
        }
    }

}
</style>