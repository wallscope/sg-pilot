import { defineStore } from 'pinia';
import api from "@/utils/api";

// Programme For Government Doc Store
export interface PfgDoc {
  uri: string;
  filename: string;
  ministerialPortfolio: string[];
  directorate: string[];
  dG: string[];
  unitBranch: string[];
  leadOfficial: string[];
  scsClearance: string[];
  fbpClearance: string[];
  primaryOutcomes: string[];
  secondaryOutcomes: string[];
  portfolioCoordinator: string[];
  policyTitle: string[];
  completionDate: string;
  keywords: string[];
}

// Template objects
export const DEF_PFGDOC_DATA: PfgDoc = {
  uri: '',
  filename: '',
  ministerialPortfolio: [''],
  directorate: [''],
  dG: [''],
  unitBranch: [''],
  leadOfficial: [''],
  scsClearance: [''],
  fbpClearance: [''],
  primaryOutcomes: [''],
  secondaryOutcomes: [''],
  portfolioCoordinator: [''],
  policyTitle: [''],
  completionDate: '',
  keywords: [''],
}

// Programme For Government Aux Store
export interface PfgAux {
  uri: string;
  id: string;
  period: string;
  accessURL: string;
  strategicPriority: string;
  legislativeProposal: string;
  buteHouseAgreementLink: string;
  completionDate: string;
  ministerialPortfolio: string[];
  directorate: string[];
  dG: string[];
  leadOfficial: string[];
  primaryOutcomes: string[];
  secondaryOutcomes: string[];
  policyTitle: string[];
  keywords: string[];
}

export interface PfgAuxOverview {
  uri: string;
  title: string;
  docType: string;
  dG: string;
  directorate: string;
  primaryOutcomes: string[];
  secondaryOutcomes: string[];
  keywords: string[];
}

// Template objects
export const DEF_PFGAUX_DATA: PfgAux = {
  uri: '',
  id: '',
  period: '',
  accessURL: '',
  strategicPriority: '',
  legislativeProposal: '',
  buteHouseAgreementLink: '',
  completionDate: '',
  ministerialPortfolio: [''],
  directorate: [''],
  dG: [''],
  leadOfficial: [''],
  primaryOutcomes: [''],
  secondaryOutcomes: [''],
  policyTitle: [''],
  keywords: [''],
}
export const DEF_PFGAUX_OVERVIEW_DATA: PfgAuxOverview = {
  uri: '',
  title: '',
  docType: '',
  dG: '',
  directorate: '',
  primaryOutcomes: [''],
  secondaryOutcomes: [''],
  keywords: [''],
}

export const usePfgStore = defineStore('pfg', {
  state: () => ({
    // PFG Docs
    pfgDocDetailedGraphData: {} as any,
    pfgDoc: DEF_PFGDOC_DATA,
    pfgDocs: [DEF_PFGDOC_DATA] as PfgDoc[],
    // PFG Auxs
    pfgAuxDetailedGraphData: {} as any,
    pfgAux: DEF_PFGAUX_DATA,
    pfgAuxs: [DEF_PFGAUX_DATA] as PfgAux[],
    PfgAuxOverview: DEF_PFGAUX_OVERVIEW_DATA,
    PfgAuxOverviews: [DEF_PFGAUX_OVERVIEW_DATA] as PfgAuxOverview[],
  }),
  actions:{
    // PFG documents
    async fetchPfgDocs() {
      const response = await api.get<PfgDoc[]>(
        `/api/pfgdoc/list`
      );
      this.pfgDocs = response.data;
    },
    // Detailed graph
    async fetchPfgDocDetailedGraph(id: string) {
      const response = await api.get<any>(
        `/api/pfgdoc/graph-detailed/${id}`
      );
      return response.data;
    },
    // Forced graph
    async fetchPfgDocForcedGraph() {
      const response = await api.get<any>(
        // Temporarily hardcoded to fetch a specific pfgdoc
        `/api/pfgdoc/forcedgraph/1`
      );
      return response.data;
    },
    async fetchPfgDocForcedGraphAll() {
      const response = await api.get<any>(
        `/api/pfgdoc/forcedgraph/list`
      );
      return response.data;
    },
    // Sankey - all docs
    async fetchPfgDocSankeyGraphAll() {
      const response = await api.get<any>(
        // Temporarily hardcoded to fetch a specific pfgdoc
        `/api/pfgdoc/sankey/list`
      );
      return response.data;
    },
    // PFG Auxiliary
    async fetchPfgAuxs() {
      const response = await api.get<PfgAux[]>(
        `/api/pfgaux/list`
      );
      this.pfgAuxs = response.data;
    },
    async fetchPfgAuxOverviews() {
      const response = await api.get<PfgAuxOverview[]>(
        `/api/pfgaux/overview/list`
      );
      this.PfgAuxOverviews = response.data;
    },
    async fetchPfgAuxDetailedGraph(id: string) {
      const response = await api.get<any>(
        `/api/pfgaux/graph-detailed/${id}`
      );
      return response.data;
    },
     // Forced graph
     async fetchPfgAuxForcedGraph() {
      const response = await api.get<any>(
        // Temporarily hardcoded to fetch a specific pfgdoc
        `/api/pfgaux/forcedgraph/HSC.044_2022-2023`
      );
      return response.data;
    },
    async fetchPfgAuxForcedGraphAll() {
      const response = await api.get<any>(
        `/api/pfgaux/forcedgraph/list`
      );
      return response.data;
    },
    getDateFormatted(date: Date): string {
      const day = date.getDate();
      const month = date.getMonth() + 1; // Months are zero-based
      const year = date.getFullYear();
    
      // Pad the day and month with leading zeros if necessary
      const formattedDay = day < 10 ? `0${day}` : day.toString();
      const formattedMonth = month < 10 ? `0${month}` : month.toString();
    
      return `${formattedDay}/${formattedMonth}/${year}`;
    }
  }
})

function getCurrentDateFormatted(): string {
  const currentDate = new Date();
  const day = currentDate.getDate();
  const month = currentDate.getMonth() + 1; // Months are zero-based
  const year = currentDate.getFullYear();

  // Pad the day and month with leading zeros if necessary
  const formattedDay = day < 10 ? `0${day}` : day.toString();
  const formattedMonth = month < 10 ? `0${month}` : month.toString();

  return `${formattedDay}/${formattedMonth}/${year}`;
}

