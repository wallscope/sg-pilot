<script setup lang="ts">
import { usePfgStore } from '@/stores/pfg';
import { ref, onMounted, Ref } from "vue";
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent, } from "echarts/components";
import { use } from "echarts/core";
import { SankeyChart } from 'echarts/charts';
import { CanvasRenderer } from "echarts/renderers";
import Chart from "vue-echarts";
import type { ECBasicOption } from "echarts/types/dist/shared";

use([GridComponent, LegendComponent, TitleComponent, TooltipComponent, SankeyChart, CanvasRenderer]);

const pfgStore = usePfgStore()

let hide = false;
//var ROOT_PATH = "";

const chartOptions: Ref<ECBasicOption | undefined> = ref(undefined);

onMounted(async () => {
  try {
    // const response = await fetch(ROOT_PATH + "/sankey_example.json");
    // const data = await response.json();
    const data = await pfgStore.fetchPfgDocSankeyGraphAll()

    chartOptions.value = {
      title: {
        text: 'Sankey Diagram'
      },
      tooltip: {
        trigger: 'item',
        triggerOn: 'mousemove',
        formatter: (params: any) => {
          const linkData = params.data;
          if (linkData.source && linkData.target) {
            // Link data (between two nodes)
            let tooltipText = "";
            if (linkData.label) {
              tooltipText += linkData.label // + "<br>";
            }
            // if (typeof linkData.value !== "undefined") {
            //   tooltipText += "Count: " + linkData.value;
            // }
            return tooltipText;
          } 
          // else {
          //   // Node data (single node)
          //   // return linkData.name;
          //   return linkData.label;
          // }
        },
      },
      series: [
        {
          type: 'sankey',
          data: data.nodes,
          links: data.links,
          emphasis: {
            focus: 'adjacency'
          },
          nodeWidth: 10, // Adjust node width to reduce space between nodes
          nodeGap: 10, // Adjust node gap to reduce space between nodes
          layoutIterations: 32, // Increase the number of iterations for better layout
          levels: [
            {
              depth: 0,
              itemStyle: {
                color: '#fbb4ae'
              },
              lineStyle: {
                color: 'source',
                opacity: 0.6
              }
            },
            {
              depth: 1,
              itemStyle: {
                color: '#b3cde3'
              },
              lineStyle: {
                color: 'source',
                opacity: 0.6
              }
            },
            {
              depth: 2,
              itemStyle: {
                color: '#ccebc5'
              },
              lineStyle: {
                color: 'source',
                opacity: 0.6
              }
            },
            {
              depth: 3,
              itemStyle: {
                color: '#decbe4'
              },
              lineStyle: {
                color: 'source',
                opacity: 0.6
              }
            }
          ],
          lineStyle: {
            width: 1,
            curveness: 0.5
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
            <div class="chart-container">
              <Chart class="chart" :option="chartOptions" :autoresize="true"></Chart>
            </div>
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
.chart-container {
  width: 1200px; /* Set the container width larger than the visible area */
  // overflow-x: auto; /* Enable horizontal scrolling */
}

.chart {
  height: 700px;
}
</style>
