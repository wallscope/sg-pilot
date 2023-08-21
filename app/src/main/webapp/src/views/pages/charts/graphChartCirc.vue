<template>
  <VRow>
    <VCol cols="12">
      <v-table>
        <template v-if="!hide">
          <div class="Tag-container">
            <v-chip v-for="tag in tags" :key="tag" class="ma-1" @click="removeTag(tag)">
              {{ tag }}
              <v-icon small>mdi-close</v-icon>
            </v-chip>
            <input v-model="newTag" @keydown.enter="addTag" placeholder="Add a tag" class="tag-input" />
          </div>
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
import { useAllDocsStore } from '@/stores/alldocs';
import { ref, onMounted, Ref, computed, watch, } from "vue";
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

const jsonData = ref<null | ForcedGraph>(null);

const fetchData = async () => {
  try {
    const outcomes = router.currentRoute.value.query.outcomes as string[];
    const searchTags = tags.value;
    let graph = null;
    if (outcomes !== undefined && outcomes.length > 0 ) {
      graph = await allDocsStore.fetchDocsForcedNpfList(outcomes, searchTags.join("|")) as ForcedGraph;
    } else {
      graph = await allDocsStore.fetchAllDocsForcedGraph(searchTags.join("|")) as ForcedGraph;
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

watch(tags, () => {
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
        layout: "circular",
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
  height: 820px;
}
</style>
