import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import "./style/global.css";
import 'lib-flexible/flexible.js'
console.log('process.env.NODE_ENV', process.env.NODE_ENV)


createApp(App).use(store).use(ElementPlus).use(router).mount('#app')
