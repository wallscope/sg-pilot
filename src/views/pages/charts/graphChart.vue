<script setup lang="ts">
// import { Measurement, MeasurementType, useMeasurementStore } from '@/stores/measurements';
// import { useMetadataStore } from '@/stores/metadata';
// import { useSummaryStore } from '@/stores/summary';
import { BarChart } from "echarts/charts";
import {
GridComponent, LegendComponent, TitleComponent,
TooltipComponent
} from "echarts/components";
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import type { ECBasicOption } from "echarts/types/dist/shared";
import { ref, type Ref } from "vue";
import Chart from "vue-echarts";

use([
  CanvasRenderer,
  BarChart,
  GridComponent,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
]);

// let hide = true;
let hide = false;

// Chart
type ChartOptions = ECBasicOption & { xAxis: any; yAxis: any };

</script>
<template>
  <VRow>
    <VCol
      cols="12"
    >
      <v-table>
        <template v-if="!hide">
          <!-- <Suspense> -->
            <!-- <chart class="chart" :option="tempChart" :autoresize="true"></chart> -->
            graph chart
          <!-- </Suspense> -->
        </template>
        <template v-else>
          <p>No activity to show</p>
        </template>
      </v-table>
    </VCol>
  </VRow>
  <p> 
    <hr>
  </p>
</template>
<style lang="scss" scoped>
.chart {
  height: 300px;
}
</style>
