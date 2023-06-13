<script setup lang="ts">
import { usePfgStore } from '@/stores/pfg';
import detailedChart from '@/views/pages/charts/leftToRightTreeChart.vue';
const pfgStore = usePfgStore()

onMounted(async () => {
  // await pfgStore.getDateFormatted
})

</script>

<template>
<div class="text-2xl mb-4">
  <v-icon icon="mdi-temperature" /> Graph
</div>
<div class="text-subtitle-2 mt-4 mb-2">
    <v-expansion-panel>
        <v-expansion-panel-title expand-icon="mdi-chevron-down" collapse-icon="mdi-chevron-up">Temperature per room</v-expansion-panel-title>
        <v-expansion-panel-text>
        <Suspense>
          <detailedChart />
        </Suspense>
        </v-expansion-panel-text>
    </v-expansion-panel>
</div>
</template>
