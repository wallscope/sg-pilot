<script setup lang="ts">
import { type NodeOverview, useAllDocsStore } from '@/stores/alldocs';
import NodeOverviewComponent from '@/views/pages/nodeOverviewComponent.vue';
import { PfgAux, PfgDoc, usePfgStore } from '@/stores/pfg';
import { BPCom, BpDoc, useBpStore } from '@/stores/bp';
import { ref, onMounted, Ref, watch, } from "vue";
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent, } from "echarts/components";
import { use } from "echarts/core";
import { GraphChart } from 'echarts/charts';
import { CanvasRenderer } from "echarts/renderers";
import Chart from "vue-echarts";
import type { ECBasicOption } from "echarts/types/dist/shared";
import { useRouter } from "vue-router";
import { ForcedGraph } from "@/stores/alldocs";
import echarts from "echarts";

use([GridComponent, LegendComponent, TitleComponent, TooltipComponent, GraphChart, CanvasRenderer]);

interface GraphNode {
  symbolSize: number;
  label?: {
    show?: boolean;
  };
}

const allDocsStore = useAllDocsStore()
const pfgStore = usePfgStore()
const bpStore = useBpStore()
const router = useRouter();

const tags = ref<string[]>([]);
const newTag = ref('');
const HL_COLOUR = "green";

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

const nodeColors = ref<{ [key: string]: string }>({});
const myChart = ref<echarts.ECharts | null>(null);
const chartOptions: Ref<ECBasicOption> = ref({});

type LegendStatus = {
  [key: string]: boolean;
};
const legend = ref<LegendStatus>({});

const fetchData = async () => {
  try {

    // Get outcomes from the table page
    let outcomes = router.currentRoute.value.query.outcomes as string[]
    if(outcomes){
      outcomes = Array.isArray(outcomes)
      ? outcomes
      : [outcomes as string];
    }

    // Get Search terms - at least 1 directorate is required to show anything on the graph, as per requirements
    const searchTags = tags.value;
    const searchDirs = dirs.value;

    if(searchTags.length > 0 || searchDirs.length > 0  || (outcomes !== undefined && outcomes.length > 0)) {
      let graph = {} as ForcedGraph;
      if (outcomes !== undefined && outcomes.length > 0 ) {
        graph = await allDocsStore.fetchDocsForcedNpfList(outcomes, searchDirs.join("|"), searchTags.join("|")) as ForcedGraph;
      } else {
        graph = await allDocsStore.fetchAllDocsForcedGraph(searchDirs.join("|"), searchTags.join("|")) as ForcedGraph;
      }

      // Assign visibility of labels depending on size
      graph.nodes.forEach(function (node: GraphNode) {
        node.label = {
          show: node.symbolSize >= 20
        };
      });

      // If empty, populate the legend with items disabled
      if (Object.keys(legend.value).length === 0) {
        legend.value = graph.categories.reduce(function (obj: { [x: string]: boolean; }, item: { name: string | number; }) {
          obj[item.name] = false;
          return obj;
        }, {});
      }

      // Update chartOptions directly
      chartOptions.value = {
        // animation: false, // animation false is NOT supported by the graph chart!
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
            selected: legend,
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
      } as ECBasicOption;
    }

    myChart.value?.setOption(chartOptions.value)
  } catch (error) {
    console.error(error);
  }
};

watch(tags, () => {
  fetchData();
});

const inferDate = (inputString: string): number[] => {
  const years: number[] = [];

  // try to match years in various formats
  const yearMatches = inputString.match(/\b\d{4}\b|\b\d{2}\b|\d{4}(?![\d-])/g);

  if (yearMatches) {
    yearMatches.forEach((yearMatch) => {
      const parsedYear = parseInt(yearMatch, 10);
      if (!isNaN(parsedYear)) {
        years.push(parsedYear);
      }
    });
  }
  return years;
}

const updateNodeOverviewList = (uriList: string[]) => {
  let nodeOverviewList: NodeOverview[] = []

  uriList.forEach((uri: string) =>{
      const [docType, docId] = uri.split("SG/")[1].split("/")

      if(docType === "BPCom"){
        const nodeDocInfo = bpStore.bpComs.find((doc: BPCom) => { return doc.uri.split("/").pop() === docId })
        const directorateName = bpStore.bpDocs.find((doc: BpDoc) => { return doc.filename.toLowerCase() === nodeDocInfo?.filename.toLowerCase() })?.directorate[0]
        
        //Push BPCom overview
        nodeOverviewList.push({
          uri: nodeDocInfo?.uri || "",
          id: "BP Commitment|" + docId || "<No id available>",
          directorate: directorateName || "<No directorate available>",
          commitmentTitle: nodeDocInfo?.commitment || "<No commitment title available>",
          date: inferDate(nodeDocInfo?.filename || "").join("-") || "<No date available>",
          priority: nodeDocInfo?.priority || "<No priority available>",
          lead: nodeDocInfo?.commitmentLead || "<No lead available>",
          keywords: nodeDocInfo?.keywords || ["<No keywords available>"],
        })
      } else if(docType === "BPDoc"){
        const nodeDocInfo = bpStore.bpDocs.find((doc: BpDoc) => { return doc.uri.split("/").pop() === docId })
        
        // Prepare id
        const id = docId.replace(".xlsx", "")
        let shortId = ""
        if(id && id.length > 20)
          shortId = id?.substring(id.length-20, id.length)
        else if(id && id.length <= 20)
          shortId = id

        //Push BPDoc overview
        nodeOverviewList.push({
          uri: nodeDocInfo?.uri || "",
          id: "BP Document|..." + shortId || "<No id available>",
          directorate: nodeDocInfo?.directorate[0] || "<No directorate available>",
          date: inferDate(nodeDocInfo?.filename || "").join("-") || "<No date available>",
          director: nodeDocInfo?.director[0] || "<No director available>",
          keyContact: nodeDocInfo?.keyContact[0] || "<No keyContact available>",
          keywords: nodeDocInfo?.keywords || ["<No keywords available>"],
        })
      } else if(docType === "PFGDoc"){
        const nodeDocInfo = pfgStore.pfgDocs.find((doc: PfgDoc) => { return doc.uri.split("/").pop() === docId })
        
        // Prepare id
        const id = docId.replace(".docx", "")
        let shortId = ""
        if(id && id.length > 20)
          shortId = id?.substring(id.length-20, id.length)
        else if(id && id.length <= 20)
          shortId = id

        //Push PFGDoc overview
        nodeOverviewList.push({
          uri: nodeDocInfo?.uri || "",
          id: "PFG Document|..." + shortId || "<No id available>",
          directorate: nodeDocInfo?.directorate[0] || "<No directorate available>",
          date: inferDate(nodeDocInfo?.filename || "").join("-") || "<No date available>",
          policyTitle: nodeDocInfo?.policyTitle[0] || "<No policy title available>",
          lead: nodeDocInfo?.leadOfficial[0] || "<No lead available>",
          keywords: nodeDocInfo?.keywords || ["<No keywords available>"],
        })
      } else if(docType === "PFGAux"){
        const nodeDocInfo = pfgStore.pfgAuxs.find((doc: PfgAux) => { return doc.uri.split("/").pop() === docId })
        
        //Push PFGAux overview
        nodeOverviewList.push({
          uri: nodeDocInfo?.uri || "",
          id: "PFG Spreadsheet|" + docId || "<No id available>",
          directorate: nodeDocInfo?.directorate[0] || "<No directorate available>",
          date: nodeDocInfo?.period || "<No date available>",
          policyTitle: nodeDocInfo?.policyTitle[0] || "<No policy title available>",
          lead: nodeDocInfo?.leadOfficial[0] || "<No lead available>",
          accessURL: nodeDocInfo?.accessURL || "<No URL available>",
          keywords: nodeDocInfo?.keywords || ["<No keywords available>"],
        })
      }
    })
    allDocsStore.updateNodeOverviewList(nodeOverviewList)
};

const handleNodeClick = (params: any) => {
  if (params.dataType === 'node') {
    const nodeData = params.data;
    const nodeId = nodeData.id;

    (chartOptions.value.series as any)[0].data.forEach((node: any) => {
      if (node.id === nodeId) {
        if(!node.itemStyle){
          node.itemStyle = { color: HL_COLOUR };
          updateNodeOverviewList(node.uriList)
        } else if(node.itemStyle.color === HL_COLOUR){
          if(nodeColors.value[nodeId]) {
            node.itemStyle = { color: nodeColors.value[nodeId] };
          } else {
            node.itemStyle.color = null;
          }
        } else if(node.itemStyle.color !== HL_COLOUR){
          nodeColors.value[nodeId] = node.itemStyle.color;
          node.itemStyle = { color: HL_COLOUR };
          updateNodeOverviewList(node.uriList)
        }
      }
    });

    myChart.value?.setOption(chartOptions.value);
  }
};

const handleLegendSelectChange = (eventData: any) => {
  const selected = eventData.selected;
  // Update the `legend` object with the new selected state
  for (const itemName in selected) {
    if (selected.hasOwnProperty(itemName)) {
      legend.value[itemName] = selected[itemName];
    }
  }
};

onMounted(() => {
  fetchData();
});
</script>

<template>
  <VRow>
    <VCol cols="12">
      <v-table>
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
            <!-- <Chart id="echarts-graph" class="chart" :option="chartOptions" @click="handleNodeClick" :autoresize="true" ></Chart> -->
            <Chart class="chart" ref="myChart" @legendselectchanged="handleLegendSelectChange" @click="handleNodeClick" :autoresize="true" ></Chart>
          </Suspense>
          <Suspense>
            <div class="NodeOverview-container">
              <NodeOverviewComponent />
            </div>
          </Suspense>
      </v-table>
    </VCol>
  </VRow>
  <p>
    <hr>
  </p>
</template>

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
  height: 500px;
}
</style>
