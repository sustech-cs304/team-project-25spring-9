<script setup>
import {
  mdiImageMultiple,
  mdiImagePlus,
  mdiImageRemove,
  mdiImageEdit,
  mdiCheckboxMultipleMarkedOutline,
  mdiCursorDefault,
  mdiRefresh
} from '@mdi/js'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'
import BaseButton from '@/components/BaseButton.vue'
import PhotoGallery from '@/components/PhotoGallery.vue'
import PhotoEditor from '@/components/PhotoEditor.vue'
import { useMainStore } from '@/stores/main'
import { ref } from 'vue'

// Get store for user data
const mainStore = useMainStore()

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
// Enable API data
const useApiData = ref(true)

// API data states
const apiPhotos = ref([])
const loading = ref(false)

// Sample photo data (fallback if API fails)
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

// Method to handle photos loaded from API
const handlePhotosLoaded = (loadedPhotos) => {
  apiPhotos.value = loadedPhotos
  console.log(`Loaded ${loadedPhotos.length} photos from API`)
}

// Method to handle view mode changes
const handleViewModeChange = (mode) => {
  currentViewMode.value = mode
}

// Open the editor
const openEditor = () => {
  if (selectedPhotos.value.length === 1) {
    const source = useApiData.value ? apiPhotos.value : photos.value
    const photo = source.find(p => p.id === selectedPhotos.value[0])
    editingPhoto.value = photo
    showEditor.value = true
  }
  toggleSelectMode()
}

const closeEditor = () => {
  showEditor.value = false
  editingPhoto.value = null
}

// Save edited photo 
const saveEditedPhoto = (updatedPhoto) => {
  //TODO: save base64 photo
//   console.log(updatedPhoto)
}

</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithButton :icon="mdiImageMultiple" title="Photos" main>
        <div class="flex">
          <!-- Add refresh button for API photos -->
          <BaseButton v-if="useApiData" :icon="mdiRefresh" tooltip="Refresh Photos" :color="isSelectMode ? 'info' : 'contrast'"
            small class="mr-2" label="Refresh" @click="$refs.photoGallery.refreshPhotos()" />
          <BaseButton :icon="isSelectMode ? mdiCursorDefault : mdiCheckboxMultipleMarkedOutline"
            :label="isSelectMode ? 'View Mode' : 'Select Mode'" :color="isSelectMode ? 'info' : 'contrast'" small
            @click="toggleSelectMode" />
        </div>
      </SectionTitleLineWithButton>

      <!-- Photo Display Component with integrated search and view controls -->
      <CardBox class="mb-6">
        <PhotoGallery ref="photoGallery" :photos="photos" :initial-view-mode="currentViewMode"
          :available-view-modes="['details', 'grid', 'large', 'small']" :is-select-mode="isSelectMode"
          :selected-photo-ids="selectedPhotos" :show-actions="true" :use-api-data="useApiData"
          :userId="mainStore.userId" @select-photo="togglePhotoSelection" @action-click="handlePhotoAction"
          @filter="handleFilteredPhotos" @update:viewMode="handleViewModeChange" @photos-loaded="handlePhotosLoaded" />
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
        @close="closeEditor" 
      />
    </SectionMain>
  </LayoutAuthenticated>
</template>
