import Vue from 'vue'
import App from './App.vue'
import './registerServiceWorker'
import router from './router'
import Router from 'vue-router'
import store from './store'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import http from '@/api/http'
// @ts-ignore
import uploader from 'vue-simple-uploader';

Vue.use(uploader);

Vue.config.productionTip = false;
Vue.use(ElementUI);
Vue.prototype.http = http;

const originalPush = Router.prototype.push
Router.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
};

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app');

declare module 'vue/types/vue' {
    interface Vue {
        http: any,
    }
}

declare global {

    class R<T> {
        data: T;
        code: number;
        msg: string;
    }

    class CommonFile {
        fileName: string;
        fileType: string;
        isDir: boolean;
        size: string;
        lastModifyTime: string;
    }
}
