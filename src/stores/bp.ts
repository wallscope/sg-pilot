import axios from 'axios';
import { defineStore } from 'pinia';
import { LabeledURI } from './model';

// Business Planning Store
export interface BpDoc {
  id: string;
  pages: BpData;
}

// Data
export interface BpData {
  frontPage: FrontPage;
  // divisional: Divisional;
  // directorate: Directorate;
  // peoplePlan: PeoplePlan;
  // itsosImprovement: ItsosImprovement;
  connections: string[];
}

export interface FrontPage {
  dg: string[];
  directorate: string[];
  director: string[];
  keyContact: string[];
  contactEmail: string[];
  links: Link[];
  divisionsPortfolios: DivisionPortfolio[];
}

export interface Link {
  name: string;
  url: string;
}

export interface DivisionPortfolio {
  name: string;
  lead: string;
}

export const DEF_BP_DATA: BpData = {
  frontPage: {
    dg: [''],
    directorate: [''],
    director: [''],
    keyContact: [''],
    contactEmail: [''],
    links: [{
      name: '',
      url: '',
    }],
    divisionsPortfolios: [{
      name: '',
      lead: '',
    }],
    
  },
  connections: [''],
};

export const useBpStore = defineStore('bp', {
  state: () => ({
    bp: {
      id: '',
      pages: DEF_BP_DATA,
    } as BpDoc,
  }),
  actions:{
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
