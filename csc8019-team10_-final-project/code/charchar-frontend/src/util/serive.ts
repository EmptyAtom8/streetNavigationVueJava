import axios from "axios"

// }
const serive=axios.create({
  // baseURL:serveurl
  // baseURL: ''
  baseURL: process.env.VUE_APP_API
});
// Adding a request interceptor
serive.interceptors.request.use(function (config) {
    // do something before sending a request
    return config;
  }, function (error) {
    // What to do about request errors
    return Promise.reject(error);
  });

// Adding a response interceptor
serive.interceptors.response.use(function (response) {
    return response.data;
  }, function (error) {
    switch (error?.response?.status) {
      case 404:
      alert("request wrong address!")
        break;
        case 500:
      alert("Server connection failed, please try again later")
        break;
      default:
        alert("Unknown error")
        break;
    }
    return Promise.reject(error);
  });

  export default serive