<script setup lang="ts">
import { ref, onMounted, Ref } from "vue";
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent, } from "echarts/components";
import { use } from "echarts/core";
import { TreeChart } from 'echarts/charts'
import { CanvasRenderer } from "echarts/renderers";
import Chart from "vue-echarts";
import type { ECBasicOption } from "echarts/types/dist/shared";

use([GridComponent, LegendComponent, TitleComponent, TooltipComponent, TreeChart, CanvasRenderer]);

let hide = false;
var ROOT_PATH = "";
const data = ref(null);

const chartOptions: Ref<ECBasicOption | null> = ref(null);

onMounted(async () => {
  try {
    const response = await fetch(ROOT_PATH + "/detailed_example.json");
    const jsonData = await response.json();

    jsonData.children.forEach(function (
      datum: { collapsed: boolean },
      index: number
    ) {
      index % 2 === 0 && (datum.collapsed = true);
    });

    data.value = jsonData;

    chartOptions.value = {
      tooltip: {
        trigger: "item",
        triggerOn: "mousemove",
      },
      series: [
        {
          type: "tree",
          data: [data.value],
          top: "1%",
          left: "7%",
          bottom: "1%",
          right: "20%",
          symbolSize: 7,
          label: {
            position: "left",
            verticalAlign: "middle",
            align: "right",
            fontSize: 16,
          },
          leaves: {
            label: {
              position: "right",
              verticalAlign: "middle",
              align: "left",
            },
          },
          emphasis: {
            focus: "descendant",
          },
          expandAndCollapse: true,
          animationDuration: 550,
          animationDurationUpdate: 750,
        },
      ],
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
