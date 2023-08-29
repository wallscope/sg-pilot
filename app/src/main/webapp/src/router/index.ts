import defaultLayout from '@/layouts/default.vue';
import { createRouter, createWebHistory } from 'vue-router';
import { usePasswordStore } from '@/stores/password';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: "Default",
      component: defaultLayout,
      // component: () => import('@/layouts/default/Default.vue'),
      beforeEnter: (to, from, next) => {
        let isPasswordCorrect = false;
  
        while (!isPasswordCorrect) {
          const inputPassword = prompt('Enter password:');
  
          if (inputPassword === null) {
            // User canceled the prompt
            next(false); // Prevent navigation
            return;
          }
  
          isPasswordCorrect = usePasswordStore().checkPassword(inputPassword);
  
          if (!isPasswordCorrect) {
            alert('Incorrect password. Please try again.');
          }
        }
  
        // Password is correct, allow navigation
        next();
      },
      children: [
        {
          path: '',
          name: 'instructions',
          // route level code-splitting
          // this generates a separate chunk (about.[hash].js) for this route
          // which is lazy-loaded when the route is visited.
          component: () => import(/* webpackChunkName: "home" */ '@/views/pages/instructions.vue'),
        },
        {
          path: "detailedChart",
          name: "detailedChart",
          component: () => import('@/pages/detailedchart.vue'),
        },
        {
          path: "protected",
          name: "protected",
          component: () => import('@/pages/Protected.vue'),
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
          path: "graphChartForce/:outcomes?",
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
        {
          path: "data",
          name: "data",
          component: () => import('@/views/pages/overviewTable.vue'),
        },
        {
          path: "about",
          name: "about",
          component: () => import('@/views/pages/aboutPilot.vue'),
        },
      ],
    },
  ],
});

export default router
