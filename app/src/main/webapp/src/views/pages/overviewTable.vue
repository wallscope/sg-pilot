<template>
<div class="answer-container">
  <div class="headers">
    <h4> Document</h4>
    <h4> Directorate</h4>
  </div>
  <div class="answers">some text
  </div>
</div>
<div class="answers">
</div>
</template>
<script setup lang="ts">
import { usePfgStore } from '@/stores/pfg';
import { ref, onMounted, Ref } from "vue";

  </script>
  
<style lang="scss" scoped>
  .answer-container {
    .headers {
      display: grid;
      grid-template-columns: 5fr 5fr;
      margin-bottom: 10px;
    }
    .answers {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }
  }
  .search-container {
    display: grid;
    grid-template-columns: 9fr 3fr;
    align-items: center;
  }
  
  .filters-container,
  .search-container {
    display: grid;
    grid-template-columns: 1fr 200px;
    grid-gap: 10px;
  }
  .filters {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    grid-gap: 15px;
    border-right: 2px solid #d9d9d9;
    padding-right: 10px;
  }
  
  .filters-container {
    padding-bottom: 10px;
    border-bottom: 2px solid #262849;
  }
  
  .reset {
    button {
      background: #262849;
      color: white;
      border: 2px solid #262849;
      transition: all 0.2s;
      border-radius: 0;
      padding: 8px 20px;
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-top: 20px;
      &:hover {
        color: #262849;
        background: white;
      }
    }
  }
  </style>
  