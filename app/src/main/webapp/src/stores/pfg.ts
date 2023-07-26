import { defineStore } from 'pinia';
import api from "@/utils/api";

// Programme For Government Store
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
export const DEF_PFG_DATA: PfgDoc = {
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
  completionDate: getCurrentDateFormatted(),
  keywords: [''],
}

export const usePfgStore = defineStore('pfg', {
  state: () => ({
    pfgDocDetailedGraphData: {} as any,
    pfgDoc: DEF_PFG_DATA,
    pfgDocs: [DEF_PFG_DATA] as PfgDoc[],
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
    async fetchPfgDocDetailedGraph() {
      const response = await api.get<any>(
        // Temporarily hardcoded to fetch a specific pfgdoc
        `/api/pfgdoc/graph-detailed/1`
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
    async fetchPfgAuxDetailedGraph() {
      const response = await api.get<any>(
        // Temporarily hardcoded to fetch a specific pfgdoc
        `/api/pfgaux/graph-detailed/CLG.001_2019-2020`
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

