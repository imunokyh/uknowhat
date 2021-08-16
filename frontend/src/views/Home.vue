<template>
  <div style="height: 768px;">
    <b-jumbotron class="h-100" bg-variant="dark" text-variant="white" border-variant="dark">
      <template #header>Uknowhat</template>

      <template #lead>
        This is problem solving and play with each others.
      </template>

      <hr class="my-4" />

      <p>Let's join together and play.</p>
      <b-button size="lg" variant="primary" href="#">참가</b-button>
    </b-jumbotron>
  </div>
</template>

<script>
import Vue from "vue";
import { BJumbotron } from "bootstrap-vue";
Vue.component("b-jumbotron", BJumbotron);

export default {
  name: "Home",
};
</script>
