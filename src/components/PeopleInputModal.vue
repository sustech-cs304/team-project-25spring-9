<script setup>
import { ref } from 'vue'
import { mdiClose, mdiAccountPlus } from '@mdi/js'
import BaseButton from '@/components/BaseButton.vue'

const props = defineProps({
  show: Boolean,
  title: {
    type: String,
    default: 'Add People Tag'
  },
  placeholder: {
    type: String,
    default: 'Enter people name'
  },
  initialValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['close', 'confirm'])

const inputValue = ref(props.initialValue)

const handleConfirm = () => {
  if (inputValue.value.trim()) {
    emit('confirm', inputValue.value.trim())
    inputValue.value = ''
    emit('close')
  }
}
</script>

<template>
  <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center backdrop-blur-sm bg-white/30">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-md p-6 border border-gray-200" @click.stop>
      <!-- Header -->
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-lg font-medium flex items-center gap-2">
          <svg class="w-6 h-6"><path fill="currentColor" :d="mdiAccountPlus" /></svg>
          {{ title }}
        </h3>
        <BaseButton :icon="mdiClose" color="whiteDark" small @click="$emit('close')" />
      </div>

      <!-- Input -->
      <div class="mb-4">
        <input
          v-model="inputValue"
          type="text"
          class="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-300 focus:border-blue-300"
          :placeholder="placeholder"
          @keyup.enter="handleConfirm"
        />
      </div>

      <!-- Actions -->
      <div class="flex justify-end gap-2">
        <BaseButton label="Cancel" color="whiteDark" @click="$emit('close')" />
        <BaseButton label="Confirm" color="info" @click="handleConfirm" />
      </div>
    </div>
  </div>
</template>