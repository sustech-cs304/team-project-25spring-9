<script setup>
import {
  mdiImageMultiple,
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
import PhotoUploader from '@/components/PhotoUploader.vue'
import { ref, computed } from 'vue'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'
import { useMainStore } from '@/stores/main'

// -----------------------------
// Global / reactive state
// -----------------------------
const mainStore = useMainStore()
const isSelectMode = ref(true)
const selectedPhotos = ref([])
const currentViewMode = ref('grid')
const useApiData = ref(true)
const photoGallery = ref(null)
const showUploader = ref(false)
const showEditor = ref(false)
const editingPhoto = ref(null)

// Generate incremental IDs for newly added items
const getNewId = computed(() => {
  const photos = photoGallery.value?.photos ?? []
  return Math.max(...photos.map(p => p.id), 0) + 1
})

// -----------------------------
// Upload / download helpers
// -----------------------------
const handleUpload = file => photoGallery.value.uploadPhotos(file)
const handleDelete = () => {
  photoGallery.value.deletePhotos(selectedPhotos.value)
  clearSelections()
}
const handleDownload = () => photoGallery.value.downloadPhotos(selectedPhotos.value)

// -----------------------------
// Stylize (local API, multipart/form-data)
// -----------------------------
function stylizePhoto () {
  if (selectedPhotos.value.length !== 1) {
    toast.error('Please select exactly one photo to stylize.')
    return
  }

  const photo = photoGallery.value.getPhotoById(selectedPhotos.value[0])

  async function getFileFromPhoto () {
    if (photo.file instanceof File) return photo.file
    if (photo.rawFile instanceof File) return photo.rawFile

    const res = await fetch(photo.url, { mode: 'cors' })
    const blob = await res.blob()
    const fileName = `${photo.name || 'photo'}.jpeg`
    return new File([blob], fileName, { type: blob.type || 'image/jpeg' })
  }

  const stylizePromise = (async () => {
    const imageFile = await getFileFromPhoto()
    const formData = new FormData()
    const styleIndex = 3
    formData.append('file', imageFile)
    formData.append('style_index', styleIndex.toString())

    const response = await fetch('http://10.24.120.158:8123/style_transfer/', {
      method: 'POST',
      body: formData
    })
    const data = await response.json()
    if (!response.ok) throw new Error(data.message || 'Stylize request failed')

    const stylizedUrl = data.result_url
    if (!stylizedUrl) throw new Error('No result_url returned by API')

    photoGallery.value.uploadPhotos([
      { id: getNewId.value, url: stylizedUrl, name: `${photo.name}-stylized` }
    ])
    clearSelections()
    return 'Stylized image added to gallery!'
  })()

  toast.promise(
    stylizePromise,
    {
      pending: 'Stylizing…',
      success: msg => msg,
      error: err => `Stylize failed: ${err.message}`
    },
    { position: toast.POSITION.TOP_RIGHT }
  )
}

// -----------------------------
// Selection helpers
// -----------------------------
function togglePhotoSelection (photoId) {
  if (selectedPhotos.value.includes(photoId)) {
    selectedPhotos.value = selectedPhotos.value.filter(id => id !== photoId)
  } else {
    selectedPhotos.value.push(photoId)
  }
}

function toggleSelectMode () {
  isSelectMode.value = !isSelectMode.value
  if (!isSelectMode.value) clearSelections()
}

function clearSelections () {
  selectedPhotos.value = []
}

// -----------------------------
// Editor helpers
// -----------------------------
function openEditor () {
  if (selectedPhotos.value.length === 1) openEditorWithPhoto(selectedPhotos.value[0])
}

function openEditorWithPhoto (photoId) {
  const photo = photoGallery.value.getPhotoById(photoId)
  editingPhoto.value = photo
  showEditor.value = true
  isSelectMode.value = false
  selectedPhotos.value = []
}

function closeEditor () {
  showEditor.value = false
  editingPhoto.value = null
}

function saveEditedPhoto (updatedPhoto) {
  photoGallery.value.uploadPhotos(updatedPhoto)
  closeEditor()
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <!-- Title line -->
      <SectionTitleLineWithButton :icon="mdiImageMultiple" title="Photos" main>
        <div class="flex">
          <BaseButton
            v-if="useApiData"
            :icon="mdiRefresh"
            tooltip="Refresh Photos"
            :color="isSelectMode ? 'info' : 'contrast'"
            small
            class="mr-2"
            label="Refresh"
            @click="$refs.photoGallery.refreshPhotos()"
          />
          <BaseButton
            :icon="isSelectMode ? mdiCursorDefault : mdiCheckboxMultipleMarkedOutline"
            :label="isSelectMode ? 'View Mode' : 'Select Mode'"
            :color="isSelectMode ? 'info' : 'contrast'"
            small
            @click="toggleSelectMode"
          />
        </div>
      </SectionTitleLineWithButton>

      <!-- Gallery -->
      <CardBox class="mb-6">
        <PhotoGallery
          ref="photoGallery"
          :initial-view-mode="currentViewMode"
          :available-view-modes="['details', 'grid', 'large', 'small']"
          :is-select-mode="isSelectMode"
          :selected-photo-ids="selectedPhotos"
          :show-actions="true"
          :use-api-data="useApiData"
          :userId="mainStore.userId"
          @select-photo="togglePhotoSelection"
          @photo-edit="openEditorWithPhoto"
        />
      </CardBox>

      <!-- Action Buttons -->
      <div class="flex justify-between mb-6">
        <div class="flex gap-2">
          <template v-if="isSelectMode">
            <BaseButton
              :icon="mdiImageEdit"
              label="Stylize"
              color="info"
              rounded-full
              small
              class="ml-2"
              :disabled="selectedPhotos.length !== 1"
              @click="stylizePhoto"
            />
          </template>
        </div>
        <div v-if="isSelectMode && selectedPhotos.length > 0" class="flex items-center">
          <span class="mr-2 text-sm text-gray-700">{{ selectedPhotos.length }} selected</span>
          <BaseButton label="Clear selection" color="whiteDark" small @click="clearSelections" />
        </div>
      </div>

      <!-- Uploader -->
      <PhotoUploader :show="showUploader" @close="showUploader = false" @upload="handleUpload" />

      <!-- Editor Modal -->
      <PhotoEditor v-if="showEditor" :photo="editingPhoto" @save="saveEditedPhoto" @close="showEditor = false" />
    </SectionMain>
  </LayoutAuthenticated>
</template>
