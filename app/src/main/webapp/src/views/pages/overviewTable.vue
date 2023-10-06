<template>
  <div class="SearchBar-container">
    <SearchBar v-model="search" placeholder="Search References"></SearchBar>
  </div>
<div class="internalDocs">
  <p>Documents ({{ filteredDocsList.length }})</p>
  <div class="docs">
    <table>
      <thead>
        <tr>
          <th>Title</th>
          <th>Directorate</th>
          <th>Outcomes</th>
          <th>Keywords</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(doc, i) in paginatedDocsList" :key="doc.uri">
          <td>
              <div class="wrapped-data" v-text="doc.title ? doc.title : '<Title>'"></div>
              <hr class="data-divider">
              <input id="Doc type" placeholder="Doc type" :value="doc.docType" disabled/>
          </td>
          <td>
            <div class="wrapped-data" v-text="doc.dg ? doc.dg : '<Director General>'"></div>
              <hr class="data-divider">
            <div class="wrapped-data" v-text="doc.directorate ? doc.directorate : '<Directorate>'"></div>
          </td>
          <td><strong>
              <p v-if="doc.primaryOutcomes.length > 0">Primary </p></strong>
            <div class="dropdown">
              <input v-if="doc.primaryOutcomes.length > 0" id="Primary Outcomes" placeholder="View primary outcomes" disabled/>
              <ul class="dropdown-content">
                <li v-for="outcome in doc.primaryOutcomes">{{ outcome }}</li>
              </ul>
            </div>
            <strong><p v-if="doc.secondaryOutcomes.length > 0">Secondary</p></strong>
            <div class="dropdown">
              <input v-if="doc.secondaryOutcomes.length > 0" id="Primary Outcomes" placeholder="View secondary outcomes" disabled/>
              <ul class="dropdown-content">
                <li v-for="outcome in doc.secondaryOutcomes">{{ outcome }}</li>
              </ul>
            </div>
          </td>
          <td>
            <input id="Keywords" placeholder="" v-model="doc.keywords[0]" disabled/>
            <input id="Keywords" placeholder="" v-model="doc.keywords[1]" disabled/>
            <input id="Keywords" placeholder="" v-model="doc.keywords[2]" disabled/>
            <input id="Keywords" placeholder="" v-model="doc.keywords[3]" disabled/>
          </td>
          <td>
            <!-- <router-link v-if="doc.primaryOutcomes.length > 0 || doc.secondaryOutcomes.length > 0" :to="{ path: 'graphChartForce', query: { outcomes: doc.primaryOutcomes.concat(doc.secondaryOutcomes) } }"> 
               <VCardTitle><v-btn variant="outlined" class="open"><v-icon icon="mdi-share-variant" /> Outcome relationships</v-btn></VCardTitle> 
             </router-link> -->
            <router-link :to="{ path: 'detailedChart', query: { uri: doc.uri.split('/SG/')[1]} }">
              <VCardTitle><v-btn variant="outlined" class="open"><v-icon icon="mdi-file-document" /> Document details</v-btn></VCardTitle>
            </router-link> 
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  <div class="pagination-container">
    <button @click="goToPage(1)" :disabled="currentPage === 1" class="pagination-button" :style="{ cursor: currentPage > 1 ? 'pointer' : 'default' }">First</button>
    <button @click="prevPage" :disabled="currentPage === 1" class="pagination-button" :style="{ cursor: currentPage > 1 ? 'pointer' : 'default' }">Previous</button>
    <span class="pagination-info">
      Page <input type="string" v-model="currentPage" @input="goToPage(currentPage)" class="editable-page-input"> of {{ totalPages }}
    </span>
    <button @click="nextPage" :disabled="currentPage === totalPages" class="pagination-button" :style="{ cursor: currentPage < totalPages ? 'pointer' : 'default' }">Next</button>
    <button @click="goToPage(totalPages)" :disabled="currentPage === totalPages" class="pagination-button" :style="{ cursor: currentPage < totalPages ? 'pointer' : 'default' }">Last</button>
  </div>
</div>
</template>
<script lang="ts">
import { defineComponent, ref, computed, watch, toRef, onMounted } from "vue";
import { useAllDocsStore } from '@/stores/alldocs';
import SearchBar from "@/layouts/components/SearchBar.vue";
import Typeahead from "vue3-simple-typeahead";
import "vue3-simple-typeahead/dist/vue3-simple-typeahead.css"; //Optional default CSS
import Fuse from "fuse.js";
import { refDebounced } from "@vueuse/core";
import { storeToRefs } from "pinia";
import { usePfgStore } from '@/stores/pfg';
import { useBpStore } from '@/stores/bp';
import { DocOverview } from "@/stores/alldocs";

type TypeInput = { input: string; items: string[] };

export default defineComponent({
  name: "OverviewTable",
  components: {
    SearchBar,
    Typeahead,
  },
  setup() {
    const pfgStore = usePfgStore();
    const bpStore = useBpStore();
    const allDocsStore = useAllDocsStore()
    const search = ref('');
    const debouncedSearchTerm = refDebounced(search, 500);

    const fuseOptions: Fuse.IFuseOptions<DocOverview> = {
      keys: [
        { name: "title", weight: 3 },
        { name: "docType", weight: 2 },
        { name: "dg", weight: 3 },
        { name: "directorate", weight: 3 },
        { name: "primaryOutcomes", weight: 1 },
        { name: "secondaryOutcomes", weight: 1 },
        { name: "keywords", weight: 1 },
      ],
      includeMatches: true,
      findAllMatches: true,
      ignoreLocation: true,
      threshold: 0,
    };
    const searchFuse = ref(new Fuse([], fuseOptions));

    const internalDocsList = computed(() => {
      const allDocs = pfgStore.PfgAuxOverviews.concat(bpStore.bpComOverviews)
      return allDocs
    });

    const filteredDocsList = computed(() => {
      if (debouncedSearchTerm.value == "") return internalDocsList.value;
      const matches = searchFuse.value.search(debouncedSearchTerm.value);
      return matches
        .sort((a, b) => (a.score || 0) - (b.score || 0))
        .map((m) => m.item);
    });

    // Watcher
    watch(internalDocsList, (newVal) => {
      searchFuse.value.setCollection(newVal);
    });

    // Pagination
    const itemsPerPage = 20;
    const currentPage = ref(1);

    const totalPages = computed(() =>
      Math.ceil(filteredDocsList.value.length / itemsPerPage)
    );

    const paginatedDocsList = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage;
      const end = start + itemsPerPage;
      return filteredDocsList.value.slice(start, end);
    });

    // Pagination methods
    function goToPage(page: number) {
      currentPage.value = page;
    }

    function prevPage() {
      if (currentPage.value > 1) {
        currentPage.value--;
      }
    }

    function nextPage() {
      if (currentPage.value < totalPages.value) {
        currentPage.value++;
      }
    }

    // Mounted
    async function mounted() {
      allDocsStore.updateNodeOverviewList([])
      await pfgStore.fetchPfgAuxOverviews();
      await bpStore.fetchBpComOverviews();
    }
    onMounted(mounted);

    return {
      search,
      internalDocsList,
      filteredDocsList,
      currentPage,
      totalPages,
      paginatedDocsList,
      goToPage,
      prevPage,
      nextPage,
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
