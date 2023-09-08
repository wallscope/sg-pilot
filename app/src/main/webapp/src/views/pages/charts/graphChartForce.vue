<template>
  <VRow>
    <VCol cols="12">
      <v-table>
        <template v-if="!hide">
          <!-- Directorate search -->
          <div class="Tag-container">
            <v-chip v-for="dir in dirs" :key="dir" class="ma-1" @click="removeDirectorate(dir)">
                {{ dir }}
                <v-icon small>mdi-close</v-icon>
              </v-chip>
              <input v-model="newDirectorate" @keydown.enter="addDirectorate" placeholder="Add a Directorate" class="tag-input" />
          </div>
          <!-- Term search -->
          <div class="Tag-container">
            <v-chip v-for="tag in tags" :key="tag" class="ma-1" @click="removeTag(tag)">
              {{ tag }}
              <v-icon small>mdi-close</v-icon>
            </v-chip>
            <input v-model="newTag" @keydown.enter="addTag" placeholder="Add a tag" class="tag-input" />
          </div>
          <Suspense>
            <Chart class="chart" :option="chartOptions" :autoresize="true" ></Chart>
            <!-- <Chart class="chart" :option="chartOptions" @click="handleNodeClick" :autoresize="true" ></Chart> -->
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
import { useAllDocsStore } from '@/stores/alldocs';
import { ref, onMounted, Ref, watch, } from "vue";
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent, } from "echarts/components";
import SearchBar from "@/layouts/components/SearchBar.vue";
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

const allDocsStore = useAllDocsStore()
let hide = false;
const router = useRouter();

const tags = ref<string[]>([]);
const newTag = ref('');

const addTag = () => {
  if (newTag.value.trim() !== '' && !tags.value.includes(newTag.value)) {
    tags.value.push(newTag.value);
    newTag.value = '';
    fetchData();
  }
};

const removeTag = (tag: string) => {
  const index = tags.value.indexOf(tag);
  if (index !== -1) {
    tags.value.splice(index, 1);
    fetchData();
  }
};

const dirs = ref<string[]>([]);
const newDirectorate = ref('');

const addDirectorate = () => {
  if (newDirectorate.value.trim() !== '' && !dirs.value.includes(newDirectorate.value)) {
    dirs.value.push(newDirectorate.value);
    newDirectorate.value = '';
    fetchData();
  }
};

const removeDirectorate = (dir: string) => {
  const index = dirs.value.indexOf(dir);
  if (index !== -1) {
    dirs.value.splice(index, 1);
    fetchData();
  }
};

const jsonData = ref<null | ForcedGraph>(null);
const nodeColors = ref<{ [key: string]: string }>({});
const chartOptions: Ref<ECBasicOption | undefined> = ref({});

const fetchData = async () => {
  try {
    chartOptions.value = {};

    let outcomes = router.currentRoute.value.query.outcomes as string[]
    if(outcomes){
      outcomes = Array.isArray(outcomes)
      ? outcomes
      : [outcomes as string];
    }

    const searchTags = tags.value;
    const searchDirs = dirs.value;

    if(searchTags.length > 0 || searchDirs.length > 0  || (outcomes !== undefined && outcomes.length > 0)) {
      let graph = null;
      if (outcomes !== undefined && outcomes.length > 0 ) {
        graph = await allDocsStore.fetchDocsForcedNpfList(outcomes, searchDirs.join("|"), searchTags.join("|")) as ForcedGraph;
      } else {
        graph = await allDocsStore.fetchAllDocsForcedGraph(searchDirs.join("|"), searchTags.join("|")) as ForcedGraph;
      }

      graph.nodes.forEach(function (node: GraphNode) {
        node.label = {
          show: node.symbolSize >= 20
        };
      });

      jsonData.value = graph;
      // Update chartOptions directly
      chartOptions.value = {
        animation: false,
        tooltip: {},
        legend: [
          {
            left: 1,
            orient: 'vertical',
            backgroundColor: 'rgba(255, 255, 255, 1)',
            borderColor: 'rgb(106, 168, 201)',
            borderWidth: 2,
            borderRadius: 5,
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
              edgeLength: 300,
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
    }

  } catch (error) {
    console.error(error);
  }
};

watch(tags, () => {
  fetchData();
});

const handleNodeClick = (params: any) => {
  if (params.dataType === 'node') {
    const nodeData = params.data;
    const nodeId = nodeData.id;

    // Toggle the color of the clicked node
    if (nodeColors.value[nodeId] === 'green') {
      delete nodeColors.value[nodeId];
    } else {
      nodeColors.value[nodeId] = 'green';
    }

    // Update the series data with the modified color
    const seriesData = chartOptions.value.series[0].data;
    seriesData.forEach((node: any) => {
      if (node.id === nodeId) {
        node.itemStyle = { color: nodeColors.value[nodeId] };
        // console.log("node: ", node)
      }
    });

  }
};

onMounted(() => {
  fetchData();
});
</script>

<style lang="scss" scoped>
.tag-input:focus {
  border-color: rgb(106, 168, 201); /* Change border color on focus */
}
.tag-input {
  flex: 1;
  border: 2px solid #fff;
  border-radius: 5px;
  padding: 5px;
  margin-right: 10px;
  outline: none;
  transition: border-color 0.2s;
}
.Tag-container {
  max-width: 1200px;
  border: 1px solid #ccc;
  border-radius: 10px;
  padding: 5px;
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);

}
.chart {
  height: 770px;
}
</style>
