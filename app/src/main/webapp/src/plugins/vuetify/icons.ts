import { Icon } from '@iconify/vue'
import type { IconProps, IconSet } from 'vuetify'
import { aliases } from 'vuetify/iconsets/fa'

export const iconify: IconSet = {
  component: (props: IconProps) => h(Icon, props),
}

export const icons = {
  defaultSet: 'iconify',
  aliases,
  sets: {
    iconify,
  },
}
