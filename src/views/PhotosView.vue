<script setup>
import {
  mdiImageMultiple,
  mdiImagePlus,
  mdiImageRemove,
  mdiImageSearch,
  mdiImageEdit,
  mdiViewList,
  mdiViewGrid,
  mdiViewGridOutline,
  mdiViewCompactOutline,
  mdiCheckboxMultipleMarkedOutline,
  mdiCursorDefault
} from '@mdi/js'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'
import BaseButton from '@/components/BaseButton.vue'
import PhotoGallery from '@/components/PhotoGallery.vue'
import { ref, computed } from 'vue'

// Define view mode and search functionality
const viewMode = ref('grid')
const searchQuery = ref('')

// Track if we're in select mode
const isSelectMode = ref(false)

// Track selected photos
const selectedPhotos = ref([])

// Sample photo data
const photos = ref([
  { id: 1, name: 'Mountain View', src: 'https://picsum.photos/id/10/300/200', size: '2.4 MB', date: '2023-09-15', type: 'JPG' },
  { id: 2, name: 'Beach Sunset', src: 'https://picsum.photos/id/11/300/200', size: '3.1 MB', date: '2023-10-02', type: 'PNG' },
  { id: 3, name: 'City Skyline', src: 'https://picsum.photos/id/12/300/200', size: '1.8 MB', date: '2023-11-20', type: 'JPG' },
  { id: 4, name: 'Forest Path', src: 'https://picsum.photos/id/13/300/200', size: '2.9 MB', date: '2024-01-05', type: 'JPG' },
  { id: 5, name: 'Desert Landscape', src: 'https://picsum.photos/id/14/300/200', size: '2.2 MB', date: '2024-02-18', type: 'PNG' },
  { id: 6, name: 'Ocean Waves', src: 'https://picsum.photos/id/15/300/200', size: '4.0 MB', date: '2024-03-10', type: 'TIFF' },
])

// Filtered photos based on search query
const filteredPhotos = computed(() => {
  if (!searchQuery.value) return photos.value
  return photos.value.filter(photo =>
    photo.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

// Method to toggle photo selection
const togglePhotoSelection = (photoId) => {
  if (selectedPhotos.value.includes(photoId)) {
    selectedPhotos.value = selectedPhotos.value.filter(id => id !== photoId)
  } else {
    selectedPhotos.value.push(photoId)
  }
}

// Method to toggle between view mode and select mode
const toggleSelectMode = () => {
  isSelectMode.value = !isSelectMode.value
  if (!isSelectMode.value) {
    // Clear selections when exiting select mode
    clearSelections()
  }
}

// Method to clear all selections
const clearSelections = () => {
  selectedPhotos.value = []
}

// Method to change view mode
const setViewMode = (mode) => {
  viewMode.value = mode
}

// Method to handle photo action clicks
const handlePhotoAction = (photo) => {
  // Handle actions like edit, delete, etc.
  console.log('Action clicked for photo:', photo)
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithButton :icon="mdiImageMultiple" title="Photos" main>
        <!-- Add toggle for select mode -->
        <BaseButton :icon="isSelectMode ? mdiCursorDefault : mdiCheckboxMultipleMarkedOutline"
          :label="isSelectMode ? 'View Mode' : 'Select Mode'" :color="isSelectMode ? 'info' : 'contrast'" small
          @click="toggleSelectMode" />
      </SectionTitleLineWithButton>

      <!-- Search Bar -->
      <div class="mb-6 flex items-center">
        <div class="relative flex-grow max-w-md">
          <input v-model="searchQuery" type="text" placeholder="Search photos"
            class="w-full py-2 pl-10 pr-4 border rounded-lg focus:outline-none focus:ring focus:border-blue-300" />
          <div class="absolute left-3 top-2 text-gray-500">
            <svg class="w-5 h-5" viewBox="0 0 24 24">
              <path fill="currentColor" :d="mdiImageSearch" />
            </svg>
          </div>
        </div>

        <!-- View Mode Switcher -->
        <div class="flex ml-4">
          <BaseButton :icon="mdiViewList" :color="viewMode === 'details' ? 'info' : 'whiteDark'" small
            @click="setViewMode('details')" class="mr-1" title="Details view" />
          <BaseButton :icon="mdiViewGrid" :color="viewMode === 'large' ? 'info' : 'whiteDark'" small
            @click="setViewMode('large')" class="mr-1" title="Large icons" />
          <BaseButton :icon="mdiViewGridOutline" :color="viewMode === 'grid' ? 'info' : 'whiteDark'" small
            @click="setViewMode('grid')" class="mr-1" title="Medium icons" />
          <BaseButton :icon="mdiViewCompactOutline" :color="viewMode === 'small' ? 'info' : 'whiteDark'" small
            @click="setViewMode('small')" title="Small icons" />
        </div>
      </div>

      <!-- Photo Display Component -->
      <CardBox class="mb-6">
        <PhotoGallery 
          :photos="filteredPhotos" 
          :view-mode="viewMode" 
          :is-select-mode="isSelectMode" 
          :selected-photo-ids="selectedPhotos"
          @select-photo="togglePhotoSelection"
          @action-click="handlePhotoAction"
        />
      </CardBox>

      <!-- Action Buttons -->
      <div class="flex justify-between">
        <div class="flex">
          <BaseButton :icon="mdiImagePlus" label="Add" color="contrast" rounded-full small class="mr-2" />
          <template v-if="isSelectMode">
            <BaseButton :icon="mdiImageRemove" label="Remove" color="danger" rounded-full small class="mx-2"
              :disabled="selectedPhotos.length === 0" />
            <BaseButton :icon="mdiImageEdit" label="Edit" color="info" rounded-full small class="ml-2"
              :disabled="selectedPhotos.length !== 1" />
          </template>
        </div>
        <div v-if="isSelectMode && selectedPhotos.length > 0" class="flex items-center">
          <span class="mr-2 text-sm text-gray-700">{{ selectedPhotos.length }} selected</span>
          <BaseButton label="Clear selection" color="whiteDark" small @click="clearSelections" />
        </div>
      </div>
    </SectionMain>
  </LayoutAuthenticated>
</template>