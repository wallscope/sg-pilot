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
        title: 'Instructions',
        to: 'instructions',
        icon: { icon: 'mdi-book-open-page-variant' }
      }"
    />
    <VerticalNavLink
      :item="{
        title: 'Data References',
        to: 'data',
        icon: { icon: 'mdi-file-document-outline' }
      }"
    />
    <VerticalNavLink
      :item="{
        title: 'Graph Overview',
        to: 'graphChartForce',
        icon: { icon: 'mdi-graph' }
      }"
    />
    <VerticalNavLink
      :item="{
        title: 'Graph Circle',
        to: 'graphChartCirc',
        icon: { icon: 'mdi-chart-donut' }
      }"
    />
    <VerticalNavLink
      :item="{
        title: 'Example - Sankey',
        to: 'sankeyChart',
        icon: { icon: 'mdi-chart-sankey' }
      }"
    />
    <VerticalNavLink
      :item="{
        title: 'About the Pilot',
        to: 'about',
        icon: { icon: 'mdi-information-outline' }
      }"
    />
    <!-- <VerticalNavLink
      :item="{
        title: 'Detailed Chart',
        to: 'detailedChart',
        icon: { icon: 'mdi-chart-bar' }
      }"
    /> -->
    <!-- <VerticalNavLink
      :item="{
        title: 'Graph Chart XY preset',
        to: 'graphChartXY',
        icon: { icon: 'mdi-chart-line' }
      }"
    /> -->
    <!-- <VerticalNavLink
      :item="{
        title: 'Other Graph Chart dep',
        to: 'graphChartDep',
        icon: { icon: 'mdi-chart-line' }
      }"
    />
    <VerticalNavLink
      :item="{
        title: 'Treemap Drill Chart',
        to: 'treemapDrillChart',
        icon: { icon: 'mdi-chart-line' }
      }"
    /> -->
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
