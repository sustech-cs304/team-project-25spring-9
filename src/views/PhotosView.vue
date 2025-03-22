<script setup>
import {
  mdiMonitorCellphone,
  mdiTableBorder,
  mdiTableOff,
  mdiGithub,
  mdiImageMultiple,
  mdiImagePlus,
  mdiImageRemove,
  mdiImageSearch,
  mdiImageEdit,
  mdiViewList,
  mdiViewGrid,
  mdiViewGridOutline,
  mdiViewCompactOutline,
  mdiDotsVertical,
  mdiCheckboxMarked,
  mdiCheckboxBlankOutline,
  mdiCheckboxMultipleMarkedOutline,
  mdiCursorDefault,
  mdiClose,
  mdiArrowLeft,
  mdiArrowRight,
  mdiInformation
} from '@mdi/js'
import SectionMain from '@/components/SectionMain.vue'
import NotificationBar from '@/components/NotificationBar.vue'
import TableSampleClients from '@/components/TableSampleClients.vue'
import CardBox from '@/components/CardBox.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'
import BaseButton from '@/components/BaseButton.vue'
import CardBoxComponentEmpty from '@/components/CardBoxComponentEmpty.vue'
import { ref, computed, onMounted, onUnmounted } from 'vue'

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

// Method to check if a photo is selected
const isPhotoSelected = (photoId) => {
  return selectedPhotos.value.includes(photoId)
}

// Method to change view mode
const setViewMode = (mode) => {
  viewMode.value = mode
}

// Track modal state and current photo
const isModalOpen = ref(false)
const currentPhoto = ref(null)

// Method to open the full image modal
const openPhotoModal = (photo) => {
  currentPhoto.value = photo
  isModalOpen.value = true
}

// Method to close the modal
const closePhotoModal = () => {
  isModalOpen.value = false
  currentPhoto.value = null
}

// Methods to navigate between photos in the modal
const viewNextPhoto = () => {
  if (!currentPhoto.value) return

  const currentIndex = photos.value.findIndex(p => p.id === currentPhoto.value.id)
  if (currentIndex < photos.value.length - 1) {
    currentPhoto.value = photos.value[currentIndex + 1]
  } else {
    // Wrap around to the first photo
    currentPhoto.value = photos.value[0]
  }
}

const viewPreviousPhoto = () => {
  if (!currentPhoto.value) return

  const currentIndex = photos.value.findIndex(p => p.id === currentPhoto.value.id)
  if (currentIndex > 0) {
    currentPhoto.value = photos.value[currentIndex - 1]
  } else {
    // Wrap around to the last photo
    currentPhoto.value = photos.value[photos.value.length - 1]
  }
}

// Handle keyboard navigation in the modal
const handleKeydown = (event) => {
  if (!isModalOpen.value) return

  switch (event.key) {
    case 'Escape':
      closePhotoModal()
      break
    case 'ArrowRight':
      viewNextPhoto()
      break
    case 'ArrowLeft':
      viewPreviousPhoto()
      break
  }
}

// Add and remove global event listeners
onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
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
        <!-- Details View (Table-like) -->
        <div v-if="viewMode === 'details'" class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="border-b">
                <th v-if="isSelectMode" class="px-3 py-2 text-left">Select</th>
                <th class="px-3 py-2 text-left">Preview</th>
                <th class="px-3 py-2 text-left">Name</th>
                <th class="px-3 py-2 text-left">Type</th>
                <th class="px-3 py-2 text-left">Size</th>
                <th class="px-3 py-2 text-left">Modified Date</th>
                <th class="px-3 py-2 text-left">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="photo in filteredPhotos" :key="photo.id" class="border-b hover:bg-gray-50"
                :class="{ 'bg-blue-50': isSelectMode && isPhotoSelected(photo.id) }">
                <td v-if="isSelectMode" class="px-3 py-2">
                  <button @click.stop="togglePhotoSelection(photo.id)" class="text-gray-500 hover:text-blue-500">
                    <svg class="w-5 h-5" viewBox="0 0 24 24">
                      <path fill="currentColor"
                        :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
                    </svg>
                  </button>
                </td>
                <td class="px-3 py-2">
                  <img :src="photo.src" class="h-12 w-16 object-cover rounded cursor-pointer"
                    @click="openPhotoModal(photo)" />
                </td>
                <td class="px-3 py-2">{{ photo.name }}</td>
                <td class="px-3 py-2">{{ photo.type }}</td>
                <td class="px-3 py-2">{{ photo.size }}</td>
                <td class="px-3 py-2">{{ photo.date }}</td>
                <td class="px-3 py-2">
                  <BaseButton :icon="mdiDotsVertical" small color="lightDark" />
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Large Grid View -->
        <div v-else-if="viewMode === 'large'" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          <div v-for="photo in filteredPhotos" :key="photo.id"
            class="flex flex-col items-center relative hover:bg-gray-50 p-2 rounded"
            :class="{ 'ring-2 ring-blue-500': isSelectMode && isPhotoSelected(photo.id) }">
            <div v-if="isSelectMode" class="absolute top-4 left-4 z-10">
              <button @click.stop="togglePhotoSelection(photo.id)"
                class="bg-white bg-opacity-70 rounded-md p-1 text-gray-700 hover:text-blue-500">
                <svg class="w-5 h-5" viewBox="0 0 24 24">
                  <path fill="currentColor"
                    :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
                </svg>
              </button>
            </div>
            <img :src="photo.src" class="w-full h-40 object-cover rounded mb-2 cursor-pointer"
              @click="openPhotoModal(photo)" />
            <span class="text-center">{{ photo.name }}</span>
          </div>
        </div>

        <!-- Medium Grid View (Default) -->
        <div v-else-if="viewMode === 'grid'" class="grid grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-3">
          <div v-for="photo in filteredPhotos" :key="photo.id"
            class="flex flex-col items-center relative hover:bg-gray-50 p-2 rounded"
            :class="{ 'ring-2 ring-blue-500': isSelectMode && isPhotoSelected(photo.id) }">
            <div v-if="isSelectMode" class="absolute top-3 left-3 z-10">
              <button @click.stop="togglePhotoSelection(photo.id)"
                class="bg-white bg-opacity-70 rounded-md p-0.5 text-gray-700 hover:text-blue-500">
                <svg class="w-4 h-4" viewBox="0 0 24 24">
                  <path fill="currentColor"
                    :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
                </svg>
              </button>
            </div>
            <img :src="photo.src" class="w-full h-24 object-cover rounded mb-1 cursor-pointer"
              @click="openPhotoModal(photo)" />
            <span class="text-center text-sm">{{ photo.name }}</span>
          </div>
        </div>

        <!-- Small Grid View -->
        <div v-else class="grid grid-cols-4 md:grid-cols-6 lg:grid-cols-10 gap-2">
          <div v-for="photo in filteredPhotos" :key="photo.id"
            class="flex flex-col items-center relative hover:bg-gray-50 p-1 rounded"
            :class="{ 'ring-1 ring-blue-500': isSelectMode && isPhotoSelected(photo.id) }">
            <div v-if="isSelectMode" class="absolute top-2 left-2 z-10">
              <button @click.stop="togglePhotoSelection(photo.id)"
                class="bg-white bg-opacity-70 rounded-md p-0.5 text-gray-700 hover:text-blue-500">
                <svg class="w-3 h-3" viewBox="0 0 24 24">
                  <path fill="currentColor"
                    :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
                </svg>
              </button>
            </div>
            <img :src="photo.src" class="w-full h-16 object-cover rounded mb-1 cursor-pointer"
              @click="openPhotoModal(photo)" />
            <span class="text-center text-xs truncate w-full">{{ photo.name }}</span>
          </div>
        </div>

        <!-- Empty State -->
        <CardBoxComponentEmpty v-if="filteredPhotos.length === 0" />
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

    <!-- Photo Modal -->
    <div v-if="isModalOpen && currentPhoto"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-75" @click="closePhotoModal">
      <div class="max-w-5xl w-full mx-4 relative" @click.stop>
        <!-- Photo Container -->
        <div class="bg-white rounded-lg overflow-hidden shadow-xl">
          <!-- Navigation and Close Buttons -->
          <div class="flex justify-between items-center p-4 bg-gray-100">
            <div class="flex items-center">
              <button class="p-1 rounded-full hover:bg-gray-200 mr-2" @click="viewPreviousPhoto">
                <svg class="w-6 h-6" viewBox="0 0 24 24">
                  <path fill="currentColor" :d="mdiArrowLeft" />
                </svg>
              </button>
              <button class="p-1 rounded-full hover:bg-gray-200" @click="viewNextPhoto">
                <svg class="w-6 h-6" viewBox="0 0 24 24">
                  <path fill="currentColor" :d="mdiArrowRight" />
                </svg>
              </button>
            </div>

            <div class="text-lg font-medium">{{ currentPhoto.name }}</div>

            <button class="p-1 rounded-full hover:bg-gray-200" @click="closePhotoModal">
              <svg class="w-6 h-6" viewBox="0 0 24 24">
                <path fill="currentColor" :d="mdiClose" />
              </svg>
            </button>
          </div>

          <!-- Photo -->
          <div class="flex justify-center bg-black p-2">
            <img :src="currentPhoto.src" class="max-h-[70vh] max-w-full object-contain" alt="Full size preview" />
          </div>

          <!-- Photo Details -->
          <div class="p-4 bg-white">
            <div class="flex items-start">
              <svg class="w-5 h-5 text-gray-500 mr-2 mt-0.5" viewBox="0 0 24 24">
                <path fill="currentColor" :d="mdiInformation" />
              </svg>
              <div>
                <div class="mb-1"><span class="font-medium">Type:</span> {{ currentPhoto.type }}</div>
                <div class="mb-1"><span class="font-medium">Size:</span> {{ currentPhoto.size }}</div>
                <div><span class="font-medium">Date:</span> {{ currentPhoto.date }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </LayoutAuthenticated>
</template>

<style scoped>
/* Add any required styles for modal animations, etc. */
</style>
