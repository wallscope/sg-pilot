<script setup lang="ts">
import miscMaskDark from '@/assets/images/pages/misc-mask-dark.png';
import miscMaskLight from '@/assets/images/pages/misc-mask-light.png';
import { useTheme } from 'vuetify';

const vuetifyTheme = useTheme()
const authThemeMask = computed(() => {
  return vuetifyTheme.global.name.value === 'light'
    ? miscMaskLight
    : miscMaskDark
})
</script>

<template>
  <div class="misc-wrapper">
    <ErrorHeader
      error-code="404"
      error-title="Page Not Found ⚠️"
      error-description="We couldn't find the page you are looking for."
    />


      <VBtn
        href="/"
        class="mt-10"
      >
        Back to Home
      </VBtn>
    </div>
    <VImg
      :src="authThemeMask"
      class="misc-footer-img d-none d-md-block"
    />
  
</template>

<style lang="scss">
@use "@core/scss/pages/misc.scss";

.misc-footer-tree {
  inline-size: 15.625rem;
  inset-block-end: 3.5rem;
  inset-inline-start: 0.375rem;
}
</style>

<route lang="yaml">
meta:
  layout: blank
  action: read
  subject: Auth
</route>
