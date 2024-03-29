import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import HelloWorld from '../components/HelloWorld.vue'
import Login from '../views/Login.vue'
import Signup from '../views/Signup.vue'
// play
import TrueFalseProb from '../views/play/TrueFalseProb.vue'
import ObjectiveProb from '../views/play/ObjectiveProb.vue'
import CorrectAns from '../views/play/CorrectAns.vue'
import IncorrectAns from '../views/play/IncorrectAns.vue'
import FinalRank from '../views/play/FinalRank.vue'
import TimeUp from '../views/play/TimeUp.vue'
import PtEnter from '../views/play/PtEnter.vue'
import Chat from '../views/play/Chat.vue'
import PlayMain from '../views/play/PlayMain.vue'
import PlayChat from '../views/play/PlayChat.vue'
// 출제자
import WaitingProc from '../views/examiner/WaitingProc.vue'
import TrueFalseProbProc from '../views/examiner/TrueFalseProbProc.vue'
import TrueFalseChoiceProc from '../views/examiner/TrueFalseChoiceProc.vue'
import TrueFalseCheckProc from '../views/examiner/TrueFalseCheckProc.vue'

import ObjectiveProbProc from '../views/examiner/ObjectiveProbProc.vue'
import ObjectiveChoiceProc from '../views/examiner/ObjectiveChoiceProc.vue'
import ObjectiveCheckProc from '../views/examiner/ObjectiveCheckProc.vue'

import ProbReg from '../views/examiner/ProbReg.vue'
import ProbBank from '../views/examiner/ProbBank.vue'
import RoomMng from '../views/examiner/RoomMng.vue'
import EmMain from '../views/examiner/EmMain.vue'
import ExamChat from '../views/examiner/ExamChat.vue'
import RoomProc from '../views/examiner/RoomProc.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },{
    path: '/h',
    name: 'HelloWorld',
    component: HelloWorld
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
    path: '/signup',
    name: 'Signup',
    component: Signup
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
  }, {
    path: '/play/enter',
    name: 'PtEnter',
    component: PtEnter
  }, {
    path: '/play/main',
    name: 'PlayMain',
    component: PlayMain,
    props: true
  }, {
    path: '/play/chat',
    name: 'Chat',
    component: Chat,
    props: true
  }, {
    path: '/play/playchat',
    name: 'PlayChat',
    component: PlayChat,
    props: true
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
  } , {
    path: '/exam/probbank',
    name: 'ProbBank',
    component: ProbBank,
    props: true
  }  , {
    path: '/exam/roommng',
    name: 'RoomMng',
    component: RoomMng
  }, {
    path: '/exam/main',
    name: 'EmMain',
    component: EmMain
  }, {
    path: '/exam/examchat',
    name: 'ExamChat',
    component: ExamChat,
    props: true
  }, {
    path: '/exam/roomproc',
    name: 'RoomProc',
    component: RoomProc,
    props: true
  }

]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
