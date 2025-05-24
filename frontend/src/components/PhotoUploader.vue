<script setup>
import { ref } from 'vue'
import { mdiCloudUpload, mdiClose } from '@mdi/js'
import BaseButton from '@/components/BaseButton.vue'

const props = defineProps({
  show: Boolean
})

const emit = defineEmits(['close', 'upload'])

const isDragging = ref(false)
const uploadQueue = ref([])

const handleDrop = (e) => {
  e.preventDefault()
  isDragging.value = false
  const files = Array.from(e.dataTransfer.files).filter(file => file.type.startsWith('image/'))
  addToQueue(files)
}

const handleFileSelect = (e) => {
  const files = Array.from(e.target.files).filter(file => file.type.startsWith('image/'))
  addToQueue(files)
}

const addToQueue = (files) => {
  for (const file of files) {
    // Add preview URL
    uploadQueue.value.push({
      file,
      preview: URL.createObjectURL(file),
      name: file.name,
      size: formatFileSize(file.size)
    })
  }
}

const removeFromQueue = (index) => {
  URL.revokeObjectURL(uploadQueue.value[index].preview)
  uploadQueue.value.splice(index, 1)
}

const uploadAll = () => {
  uploadQueue.value.forEach(item => {
    emit('upload', item.file)
  })
  uploadQueue.value = []
  emit('close')
}

const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B'
  else if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB'
  else if (bytes < 1073741824) return (bytes / 1048576).toFixed(1) + ' MB'
  return (bytes / 1073741824).toFixed(1) + ' GB'
}
</script>

<template>
  <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center backdrop-blur-sm bg-white/30">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-2xl p-6 border border-gray-200" @click.stop>
      <!-- Header -->
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-lg font-medium">Upload Photos</h3>
        <BaseButton :icon="mdiClose" color="whiteDark" small @click="$emit('close')" />
      </div>

      <!-- Drop Zone -->
      <div
        class="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center mb-4 transition-colors duration-200"
        :class="{ 'border-blue-500 bg-blue-50': isDragging }"
        @dragenter.prevent="isDragging = true"
        @dragleave.prevent="isDragging = false"
        @dragover.prevent
        @drop="handleDrop"
      >
        <svg class="mx-auto h-12 w-12 text-gray-400" viewBox="0 0 24 24">
          <path fill="currentColor" :d="mdiCloudUpload" />
        </svg>
        <p class="mt-2 text-gray-600">
          Drag and drop photos here, or
          <label class="text-blue-500 hover:text-blue-600 cursor-pointer">
            browse
            <input type="file" class="hidden" multiple accept="image/*" @change="handleFileSelect">
          </label>
        </p>
      </div>

      <!-- Upload Queue -->
      <div v-if="uploadQueue.length > 0" class="mb-4">
        <div class="max-h-60 overflow-y-auto">
          <div v-for="(item, index) in uploadQueue" :key="index"
               class="flex items-center p-2 hover:bg-gray-50 rounded">
            <img :src="item.preview" class="w-16 h-16 object-cover rounded mr-4">
            <div class="flex-1">
              <div class="text-sm font-medium text-gray-900">{{ item.name }}</div>
              <div class="text-sm text-gray-500">{{ item.size }}</div>
            </div>
            <BaseButton :icon="mdiClose" color="danger" small @click="removeFromQueue(index)" />
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="flex justify-end gap-2">
        <BaseButton label="Cancel" color="whiteDark" @click="$emit('close')" />
        <BaseButton
          v-if="uploadQueue.length > 0"
          label="Upload All"
          color="info"
          @click="uploadAll"
        />
      </div>
    </div>
  </div>
</template>
