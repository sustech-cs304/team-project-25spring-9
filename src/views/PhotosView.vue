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
import { useToast } from 'vue-toastification'

const toast = useToast()
import { useMainStore } from '@/stores/main'
import { ref, computed } from 'vue'

// Get store for user data
const mainStore = useMainStore()

// Track if we're in select mode
const isSelectMode = ref(true)

// Track selected photos
const selectedPhotos = ref([])

// Track current view mode
const currentViewMode = ref('grid')

// Enable API data
const useApiData = ref(true)


const photoGallery = ref(null)

// Add new upload method
const handleUpload = (event) => {
  photoGallery.value.uploadPhotos(event.target.files[0])
}

// Add delete method
const handleDelete = () => {
  photoGallery.value.deletePhotos(selectedPhotos)
  clearSelections()
}

// Add download method
const handleDownload = () => {
  photoGallery.value.downloadPhotos(selectedPhotos)
}

const showEditor = ref(false)
const editingPhoto = ref(null)

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

// Open the editor
const openEditor = () => {
  if (selectedPhotos.value.length === 1) {
    openEditorWithPhoto(selectedPhotos.value[0]) 
  }
}

// Open editor with photo ID
const openEditorWithPhoto = (photoId) => {
    const photo = photoGallery.value.getPhotoById(photoId)
    editingPhoto.value = photo
    showEditor.value = true
    toggleSelectMode()
}

// Close editor without save
const closeEditor = () => {
  showEditor.value = false
  editingPhoto.value = null
}

// Save edited photo 
const saveEditedPhoto = (updatedPhoto) => {
  //TODO: save edited photo
  photoGallery.value.uploadPhotos(updatedPhoto)
  closeEditor()
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
              :disabled="selectedPhotos.length === 0" @click="handleDelete"/>
            <BaseButton :icon="mdiDownload" label="Download" color="success" rounded-full small class="ml-2"
              :disabled="selectedPhotos.length === 0" @click="handleDownload" />
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
