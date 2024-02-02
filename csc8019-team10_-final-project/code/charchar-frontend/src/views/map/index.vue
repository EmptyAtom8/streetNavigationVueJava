<template>
  <div class="container">
    <div class="header-wrap">
      <el-button @click="toProfile" round>Profile</el-button>
      <!-- <el-input placeholder="search here" /> -->
    </div>
    <div class="map-wrap">
      <div id="map"></div>
    </div>
    <div class="footer">
      <!-- <el-button style="color: red">Select on Map</el-button> -->
      <el-button @click="myAddress">Get My Position</el-button>
      <el-button @click="toMap">Search near by attraction</el-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, watch } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { getAccount, setAccount } from "@/util";

const router = useRouter();

const map = ref(null);
const locations = ref([
  { name: "balboaPark", loc: { lat: 54.9782, lng: -1.61778 } },
  { name: "petcoPark", loc: { lat: 32.7075, lng: -117.1575 } },
  { name: "sanDiegoAirport", loc: { lat: 32.7323, lng: -117.196 } },
]);
const jingdian = ref([]);
const walkingTime = ref("");
const currentPosition = reactive({
  latitude: null,
  longitude: null
});

const toProfile = () => {
  router.push("/userinfo");
};

const toMap = () => {
  if (!currentPosition.latitude || !currentPosition.longitude) {
    ElMessage.warning('Please get the current location first')
    return
  }
  router.push({
    path: '/map-detail', query: {
      latitude: currentPosition.latitude,
      longitude: currentPosition.longitude
    }
  });
};

const myAddress = () => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const pos = {
          lat: position.coords.latitude,
          lng: position.coords.longitude,
        };
        // Record current position
        currentPosition.latitude = position.coords.latitude
        currentPosition.longitude = position.coords.longitude
        const account = getAccount()
        setAccount({...account, latitude: position.coords.latitude, longitude: position.coords.longitude})
        map.value.setCenter(pos);
        ElMessage({
          message: "success！",
          type: "success",
        });
      },
      (error) => {
        // handle errors
        console.log(error);
        ElMessage({
          message: "error!",
          type: "error",
        });
      }
    );
  } else {
    // console.log('Failed to get position');
  }
};

const addMarker = () => {
  const marker = new window.google.maps.Marker({
    position: locations.value[0].loc,
    animation: window.google.maps.Animation.DROP,
    draggable: true,
  });
  marker.setMap(map.value);
};

const initMap = () => {
  const mapElement = document.getElementById("map");
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
    // language: 'en',
  };

  map.value = new window.google.maps.Map(mapElement, mapOptions);
  addMarker();
};


onMounted(() => {
  initMap();
});
</script>

<style lang="scss" scoped>
.container {
  position: relative;
  width: 100%;
  height: 100%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  .header-wrap {
    height: 60px;
    line-height: 60px;
    padding: 10px 0;
    width: 90%;
    margin: auto;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    // justify-content: space-between;
    justify-content: flex-end;

  }

  .map-wrap {
    width: 90%;
    height: calc(100% - 120px);

    #map {
      width: 100%;
      height: 100%;
    }
  }

  .footer {
    width: 90%;
    height: 150px;
    line-height: 150px;
    box-sizing: border-box;
    padding: 20px 0;
    display: flex;
    flex-wrap: wrap;

    // align-items: center;
    // justify-content: space-between;
    .el-button {
      // margin-right: 20px;
    }
  }
}
</style>