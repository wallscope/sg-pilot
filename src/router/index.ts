import defaultLayout from '@/layouts/default.vue';
import internalLayout from '@/layouts/internal.vue';
import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/internal",
      name: "InternalLayout",
      component: internalLayout,
      children: [
        {
          path: "leftToRightTreeChart",
          name: "leftToRightTreeChart",
          component: () => import('@/views/pages/charts/leftToRightTreeChart.vue'),
        },
      ],
    },
    {
      path: '/',
      name: "Default",
      component: defaultLayout,
      // component: () => import('@/layouts/default/Default.vue'),
      children: [
        {
          path: 'home',
          name: 'home',
          // route level code-splitting
          // this generates a separate chunk (about.[hash].js) for this route
          // which is lazy-loaded when the route is visited.
          component: () => import(/* webpackChunkName: "home" */ '@/pages/Home.vue'),
        },
      ],
    },
  ],
});

export default router
