<template>
 <div class="text-white bg-dark" style="height: 768px">
    <!-- 게임 플레이 화면 (OX 문제 정답 인원) -->
    <b-container>
      <b-row>&nbsp;</b-row>
      <b-row>&nbsp;</b-row>
      <b-row>
        <b-col></b-col>
        <b-col ><h2 class="text-success">참 거짓 문제</h2></b-col>
        <b-col></b-col>
      </b-row>
      <b-row>
        <b-col></b-col>
        <b-col></b-col>
        <b-col><b-button variant="info">다음</b-button></b-col>
      </b-row>
          <b-row>
      <b-col cols="2"></b-col>
      <b-col cols="8"><h1>{{ prob }}</h1></b-col>
      <b-col cols="2"></b-col>
      
    </b-row>
   
    </b-container>
    
  </div>
</template>


<script>
export default {
  data() {
    return {
      name: "TrueFalseProb",
      prob: "하나님께서 천지를 창조하셨다",
    };
  },
};
</script>

<style scoped>
/* Custom default button */
.btn-secondary,
.btn-secondary:hover,
.btn-secondary:focus {
  color: #333;
  text-shadow: none; /* Prevent inheritance from `body` */
}

/*
 * Base structure
 */

body {
  text-shadow: 0 0.05rem 0.1rem rgba(0, 0, 0, 0.5);
  box-shadow: inset 0 0 5rem rgba(0, 0, 0, 0.5);
}

.cover-container {
  max-width: 42em;
}

/*
 * Header
 */

.nav-masthead .nav-link {
  padding: 0.25rem 0;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.5);
  background-color: transparent;
  border-bottom: 0.25rem solid transparent;
}

.nav-masthead .nav-link:hover,
.nav-masthead .nav-link:focus {
  border-bottom-color: rgba(255, 255, 255, 0.25);
}

.nav-masthead .nav-link + .nav-link {
  margin-left: 1rem;
}

.nav-masthead .active {
  color: #fff;
  border-bottom-color: #fff;
}

.bd-placeholder-img {
  font-size: 1.125rem;
  text-anchor: middle;
  -webkit-user-select: none;
  -moz-user-select: none;
  user-select: none;
}

@media (min-width: 768px) {
  .bd-placeholder-img-lg {
    font-size: 3.5rem;
  }
}
</style>