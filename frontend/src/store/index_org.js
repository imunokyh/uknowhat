import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate';
import  roomStore  from './roomStore';

Vue.use(Vuex)

export const store = new Vuex.Store({
    // ref: https://question0.tistory.com/16
    state: { // 공통 관리되는 상태값을 관리
        counter: 0
    },
    getters: { // 공유되는 상태 값을 조회 로직을 관리
        getCounter(state) {
            return state.counter
        }
    },
    mutations: { // 상태 값을 변경하는 로직을 관리
        addCounter: function (state, payload) {
            console.log(payload)
            state.counter++
        },
        subCounter: function (state, payload) {
            state.counter--
        },
        doubleCounter: function (state, paload) {
            state.counter = state.counter * 2
        }
    },
    actions: { // 비동기 통신 및 동작을 정의하고 관리
        subCounter: function ({ commit, state }, payload) {
            return commit('subCounter')
        },
        asyncAddCounter: function (context, palyload) {
            return setTimeout(() => {
                context.commit('addCounter')
            }, 1000)
        }
    },
    modules: {
        roomStore: roomStore,
    },
    plugins: [
        createPersistedState({
            paths: ['roomStore'],
        })
    ]
})