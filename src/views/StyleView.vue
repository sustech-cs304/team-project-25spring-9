<script setup>
import {
  mdiImageMultiple,
  mdiImagePlus,
  mdiImageRemove,
  mdiImageEdit,
  mdiCheckboxMultipleMarkedOutline,
  mdiCursorDefault,
  mdiRefresh,
  mdiDownload
} from '@mdi/js'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'
import BaseButton from '@/components/BaseButton.vue'
import PhotoGallery from '@/components/PhotoGallery.vue'
import PhotoEditor from '@/components/PhotoEditor.vue'
import PhotoUploader from '@/components/PhotoUploader.vue'
import { ref, computed } from 'vue'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'

import { useMainStore } from '@/stores/main'

// Get store for user data
const mainStore = useMainStore()

// Track if we're in select mode
const isSelectMode = ref(true)

// Track selected photos
const selectedPhotos = ref([])

// Track filtered photos
const displayedPhotos = ref([])

// Track current view mode
const currentViewMode = ref('grid')

// Enable API data
const useApiData = ref(true)

const photoGallery = ref(null)

// Add showUploader state
const showUploader = ref(false)

// Method to generate a new unique ID
const getNewId = computed(() => Math.max(...photoGallery.value?.photos?.map(p => p.id) ?? [0]) + 1)

// Modify handleUpload to handle both single and multiple files
const handleUpload = (file) => {
  photoGallery.value.uploadPhotos(file)
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

// --------------------------
// Stylize integration (Local API)
// --------------------------
const stylizePhoto = () => {
  if (selectedPhotos.value.length !== 1) {
    toast.error('Please select exactly one photo to stylize.')
    return
  }
  const photo = photoGallery.value.getPhotoById(selectedPhotos.value[0])
  const photoUrl = `http://10.16.60.67:9000/softwareeng/upload-img/${photo.imgId}.jpeg`
  if (!photoUrl) {
    toast.error('Unable to locate the selected photo URL.')
    return
  }

  const stylizePromise = fetch('http://10.24.120.158:8123/style_transfer/', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ image_url: photoUrl, style_index: 3 })
  })
    .then(async (res) => {
      const data = await res.json()
      if (!res.ok) throw new Error(data.message || 'Failed to stylize image')
      const stylizedImageUrl = data.result_url
      if (!stylizedImageUrl) throw new Error('No stylized image URL returned')
      // 将新图加入画廊
      photoGallery.value.uploadPhotos([
        { id: getNewId.value, url: stylizedImageUrl, name: `${photo.name}-stylized` }
      ])
      clearSelections()
      return 'Stylized image added to gallery!'
    })

  toast.promise(
    stylizePromise,
    {
      pending: 'Stylizing…',
      success: 'Stylize successful! New image added to gallery.',
      error: 'Stylize failed: {{message}}'
    },
    { position: toast.POSITION.BOTTOM_CENTER }
  )
}

// --------------------------
// Existing editor helpers (kept for future use)
// --------------------------
const togglePhotoSelection = (photoId) => {
  if (selectedPhotos.value.includes(photoId)) {
    selectedPhotos.value = selectedPhotos.value.filter(id => id !== photoId)
  } else {
    selectedPhotos.value.push(photoId)
  }
}

const toggleSelectMode = () => {
  isSelectMode.value = !isSelectMode.value
  if (!isSelectMode.value) clearSelections()
}
const clearSelections = () => { selectedPhotos.value = [] }

// Editor workflows (unchanged)
const openEditor = () => {
  if (selectedPhotos.value.length === 1) openEditorWithPhoto(selectedPhotos.value[0])
}
const openEditorWithPhoto = (photoId) => {
  const photo = photoGallery.value.getPhotoById(photoId)
  editingPhoto.value = photo
  showEditor.value = true
  isSelectMode.value = false
  selectedPhotos.value = []
}
const closeEditor = () => { showEditor.value = false; editingPhoto.value = null }
const saveEditedPhoto = (updatedPhoto) => {
  photoGallery.value.uploadPhotos(updatedPhoto)
  closeEditor()
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithButton :icon="mdiImageMultiple" title="Photos" main>
        <div class="flex">
          <BaseButton v-if="useApiData" :icon="mdiRefresh" tooltip="Refresh Photos"
            :color="isSelectMode ? 'info' : 'contrast'" small class="mr-2" label="Refresh"
            @click="$refs.photoGallery.refreshPhotos()" />
          <BaseButton :icon="isSelectMode ? mdiCursorDefault : mdiCheckboxMultipleMarkedOutline"
            :label="isSelectMode ? 'View Mode' : 'Select Mode'" :color="isSelectMode ? 'info' : 'contrast'" small
            @click="toggleSelectMode" />
        </div>
      </SectionTitleLineWithButton>

      <!-- Photo Display Component with integrated search and view controls -->
      <CardBox class="mb-6">
        <PhotoGallery ref="photoGallery" :initial-view-mode="currentViewMode"
          :available-view-modes="['details', 'grid', 'large', 'small']" :is-select-mode="isSelectMode"
          :selected-photo-ids="selectedPhotos" :show-actions="true" :use-api-data="useApiData"
          :userId="mainStore.userId" @select-photo="togglePhotoSelection" @photo-edit="openEditorWithPhoto" />
      </CardBox>

      <!-- Action Buttons -->
      <div class="flex justify-between mb-6">
        <div class="flex gap-2">
          <template v-if="isSelectMode">
            <BaseButton :icon="mdiImageEdit" label="Stylize" color="info" rounded-full small class="ml-2"
              :disabled="selectedPhotos.length !== 1" @click="stylizePhoto" />
          </template>
        </div>
        <div v-if="isSelectMode && selectedPhotos.length > 0" class="flex items-center">
          <span class="mr-2 text-sm text-gray-700">{{ selectedPhotos.length }} selected</span>
          <BaseButton label="Clear selection" color="whiteDark" small @click="clearSelections" />
        </div>
      </div>

      <!-- Add PhotoUploader component -->
      <PhotoUploader :show="showUploader" @close="showUploader = false" @upload="handleUpload" />

      <!-- Photo Editor Modal -->
      <PhotoEditor v-if="showEditor" :photo="editingPhoto" @save="saveEditedPhoto" @close="showEditor = false" />
    </SectionMain>
  </LayoutAuthenticated>
</template>
