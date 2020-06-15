import Vue from 'vue'
import App from './App.vue'
import './registerServiceWorker'
import router from './router'
import store from './store'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import http from '@/api/http'

Vue.config.productionTip = false;
Vue.use(ElementUI);
Vue.prototype.http = http;

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
    }
}
