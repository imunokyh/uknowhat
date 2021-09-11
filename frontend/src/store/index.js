import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate';
import  roomStore  from './roomStore';
import  counterStore  from './counterStore';

Vue.use(Vuex)

export const store = new Vuex.Store({
    
    modules: {
        roomStore: roomStore,
        counterStore: counterStore,
    },
    plugins: [
        createPersistedState({
            paths: ['roomStore'],
        })
    ]
})