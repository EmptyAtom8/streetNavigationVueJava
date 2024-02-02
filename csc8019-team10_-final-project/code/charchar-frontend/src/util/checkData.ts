export function checkEmail(value){
    const regEmail = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/;
    return regEmail.test(value)?true:false;
}
export function checkPassword(value){
    const regPass =/^[0-9a-z]{6,16}$/;
    return regPass.test(value)?true:false;
}