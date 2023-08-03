<template>

  <div class="SearchBar-container">
    <SearchBar v-model="search" placeholder="SearchBar for internal user"></SearchBar>
  </div>
  <!-- <div class="add-user">
    <p>Users ({{ internalUsersList.length }})</p>
  </div><strong style="height: 1px; display: block;">
    <label>{{ userFeedback }}</label></strong> -->

<!-- <div class="internalUsers">
  <p><strong>All users</strong></p>
  <div class="users">
    <table>
      <thead>
        <tr>
          <th>Contact information </th>
          <th>Work information</th>
          <th>Organisation</th>
          <th>System role</th>
          <th>Account</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(user, i) in filteredUsersList" :key="user.username">
          <td> <strong>
              <p>First name:</p></strong>
            <input id="userFirstName" placeholder="First name" :disabled="user.username !== editUser" v-model="user.firstName" :class="{ 'border': user.username !== editUser }"/><strong>
              <p>Last name:</p></strong>
            <input id="lastName" placeholder="Last name" :disabled="user.username !== editUser" v-model="user.lastName" :class="{ 'border': user.username !== editUser }"/><strong>
              <p>Email:</p></strong>
            <input id="email" placeholder="Email" :disabled="user.username !== editUser" v-model="user.email" :class="{ 'border': user.username !== editUser }"/>
          </td>
          <td><strong>
              <p>Department:</p></strong>
            <input id="department" placeholder="Department" :disabled="user.username !== editUser" v-model="user.attributes.department[0]" :class="{ 'border': user.username !== editUser }"/><strong>
              <p>Role:</p></strong>
            <input id="role" placeholder="Role" :disabled="user.username !== editUser" v-model="user.attributes.role[0]" :class="{ 'border': user.username !== editUser }"/><strong>
              <p>Job title:</p></strong>
            <input id="jobTitle" placeholder="Job title" :disabled="user.username !== editUser" v-model="user.attributes.jobTitle[0]" :class="{ 'border': user.username !== editUser }"/>
          </td>
          <td style="width:22%;">
            <typeahead :items="organisationIds" :minInputLength="minInput" placeholder="Organisation" :defaultItem="user.attributes.organisation[0]" :disabled="user.username !== editUser" v-model="user.attributes.organisation[0]" :itemProjection="(item) =&gt; getOrgName(item)" @selectItem="user.attributes.organisation[0] = $event" :class="{ 'border': user.username !== editUser }" style="width: 98%;"></typeahead>
          </td>
          <td style="width:12%;">
            <select id="userRole" placeholder="admin" :disabled="user.username !== editUser" v-model="user.userRole" :class="{ 'border': user.username !== editUser }">
              <option v-if="isAdmin" value="admin">Admin</option>
              <option value="lead">Lead</option>
              <option value="user">User</option>
            </select>
          </td>
          <td style="width:13%;">
            <button class="small white-then-blue" @click="editToggle(user)">
              <font-awesome-icon :icon=" user.username !== editUser ? 'fa-solid fa-pen-to-square':'fa-solid fa-check' "></font-awesome-icon>{{ user.username !== editUser ? 'Edit':'Save'}}
            </button><br/><strong>
              <p>Status:</p></strong>
            <button class="small white-then-blue" @click="toggleAccountStatus(user)" :class="{ 'green-background': user.enabled === true, 'red-background': user.enabled !== true }">
              <font-awesome-icon :icon=" user.enabled === true ? 'fa-solid fa-check':'fa-solid fa-ban' "></font-awesome-icon>{{ user.enabled === true ? 'Enabled':'Disabled'}}          
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div> -->
          
</template>
<script lang="ts">
import { defineComponent, ref, computed, watch, toRef, onMounted } from "vue";
import SearchBar from "@/layouts/components/SearchBar.vue";
import Typeahead from "vue3-simple-typeahead";
import "vue3-simple-typeahead/dist/vue3-simple-typeahead.css"; //Optional default CSS
import Fuse from "fuse.js";
import { refDebounced } from "@vueuse/core";
import { storeToRefs } from "pinia";

type TypeInput = { input: string; items: string[] };

export default defineComponent({
  name: "OverviewTable",
  components: {
    SearchBar,
    Typeahead,
  },
  setup() {
    // const orgStore = useOrgStore();
    // const userFeedback = ref("");
    // const edit = ref(false);
    // const editUser = ref("");
    const minInput = ref(3);
    // const { organisationIds } = storeToRefs(orgStore);
    const search = refDebounced(ref(""), 1000);
    // const orgId = ref("");
    // const isAdmin = ref(false);

    // const fuseOptions: Fuse.IFuseOptions<User> = {
    //   keys: [
    //     { name: "firstName", weight: 1 },
    //     { name: "lastName", weight: 2 },
    //     { name: "email", weight: 2 },
    //     { name: "attributes.department", weight: 1 },
    //     { name: "attributes.role", weight: 1 },
    //     { name: "attributes.jobTitle", weight: 1 },
    //     {
    //       name: "organisation",
    //       getFn: (user) => getOrgName(user.attributes.organisation[0]),
    //       weight: 3,
    //     },
    //   ],
    //   includeMatches: true,
    //   findAllMatches: true,
    //   ignoreLocation: true,
    //   threshold: 0,
    // };
    // const searchFuse = ref(new Fuse([], fuseOptions));
    
    // Computed
    const tableStyles = computed(() => {
      return {
        width: 250 + "px",
      };
    });

    // const internalUsersList = computed(() => {
    //   const allUsers = keycloakStore.admins.concat(keycloakStore.leads).concat(keycloakStore.users);

    //   // Admin sees all users
    //   // Lead sees non-admin users from their org
    //   return isAdmin.value
    //     ? allUsers
    //     : allUsers.filter(user => 
    //       user.attributes.organisation.includes(orgId.value) 
    //       && !user.userRole.includes('admin'));  
    // });

    // const filteredUsersList = computed(() => {
    //   if (search.value == "") return internalUsersList.value;
    //   const matches = searchFuse.value.search(search.value);
    //   return matches
    //     .sort((a, b) => (a.score || 0) - (b.score || 0))
    //     .map((m) => m.item);
    // });

    // // Watcher
    // watch(internalUsersList, (newVal) => {
    //   searchFuse.value.setCollection(newVal);
    // });

    // Mounted
    async function mounted() {
    }
    onMounted(mounted);

    return {
      // orgStore,
      // userFeedback,
      // edit,
      // editUser,
      minInput,
      search,
      // organisationIds,
      tableStyles,
      // internalUsersList,
      // filteredUsersList,
      // getOrgName,
      // editToggle,
      // toggleAccountStatus,
      // orgId,
      // isAdmin,
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
  border: 2px solid black;
  border-radius: 5px;
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
.users {
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
.internalUsers {
  padding:20px;
}
input.internalUsers {
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
table {
  border-spacing: 0;
}
th {
  background: #8bc0e7;
  text-align: left;
}
th,
td {
  border: 1px solid #e0e0e0;
  padding: 5px 8px;
  width: 182px;
}
td {
  background: white;
  font-size: 16px;
  transition: all 0.2s;
  color: black;
}
tr:hover {
  td {
    background: #deafcc;
  }
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
.SearchBar-container, .add-user {
  max-width: 1000px;
}
.add-user {
  display: flex;
  align-items: center;
  button {
    margin-left: auto;
  }
}

</style>
