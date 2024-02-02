import moment from 'moment'

const ACCOUNT = "account";
export const getAccount = () => {
    const account = window.sessionStorage.getItem(ACCOUNT);
    return JSON.parse(account) || {};
};

export const setAccount = (data) => {
    const sessionData = JSON.stringify(data);
    return window.sessionStorage.setItem(ACCOUNT, sessionData);
};

export const removeAccount = () => {
    return window.sessionStorage.removeItem(ACCOUNT);
};

export const formatDate = (date) => {
    return moment(date).format('YYYY-MM-DD HH:MM:DD')
}