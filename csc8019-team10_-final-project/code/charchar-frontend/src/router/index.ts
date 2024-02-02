import { createRouter, RouteRecordRaw, createWebHashHistory } from 'vue-router'
const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    redirect: '/index',
  },
  {
    path: '/index',
    name: 'index',
    component: () => import('../views/login/index.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/login/login.vue')
  },
  {
    path: '/userinfo',
    name: 'userinfo',
    component: () => import('../views/main/UserInfo.vue'),
  },
  {
    path: '/contact',
    name: 'contact',
    component: () => import('../views/main/ContactUs.vue'),
    
  },
  {
    path: '/infoedit',
    name: 'infoedit',
    component: () => import('../views/main/InfoEdit.vue'),
   
  },
  {
    path: '/reset-password',
    name: 'resetPassword',
    component: () => import('../views/main/ResetPassword.vue'),
   
  },
  {
    path: '/delete-account',
    name: 'delete-account',
    component: () => import('../views/main/DeleteAccount.vue'),
   
  },
  {
    path: '/reviews',
    name: 'reviews',
    component: () => import('../views/main/Reviews.vue'),
 
  },
  {
    path: '/map',
    name: 'Map',
    component: () => import('../views/map/index.vue'),
  },
  {
    path: '/map-detail',
    name: 'MapDetail',
    component: () => import('../views/map-detail/index.vue'),
  },
  {
    path: '/place-detail',
    name: 'PlaceDetail',
    component: () => import('../views/place-detail/index.vue'),
  },
]

const router = createRouter({
  history: createWebHashHistory(process.env.BASE_URL),
  routes
})

export default router
