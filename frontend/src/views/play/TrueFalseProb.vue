<template>
  <b-container>
    <b-row>
      <b-col>{{ question }}</b-col>
    </b-row>
    <b-row>
      <b-col cols="6">O</b-col>
      <b-col cols="6">X</b-col>
    </b-row>
  </b-container>
</template>

<script>
export default {
  data() {
    return {
      name: "TrueFalseProb",
      question: "한국은 영어로 KOREA이다"
    };
  },
};
</script>

<style>
</style>