import Vue from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios' // import axios
import {store} from './store'

import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
//import { LayoutPlugin } from 'bootstrap-vue'
// Import Bootstrap an BootstrapVue CSS files (order is important)
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

import Chat from 'vue-beautiful-chat'

Vue.prototype.$http = axios // prototype에 axios 추가
Vue.config.productionTip = false

// Make BootstrapVue available throughout your project
Vue.use(BootstrapVue)
// Optionally install the BootstrapVue icon components plugin
Vue.use(IconsPlugin)
//Vue.use(LayoutPlugin)
Vue.use(Chat)

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
