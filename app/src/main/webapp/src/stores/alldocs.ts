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

// Node Overview
export interface NodeOverview {
  uri: string;
  id: string;
  directorate: string;
  policyTitle?: string;
  commitmentTitle?: string;
  date?: string;
  priority?: string;
  lead?: string;
  director?: string;
  keyContact?: string;
  accessURL?: string;
  keywords: string[];
}

export const NODE_OVERVIEW_DATA: NodeOverview = {
  uri: '',
  id: '',
  directorate: '',
  policyTitle: '',
  commitmentTitle: '',
  lead: '',
  director: '',
  keyContact: '',
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
    nodeOverviewList: [] as NodeOverview[],
  }),
  actions:{
    updateNodeOverviewList(newData: NodeOverview[]) {
      this.nodeOverviewList = newData;
    },
    // Forced graph
    async fetchAllDocsForcedGraph(searchDirs: string, searchString: string) {
      const pfgStore = usePfgStore();
      const bpStore = useBpStore()
      const response = await api.get<any>(
        `/api/alldocs/forcedgraph/list`, {
          params: {
            searchDirs: searchDirs,
            searchString: searchString,
          }
        }
      );

      const data = response.data;
      pfgStore.pfgDocs = data.pfgDocs
      pfgStore.pfgAuxs = data.pfgAuxs
      bpStore.bpDocs = data.bpDocs
      bpStore.bpComs = data.bpComs
      const graph = JSON.parse(data.graph) as ForcedGraph;

      return graph;
    },
    async fetchDocsForcedNpfList(outcomes: string[], searchDirs: string, searchString: string) {
      const pfgStore = usePfgStore();
      const bpStore = useBpStore()
      try {
        const response = await api.post('/api/alldocs/forcedgraph-npf/list', 
        outcomes, {
          params: { 
            searchDirs: searchDirs, 
            searchString: searchString 
          }
        }
        );
        // return response.data;
        const data = response.data;
        pfgStore.pfgDocs = data.pfgDocs
        pfgStore.pfgAuxs = data.pfgAuxs
        bpStore.bpDocs = data.bpDocs
        bpStore.bpComs = data.bpComs
        const graph = JSON.parse(data.graph) as ForcedGraph;
        
        return graph;
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
    },
  }
})
