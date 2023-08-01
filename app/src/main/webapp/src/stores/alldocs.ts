import { defineStore } from 'pinia';
import api from "@/utils/api";

export const useAllDocsStore = defineStore('alldocs', {
  state: () => ({
    allDocsForcedGraphData: {} as any,
  }),
  actions:{
    // Forced graph
    async fetchAllDocsForcedGraph() {
      const response = await api.get<any>(
        `/api/alldocs/forcedgraph/list`
      );
      return response.data;
    },
    // Sankey
    async fetchAllDocsSankeyGraph() {
      const response = await api.get<any>(
        `/api/alldocs/sankey/list`
      );
      return response.data;
    },
  }
})
