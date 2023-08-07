<template>
  <div class="SearchBar-container">
    <SearchBar v-model="search" placeholder="SearchBar for Docs"></SearchBar>
  </div>
  <div class="add-doc">
    <p>PFG Auxs ({{ filteredDocsList.length }})</p>
  </div>
<div class="internalDocs">
  <p><strong>All Docs</strong></p>
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
            <input id="policyTitle" placeholder="Title" v-model="doc.title" disabled/>
            <input id="Doc type" placeholder="Doc type" :value="doc.docType" disabled/>
          </td>
          <td>
            <input id="dG" placeholder="Director General" v-model="doc.dG" disabled/>
            <input id="directorate" placeholder="Directorate" v-model="doc.directorate" disabled/>
          </td>
          <td><strong>
              <p>Primary </p></strong>
            <input id="Primary Outcomes" placeholder="Primary Outcomes" v-model="doc.primaryOutcomes[0]" disabled/><strong>
              <p>Secondary</p></strong>
            <input id="Secondary Outcomes" placeholder="Secondary Outcomes" v-model="doc.secondaryOutcomes[0]" disabled/>
          </td>
          <td>
            <input id="Keywords" placeholder="Keywords" v-model="doc.keywords[0]" disabled/>
          </td>
          <td>
            <router-link :to="{ path: 'detailedChart', query: { uri: doc.uri.split('/SG/')[1]} }">
              <VCardTitle><v-btn variant="outlined" class="open"><v-icon icon="mdi-eye-outline" /> Document details</v-btn></VCardTitle>
            </router-link> 
            <router-link :to="{ path: 'graphChartForce', query: { uri: doc.uri.split('/SG/')[1]} }">
              <VCardTitle><v-btn variant="outlined" class="open"><v-icon icon="mdi-eye-outline" /> NPF relationships</v-btn></VCardTitle>
            </router-link> 
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  <div class="pagination-container">
    <button @click="goToPage(1)" :disabled="currentPage === 1" class="pagination-button" :style="{ cursor: currentPage > 1 ? 'pointer' : 'default' }">First</button>
    <button @click="prevPage" :disabled="currentPage === 1" class="pagination-button" :style="{ cursor: currentPage > 1 ? 'pointer' : 'default' }">Previous</button>
    <span class="pagination-info">Page {{ currentPage }} of {{ totalPages }}</span>
    <button @click="nextPage" :disabled="currentPage === totalPages" class="pagination-button" :style="{ cursor: currentPage < totalPages ? 'pointer' : 'default' }">Next</button>
    <button @click="goToPage(totalPages)" :disabled="currentPage === totalPages" class="pagination-button" :style="{ cursor: currentPage < totalPages ? 'pointer' : 'default' }">Last</button>
  </div>
</div>
</template>
<script lang="ts">
import { defineComponent, ref, computed, watch, toRef, onMounted } from "vue";
import SearchBar from "@/layouts/components/SearchBar.vue";
import Typeahead from "vue3-simple-typeahead";
import "vue3-simple-typeahead/dist/vue3-simple-typeahead.css"; //Optional default CSS
import Fuse from "fuse.js";
import { refDebounced } from "@vueuse/core";
import { storeToRefs } from "pinia";
import { PfgAuxOverview, usePfgStore } from '@/stores/pfg';

type TypeInput = { input: string; items: string[] };

export default defineComponent({
  name: "OverviewTable",
  components: {
    SearchBar,
    Typeahead,
  },
  setup() {
    const pfgStore = usePfgStore();
    const minInput = ref(3);
    const search = refDebounced(ref(""), 2000);

    const fuseOptions: Fuse.IFuseOptions<PfgAuxOverview> = {
      keys: [
        { name: "title", weight: 3 },
        { name: "docType", weight: 2 },
        { name: "dG", weight: 3 },
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
    
    // Computed
    const tableStyles = computed(() => {
      return {
        width: 250 + "px",
      };
    });

    const internalDocsList = computed(() => {
      const allDocs = pfgStore.PfgAuxOverviews;

      return allDocs
    });

    const filteredDocsList = computed(() => {
      if (search.value == "") return internalDocsList.value;
      const matches = searchFuse.value.search(search.value);
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
      await pfgStore.fetchPfgAuxOverviews();
    }
    onMounted(mounted);

    return {
      minInput,
      search,
      tableStyles,
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
  background: #cb5185;
}
::-webkit-scrollbar-thumb:hover {
  background: #a5416c;
}
.SearchBar-container, .add-doc {
  max-width: 1000px;
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
}
</style>
