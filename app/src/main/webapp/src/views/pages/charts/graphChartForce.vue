<script setup lang="ts">
import { usePfgStore } from '@/stores/pfg';
import { useBpStore } from '@/stores/bp';
import { useAllDocsStore } from '@/stores/alldocs';
import { ref, onMounted, Ref } from "vue";
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent, } from "echarts/components";
import { use } from "echarts/core";
import { GraphChart } from 'echarts/charts';
import { CanvasRenderer } from "echarts/renderers";
import Chart from "vue-echarts";
import type { ECBasicOption } from "echarts/types/dist/shared";
import { useRouter } from "vue-router";

use([GridComponent, LegendComponent, TitleComponent, TooltipComponent, GraphChart, CanvasRenderer]);

interface GraphNode {
  symbolSize: number;
  label?: {
    show?: boolean;
  };
}

const pfgStore = usePfgStore()
const bpStore = useBpStore()
const allDocsStore = useAllDocsStore()
let hide = false;
var ROOT_PATH = "";
const router = useRouter();

const chartOptions: Ref<ECBasicOption | undefined> = ref(undefined);

  onMounted(async () => {
  try {
  
    const outcomes = router.currentRoute.value.query.outcomes as string[]
    let graph = null

    if (outcomes !== undefined && outcomes.length > 0 ) {
      graph = await allDocsStore.fetchDocsForcedNpfList(outcomes)
    } else {
      graph = await allDocsStore.fetchAllDocsForcedGraph()
    }

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
          }),
          selected: graph.categories.reduce(function (obj: { [x: string]: boolean; }, item: { name: string | number; }) {
            obj[item.name] = false;
            return obj;
          }, {})
        }
      ],
      series: [
        {
          name: "",
          type: "graph",
          layout: "force",
          force: {
            repulsion: 700,
            edgeLength: 200,
          },
          data: graph.nodes,
          links: graph.links,
          categories: graph.categories,
          roam: true,
          label: {
            position: 'right',
            // formatter: '{b}'
            formatter: function(params: { name: string; }) {
              return params.name.length > 20 ? params.name.substring(0, 20) + '...' : params.name;
            }
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
  height: 820px;
}
</style>
