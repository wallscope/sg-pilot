// passwordStore.js
import { defineStore } from 'pinia';

export const usePasswordStore = defineStore('password', {
  state: () => ({
    password: 'GBJ89Dp3Ivu025r' // Replace with your actual password
  }),
  actions: {
    checkPassword(inputPassword: string) {
      return inputPassword === this.password;
    }
  }
});
