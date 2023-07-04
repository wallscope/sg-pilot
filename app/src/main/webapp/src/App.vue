<script setup lang="ts">

</script>

<template>
  <VApp>
    <VLayout class="layout-wrapper layout-nav-type-vertical">
      <RouterView :key="$route.fullPath"/>
    </VLayout>
  </VApp>
</template>

<script lang="ts" setup>
  //
</script>
