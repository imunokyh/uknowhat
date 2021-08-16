<template>
<b-container>
  당신의 최종순위는 {{rank}}입니다
</b-container>
</template>

<script>
export default {
  data() {
    return {
      name: 'YourFinalRank',
      rank: 1
    };
  },
};
</script>

<style>
</style>