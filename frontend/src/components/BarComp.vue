<script>
// ref: https://ayoteralab.tistory.com/entry/Vuejs-14-use-Vue-chart-plugin
import { Bar, mixins } from "vue-chartjs";
import ChartDataLabels from 'chartjs-plugin-datalabels';

Chart.plugins.register(ChartDataLabels);

export default {
  name: "bar-comp",
  extends: Bar,
  mixins: [mixins.reactiveProp],
  props: ["chartData", "chartOptions"],
  mounted() {
    this.renderChart(this.chartData, this.chartOptions);
  },
  watch: {
    chartOptions() {
      this.renderChart(this.chartData, this.chartOptions);
    },
  },
};
</script>

<style>
</style>