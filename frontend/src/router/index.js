import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import ObjectiveQuestion from '../views/ObjectiveQuestion.vue'
import SubjectiveQuestion from '../views/SubjectiveQuestion.vue'
import TrueFalseQuestion from '../views/TrueFalseQuestion.vue'
// login
import Participation from '../views/login/Participation.vue'
import Nickname from '../views/login/Nickname.vue'
import Waiting from '../views/login/Waiting.vue'
// play
import TrueFalseProb from '../views/play/TrueFalseProb.vue'
import ObjectiveProb from '../views/play/ObjectiveProb.vue'
import CorrectAns from '../views/play/CorrectAns.vue'
import IncorrectAns from '../views/play/IncorrectAns.vue'
import FinalRank from '../views/play/FinalRank.vue'
import TimeUp from '../views/play/TimeUp.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  }, {
    path: '/about',
    name: 'About',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
  },  {
    path: '/login',
    name: 'Login',
    component: Login
  },  {
    path: '/oqsn',
    name: 'ObjectiveQuestion',
    component: ObjectiveQuestion
  },  {
    path: '/sqsn',
    name: 'SubjectiveQuestion',
    component: SubjectiveQuestion
  },  {
    path: '/tfqsn',
    name: 'TrueFalseQuestion',
    component: TrueFalseQuestion
  } 
  // login
  , {
    path: '/login/ppn',
    name: 'Participation',
    component: Participation
  }, {
    path: '/login/nickname',
    name: 'Nickname',
    component: Nickname
  }, {
    path: '/login/waiting',
    name: 'Waiting',
    component: Waiting
  }
    // play
   , {
    path: '/play/tfprob',
    name: 'TrueFalseProb',
    component: TrueFalseProb
  }  , {
    path: '/play/objprob',
    name: 'ObjectiveProb',
    component: ObjectiveProb
  }, {
    path: '/play/corans',
    name: 'CorrectAns',
    component: CorrectAns
  }, {
    path: '/play/incorans',
    name: 'IncorrectAns',
    component: IncorrectAns
  }, {
    path: '/play/timeup',
    name: 'TimeUp',
    component: TimeUp
  }, {
    path: '/play/finalrank',
    name: 'FinalRank',
    component: FinalRank
  }

]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
