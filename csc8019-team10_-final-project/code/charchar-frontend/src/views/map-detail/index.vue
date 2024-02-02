<template>
  <div class="container">
    <div class="header-wrap">
      <el-button @click="toMap" round>Back</el-button>
      <!-- <el-input placeholder="search here" /> -->
    </div>
    <div class="map-wrap">
      <div id="mapNode"></div>
    </div>
    <div class="query-zone">
      <el-select @change="handlePlaceList" clearable v-model="filterValue" placeholder="filter">
        <el-option v-for="item in filterOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <!-- <button></button> -->
      <el-select @change="handlePlaceList" clearable v-model="sortedValue" placeholder="Sorted By">
        <el-option v-for="item in sortedOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </div>
    <div class="place-list">
      <div class="container">
        <div @click="toPlaceDetail(item.placeId, item.attractionId)" v-for="item, idx of placeList" :key="idx" class="list-item">
          <span class="name">{{ item.attractionName }}</span>
          <div class="content">
            <span>{{ item.address }}</span>
            <!-- <span>{{ item.openingHours ? 'Open' : 'Closes' }}</span> -->
          </div>
          <div class="info-container">
            <span style="margin-right: 10px;">open: {{item.openingHours}} </span><span style="margin-right: 10px;">walking time: {{item.walkingTime}} mins</span>
            
          </div>
          <div class="info-container">
            <span >price: {{item.ticketPrice}} £</span> <span>rating: {{item.attrRating}}</span>
          </div>
          <div class="icon-div" >
            <a href="#"  class="guide-container" @click.prevent.stop="guide(item.attractionName)">
              <div><el-icon class="guide-icon"><Guide /></el-icon></div>
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, watch } from "vue";
import { filterOptions, sortedOptions, radiusOptions } from '@/common/constant'
import { useRoute, useRouter } from "vue-router";
import { getNearByLocation } from "@/api/place";
import { NearByLocationList } from "@/mock/data";
import { getAccount } from "@/util";
import { Guide } from "@element-plus/icons-vue"

const router = useRouter()
const route = useRoute()
const account = getAccount()
console.log('query', route.query);
const { latitude, longitude } = route.query

const toMap = () => {
  router.push('/map')
}

// 跳转至景点详情
const toPlaceDetail = (placeId, attractionId) => {
  router.push({ path: '/place-detail', query: { placeId, attractionId } })
}

const map = ref(null);
const locations = ref([
  { name: "balboaPark", loc: { lat: 54.9782, lng: -1.61778 } },
  { name: "petcoPark", loc: { lat: 32.7075, lng: -117.1575 } },
  { name: "sanDiegoAirport", loc: { lat: 32.7323, lng: -117.196 } },
]);
const filterValue = ref("");
const sortedValue = ref("");
const radiusValue = ref('1000');

// List of original attractions
const originPlaceList = ref([])
//List of attractions
const placeList = ref([])

const initMap = () => {
  const mapElement = document.getElementById("mapNode");
  const mapOptions = {
    center: locations.value[0].loc,
    zoom: 13,
    mapTypeId: window.google.maps.MapTypeId.ROADMAP,
    panControl: true,
    zoomControl: true,
    mapTypeControl: true,
    scaleControl: true,
    streetViewControl: true,
    overviewMapControl: true,
    rotateControl: true,
  };

  map.value = new window.google.maps.Map(mapElement, mapOptions);
  addMarker();
};
const guide = (attractionName) => {
  const location = attractionName.replace(" ", "+");
  const url = `https://www.google.com/maps?q=${location}&travelmode=walking&language=en`;
    window.open(url, 'mapWindow')
}

const addMarker = () => {
  const marker = new window.google.maps.Marker({
    position: locations.value[0].loc,
    animation: window.google.maps.Animation.DROP,
    draggable: true,
  });
  marker.setMap(map.value);
};

const getNearByLocationImpl = async () => {
  const res: any = await getNearByLocation({ latitude: account.latitude || latitude, longitude: account.longitude || longitude, radius: radiusValue.value })
  // const res = NearByLocationList
  console.log('res', res);
  placeList.value = res
  originPlaceList.value = res

}

const handlePlaceList = () => {
  let result = []
  switch (filterValue.value) {
    case 'WC':
      result = originPlaceList.value.filter(item => item.wheelchairAllow == 1)
      break;
    case 'OS':
      result = originPlaceList.value.filter(item => item.openingStatus == 1)
      break;
    case 'HA':
      result = originPlaceList.value.filter(item => item.hearingAllow == 1)
      break;
    case 'PA':
      result = originPlaceList.value.filter(item => item.parmAllow == 1)
      break;
    default:
      result = originPlaceList.value
      break;
  }
  switch (sortedValue.value) {
    case 'MON':
      result = result.sort((a, b) => a.ticketPrice - b.ticketPrice)
      break;
    case 'RAT':
      result = result.sort((a, b) => b.attrRating - a.attrRating)
      break;
    case 'WALK':
      result = result.sort((a, b) => a.walkingTime - b.walkingTime)
      break;
    default:
      break;
  }
  placeList.value = result
}

onMounted(() => {
  initMap();
  getNearByLocationImpl()
});
</script>

<style lang="scss" scoped>
.container {
  position: relative;
  width: 100%;
  height: 100%;
  margin: 0 auto;

  .header-wrap {
    height: 8%;
    line-height: 8%;
    padding: 10px 0;
    width: 90%;
    margin: auto;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: space-between;

  }

  .map-wrap {
    width: 90%;
    margin: auto;
    height: 30%; // calc(100% - 120px);

    #mapNode {
      width: 100%;
      height: 100%;
    }
  }

  .query-zone {
    height: 8%;
    line-height: 8%;
    width: 90%;
    margin: auto;
    display: flex;
    /* // align-items: center; */
    padding-top: 20px;
    box-sizing: border-box;

    // justify-content: space-between;
    .el-select {
      margin-right: 20px;
    }
  }

  .place-list {
    width: 90%;
    margin: auto;
    padding: .266667rem 0;
    height: 50%;
    box-sizing: border-box;

    .container {
      height: 100%;
      width: 100%;
      border: 1px solid #999;
      border-radius: 20px;
      padding: 5px 10px;
      overflow: auto;

      .list-item {
        width: 100%;
        height: 2rem;
        /* line-height: 80px; */
        margin: 2px 0;
        padding: 5px 10px;
        border-radius: 5px;
        box-sizing: border-box;
        background-color: antiquewhite;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .name {
          flex: 1;
          font-size: .346667rem;
        }

        .content {
          flex: 1;
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin: 0.15rem 0 0;
          font-size: .266667rem;
          color: #999;
        }
        
      }
      .icon-div {
        display: flex;
        top: -4rem !important;
      }
      .info-container {
        display: flex;
        width: 95%;
        margin: .15rem 0 0 !important;
        justify-content: space-around; 
        align-items: center;
        margin-bottom: 1.2rem;
        
      }
      .info-container span {
        font-size: 0.23rem;
        margin: 0 5px;
        flex: 0.5;
      }
      .guide-container {
        display: inline-block;
        left: 92.5%;
        // top: -10rem!important;
        position: relative;
        padding: 0;
        margin: 0;
        
        // float: right;
        top: .266667rem !important
      }

      .guide-icon {
        // z-index: 9999;
        top: -1.4rem;
        font-size: 0.8rem; /* Control icon size */
      }
    }
  }

}
</style>