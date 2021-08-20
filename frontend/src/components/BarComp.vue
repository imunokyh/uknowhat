<script>
// ref: https://ayoteralab.tistory.com/entry/Vuejs-14-use-Vue-chart-plugin
import { Bar, mixins } from "vue-chartjs";
export default {
  extends: Bar,
  mixins: [mixins.reactiveProp],
  props: ["options"],
  mounted() {
    this.renderChart(this.chartData, this.options);
  },
};
</script>

<style>
</style>