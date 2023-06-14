import axios from 'axios';
import { defineStore } from 'pinia';
import { LabeledURI } from './model';

// Programme For Government Store
export interface PfgDoc {
  id: string;
  data: PfgData
}

// Data
export interface PfgData {
  ministerialPortfolio: string[];
  dg: string[];
  directorate: string[];
  unitBranch: string[];
  leadOfficial: string[];
  scsClearance: string[];
  portfolioFbpClearance: string[];
  portfolioCoordinator: string[];
  dateOfCompletion: string;
  connections: string[];
}

// Template objects
export const DEF_PFG_DATA: PfgData = {
  ministerialPortfolio: [''],
  dg: [''],
  directorate: [''],
  unitBranch: [''],
  leadOfficial: [''],
  scsClearance: [''],
  portfolioFbpClearance: [''],
  portfolioCoordinator: [''],
  dateOfCompletion: getCurrentDateFormatted(),
  connections: [''],
}

export const usePfgStore = defineStore('pfg', {
  state: () => ({
    pfg: {
      id: '', 
      data: DEF_PFG_DATA,
    } as PfgDoc,
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

