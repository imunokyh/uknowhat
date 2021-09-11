<template>
  <div class="hello">
    Parent counter : {{ getCounter }} <br>
    <!--
    Parent counter : {{ this.$store.state.counter }} <br>-->
    <button @click="addCounter">+</button>
    <button @click="subCounter">-</button>
    <button @click="doubleCounter">*</button>
    <button @click="asyncAddCounter">~+</button>

    <!-- Child 컴포넌트를 등록하고 counter 데이터 속성을 props 로 전달한다. -->
    <!-- <child v-bind:passedCounter="counter"></child> -->
    <child></child>
  </div>
</template>

<script>
import Child from './Child.vue'
import {mapGetters, mapMutations, mapActions} from 'vuex'

export default {
  computed: {
    ...mapGetters([
      'getCounter'
    ])
  },
  methods: {
    // 이벤트 추가
    addCounter () {
      this.$store.commit('addCounter')
    },
    subCounter () {
      // this.$store.state.counter--
      this.$store.dispatch('subCounter')
    },
    ...mapMutations([
      'doubleCounter'
    ]),
    ...mapActions([
      'asyncAddCounter'
    ])
  },
  components: {
    // Child 컴포넌트를 하위 컴포넌트로 등록
    'child': Child
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>