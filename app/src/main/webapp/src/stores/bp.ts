import { defineStore } from 'pinia';
import api from "@/utils/api";

// Business Planning Store
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

export const useBpStore = defineStore('bp', {
  state: () => ({
    pfgDocDetailedGraphData: {} as any,
    bpDoc: DEF_BP_DATA,
    bpDocs: [DEF_BP_DATA] as BpDoc[],
  }),
  actions:{
    async fetchBPDocs() {
      const response = await api.get<BpDoc[]>(
        `/api/bpdoc/list`
      );
      this.bpDocs = response.data;
    },
    async fetchBpDocDetailedGraph() {
      const response = await api.get<any>(
        // Temporarily hardcoded to fetch a specific pfgdoc
        `/api/bpdoc/graph-detailed/1`
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
