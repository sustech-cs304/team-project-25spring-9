<script setup>
import { ref } from 'vue'
import { useToast } from 'vue-toastification'
import { mdiCloudUpload, mdiClose } from '@mdi/js'
import BaseButton from '@/components/BaseButton.vue'

const toast = useToast()

const props = defineProps({
  show: Boolean
})

const emit = defineEmits(['close', 'upload'])

const isDragging = ref(false)
const uploadQueue = ref([])

// 新增：每张图片的 manage tag 面板状态
const showTagDialog = ref([])
const manualTagInput = ref([])
const pendingTags = ref([]) // 每个图片一个数组
const autoTagLoading = ref([])
const autoTagError = ref([])
const autoTagOptions = ref([])

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
    uploadQueue.value.push({
      file,
      preview: URL.createObjectURL(file),
      name: file.name,
      size: formatFileSize(file.size),
    })
    // 初始化每个图片的 tag 管理状态
    showTagDialog.value.push(false)
    manualTagInput.value.push('')
    pendingTags.value.push([])
    autoTagLoading.value.push(false)
    autoTagError.value.push('')
    autoTagOptions.value.push([])
  }
}

const removeFromQueue = (index) => {
  URL.revokeObjectURL(uploadQueue.value[index].preview)
  uploadQueue.value.splice(index, 1)
  showTagDialog.value.splice(index, 1)
  manualTagInput.value.splice(index, 1)
  pendingTags.value.splice(index, 1)
  autoTagLoading.value.splice(index, 1)
  autoTagError.value.splice(index, 1)
  autoTagOptions.value.splice(index, 1)
}

const uploadAll = async () => {
  if (uploadQueue.value.length === 0) {
    return
  }

  try {
    for (let i = 0; i < uploadQueue.value.length; i++) {
      const item = uploadQueue.value[i]
      const tags = pendingTags.value[i] || []
      if (!item.file) continue
      emit('upload', item.file, tags)
    }
    uploadQueue.value = []
    showTagDialog.value = []
    manualTagInput.value = []
    pendingTags.value = []
    autoTagLoading.value = []
    autoTagError.value = []
    autoTagOptions.value = []
    emit('close')
  } catch (error) {
    console.error('Upload error:', error)
  }
}

const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B'
  else if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB'
  else if (bytes < 1073741824) return (bytes / 1048576).toFixed(1) + ' MB'
  return (bytes / 1073741824).toFixed(1) + ' GB'
}

// tag manage methods for each image
const handleManualTagInput = (e, idx) => {
  if (e.key === 'Enter' && manualTagInput.value[idx].trim()) {
    const newTag = manualTagInput.value[idx].trim()
    if (!pendingTags.value[idx].includes(newTag)) {
      pendingTags.value[idx].push(newTag)
    }
    manualTagInput.value[idx] = ''
  }
}
const removePendingTag = (idx, tag) => {
  pendingTags.value[idx] = pendingTags.value[idx].filter(t => t !== tag)
}
const openAutoTagModal = async (idx) => {
  autoTagLoading.value[idx] = true
  autoTagError.value[idx] = ''
  autoTagOptions.value[idx] = []
  try {
    const file = uploadQueue.value[idx].file
    if (!file) throw new Error('No file to autotag')
    const formData = new FormData()
    formData.append('file', file)
    const response = await fetch('http://10.16.60.67:8123/auto_tag/', {
      method: 'POST',
      body: formData
    })
    const result = await response.json()
    if (result && Array.isArray(result.tags)) {
      autoTagOptions.value[idx] = result.tags
    } else {
      throw new Error(result.msg || 'No tags found')
    }
  } catch (err) {
    autoTagError.value[idx] = err.message || 'Failed to fetch autotags'
  } finally {
    autoTagLoading.value[idx] = false
  }
}
const toggleSelectAutoTag = (idx, tag) => {
  if (!pendingTags.value[idx].includes(tag)) {
    pendingTags.value[idx].push(tag)
  }
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
               class="flex items-center p-2 hover:bg-gray-50 rounded gap-4">
            <img :src="item.preview" class="w-16 h-16 object-cover rounded mr-2">
            <div class="flex-1">
              <div class="text-sm font-medium text-gray-900">{{ item.name }}</div>
              <div class="text-sm text-gray-500">{{ item.size }}</div>
              <!-- 显示待添加的标签，并可移除 -->
              <div v-if="pendingTags[index] && pendingTags[index].length" class="flex flex-wrap gap-1 mt-1">
                <span v-for="tag in pendingTags[index]" :key="tag"
                  class="px-2 py-0.5 rounded-full bg-blue-50 text-blue-700 border border-blue-200 text-xs flex items-center gap-1">
                  {{ tag }}
                  <button
                    @click="removePendingTag(index, tag)"
                    class="ml-1 text-xs text-gray-400 hover:text-red-500 focus:outline-none"
                    title="Remove tag"
                    type="button"
                  >×</button>
                </span>
              </div>
              <!-- 新增：Manage Tag 面板按钮 -->
              <button class="mt-1 px-2 py-1 rounded bg-blue-100 hover:bg-blue-200 text-blue-700 text-xs"
                @click="showTagDialog[index] = true">
                Add Tags
              </button>
              <!-- Manage Tag 面板 -->
              <div v-if="showTagDialog[index]" class="fixed inset-0 z-60 flex items-center justify-center bg-black/40">
                <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-md relative" @click.stop>
                  <div class="flex justify-between items-center mb-4">
                    <h3 class="text-lg font-medium">Add Tags</h3>
                    <button class="p-1 rounded-full hover:bg-gray-200" @click="showTagDialog[index] = false">
                      <svg class="w-6 h-6" viewBox="0 0 24 24">
                        <path fill="currentColor" :d="mdiClose" />
                      </svg>
                    </button>
                  </div>
                  <!-- Tags to Add -->
                  <div class="mb-6">
                    <label class="block text-sm font-medium text-gray-700 mb-2">Tags to Add</label>
                    <div class="flex flex-wrap gap-2 mb-4">
                      <span v-for="tag in pendingTags[index]" :key="tag"
                        class="px-3 py-1 rounded-full bg-blue-50 text-blue-700 border border-blue-200 flex items-center gap-1">
                        {{ tag }}
                        <button @click="removePendingTag(index, tag)"
                          class="hover:text-red-500 focus:outline-none"
                          type="button">
                          ×
                        </button>
                      </span>
                    </div>
                    <div class="flex gap-2">
                      <input
                        v-model="manualTagInput[index]"
                        type="text"
                        class="flex-1 px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-300"
                        placeholder="Type a tag and press Enter"
                        @keyup.enter="handleManualTagInput($event, index)"
                      />
                    </div>
                  </div>
                  <!-- Auto-tag Section -->
                  <div class="mb-6">
                    <div class="flex justify-between items-center mb-2">
                      <label class="text-sm font-medium text-gray-700">Auto-Generated Tags</label>
                      <button
                        class="px-3 py-1 text-sm rounded bg-blue-100 hover:bg-blue-200 text-blue-700"
                        :class="{ 'opacity-50 cursor-wait': autoTagLoading[index] }"
                        @click="openAutoTagModal(index)"
                        :disabled="autoTagLoading[index]"
                      >
                        {{ autoTagLoading[index] ? 'Generating...' : 'Generate Tags' }}
                      </button>
                    </div>
                    <div v-if="autoTagLoading[index]" class="py-8 text-center">
                      <div class="animate-spin rounded-full h-8 w-8 border-4 border-blue-500 border-t-transparent mx-auto mb-2"></div>
                      <div class="text-gray-600">Analyzing image...</div>
                    </div>
                    <div v-else-if="autoTagError[index]" class="bg-red-50 text-red-600 p-4 rounded-lg text-center">
                      {{ autoTagError[index] }}
                    </div>
                    <div v-else-if="autoTagOptions[index].length > 0" class="flex flex-wrap gap-2">
                      <button
                        v-for="tag in autoTagOptions[index]"
                        :key="tag"
                        @click="toggleSelectAutoTag(index, tag)"
                        :class="[
                          'px-3 py-1 rounded-full border-2 text-sm transition-all duration-200',
                          pendingTags[index].includes(tag)
                            ? 'bg-blue-50 text-blue-700 border-blue-500'
                            : 'bg-white text-gray-700 border-gray-300 hover:border-blue-400'
                        ]"
                      >
                        {{ tag }}
                      </button>
                    </div>
                  </div>
                  <div class="flex justify-end gap-3 pt-4 border-t">
                    <button
                      class="px-4 py-2 rounded-lg hover:bg-gray-100 text-gray-700 font-medium"
                      @click="showTagDialog[index] = false"
                    >
                      Done
                    </button>
                  </div>
                </div>
              </div>
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
