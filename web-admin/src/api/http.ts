import Axios from 'axios'
import {Message, Loading} from 'element-ui';

let loading: any;        //定义loading变量

function startLoading() {    //使用Element loading-start 方法
    loading = Loading.service({
        lock: true,
        text: '加载数据中……',
        background: 'rgba(0, 0, 0, 0.3)'
    })
}

function endLoading() {    //使用Element loading-close 方法
    loading.close()
}

//那么 showFullScreenLoading() tryHideFullScreenLoading() 要干的事儿就是将同一时刻的请求合并。
//声明一个变量 needLoadingRequestCount，每次调用showFullScreenLoading方法 needLoadingRequestCount + 1。
//调用tryHideFullScreenLoading()方法，needLoadingRequestCount - 1。needLoadingRequestCount为 0 时，结束 loading。
let needLoadingRequestCount = 0;

export function showFullScreenLoading() {
    if (needLoadingRequestCount === 0) {
        startLoading()
    }
    needLoadingRequestCount++
}

export function tryHideFullScreenLoading() {
    if (needLoadingRequestCount <= 0) return;
    needLoadingRequestCount--;
    if (needLoadingRequestCount === 0) {
        endLoading()
    }
}

Axios.defaults.timeout = 60000;
Axios.defaults.baseURL = 'http://127.0.0.1:18080/file/';


//http request 拦截器
Axios.interceptors.request.use(
    config => {
        if (config.method === 'post') {
            config.headers = {
                'Content-Type': 'application/json;charset=UTF-8'
            }
        } else if (config.method === 'get') {
            let newParams: any = {};
            for (let key in config.params) {
                newParams[key] = encodeURIComponent(config.params[key])
            }
            config.params = newParams;

            config.headers = {
                'Content-Type': 'application/json;charset=UTF-8'
            }
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
);

//http response 拦截器
Axios.interceptors.response.use(
    response => {
        let data = response.data;

        if (response.config.responseType == 'blob') {
            return Promise.resolve(response);
        }
        //隐藏等待框
        if (data.code === 0) {
            return Promise.resolve(data)
        } else {
            return Promise.reject(data)
        }
    },
    error => {
        Message.error("系统异常");
        return Promise.reject(error)
    }
);

/**
 * 封装get方法
 * @param url
 * @param params
 * @param showLoading
 * @returns {Promise}
 */
const get = function get(url: string, params = {}, showLoading = true) {
    return new Promise((resolve, reject) => {
        showLoading && showFullScreenLoading();//显示等待框
        Axios.get(url, {
            params: params
        })
            .then(response => {
                showLoading && tryHideFullScreenLoading(); //隐藏等待框
                resolve(response);
            })
            .catch(err => {
                showLoading && tryHideFullScreenLoading(); //隐藏等待框
                reject(err)
            })
    })
};


/**
 * 封装post请求
 * @param url
 * @param params
 * @param showLoading
 * @param showError
 * @param showSuccess
 * @returns {Promise}
 */

const post = function post(url: string, params = {}, showLoading = true, showError = true, showSuccess = false, config = {}) {
    return new Promise((resolve, reject) => {
        showLoading && showFullScreenLoading(); //显示等待框

        Axios.post(url, params, {})
            .then((response: any) => {
                showLoading && tryHideFullScreenLoading();//隐藏等待框
                if (showSuccess) {
                    Message.success(response.msg)
                }
                resolve(response);
            }, err => {
                showLoading && tryHideFullScreenLoading();//隐藏等待框
                if (showError) {
                    Message.error(err.msg);
                }
                reject(err)
            })
    })
};


export default {
    post,
    get
}