<script setup>
import {
  mdiImageMultiple,
  mdiImageEdit,
  mdiCheckboxMultipleMarkedOutline,
  mdiCursorDefault,
  mdiRefresh,
  mdiHelpCircle
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
const showStylizeHelp = ref(false)

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
function stylizePhoto() {
  if (selectedPhotos.value.length !== 1) {
    toast.error('Please select exactly one photo to stylize.')
    return
  }

  const photo = photoGallery.value.getPhotoById(selectedPhotos.value[0])

  async function getFileFromPhoto() {


    const photoUrl = `http://10.16.60.67:9000/softwareeng/upload-img/${photo.id}.jpeg`;
    const res = await fetch(photoUrl, { mode: 'cors' })
    const blob = await res.blob()
    const fileName = `${photo.name || 'photo'}.jpeg`
    return new File([blob], fileName, { type: blob.type || 'image/jpeg' })
  }

  const stylizePromise = (async () => {
    const imageFile = await getFileFromPhoto()
    const formData = new FormData()
    const styleIndex = 3
    formData.append('file', imageFile)
    formData.append('style_index', styleIndex)

    const response = await fetch('http://10.24.120.158:8123/style_transfer/', {
      method: 'POST',
      body: formData
    })
    const data = await response.json()
    if (!response.ok) throw new Error(data.message || 'Stylize request failed')

    const stylizedUrl = data.result_url
    if (!stylizedUrl) throw new Error('No result_url returned by API')

    const link = document.createElement('a')
    link.href = stylizedUrl
    link.download = `${photo.name}-stylized.jpeg`
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    clearSelections()
    return 'Stylized image downloaded successfully!'
  })()

  toast.promise(
    stylizePromise,
    {
      pending: 'Stylizing…',
      success: 'Stylize successful! Downloading...',
      error: err => `Stylize failed: ${err.message}`
    },
    { position: toast.POSITION.TOP_RIGHT }
  )
}

// -----------------------------
// Selection helpers
// -----------------------------
function togglePhotoSelection(photoId) {
  if (selectedPhotos.value.includes(photoId)) {
    selectedPhotos.value = selectedPhotos.value.filter(id => id !== photoId)
  } else {
    selectedPhotos.value.push(photoId)
  }
}

function toggleSelectMode() {
  isSelectMode.value = !isSelectMode.value
  if (!isSelectMode.value) clearSelections()
}

function clearSelections() {
  selectedPhotos.value = []
}

// -----------------------------
// Editor helpers
// -----------------------------
function openEditor() {
  if (selectedPhotos.value.length === 1) openEditorWithPhoto(selectedPhotos.value[0])
}

function openEditorWithPhoto(photoId) {
  const photo = photoGallery.value.getPhotoById(photoId)
  editingPhoto.value = photo
  showEditor.value = true
  isSelectMode.value = false
  selectedPhotos.value = []
}

function closeEditor() {
  showEditor.value = false
  editingPhoto.value = null
}

function saveEditedPhoto(updatedPhoto) {
  photoGallery.value.uploadPhotos(updatedPhoto)
  closeEditor()
}

// -----------------------------
// Help modal helper
// -----------------------------
function toggleStylizeHelp() {
  showStylizeHelp.value = !showStylizeHelp.value
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <!-- Title line -->
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

      <!-- Gallery -->
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
            <BaseButton :icon="mdiHelpCircle" label="Help" color="whiteDark" rounded-full small
              @click="toggleStylizeHelp" />
          </template>
        </div>
        <div v-if="isSelectMode && selectedPhotos.length > 0" class="flex items-center">
          <span class="mr-2 text-sm text-gray-700">{{ selectedPhotos.length }} selected</span>
          <BaseButton label="Clear selection" color="whiteDark" small @click="clearSelections" />
        </div>
      </div>

      <!-- Stylize Help Modal -->
      <div v-if="showStylizeHelp"
        class="fixed inset-0 bg-opacity-10 backdrop-blur-sm flex items-center justify-center z-50"
        @click="showStylizeHelp = false">
        <div class="bg-white rounded-lg p-6 max-w-2xl max-h-[80vh] overflow-auto" @click.stop>
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold">Stylize Help</h3>
            <BaseButton label="×" color="whiteDark" small @click="showStylizeHelp = false" />
          </div>
          <div class="text-center">
            <img src="/stylize.png" alt="Stylize Help" class="max-w-full h-auto rounded-lg shadow-lg"
              @error="$event.target.style.display = 'none'" />
            <p class="mt-4 text-sm text-gray-600">
              Select exactly one photo and click the Stylize button to apply artistic styles to your image.
            </p>
          </div>
        </div>
      </div>

      <!-- Uploader -->
      <PhotoUploader :show="showUploader" @close="showUploader = false" @upload="handleUpload" />

      <!-- Editor Modal -->
      <PhotoEditor v-if="showEditor" :photo="editingPhoto" @save="saveEditedPhoto" @close="showEditor = false" />
    </SectionMain>
  </LayoutAuthenticated>
</template>
