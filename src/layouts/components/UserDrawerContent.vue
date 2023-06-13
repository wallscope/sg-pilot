<script setup lang="ts">
import { VerticalNavLink, VerticalNavSectionTitle } from '@layouts';
import { useTheme } from 'vuetify';

const vuetifyTheme = useTheme()

</script>

<template>
  <!-- 👉 Nav header -->
  <div class="nav-header">
    <RouterLink
      to="/"
      class="app-logo d-flex align-center gap-x-3 app-title-wrapper"
    >
       <Transition name="vertical-nav-app-title">
        <h1 class="font-weight-semibold leading-normal text-xl text-uppercase">
          Graph Pilot
        </h1>
      </Transition>
    </RouterLink>
  </div>
  <ul>
  <!-- 👉 Nav items -->
    <VerticalNavSectionTitle :item="{ heading: 'Charts' }" />
    <VerticalNavLink
      :item="{
        title: 'Detailed Chart',
        to: 'leftToRightTreeChart',
        icon: { icon: 'mdi-chart-bar' }
      }"
    />
    <VerticalNavLink
      :item="{
        title: 'Graph Chart',
        to: 'graphChart',
        icon: { icon: 'mdi-chart-line' }
      }"
    />    
  </ul>
</template>

<style lang="scss">
 .v-navigation-drawer {
   height: 100%;
   .v-navigation-drawer__content {
     display: flex;
    flex-direction: column;

    > ul {
      flex-grow: 1;
     }
   }
 }
</style>
