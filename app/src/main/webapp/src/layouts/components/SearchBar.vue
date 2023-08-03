<template>
  <div class="search-container">
    <input
      id="search"
      type="text"
      :placeholder="placeholder"
      :value="modelValue"
      @input="updateModelValue"
    >
    <div class="icon-holder">
      <v-icon v-if="!modelValue">mdi-magnify</v-icon>
      <template v-else>
        <button @click="resetInput">
          <v-icon>mdi-close-circle</v-icon>
        </button>
      </template>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, EmitsOptions } from "vue";

export default defineComponent({
  name: "SearchBar",
  props: {
    placeholder: String,
    modelValue: String,
  },
  emits: ['update:modelValue'] as EmitsOptions,
  setup(props, { emit }) {
    
    const resetInput = () => {
      emit('update:modelValue', '');
    };

    const updateModelValue = (event: Event) => {
      const target = event.target as HTMLInputElement;
      const newValue = target.value;
      emit('update:modelValue', newValue || ''); // Emit an empty string if newValue is null
    };

    return {
      resetInput,
      updateModelValue
    };
  },
});
</script>



<style lang="scss" scoped>
.search-container {
  padding: 5px;
  display: flex;
  align-items: center;
  .icon-holder {
    margin-left: -35px;
  }
  input {
    border: 2px solid black;
    border-radius: 5px;
    padding: 4.5px 25px 4.5px 5px;
    font-size: 16px;
    font-weight: normal;
    width: calc(100% - 30px);
    &:focus {
      border: 2px solid #4970c4;
    }
  }
  button {
    border: none;
    background: none;
  }
  button,
  svg {
    width: 20px;
  }
}
</style>
