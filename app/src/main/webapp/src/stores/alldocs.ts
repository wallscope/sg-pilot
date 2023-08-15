import { defineStore } from 'pinia';
import api from "@/utils/api";
import { usePfgStore } from '@/stores/pfg';
import { useBpStore } from '@/stores/bp';

// Overview Table Doc
export interface DocOverview {
  uri: string;
  title: string;
  docType: string;
  dg: string;
  directorate: string;
  primaryOutcomes: string[];
  secondaryOutcomes: string[];
  keywords: string[];
}

export const DEF_DOC_OVERVIEW_DATA: DocOverview = {
  uri: '',
  title: '',
  docType: '',
  dg: '',
  directorate: '',
  primaryOutcomes: [''],
  secondaryOutcomes: [''],
  keywords: [''],
}

// Forced graph JSON shape
export interface ForcedGraph {
  nodes: ForcedNode[];
  links: ForcedLink[];
  categories: ForcedCategory[];
}

export interface ForcedNode {
  id: string;
  name: string;
  symbolSize: number;
  value: string;
  category?: number;
}

export interface ForcedLink {
  source: string;
  target: string;
}

export interface ForcedCategory {
  name: string;
}

export const DEF_DOC_FORCED_CATEGORY: ForcedCategory = {
  name: '',
}

export const DEF_DOC_FORCED_LINK: ForcedLink = {
  source: '',
  target: '',
}

export const DEF_DOC_FORCED_NODE: ForcedNode = {
  id: '',
  name: '',
  symbolSize: 0,
  value: '',
}

export const DEF_DOC_FORCED_GRAPH: ForcedGraph = {
  nodes: [DEF_DOC_FORCED_NODE],
  links: [DEF_DOC_FORCED_LINK],
  categories: [DEF_DOC_FORCED_CATEGORY],
}

export const useAllDocsStore = defineStore('alldocs', {
  state: () => ({
    allDocsForcedGraphData: {} as any,
  }),
  actions:{
    // Forced graph
    async fetchAllDocsForcedGraph(searchTerm: string) {
      const response = await api.get<any>(
        `/api/alldocs/forcedgraph/list`, {
          params: {
            searchTerm: searchTerm
          }
        }
      );
      return response.data;
    },
    async fetchDocsForcedNpfList(outcomes: string[], searchTerm: string) {
      try {
        const response = await api.post('/api/alldocs/forcedgraph-npf/list', 
        outcomes, {
          params: { searchTerm: searchTerm }
        }
        );
        return response.data;
      } catch (error) {
        console.error('Error:', error);
      }
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
