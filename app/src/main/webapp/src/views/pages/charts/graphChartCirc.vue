<script setup lang="ts">
import { useAllDocsStore } from '@/stores/alldocs';
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

const allDocsStore = useAllDocsStore()
let hide = false;
var ROOT_PATH = "";

const chartOptions: Ref<ECBasicOption | undefined> = ref(undefined);

onMounted(async () => {
  try {
    // const response = await fetch(ROOT_PATH + "/graph_example.json");
    // const graph = await response.json();

    const graph = await allDocsStore.fetchAllDocsForcedGraph()

    graph.nodes.forEach(function (node: GraphNode) {
      node.label = {
        show: node.symbolSize > 30
      };
    });

    // Circular
    chartOptions.value = {
      title: {
        text: 'Circular Big graph',
        subtext: 'Circular Layout',
        top: 'bottom',
        left: 'right'
      },
      tooltip: {},
      legend: [
        {
          data: graph.categories.map(function (a: any) {
            return a.name;
          })
        }
      ],
      animationDuration: 200,
      animationEasingUpdate: 'quinticInOut',
      series: [
        {
          type: 'graph',
          layout: 'circular',
          circular: {
            rotateLabel: true
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


    // Force
    // chartOptions.value = {
    //   title: {
    //     text: 'Les Miserables',
    //     subtext: 'Default layout',
    //     top: 'bottom',
    //     left: 'right'
    //   },
    //   tooltip: {},
    //   legend: [
    //     {
    //       // selectedMode: 'single',
    //       data: graph.categories.map(function (a: { name: string }) {
    //         return a.name;
    //       })
    //     }
    //   ],
    //   animationDuration: 200,
    //   animationEasingUpdate: 'quinticInOut',
    //   series: [
    //     {
    //       name: "Les Miserables",
    //       type: "graph",
    //       layout: "force",
    //       force: {
    //         repulsion: 200,
    //         edgeLength: 400,
    //       },
    //       data: graph.nodes,
    //       links: graph.links,
    //       categories: graph.categories,
    //       roam: true,
    //       label: {
    //         position: 'right',
    //         formatter: '{b}'
    //       },
    //       lineStyle: {
    //         color: 'source',
    //         curveness: 0.3
    //       },
    //       emphasis: {
    //         focus: 'adjacency',
    //         lineStyle: {
    //           width: 10
    //         }
    //       }
    //     }
    //   ]
    // };

    //Preassigned x and y
    // chartOptions.value = {
    //   title: {
    //     text: 'Les Miserables',
    //     subtext: 'Default layout',
    //     top: 'bottom',
    //     left: 'right'
    //   },
    //   tooltip: {},
    //   legend: [
    //     {
    //       // selectedMode: 'single',
    //       data: graph.categories.map(function (a: { name: string }) {
    //         return a.name;
    //       })
    //     }
    //   ],
    //   animationDuration: 200,
    //   animationEasingUpdate: 'quinticInOut',
    //   series: [
    //     {
    //       name: 'Les Miserables',
    //       type: 'graph',
    //       layout: 'none',
    //       data: graph.nodes,
    //       links: graph.links,
    //       categories: graph.categories,
    //       roam: true,
    //       label: {
    //         position: 'right',
    //         formatter: '{b}'
    //       },
    //       lineStyle: {
    //         color: 'source',
    //         curveness: 0.3
    //       },
    //       emphasis: {
    //         focus: 'adjacency',
    //         lineStyle: {
    //           width: 10
    //         }
    //       }
    //     }
    //   ]
    // };
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
