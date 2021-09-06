<template>
  <div class="row h-100">
    <b-button id="true" variant="primary" class="col-sm-6" @click="submit($event)">O</b-button>
    <b-button id="false" variant="danger" class="col-sm-6" @click="submit($event)">X</b-button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      name: "TrueFalseProb"
    };
  },
  methods: {
    submit(event) {
      console.log(event.target.id);
    },
  },
};
</script>

<style>
#true, #false {
  font-size: 100px;
  font-weight: bold;
}
</style>