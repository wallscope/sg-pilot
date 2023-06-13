<script setup lang="ts">
import type { Anchor } from 'vuetify/lib/components';

const avatarBadgeProps = {
  dot: true,
  location: 'bottom right' as Anchor,
  offsetX: 3,
  offsetY: 3,
  color: 'success',
  bordered: true,
}


</script>

<template>
<Suspense>
  <div><VIcon
                class="me-2"
                icon="mdi-account-outline"
                size="22"
              />
              John Crichton
  </div>
</Suspense>
  <div class="logout">
    <v-btn variant="outlined">
      <VIcon
        class="me-2"
        icon="mdi-logout-variant"
        size="22"
      /> Logout
    </v-btn>
  </div>

</template>
<style lang="scss" scoped>
.logout {
  margin-left:10px;
}


</style>
