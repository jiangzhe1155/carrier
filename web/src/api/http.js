import Axios from 'axios'
import {Message, Loading} from 'element-ui';

let loading;        //定义loading变量

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


Axios.interceptors.request.use(
  config => {
    if (config.method === 'post') {
      config.headers = {
        'Content-Type': 'application/json;charset=UTF-8'
      }
    } else if (config.method === 'get') {
      let newParams = {};
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


Axios.interceptors.response.use(
  response => {
    let data = response.data;
    if (response.config.responseType === 'blob') {
      return Promise.resolve(response);
    }
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

export function post(url, params = {}, showLoading = true, config = {}) {
  return new Promise((resolve, reject) => {
    showLoading && showFullScreenLoading(); //显示等待框
    Axios.post(url, params, {})
      .then((response) => {
        showLoading && tryHideFullScreenLoading();//隐藏等待框
        if (showLoading) {
          Message.success(response.msg)
        }
        resolve(response);
      }, err => {
        showLoading && tryHideFullScreenLoading();//隐藏等待框
        Message.error(err.msg);
        reject(err)
      })
  })
}

