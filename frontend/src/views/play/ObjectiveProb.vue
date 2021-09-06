<template>
  <div class="h-100">
    <div class="row h-50">
        <b-button id="one" variant="primary" class="col-sm-6" @click="submit($event)">1</b-button>
        <b-button id="two" variant="danger" class="col-sm-6" @click="submit($event)">2</b-button>
    </div>
    <div class="row h-50">
        <b-button id="three" variant="success" class="col-sm-6" @click="submit($event)">3</b-button>
        <b-button id="four" variant="warning" class="col-sm-6" @click="submit($event)">4</b-button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      name: "ObjectiveProb",
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
#one, #two, #three, #four {
  color: white;
  font-size: 100px;
  font-weight: bold;
}


</style>