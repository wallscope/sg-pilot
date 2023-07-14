<script setup lang="ts">
// import { usePfgStore } from '@/stores/pfg';
import detailedChart from '@/views/pages/charts/leftToRightTreeChart.vue';
// const pfgStore = usePfgStore()

// console.log("trying to get the doc")

// onMounted(async () => {
//   // console.log("trying to get the doc")
//   await pfgStore.fetchPfgDocDetailedGraph()
// })

</script>

<template>
<div class="text-2xl mb-4">
  <v-icon icon="mdi-chart-bar" /> Graph
</div>
<div class="text-subtitle-2 mt-4 mb-2">
        <Suspense>
          <detailedChart />
        </Suspense>
</div>
</template>
