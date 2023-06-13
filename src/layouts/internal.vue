<script lang="ts" setup>
import InternalLayoutWithVerticalNav from './components/InternalLayoutWithVerticalNav.vue';

defineComponent({
name: "InternalLayout",
components: {
  InternalLayoutWithVerticalNav,
},
});
</script>

<template>
  <InternalLayoutWithVerticalNav />
</template>

<style lang="scss">
@use "@layouts/styles/default-layout";
</style>
