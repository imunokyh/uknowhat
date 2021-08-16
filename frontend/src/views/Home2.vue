<template>
  <div>
    <b-jumbotron>
     <template #header>Uknowhat</template>

    <template #lead>
      This is problem solving and play with each others.
    </template>

    <hr class="my-4">

    <p>
      Let's join together and play.
    </p>

      <b-button variant="primary" href="#">Do Something</b-button>
      <b-button variant="success" href="#">Do Something Else</b-button>
    </b-jumbotron>
  </div>
</template>

<script>
import Vue from "vue";
import { BJumbotron } from "bootstrap-vue";
Vue.component("b-jumbotron", BJumbotron);

export default {
  name: "Home2",
};
</script>
