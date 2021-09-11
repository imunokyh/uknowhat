<template>
  <div>
    <div>
      <hr>
      Child counter : {{ childCounter }} <br>
      <button @click="addCounter">+</button>
      <button @click="subCounter">-</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Child',
  computed: {
    childCounter () {
      return this.$store.getters.getCounter
    }
  },
  methods: {
    // 이벤트 추가
    addCounter () {
      this.$store.commit('addCounter')
    },
    subCounter () {
      // this.$store.state.counter--
      this.$store.dispatch('subCounter')
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>