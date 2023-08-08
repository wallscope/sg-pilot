<script setup lang="ts">
import { useAllDocsStore } from '@/stores/alldocs';
import { ref, onMounted, Ref } from "vue";
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent, } from "echarts/components";
import { use } from "echarts/core";
import { TreeChart } from 'echarts/charts'
import { CanvasRenderer } from "echarts/renderers";
import Chart from "vue-echarts";
import type { ECBasicOption } from "echarts/types/dist/shared";
import { useRoute } from 'vue-router';

use([GridComponent, LegendComponent, TitleComponent, TooltipComponent, TreeChart, CanvasRenderer]);

const route = useRoute()
const allDocsStore = useAllDocsStore()

let hide = false;
const data: Ref<null | { name: any; children: any }> = ref(null);
const chartOptions: Ref<ECBasicOption | undefined> = ref(undefined);

onMounted(async () => {
  try {
    const uri = route.query.uri as string
    const [docType, id] = uri.split('/')

    const jsonData = await allDocsStore.fetchDetailedDocGraph(docType, id)
    const graphTitle = jsonData.children[0].name

    const graphData = {
      name: "",
      children: jsonData.children[0].children
    };

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
