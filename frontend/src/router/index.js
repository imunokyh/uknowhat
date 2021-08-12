import Vue from 'vue'
import VueRouter from 'vue-router'
import HelloWorld from '@/components/HelloWorld'

import Login from '../views/Login.vue'
import ObjectiveQuestion from '../views/ObjectiveQuestion.vue'
import SubjectiveQuestion from '../views/SubjectiveQuestion.vue'
import TrueFalseQuestion from '../views/TrueFalseQuestion.vue'
import Basic from '../views/Basic.vue'
import Carousel from '../views/Carousel.vue'

Vue.use(VueRouter)

const routes = [
    {
      path: '/',
      name: 'HelloWorld',
      component: HelloWorld
    }, {
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
    },  {
      path: '/basic',
      name: 'Basic',
      component: Basic
    },  {
      path: '/carousel',
      name: 'Carousel',
      component: Carousel
    }
  ];

  const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
  })
  
  export default router
