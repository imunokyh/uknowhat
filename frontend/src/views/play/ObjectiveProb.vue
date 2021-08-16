<template>
 <b-container>
    <b-row>
      <b-col>{{ question }}</b-col>
    </b-row>
    <b-row>
      <b-col cols="6">{{ans1}}</b-col>
      <b-col cols="6">{{ans2}}</b-col>
    </b-row>
    <b-row>
      <b-col cols="6">{{ans3}}</b-col>
      <b-col cols="6">{{ans4}}</b-col>
    </b-row>
  </b-container>  
</template>

<script>
export default {
  data() {
    return {
      name: "ObjectivePro",
      question: "다음은 나다윗의 영어 이름은?",
      ans1: "Paul",
      ans2: "Queen",
      ans3: "David",
      ans4: "Matthew"
    };
  },
};
</script>

<style>

</style>