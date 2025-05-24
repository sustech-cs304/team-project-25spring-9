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
  tagColorClasses: Array
})

const emit = defineEmits([
  'close',
  'previous',
  'next',
  'action',
  'tag-click',
  'add-tag',
  'delete-tag',
  'rename'
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
              <div v-if="photo?.tags?.length" class="flex flex-wrap gap-2">
                <span v-for="(tag, index) in photo.tags" :key="tag"
                      class="px-2 py-1 rounded cursor-pointer hover:opacity-80"
                      :class="getTagColor(index)"
                      @click="$emit('tag-click', tag)"
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
                <button class="px-2 py-0.5 text-xs rounded bg-gray-200 hover:bg-gray-300 cursor-pointer"
                        @click="$emit('add-tag')">
                  +
                </button>
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
