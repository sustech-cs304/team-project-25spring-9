<script setup>
import {
  mdiImageMultiple,
  mdiImagePlus,
  mdiImageRemove,
  mdiImageEdit,
  mdiCheckboxMultipleMarkedOutline,
  mdiCursorDefault
} from '@mdi/js'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'
import BaseButton from '@/components/BaseButton.vue'
import PhotoGallery from '@/components/PhotoGallery.vue'
import PhotoEditor from '@/components/PhotoEditor.vue'
import { ref } from 'vue'

// Track if we're in select mode
const isSelectMode = ref(false)

// Track selected photos
const selectedPhotos = ref([])

// Track filtered photos
const displayedPhotos = ref([])

// Track current view mode
const currentViewMode = ref('grid')

const showEditor = ref(false)
const editingPhoto = ref(null)

// Sample photo data
const photos = ref([
  { id: 1, name: 'Mountain View', src: 'https://picsum.photos/id/10/300/200', size: '2.4 MB', date: '2023-09-15', type: 'JPG' },
  { id: 2, name: 'Beach Sunset', src: 'https://picsum.photos/id/11/300/200', size: '3.1 MB', date: '2023-10-02', type: 'PNG' },
  { id: 3, name: 'City Skyline', src: 'https://picsum.photos/id/12/300/200', size: '1.8 MB', date: '2023-11-20', type: 'JPG' },
  { id: 4, name: 'Forest Path', src: 'https://picsum.photos/id/13/300/200', size: '2.9 MB', date: '2024-01-05', type: 'JPG' },
  { id: 5, name: 'Desert Landscape', src: 'https://picsum.photos/id/14/300/200', size: '2.2 MB', date: '2024-02-18', type: 'PNG' },
  { id: 6, name: 'Ocean Waves', src: 'https://picsum.photos/id/15/300/200', size: '4.0 MB', date: '2024-03-10', type: 'TIFF' },
])

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

// Method to handle photo action clicks
const handlePhotoAction = (photo) => {
  // Handle actions like edit, delete, etc.
  console.log('Action clicked for photo:', photo)
}

// Method to handle filtered photos from the component
const handleFilteredPhotos = (filteredPhotos) => {
  displayedPhotos.value = filteredPhotos
}

// Method to handle view mode changes
const handleViewModeChange = (mode) => {
  currentViewMode.value = mode
}

// Open the editor
const openEditor = () => {
  if (selectedPhotos.value.length === 1) {
    const photo = photos.value.find(p => p.id === selectedPhotos.value[0])
    editingPhoto.value = photo
    showEditor.value = true
  }
  toggleSelectMode()
}

// Save edited photo 
const saveEditedPhoto = (updatedPhoto) => {
  //TODO: save edited photo
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

      <!-- Photo Display Component with integrated search and view controls -->
      <CardBox class="mb-6">
        <PhotoGallery 
          :photos="photos" 
          :initial-view-mode="currentViewMode"
          :available-view-modes="['details', 'grid', 'large', 'small']"
          :is-select-mode="isSelectMode" 
          :selected-photo-ids="selectedPhotos"
          @select-photo="togglePhotoSelection"
          @action-click="handlePhotoAction"
          @filter="handleFilteredPhotos"
          @update:viewMode="handleViewModeChange"
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
              :disabled="selectedPhotos.length !== 1" @click="openEditor"/>
          </template>
        </div>
        <div v-if="isSelectMode && selectedPhotos.length > 0" class="flex items-center">
          <span class="mr-2 text-sm text-gray-700">{{ selectedPhotos.length }} selected</span>
          <BaseButton label="Clear selection" color="whiteDark" small @click="clearSelections" />
        </div>
      </div>

      <!-- Photo Editor Modal -->
      <PhotoEditor 
        v-if="showEditor" 
        :photo="editingPhoto" 
        @save="saveEditedPhoto" 
        @close="showEditor = false" 
      />
    </SectionMain>
  </LayoutAuthenticated>
</template>