import defaultLayout from '@/layouts/default.vue';
import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: "Default",
      component: defaultLayout,
      // component: () => import('@/layouts/default/Default.vue'),
      children: [
        {
          path: '',
          name: 'home',
          // route level code-splitting
          // this generates a separate chunk (about.[hash].js) for this route
          // which is lazy-loaded when the route is visited.
          component: () => import(/* webpackChunkName: "home" */ '@/pages/home.vue'),
        },
        {
          path: "leftToRightTreeChart",
          name: "leftToRightTreeChart",
          component: () => import('@/views/pages/charts/leftToRightTreeChart.vue'),
        },
        {
          path: "graphChartXY",
          name: "graphChartXY",
          component: () => import('@/views/pages/charts/graphChartXY.vue'),
        },
        {
          path: "graphChartCirc",
          name: "graphChartCirc",
          component: () => import('@/views/pages/charts/graphChartCirc.vue'),
        },
        {
          path: "graphChartForce",
          name: "graphChartForce",
          component: () => import('@/views/pages/charts/graphChartForce.vue'),
        },
        {
          path: "graphChartDep",
          name: "graphChartDep",
          component: () => import('@/views/pages/charts/graphChartDep.vue'),
        },
        {
          path: "treemapDrillChart",
          name: "treemapDrillChart",
          component: () => import('@/views/pages/charts/treemapDrillChart.vue'),
        },
        {
          path: "sankeyChart",
          name: "sankeyChart",
          component: () => import('@/views/pages/charts/sankeyChart.vue'),
        },
      ],
    },
  ],
});

export default router
