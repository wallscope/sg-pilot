<script setup lang="ts">
import { usePfgStore } from '@/stores/pfg';
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

const pfgStore = usePfgStore()
let hide = false;
var ROOT_PATH = "";

const chartOptions: Ref<ECBasicOption | undefined> = ref(undefined);

  onMounted(async () => {
  try {
    // const response = await fetch(ROOT_PATH + "/graph_example.json");
    // const graph = await response.json();

    // const graph = await pfgStore.fetchPfgDocForcedGraph()
    const graph = await pfgStore.fetchPfgDocForcedGraphAll()

    graph.nodes.forEach(function (node: GraphNode) {
      node.label = {
        show: node.symbolSize > 30
      };
    });

    // Force
    chartOptions.value = {
      animation: false,
      title: {
        text: 'Big graph',
        subtext: 'Default layout',
        top: 'bottom',
        left: 'right'
      },
      tooltip: {},
      legend: [
        {
          // selectedMode: 'single',
          data: graph.categories.map(function (a: { name: string }) {
            return a.name;
          })
        }
      ],
      series: [
        {
          name: "",
          type: "graph",
          layout: "force",
          force: {
            repulsion: 800,
            edgeLength: 300,
          },
          data: graph.nodes,
          links: graph.links,
          categories: graph.categories,
          roam: true,
          label: {
            position: 'right',
            formatter: '{b}'
          },
          lineStyle: {
            color: 'source',
            curveness: 0.3
          },
          emphasis: {
            focus: 'adjacency',
            lineStyle: {
              width: 10
            }
          }
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
