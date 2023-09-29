<script lang="ts" setup>
import { useDisplay } from 'vuetify'
const { lgAndUp, mdAndDown } = useDisplay()

const drawer = ref(false)
</script>

<template>
  <VNavigationDrawer
    v-model="drawer"
    app
    floating
    sticky
    :absolute="false"
    style="background: rgb(var(--v-theme-background))"
    class="layout-vertical-nav position-fixed"
  >
    <slot name="navigation-drawer-content" />
  </VNavigationDrawer>
  <VAppBar @click="drawer = !drawer">
    <VAppBarNavIcon
        style="padding-left: 10px;"
        class="d-block me-2 ms-n3"
        color="inherit"
        @click="drawer = true"
      />
    <VAppNavbarTitle><h3>{{ drawer ? 'Close' : 'Open' }} Menu</h3></VAppNavbarTitle>
  </VAppBar>
  <VMain class="w-100">
    <div class="pa-6">
      <slot />
    </div>
  </VMain>
  <VFooter
    app
    style="background: transparent"
  >
    <slot name="footer" />
  </VFooter>
</template>

<style lang="scss">
.layout-vertical-nav {
  top: 0 !important;
  height: 100% !important;

  .v-navigation-drawer__content {
    display: flex;
    flex-direction: column;

    > ul {
      flex-grow: 1;
    }
  }
}

.v-toolbar__prepend {
  margin-inline-start: 0 !important;
}

.v-footer {
  padding-left: 24px !important;
  padding-right: 24px !important;
}
</style>
