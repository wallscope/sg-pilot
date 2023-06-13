<script setup lang="ts">
// import { useRoute } from 'vue-router';
// import { useTheme } from 'vuetify';
// const vuetifyTheme = useTheme()

// const route = useRoute()

// const item_id = route.query.item_id as string
// const item_name = route.query.item_name as string
</script>

<template>
  <!-- 👉 Nav header -->
  <div class="nav-header">
    <RouterLink
      to="/home"
      class="app-logo d-flex align-center gap-x-3 app-title-wrapper"
    >
       <Transition name="vertical-nav-app-title">
        <h1 class="font-weight-semibold leading-normal text-xl text-uppercase">
          Scottish Government Knowledge Graph Pilot
        </h1>
      </Transition>
    </RouterLink>
  </div>

  <!-- 👉 Nav items -->
  <ul>
    
        <v-list>     
          <Suspense>
            <router-link :to="{ name: 'leftToRightTreeChart', query: { icon: 'mdi-chart-bar' } }">
              <v-list-item>
                <v-icon>mdi-chart-bar</v-icon>
                <span>Detailed Chart</span>
              </v-list-item>
            </router-link>
          </Suspense>
          
        </v-list> 
  </ul>
</template>

<style lang="scss" scoped>
// .v-navigation-drawer {
//   height: 100%;

//   .v-navigation-drawer__content {
//     display: flex;
//     flex-direction: column;

//     > ul {
//       flex-grow: 1;
//     }
//   }
// }
.v-list-item a:active {
  background-color: rgb(118, 169, 236) !important;
}
.v-title {
    background-color: rgb(118, 169, 236) !important;
  }
.active-panel-edited {
    background-color: #ff000038 !important;
  }

.active-panel-normal {
  background-color: #00ff1438 !important;    
} 

</style>
