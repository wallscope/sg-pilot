<template>
<div class="internalDocs" v-if="internalDocsList && internalDocsList.length > 0">
  <h3>Relationship details</h3>
  <div class="docs">
    <table>
      <thead>
        <tr>
          <th>Key Info</th>
          <th>Contact Info</th>
          <th>Extra Info</th>
          <th>Keywords</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(doc, i) in internalDocsList" :key="doc.uri">
          <!-- key info -->
          <td>
            <div class="wrapped-data"><strong>Directorate: </strong>{{ doc.directorate }}</div>
            <div class="wrapped-data" v-if="doc.policyTitle"><strong>Policy: </strong>{{ doc.policyTitle }}</div>
            <div class="wrapped-data" v-if="doc.commitmentTitle"><strong>Commitment: </strong>{{ doc.commitmentTitle }}</div>
          </td>
          <!-- contact info -->
          <td>
            <div class="wrapped-data" v-if="doc.director"><strong>Director: </strong>{{ doc.director }}</div>
            <div class="wrapped-data" v-if="doc.keyContact"><strong>Key contact: </strong>{{ doc.keyContact }}</div>
            <div class="wrapped-data" v-if="doc.lead"><strong>Lead: </strong>{{ doc.lead }}</div>
          </td>
          <!-- extra info -->
          <td>
            <div class="wrapped-data" v-if="doc.id"><strong>Id: </strong>{{ doc.id }}</div>
            <div class="wrapped-data" v-if="doc.date"><strong>Date: </strong>{{ doc.date }}</div>
            <div class="wrapped-data" v-if="doc.priority"><strong>Priority: </strong>{{ doc.priority }}</div>
            <div class="wrapped-data" v-if="doc.accessURL"><strong>URL: </strong><a :href="doc.accessURL" target="_blank">{{ doc.accessURL }}</a></div>
          </td>
          <!-- keywords -->
          <td>
            <input id="Keywords" placeholder="" v-model="doc.keywords[0]" disabled/>
            <input id="Keywords" placeholder="" v-model="doc.keywords[1]" disabled/>
            <input id="Keywords" placeholder="" v-model="doc.keywords[2]" disabled/>
            <input id="Keywords" placeholder="" v-model="doc.keywords[3]" disabled/>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
</template>
<script lang="ts">
import { defineComponent, computed} from "vue";
import { useAllDocsStore } from '@/stores/alldocs';

export default defineComponent({
  name: "NodeOverviewTable",
  setup() {
    const allDocsStore = useAllDocsStore();

    const internalDocsList = computed(() => {
      const allDocs = allDocsStore.nodeOverviewList
      return allDocs
    });

    return {
      internalDocsList,
    };
  },
});
</script>

<style lang="scss" scoped>
td p, td input {
  margin: 0;
  padding: 0;
}
.red-background {
  font-weight: bold;
  color: white;
  background-color: #dc3545;
}
.green-background {
  font-weight: bold;
  color: white;
  background-color: green;
}
input,
textarea {
  padding: 4.5px 5px;
  font-size: 16px;
  font-weight: normal;
  width: 98%;
}
.wrapped-data {
  padding: 4.5px 5px;
  font-size: 16px;
  font-weight: normal;
  width: 100%;
  max-height: auto;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal;
  border: none;
  background-color: transparent;
  margin: 1px 0;
}
.data-divider {
  margin: 1px auto;
  width: 5%;
}

input.border {
  border: 2px solid #ffffff;
  background-color: #ffffff;
}

label {
  font-size: 14px;
  font-weight: 500;
  color: black;
}
select {
  font-size: 16px;
  padding: 5.5px 8px;
  border: 2px solid black;
  border-radius: 5px;
  background-color: #ffffff;
  &:disabled {
    margin-left: 6px;
    color: black;
    opacity: 1;
    border: none;
    border-radius: 5px;
    appearance: none;
    background: none;
  }
}
.docs {
  display: flex;
  flex-direction: row;
  overflow-x: scroll;
}
.add {margin-bottom: 20px;}
.add form{
  display: flex;
    flex-flow: row wrap;
}
.add label{
  margin-right:15px;
}
.add input{margin-right:15px;}
.internalDocs {
  padding:20px;
}
input.internalDocs {
  margin-right:20px;
}
.actions {
  display: flex;
  button {
    margin-left: 5px;
    &:first-child {
      margin-left: 0;
    }
  }
}
//.v-btn:hover {
//  color: white;
//  background: rgb(106, 168, 201);
//}
table {
  border-spacing: 0;
}
th {
  background: rgb(106, 168, 201);
  text-align: left;
  color: white;
}
th,
td {
  border: 1px solid #e0e0e0;
  padding: 5px 8px;
  width: 252px;
}
td {
  background: white;
  font-size: 16px;
  transition: all 0.2s;
  color: black;
}

// scroll bar
::-webkit-scrollbar {
  width: 8px;
}
::-webkit-scrollbar-track {
  background: #e5e5e5;
}
::-webkit-scrollbar-thumb {
  background: rgb(106, 168, 201);
}
::-webkit-scrollbar-thumb:hover {
  background: rgb(106, 168, 201);
}
.SearchBar-container, .add-doc {
  max-width: 1200px;
}
.add-doc {
  display: flex;
  align-items: center;
  button {
    margin-left: auto;
  }
}
.pagination-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 1rem;
}

.pagination-button {
  margin: 0 0.2rem;
  padding: 0.5rem 1rem;
  border: 1px solid #ccc;
  background-color: #fff;
}

.pagination-info {
  margin: 0 1rem;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
}

.editable-page-input {
  margin: 0 1rem;
  width: 45px;
  text-align: center;
}
.dropdown {
  position: relative;
  display: inline-block;
}

.dropdown-content {
  display: none;
  position: absolute;
  background-color: white;
  border: 2px solid rgb(106, 168, 201);
  list-style: none;
  padding: 0;
  max-height: 120px;
  overflow-y: auto;
  z-index: 9999;
}

.dropdown-content li {
  padding: 5px;
  cursor: pointer;
}

.dropdown:hover .dropdown-content {
  display: block;
}
.dropdown input::placeholder {
  color: rgb(106, 168, 201); 
}
.disabled {
  pointer-events: none;
  opacity: 0.5;
}
</style>
