<script setup lang="ts">
import { ref, onMounted, Ref } from "vue";
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent, } from "echarts/components";
import { use } from "echarts/core";
import { GraphChart } from 'echarts/charts';
import { CanvasRenderer } from "echarts/renderers";
import Chart from "vue-echarts";
import type { ECBasicOption } from "echarts/types/dist/shared";

use([GridComponent, LegendComponent, TitleComponent, TooltipComponent, GraphChart, CanvasRenderer]);

interface GraphNode {
  symbolSize: number;
  label?: {
    show?: boolean;
  };
}

let hide = false;
var ROOT_PATH = "";

const chartOptions: Ref<ECBasicOption | null> = ref(null);

onMounted(async () => {
  try {
    const response = await fetch(ROOT_PATH + "/dep_example.json");
    const webkitDep = await response.json();
    
    // Dep
    chartOptions.value = {
      legend: {
        data: ['HTMLElement', 'WebGL', 'SVG', 'CSS', 'Other']
      },
      series: [
        {
          type: 'graph',
          layout: 'force',
          animation: false,
          label: {
            position: 'right',
            formatter: '{b}'
          },
          draggable: true,
          data: webkitDep.nodes.map(function (node: any, idx: any) {
            node.id = idx;
            return node;
          }),
          categories: webkitDep.categories,
          force: {
            edgeLength: 5,
            repulsion: 20,
            gravity: 0.2
          },
          edges: webkitDep.links
        }
      ]
    };

  } catch (error) {
    console.error(error);
  }
});
</script>

<template>
  <VRow>
    <VCol cols="12">
      <v-table>
        <template v-if="!hide">
          <Suspense>
            <Chart class="chart" :option="chartOptions" :autoresize="true"></Chart>
          </Suspense>
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
  height: 600px;
}
</style>
