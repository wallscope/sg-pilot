import { defineStore } from 'pinia';
import api from "@/utils/api";

// Business Planning Doc Store
export interface BpDoc {
  uri: string;
  filename: string;
  dg: string[];
  directorate: string[];
  director: string[];
  keyContact: string[];
  contactEmail: string[];
  divisions: string[];
  divisionLeads: string[];
  resProgramme: string;
  resTotalOperatingCosts: string;
  resCorporateRunningCosts: string;
  resTotal: string;
  resCapital: string;
  resFinancialTransactions: string;
  keywords: string[];
}

// Initialized BPDoc
export const DEF_BP_DATA: BpDoc = {
  uri: '',
  filename: '',
  dg: [''],
  directorate: [''],
  director: [''],
  keyContact: [''],
  contactEmail: [''],
  divisions: [''],
  divisionLeads: [''],
  resProgramme: '',
  resTotalOperatingCosts: '',
  resCorporateRunningCosts: '',
  resTotal: '',
  resCapital: '',
  resFinancialTransactions: '',
  keywords: [''],
};

// Business Planning commitment Store
export interface BPCom {
  uri: string;
  filename: string;
  commitment: string;
  priority: string;
  commitmentLead: string;
  projectBudget: string;
  budgetSufficient: string;
  deliveryPartners: string[];
  primaryOutcomes: string[];
  secondaryOutcomes: string[];
  keywords: string[];
}

// Template objects
export const DEF_BPCOM_DATA: BPCom = {
  uri: '',
  filename: '',
  commitment: '',
  priority: '',
  commitmentLead: '',
  projectBudget: '',
  budgetSufficient: '',
  deliveryPartners: [''],
  primaryOutcomes: [''],
  secondaryOutcomes: [''],
  keywords: [''],
}

export const useBpStore = defineStore('bp', {
  state: () => ({
    pfgDocDetailedGraphData: {} as any,
    bpDoc: DEF_BP_DATA,
    bpDocs: [DEF_BP_DATA] as BpDoc[],
  }),
  actions:{
    // BP Documents
    async fetchBPDocs() {
      const response = await api.get<BpDoc[]>(
        `/api/bpdoc/list`
      );
      this.bpDocs = response.data;
    },
    // Detailed graph
    async fetchBpDocDetailedGraph() {
      const response = await api.get<any>(
        // Temporarily hardcoded to fetch a specific bpdoc
        `/api/bpdoc/graph-detailed/BusinessPlan2023-24-DGCorporate_Propriety_EthicsDirectoratealiascopy_2_.xlsx`
      );
      return response.data;
    },
    // Forced graph
    async fetchBpDocForcedGraph() {
      const response = await api.get<any>(
        // Temporarily hardcoded to fetch a specific bpdoc
        `/api/bpdoc/forcedgraph/BusinessPlan2023-24-DGCommunities_UkraineSettlementDirectoratealiascopy.xlsx`
      );
      return response.data;
    },
    async fetchBpDocForcedGraphAll() {
      const response = await api.get<any>(
        // Temporarily hardcoded to fetch a specific bpdoc
        `/api/bpdoc/forcedgraph/list`
      );
      return response.data;
    },
    // BP Commitments
    async fetchBpComDetailedGraph() {
      const response = await api.get<any>(
        // Temporarily hardcoded to fetch a specific bpdoc
        `/api/bpcom/graph-detailed/75`
      );
      return response.data;
    },
    // Forced graph
    async fetchBpComForcedGraph() {
      const response = await api.get<any>(
        // Temporarily hardcoded to fetch a specific bpcom
        `/api/bpcom/forcedgraph/75`
      );
      return response.data;
    },
    async fetchBpComForcedGraphAll() {
      const response = await api.get<any>(
        `/api/bpcom/forcedgraph/list`
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
