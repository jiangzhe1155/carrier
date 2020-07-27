import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import VueRouter from "vue-router";
import {post} from './api/http';
import {formatSize} from './api/common'
import './assets/icon/iconfont.css'

Vue.config.productionTip = false;
Vue.prototype.post = post;
Vue.prototype.formatSize = formatSize;


const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function (location) {
  return originalPush.call(this, location).catch(err => err)
};

Vue.use(ElementUI);


new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');
