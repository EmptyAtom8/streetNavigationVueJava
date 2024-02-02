
const HomeModules = ({
  state: {
    userList: {
      // username: 'Tom'
    },
  },
  mutations: {
    USER_INFO(state: any, userList: any) {
      state.userList = userList;
    }
  },
  actions: {

  },
  modules: {
  }
})
export default HomeModules
