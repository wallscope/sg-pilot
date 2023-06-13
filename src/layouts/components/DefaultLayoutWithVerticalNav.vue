<script lang="ts" setup>
import { VerticalNavLayout } from '@layouts';
import DrawerContent from './UserDrawerContent.vue';

// Components
// import UserProfile from '@/layouts/components/UserProfile.vue';
</script>

<template>
  <VerticalNavLayout>
    <!-- 👉 navbar -->
    <template #navbar>  
      <VSpacer />
    <!-- <Suspense>
      <UserProfile />
    </Suspense> -->
    </template>
    <!-- 👉 Drawer content -->
    <template #navigation-drawer-content>
      <DrawerContent />
    </template>
    <!-- 👉 Pages -->
    <div class="layout-page-content">
      <RouterView />
    </div>
  </VerticalNavLayout>
</template>
<style lang="scss">

  .v-field__outline__start {
    border-radius: 28px 0 0 28px !important;
    flex-basis: 20px !important;
  }

  .v-field__outline__end {
    border-radius: 0 20px 20px 0 !important;
  }

</style>
