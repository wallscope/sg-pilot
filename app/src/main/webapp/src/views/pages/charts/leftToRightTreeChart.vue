<script setup lang="ts">
import { usePfgStore } from '@/stores/pfg';
import { useBpStore } from '@/stores/bp';
import { ref, onMounted, Ref } from "vue";
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent, } from "echarts/components";
import { use } from "echarts/core";
import { TreeChart } from 'echarts/charts'
import { CanvasRenderer } from "echarts/renderers";
import Chart from "vue-echarts";
import type { ECBasicOption } from "echarts/types/dist/shared";

use([GridComponent, LegendComponent, TitleComponent, TooltipComponent, TreeChart, CanvasRenderer]);

const pfgStore = usePfgStore()
const bpStore = useBpStore()
let hide = false;
const data: Ref<null | { name: any; children: any }> = ref(null);
const chartOptions: Ref<ECBasicOption | undefined> = ref(undefined);

onMounted(async () => {
  try {
    // PFG Doc test
     const jsonData = await pfgStore.fetchPfgDocDetailedGraph()
    // const jsonData = await pfgStore.fetchPfgAuxDetailedGraph()

    // BP Doc test
    // const jsonData = await bpStore.fetchBpDocDetailedGraph()
    //const jsonData = await bpStore.fetchBpComDetailedGraph()

    // Extract the content of the children array to hide the uri from the graph
    // const graphData = {
    //   name: jsonData.children[0].name,
    //   children: jsonData.children[0].children
    // };

    const graphTitle = jsonData.children[0].name

    const graphData = {
      name: "",
      children: jsonData.children[0].children
    };

    console.log("pfgdoc data shown: ", graphData)

    graphData.children.forEach(function (
        datum: { collapsed: boolean },
        index: number
      ) {
        index % 2 === 0 && (datum.collapsed = true);
      });

    data.value = graphData;

    chartOptions.value = {
  tooltip: {
    trigger: "item",
    triggerOn: "mousemove",
  },
  title: {
    text: graphTitle,
    left: "center",
    top: "1%",  // Adjust the top position
    textStyle: {
      fontSize: 18,
      fontWeight: "bold",
    },
  },
  series: [
    {
      type: "tree",
      data: [data.value],
      top: "10%",  // Adjust the top position to provide space for the title
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
