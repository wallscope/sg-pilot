<template>
  <VRow>
    <VCol cols="12">
      <v-table>
        <div class="search-bar">
          <input v-model="searchTerm" placeholder="Search..." />
        </div>
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

<script setup lang="ts">
// import { usePfgStore } from '@/stores/pfg';
// import { useBpStore } from '@/stores/bp';
import { useAllDocsStore } from '@/stores/alldocs';
import { ref, onMounted, Ref, computed, watch, } from "vue";
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent, } from "echarts/components";
import { use } from "echarts/core";
import { GraphChart } from 'echarts/charts';
import { CanvasRenderer } from "echarts/renderers";
import Chart from "vue-echarts";
import type { ECBasicOption } from "echarts/types/dist/shared";
import { useRouter } from "vue-router";
import { ForcedGraph } from "@/stores/alldocs";

use([GridComponent, LegendComponent, TitleComponent, TooltipComponent, GraphChart, CanvasRenderer]);

interface GraphNode {
  symbolSize: number;
  label?: {
    show?: boolean;
  };
}

// const pfgStore = usePfgStore()
// const bpStore = useBpStore()
const allDocsStore = useAllDocsStore()
let hide = false;
// var ROOT_PATH = "";
const router = useRouter();

const searchTerm = ref('');
const debouncedSearchTerm = refDebounced(searchTerm, 500); // Debounce for 2 seconds
const jsonData = ref<null | ForcedGraph>(null);

const fetchData = async () => {
  try {
    const outcomes = router.currentRoute.value.query.outcomes as string[];
    const searchTermValue = debouncedSearchTerm.value;
    let graph = null;
    // console.log("searchTermValue: ", searchTermValue)
    if (outcomes !== undefined && outcomes.length > 0 ) {
      // console.log("outcomes: ", outcomes)
      graph = await allDocsStore.fetchDocsForcedNpfList(outcomes, searchTermValue) as ForcedGraph;
    } else {
      // console.log("No outcomes")
      graph = await allDocsStore.fetchAllDocsForcedGraph(searchTermValue) as ForcedGraph;
    }

    graph.nodes.forEach(function (node: GraphNode) {
      node.label = {
        show: node.symbolSize > 30
      };
    });

    jsonData.value = graph;

  } catch (error) {
    console.error(error);
  }
};

watch(debouncedSearchTerm, () => {
  fetchData();
});

const chartOptions: Ref<ECBasicOption | undefined> = computed(() => {
  const graph = jsonData.value;
  
  if (!graph) {
    return {};
  }

  return {
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
});

onMounted(() => {
  fetchData();
});
</script>

<style lang="scss" scoped>
.chart {
  height: 820px;
}
</style>
