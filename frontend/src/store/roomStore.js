// https://kyounghwan01.github.io/blog/Vue/vuex/vuex-persistedstate/#vuex-persistedstate-%E1%84%8B%E1%85%B5%E1%86%AF%E1%84%87%E1%85%AE%E1%84%86%E1%85%A1%E1%86%AB-%E1%84%8C%E1%85%A5%E1%84%8C%E1%85%A1%E1%86%BC
// https://uxgjs.tistory.com/207

const state = {
    id: 1
}

const getters = {
    getRoomId(state){
        return state.id
    }
}

const mutations = {
    updateRoomId(state, id) {
        state.id = id
    }
}

const actions = {
    asyncUpdateRoomId({ commit, state }, data) {
        commit('updateRoomId', data);
    }
}

export default {
    strict: process.env.NODE_ENV !== 'production',
    state: {
        ...state
    },
    getters,
    mutations,
    actions
}
