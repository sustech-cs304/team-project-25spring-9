<script setup>
import {
  mdiClose,
  mdiArrowLeft,
  mdiArrowRight,
  mdiInformation,
  mdiImageEdit,
  mdiDelete,
  mdiDownload,
  mdiShareVariant,
} from '@mdi/js'
import { ref } from 'vue'

const props = defineProps({
  isOpen: Boolean,
  photo: Object,
  showActions: Boolean,
  tagColorClasses: Array,
  addTagToPhoto: Function // Add this prop
})

const emit = defineEmits([
  'close',
  'previous',
  'next',
  'action',
  'tag-click',
  'add-tag',
  'delete-tag',
  'rename',
  'autotag'
])

const getTagColor = (index) => {
  return props.tagColorClasses[index % props.tagColorClasses.length]
}

const hoveredTag = ref(null)
const RenamingPhotoName = ref('')
const isRenaming = ref(false)

const startRenaming = () => {
  RenamingPhotoName.value = props.photo?.name || ''
  isRenaming.value = true
}

const savePhotoName = () => {
  if (RenamingPhotoName.value.trim() && RenamingPhotoName.value !== props.photo?.name) {
    emit('rename', RenamingPhotoName.value)
  }
  isRenaming.value = false
  RenamingPhotoName.value = ''
}

const cancelRenaming = () => {
  isRenaming.value = false
  RenamingPhotoName.value = ''
}

// 自动标签弹窗相关
const showAutoTagModal = ref(false)
const autoTagLoading = ref(false)
const autoTagError = ref('')
const autoTagOptions = ref([])
const selectedAutoTags = ref([]) // 可以移除，不再需要

// 修改变量定义
const manualTagInput = ref('')
const pendingTags = ref([]) // 新增：存储待添加的标签

// 自动标签功能
const openAutoTagModal = async () => {
  showAutoTagModal.value = true
  autoTagLoading.value = true
  autoTagError.value = ''
  autoTagOptions.value = []
  selectedAutoTags.value = []

  try {
    // 用 fetch POST 上传 file 到 10.16.60.67:8123/autotag
    // 需要 props.photo.file 或 props.photo.src
    let file = props.photo?.file
    if (!file && props.photo?.src) {
      // 如果没有 file，但有 src，则尝试 fetch blob
      const res = await fetch(props.photo.src)
      file = new File([await res.blob()], props.photo.name || 'image.jpg')
    }
    if (!file) throw new Error('No file to autotag')

    const formData = new FormData()
    formData.append('file', file)

    const response = await fetch('http://10.16.60.67:8123/auto_tag/', {
      method: 'POST',
      body: formData
    })
    const result = await response.json()
    if (result && Array.isArray(result.tags)) {
      autoTagOptions.value = result.tags
    } else {
      throw new Error(result.msg || 'No tags found')
    }
  } catch (err) {
    autoTagError.value = err.message || 'Failed to fetch autotags'
  } finally {
    autoTagLoading.value = false
  }
}

const closeAutoTagModal = () => {
  showAutoTagModal.value = false
  autoTagOptions.value = []
  pendingTags.value = []
  autoTagError.value = ''
  manualTagInput.value = ''
}

const toggleSelectAutoTag = (tag) => {
  // 只要 pendingTags 和 photo.tags 都没有才添加
  if (!pendingTags.value.includes(tag) && !(props.photo?.tags || []).includes(tag)) {
    pendingTags.value.push(tag)
  }
}

// 修正 addSelectedAutoTags，确保 addTagToPhoto 正确调用
const submitTags = async () => {
  if (props.photo && pendingTags.value.length > 0 && props.addTagToPhoto) {
    for (const tag of pendingTags.value) {
      await props.addTagToPhoto(props.photo, tag)
    }
  }
  closeAutoTagModal()
}

// 移除待添加的标签
const removePendingTag = (tag) => {
  pendingTags.value = pendingTags.value.filter(t => t !== tag)
}

// Add new refs for unified tag management
const showTagDialog = ref(false)

// Add method to handle manual tag input
const handleManualTagInput = (e) => {
  if (e.key === 'Enter' && manualTagInput.value.trim()) {
    const newTag = manualTagInput.value.trim()
    // 检查是否重复
    if (!pendingTags.value.includes(newTag) && !props.photo?.tags?.includes(newTag)) {
      pendingTags.value.push(newTag)
    }
    manualTagInput.value = ''
  }
}
</script>

<template>
  <div v-if="isOpen"
     class="fixed inset-0 z-50 flex items-center justify-center bg-black/30 backdrop-blur-sm"
     @click="$emit('close')">
    <div class="max-w-5xl w-full mx-4 relative" @click.stop>
      <div class="bg-white rounded-lg overflow-hidden shadow-xl">
        <!-- Header -->
        <div class="flex justify-between items-center p-4 bg-gray-100">
          <div class="flex items-center">
            <button class="p-1 rounded-full hover:bg-gray-200 mr-2" @click="$emit('previous')">
              <svg class="w-6 h-6" viewBox="0 0 24 24">
                <path fill="currentColor" :d="mdiArrowLeft" />
              </svg>
            </button>
            <button class="p-1 rounded-full hover:bg-gray-200" @click="$emit('next')">
              <svg class="w-6 h-6" viewBox="0 0 24 24">
                <path fill="currentColor" :d="mdiArrowRight" />
              </svg>
            </button>
          </div>

          <div v-if="!isRenaming"
               class="text-lg font-medium break-all cursor-pointer"
               @dblclick="startRenaming">
            {{ photo?.name }}
          </div>
          <input v-else
                 v-model="RenamingPhotoName"
                 class="text-lg font-medium px-2 py-1 border rounded w-full"
                 @blur="savePhotoName"
                 @keyup.enter="savePhotoName"
                 @keyup.esc="cancelRenaming"
                 ref="nameInput"
                 autofocus />

          <div class="flex items-center gap-2">
            <template v-if="showActions && !photo?.uploadFailed">
              <button v-for="(action, index) in [
                { icon: mdiImageEdit, label: 'Edit', value: 'edit', disabled: photo?.isUploading },
                { icon: mdiDelete, label: 'Delete', value: 'delete', disabled: photo?.isUploading },
                { icon: mdiDownload, label: 'Download', value: 'download', disabled: photo?.isUploading },
                { icon: mdiShareVariant, label: 'Share', value: 'share', disabled: photo?.isUploading }
              ]" :key="index"
                class="p-1 rounded-full hover:bg-gray-200 flex items-center"
                :class="{ 'opacity-50 cursor-not-allowed': action.disabled }"
                @click="!action.disabled && $emit('action', action.value)"
                :title="action.disabled ? 'Not available while uploading' : action.label">
                <svg class="w-6 h-6" viewBox="0 0 24 24">
                  <path fill="currentColor" :d="action.icon" />
                </svg>
              </button>
            </template>
            <button class="p-1 rounded-full hover:bg-gray-200" @click="$emit('close')">
              <svg class="w-6 h-6" viewBox="0 0 24 24">
                <path fill="currentColor" :d="mdiClose" />
              </svg>
            </button>
          </div>
        </div>

        <!-- Photo -->
        <div class="flex justify-center bg-black p-2">
          <img :src="photo?.src"
               class="max-h-[70vh] max-w-full object-contain"
               :class="{ 'opacity-70': photo?.isUploading }"
               alt="Full size preview" />
        </div>

        <!-- Details -->
        <div class="p-4 bg-white">
          <div class="flex items-start">
            <svg class="w-5 h-5 text-gray-500 mr-2 mt-0.5" viewBox="0 0 24 24">
              <path fill="currentColor" :d="mdiInformation" />
            </svg>
            <div class="flex-1">
              <div class="mb-1"><span class="font-medium">Size:</span> {{ photo?.size }}</div>
              <div class="mb-1"><span class="font-medium">Date:</span> {{ photo?.displayDate }}</div>
              <!-- 移除 v-if 条件，初始化 tags 数组 -->
              <div class="flex flex-wrap gap-2">
                <!-- Show existing tags -->
                <span v-for="(tag, index) in (photo?.tags || [])"
                  :key="tag"
                  class="px-2 py-1 rounded cursor-pointer hover:opacity-80"
                  :class="getTagColor(index)"
                  @mouseenter="hoveredTag = tag"
                  @mouseleave="hoveredTag = null">
                  {{ tag }}
                  <button v-show="hoveredTag === tag"
                    @click.stop="$emit('delete-tag', tag)"
                    class="hover:text-red-500 focus:outline-none"
                    type="button">
                    ×
                  </button>
                </span>

                <!-- Unified Manage Tags button -->
                <button class="px-3 py-1 rounded bg-blue-500 text-white hover:bg-blue-600 text-sm flex items-center gap-1"
                  @click.stop="showAutoTagModal = true">
                  <span class="font-medium">Add Tags</span>
                </button>
              </div>

              <!-- Tag Management Modal -->
              <div v-if="showAutoTagModal"
                class="fixed inset-0 z-60 flex items-center justify-center bg-black/40">
                <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-md relative" @click.stop>
                  <div class="flex justify-between items-center mb-4">
                    <h3 class="text-lg font-medium">Add Tags</h3>
                    <button class="p-1 rounded-full hover:bg-gray-200" @click="closeAutoTagModal">
                      <svg class="w-6 h-6" viewBox="0 0 24 24">
                        <path fill="currentColor" :d="mdiClose" />
                      </svg>
                    </button>
                  </div>

                  <!-- Manual Tag Input -->
                  <div class="mb-6">
                    <label class="block text-sm font-medium text-gray-700 mb-2">Tags to Add</label>
                    <!-- 待添加的标签列表 -->
                    <div class="flex flex-wrap gap-2 mb-4">
                      <span v-for="tag in pendingTags"
                            :key="tag"
                            class="px-3 py-1 rounded-full bg-blue-50 text-blue-700 border border-blue-200 flex items-center gap-1">
                        {{ tag }}
                        <button @click="removePendingTag(tag)"
                                class="hover:text-red-500 focus:outline-none"
                                type="button">
                          ×
                        </button>
                      </span>
                    </div>

                    <!-- 手动输入框 -->
                    <div class="flex gap-2">
                      <input
                        v-model="manualTagInput"
                        type="text"
                        class="flex-1 px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-300"
                        placeholder="Type a tag and press Enter"
                        @keyup.enter="handleManualTagInput"
                      />
                    </div>
                  </div>

                  <!-- Auto-tag Section -->
                  <div class="mb-6">
                    <div class="flex justify-between items-center mb-2">
                      <label class="text-sm font-medium text-gray-700">Auto-Generated Tags</label>
                      <button
                        class="px-3 py-1 text-sm rounded bg-blue-100 hover:bg-blue-200 text-blue-700"
                        :class="{ 'opacity-50 cursor-wait': autoTagLoading }"
                        @click="openAutoTagModal"
                        :disabled="autoTagLoading"
                      >
                        {{ autoTagLoading ? 'Generating...' : 'Generate Tags' }}
                      </button>
                    </div>

                    <div v-if="autoTagLoading" class="py-8 text-center">
                      <div class="animate-spin rounded-full h-8 w-8 border-4 border-blue-500 border-t-transparent mx-auto mb-2"></div>
                      <div class="text-gray-600">Analyzing image...</div>
                    </div>

                    <div v-else-if="autoTagError" class="bg-red-50 text-red-600 p-4 rounded-lg text-center">
                      {{ autoTagError }}
                    </div>

                    <div v-else-if="autoTagOptions.length > 0" class="flex flex-wrap gap-2">
                      <button
                        v-for="tag in autoTagOptions"
                        :key="tag"
                        @click="toggleSelectAutoTag(tag)"
                        :class="[
                          'px-3 py-1 rounded-full border-2 text-sm transition-all duration-200',
                          pendingTags.includes(tag)  // 改用 pendingTags
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
                      @click="closeAutoTagModal"
                    >
                      Cancel
                    </button>
                    <button
                      v-if="pendingTags.length > 0"
                      class="px-4 py-2 rounded-lg bg-blue-500 text-white font-medium hover:bg-blue-600"
                      @click="submitTags"
                    >
                      Add {{ pendingTags.length }} Tags
                    </button>
                  </div>
                </div>
              </div>

              <div v-if="photo?.desc" class="mt-2">
                <span class="font-medium">Description:</span>
                <p class="mt-1 text-gray-600">{{ photo?.desc }}</p>
              </div>
              <div v-if="photo?.isUploading" class="mt-2 text-blue-500">
                Upload in progress...
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
