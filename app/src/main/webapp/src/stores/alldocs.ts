import { defineStore } from 'pinia';
import api from "@/utils/api";
import { usePfgStore } from '@/stores/pfg';
import { useBpStore } from '@/stores/bp';

export interface DocOverview {
  uri: string;
  title: string;
  docType: string;
  dG: string;
  directorate: string;
  primaryOutcomes: string[];
  secondaryOutcomes: string[];
  keywords: string[];
}

export const DEF_DOC_OVERVIEW_DATA: DocOverview = {
  uri: '',
  title: '',
  docType: '',
  dG: '',
  directorate: '',
  primaryOutcomes: [''],
  secondaryOutcomes: [''],
  keywords: [''],
}

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
    // Table Overview
    async fetchDetailedDocGraph(docType: string, id: string) {
      const pfgStore = usePfgStore();
      const bpStore = useBpStore()
      
      switch (docType) {
        case "PFGDoc":
          return pfgStore.fetchPfgDocDetailedGraph(id)
        case "PFGAux":
          return pfgStore.fetchPfgAuxDetailedGraph(id)
        case "BPDoc":
          return bpStore.fetchBpDocDetailedGraph(id)
        case "BPCom":
          return bpStore.fetchBpComDetailedGraph(id)
        default:
          break;
      }

      const response = await api.get<any>(
        `/api/alldocs/sankey/list`
      );
      return response.data;
    },
  }
})
