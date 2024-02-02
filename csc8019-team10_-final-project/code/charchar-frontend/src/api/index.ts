import request from '@/util/serive'
import Url from './url'

//log in
export function login (params) {
  return request({
    url: Url.login,
    // method: 'get',
    method: 'post',
    // params
    data: {
      email: params.email,
      password: params.password
    }
  })
}
// export function login(params) {
//   return new Promise((resolve, reject) => {
//     request({
//       url: Url.login,
//       method: 'post',
//       data: {
//         email: params.email,
//         password: params.password
//       }
//     }).then(response => {
//       console.log(response) // 打印 response 对象
//       console.log(response.data) // 打印 response.data 数据

//       if (!response || !response.data) {
//         reject(new Error('Invalid response'))
//       }

//       const data = Object.fromEntries(response.data.entries())
//       const processedData = processData(data)
//       resolve(processedData)
//     }).catch(error => {
//       reject(error)
//     })
//   })
// }

// function processData(data) {
//   if (!data) {
//     return {}
//   }

//   const processedData = {}

//   for (const [key, value] of Object.entries(data)) {
//     processedData[key.toLowerCase()] = value
//   }

//   return processedData
// }

// sign up
export function register (data) {
  return request({
    url: Url.register,
    method: 'post',
    data:{
      username: data.username,
      email: data.email,
      password: data.password
    }
  })
}

// get all the users information
export function getAllUserInfo () {
  return request({
    url: Url.userInfo,
    method: 'get',
  })
}

// get one user information
export function getUserInformation (params) {
  return request({
    url: Url.getUserInformation,
    method: 'get',
    params
  })
}

// update one user information
export function updateOneUserInfomation (data) {
  if (!data.username) {
    alert('please enter your user name');
    return;
  }

  console.log(data);
  return request({
    url: Url.updateOneUserInfomation,
    method: 'post',
    data:{
      username: data.username,
      email: data.email
    }
    
  })
}

// update user password
export function updateUserPassword (data) {
  if (!data.password) {
    alert('Please fill in the password');
    return;
  }
  if (!data.verificationCode) {
    alert('Pleae fill in the verification code');
    return;
  }
  return request({
    url: Url.updateUserPassword,
    method: 'put',
    data:{
      email: data.email,
      password: data.password,
      verificationCode: data.verificationCode
    }
  })
}
// Send verification code
export function sendVerificationCode (data) {
  return request({
    url: Url.sendVerificationCode,
    method: 'post',
    data:{
      email: data.email
    }
  })
}

// Forgotten user password
export function forgetUserPassword (data) {
  return request({
    url: Url.forgetUserPassword,
    method: 'put',
    data:{
      email: data.email,
      verificationCode: data.verificationCode
    }
  })
}

// user logout
export function deleteUser (data) {
  return request({
    url: Url.deleteUser,
    method: 'delete',
    data:{
      email: data.email,
      password: data.password
    }
  })
}

