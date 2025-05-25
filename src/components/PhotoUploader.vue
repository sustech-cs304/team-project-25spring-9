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

// 新增：每张图片的 tags 输入
const tagInputs = ref([])

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
      tags: [] // Initialize tags array for each file
    })
    tagInputs.value.push('') // Initialize tag input string
  }
}

const removeFromQueue = (index) => {
  URL.revokeObjectURL(uploadQueue.value[index].preview)
  uploadQueue.value.splice(index, 1)
  tagInputs.value.splice(index, 1)
}

const uploadAll = async () => {
  if (uploadQueue.value.length === 0) {
    toast.error('Please select files first')
    return
  }

  try {
    // 对每个文件进行上传
    for (let i = 0; i < uploadQueue.value.length; i++) {
      const item = uploadQueue.value[i]
      const tags = tagInputs.value[i]
        ?.split(',')
        .map(tag => tag.trim())
        .filter(tag => tag.length > 0) || []

      // 确保文件内容存在
      if (!item.file) {
        toast.error(`Failed to upload ${item.name || 'file'}: No content`)
        continue
      }

      emit('upload', item.file, tags)
    }

    // 清理状态
    uploadQueue.value = []
    tagInputs.value = []
    emit('close')
    toast.success('All files uploaded successfully')

  } catch (error) {
    console.error('Upload error:', error)
    toast.error(`Upload failed: ${error.message}`)
  }
}

// 其他辅助方法
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
               class="flex items-center p-2 hover:bg-gray-50 rounded gap-4">
            <img :src="item.preview" class="w-16 h-16 object-cover rounded mr-2">
            <div class="flex-1">
              <div class="text-sm font-medium text-gray-900">{{ item.name }}</div>
              <div class="text-sm text-gray-500">{{ item.size }}</div>
              <!-- 新增：Tag 输入 -->
              <input
                v-model="tagInputs[index]"
                type="text"
                class="mt-1 w-full px-2 py-1 border rounded text-xs"
                placeholder="Tags (comma separated, e.g. cat, summer, travel)"
              />
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
