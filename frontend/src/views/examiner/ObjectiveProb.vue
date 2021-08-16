<template>
  <!-- 게임 플레이 화면 (4지 선다 문제 표시) -->
  <b-container>
    
    {{prob}}
  </b-container>
</template>

<script>
export default {
  data(){
    return{
      name: 'ObjectiveProb',
      prob: '다음중 광운UBF의 위치는?'
    }
  }

}
</script>

<style>

</style>