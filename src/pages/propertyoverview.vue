<script setup lang="ts">

onMounted(async () => {
  console.log("Overview")
})
</script>

<template>
  <div>
    <div class="text-2xl mb-4">
       <v-icon icon="mdi-home" /> Overview
    </div>
    <div class="mb-4">
       Today | Time between 12am to 12pm /  Date: {{ new Date().getDate() }} {{ new Date().getMonth()+1 }} {{ new Date().getFullYear() }}
    </div>
    <Suspense>
      Overview
    </Suspense>
  </div>
  <div class="text-subtitle-2 mt-4 mb-2"></div>
    <v-expansion-panels variant="accordion">
    <v-expansion-panel>
        <v-expansion-panel-title expand-icon="mdi-chevron-down" collapse-icon="mdi-chevron-up">Rooms | movement/temperature/humidity</v-expansion-panel-title>
        <v-expansion-panel-text>
          <Suspense>
            ok
          </Suspense>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  <div>

    
  </div>
</template>
