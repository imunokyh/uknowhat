<template>
  <div id="app">
    <!--    
    <div id="nav">
      <router-link to="/">Home</router-link> |      
       <router-link to="/exam/probgrpmng">문제그룹</router-link> |      
      <router-link to="/exam/probreg">문제등록</router-link> |      
    </div>
    <div>
      <router-link to="/exam/waiting">대기화면</router-link> |
      <router-link to="/exam/tfprob">OX문제</router-link> |
      <router-link to="/exam/tfchoice">OX선택</router-link> |
      <router-link to="/exam/tfcheck">OX체크</router-link>
    </div>
    <div>
      <router-link to="/exam/waiting">대기화면</router-link> |
      <router-link to="/exam/obprob">객관문제</router-link> |
      <router-link to="/exam/obchoice">객관선택</router-link> |
      <router-link to="/exam/obcheck">객관체크</router-link>
    </div>
    -->
    <!--
    <div>
      <router-link to="/login/ppn">게임참가화면(핀번호)</router-link> |
      <router-link to="/login/nickname">닉네임입력</router-link> |
      <router-link to="/login/waiting">대기화면</router-link> |
    </div>
    <div>
      <router-link to="/play/tfprob">OX문제</router-link> |
      <router-link to="/play/objprob">4지선다</router-link> |
      <router-link to="/play/corans">정답</router-link> |
      <router-link to="/play/incorans">오답</router-link> |
      <router-link to="/play/timeup">시간초과</router-link> |
      <router-link to="/play/finalrank">최종순위</router-link> |
    </div>
    -->
    <router-view />
  </div>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}
</style>

