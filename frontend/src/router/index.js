import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Home2 from '../views/Home2.vue'
import Home3 from '../views/Home3.vue'
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

// 출제자
import WaitingProc from '../views/examiner/WaitingProc.vue'
import TrueFalseProbProc from '../views/examiner/TrueFalseProbProc.vue'
import TrueFalseChoiceProc from '../views/examiner/TrueFalseChoiceProc.vue'
import TrueFalseCheckProc from '../views/examiner/TrueFalseCheckProc.vue'

import ObjectiveProbProc from '../views/examiner/ObjectiveProbProc.vue'
import ObjectiveChoiceProc from '../views/examiner/ObjectiveChoiceProc.vue'
import ObjectiveCheckProc from '../views/examiner/ObjectiveCheckProc.vue'

import ProbReg from '../views/examiner/ProbReg.vue'
import ProbGrpMng from '../views/examiner/ProbGrpMng.vue'


Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },{
    path: '/h2',
    name: 'Home2',
    component: Home2
  }, {
    path: '/h3',
    name: 'Home3',
    component: Home3
  },{
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
  // 출제자
  , {
    path: '/exam/waiting',
    name: 'WaitingProc',
    component: WaitingProc
  }, {
    path: '/exam/tfprob',
    name: 'TrueFalseProbProc',
    component: TrueFalseProbProc
  }, {
    path: '/exam/tfchoice',
    name: 'TrueFalseChoiceProc',
    component: TrueFalseChoiceProc
  }, {
    path: '/exam/tfcheck',
    name: 'TrueFalseCheckProc',
    component: TrueFalseCheckProc
  }, {
    path: '/exam/obprob',
    name: 'ObjectiveProbProc',
    component: ObjectiveProbProc
  }, {
    path: '/exam/obchoice',
    name: 'ObjectiveChoiceProc',
    component: ObjectiveChoiceProc
  }, {
    path: '/exam/obcheck',
    name: 'ObjectiveCheckProc',
    component: ObjectiveCheckProc
  }, {
    path: '/exam/probreg',
    name: 'ProbReg',
    component: ProbReg,
    props: true
  }  , {
    path: '/exam/probgrpmng',
    name: 'ProbGrpMng',
    component: ProbGrpMng
  }

]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
