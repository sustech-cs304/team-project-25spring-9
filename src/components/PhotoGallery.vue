<script setup>
import {
  mdiCheckboxMarked,
  mdiCheckboxBlankOutline,
  mdiDotsVertical,
  mdiClose,
  mdiArrowLeft,
  mdiArrowRight,
  mdiInformation
} from '@mdi/js'
import CardBoxComponentEmpty from '@/components/CardBoxComponentEmpty.vue'
import BaseButton from '@/components/BaseButton.vue'
import { ref, onMounted, onUnmounted } from 'vue'

// Define props for the component
const props = defineProps({
  photos: {
    type: Array,
    required: true
  },
  viewMode: {
    type: String,
    default: 'grid',
    validator: (value) => ['details', 'list', 'grid', 'large', 'small'].includes(value)
  },
  isSelectMode: {
    type: Boolean,
    default: false
  },
  selectedPhotoIds: {
    type: Array,
    default: () => []
  },
  showActions: {
    type: Boolean,
    default: true
  }
})

// Define emits
const emit = defineEmits(['select-photo', 'view-photo', 'action-click'])

// Track modal state and current photo
const isModalOpen = ref(false)
const currentPhoto = ref(null)

// Method to check if a photo is selected
const isPhotoSelected = (photoId) => {
  return props.selectedPhotoIds.includes(photoId)
}

// Method to toggle photo selection
const togglePhotoSelection = (photoId) => {
  emit('select-photo', photoId)
}

// Method to open the full image modal
const openPhotoModal = (photo) => {
  currentPhoto.value = photo
  isModalOpen.value = true
  emit('view-photo', photo)
}

// Method to close the modal
const closePhotoModal = () => {
  isModalOpen.value = false
  currentPhoto.value = null
}

// Methods to navigate between photos in the modal
const viewNextPhoto = () => {
  if (!currentPhoto.value) return

  const currentIndex = props.photos.findIndex(p => p.id === currentPhoto.value.id)
  if (currentIndex < props.photos.length - 1) {
    currentPhoto.value = props.photos[currentIndex + 1]
  } else {
    // Wrap around to the first photo
    currentPhoto.value = props.photos[0]
  }
}

const viewPreviousPhoto = () => {
  if (!currentPhoto.value) return

  const currentIndex = props.photos.findIndex(p => p.id === currentPhoto.value.id)
  if (currentIndex > 0) {
    currentPhoto.value = props.photos[currentIndex - 1]
  } else {
    // Wrap around to the last photo
    currentPhoto.value = props.photos[props.photos.length - 1]
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

// Method to handle action button click
const handleActionClick = (photo) => {
  emit('action-click', photo)
}
</script>

<template>
  <div>
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
            <th v-if="showActions" class="px-3 py-2 text-left">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="photo in photos" :key="photo.id" class="border-b hover:bg-gray-50"
            :class="{ 'bg-blue-50': isSelectMode && isPhotoSelected(photo.id) }">
            <td v-if="isSelectMode" class="px-3 py-2">
              <button @click.stop="togglePhotoSelection(photo.id)" class="text-gray-500 hover:text-blue-500">
                <svg class="w-5 h-5" viewBox="0 0 24 24">
                  <path fill="currentColor" :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
                </svg>
              </button>
            </td>
            <td class="px-3 py-2">
              <img :src="photo.src" class="h-12 w-16 object-cover rounded cursor-pointer" @click="openPhotoModal(photo)" />
            </td>
            <td class="px-3 py-2">{{ photo.name }}</td>
            <td class="px-3 py-2">{{ photo.type }}</td>
            <td class="px-3 py-2">{{ photo.size }}</td>
            <td class="px-3 py-2">{{ photo.date }}</td>
            <td v-if="showActions" class="px-3 py-2">
              <BaseButton :icon="mdiDotsVertical" small color="lightDark" @click="handleActionClick(photo)" />
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- List View -->
    <div v-else-if="viewMode === 'list'" class="overflow-x-auto">
      <table class="w-full">
        <thead>
          <tr class="border-b">
            <th v-if="isSelectMode" class="px-3 py-2 text-left">Select</th>
            <th class="px-3 py-2 text-left">Preview</th>
            <th class="px-3 py-2 text-left">Name</th>
            <th class="px-3 py-2 text-left">Date</th>
            <th class="px-3 py-2 text-left">Type</th>
            <th class="px-3 py-2 text-left">Size</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="photo in photos" :key="photo.id" class="border-b hover:bg-gray-50"
            :class="{ 'bg-blue-50': isSelectMode && isPhotoSelected(photo.id) }">
            <td v-if="isSelectMode" class="px-3 py-2">
              <button @click.stop="togglePhotoSelection(photo.id)" class="text-gray-500 hover:text-blue-500">
                <svg class="w-5 h-5" viewBox="0 0 24 24">
                  <path fill="currentColor" :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
                </svg>
              </button>
            </td>
            <td class="px-3 py-2">
              <img :src="photo.src" class="h-12 w-16 object-cover rounded cursor-pointer" @click="openPhotoModal(photo)" />
            </td>
            <td class="px-3 py-2">{{ photo.name }}</td>
            <td class="px-3 py-2">{{ photo.date }}</td>
            <td class="px-3 py-2">{{ photo.type }}</td>
            <td class="px-3 py-2">{{ photo.size }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Large Grid View -->
    <div v-else-if="viewMode === 'large'" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
      <div v-for="photo in photos" :key="photo.id"
        class="flex flex-col items-center relative hover:bg-gray-50 p-2 rounded"
        :class="{ 'ring-2 ring-blue-500': isSelectMode && isPhotoSelected(photo.id) }">
        <div v-if="isSelectMode" class="absolute top-4 left-4 z-10">
          <button @click.stop="togglePhotoSelection(photo.id)"
            class="bg-white bg-opacity-70 rounded-md p-1 text-gray-700 hover:text-blue-500">
            <svg class="w-5 h-5" viewBox="0 0 24 24">
              <path fill="currentColor" :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
            </svg>
          </button>
        </div>
        <img :src="photo.src" class="w-full h-40 object-cover rounded mb-2 cursor-pointer" @click="openPhotoModal(photo)" />
        <span class="text-center">{{ photo.name }}</span>
      </div>
    </div>

    <!-- Medium Grid View (Default) -->
    <div v-else-if="viewMode === 'grid'" class="grid grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-3">
      <div v-for="photo in photos" :key="photo.id"
        class="flex flex-col items-center relative hover:bg-gray-50 p-2 rounded"
        :class="{ 'ring-2 ring-blue-500': isSelectMode && isPhotoSelected(photo.id) }">
        <div v-if="isSelectMode" class="absolute top-3 left-3 z-10">
          <button @click.stop="togglePhotoSelection(photo.id)"
            class="bg-white bg-opacity-70 rounded-md p-0.5 text-gray-700 hover:text-blue-500">
            <svg class="w-4 h-4" viewBox="0 0 24 24">
              <path fill="currentColor" :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
            </svg>
          </button>
        </div>
        <img :src="photo.src" class="w-full h-24 object-cover rounded mb-1 cursor-pointer" @click="openPhotoModal(photo)" />
        <span class="text-center text-sm">{{ photo.name }}</span>
      </div>
    </div>

    <!-- Small Grid View -->
    <div v-else class="grid grid-cols-4 md:grid-cols-6 lg:grid-cols-10 gap-2">
      <div v-for="photo in photos" :key="photo.id"
        class="flex flex-col items-center relative hover:bg-gray-50 p-1 rounded"
        :class="{ 'ring-1 ring-blue-500': isSelectMode && isPhotoSelected(photo.id) }">
        <div v-if="isSelectMode" class="absolute top-2 left-2 z-10">
          <button @click.stop="togglePhotoSelection(photo.id)"
            class="bg-white bg-opacity-70 rounded-md p-0.5 text-gray-700 hover:text-blue-500">
            <svg class="w-3 h-3" viewBox="0 0 24 24">
              <path fill="currentColor" :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
            </svg>
          </button>
        </div>
        <img :src="photo.src" class="w-full h-16 object-cover rounded mb-1 cursor-pointer" @click="openPhotoModal(photo)" />
        <span class="text-center text-xs truncate w-full">{{ photo.name }}</span>
      </div>
    </div>

    <!-- Empty State -->
    <CardBoxComponentEmpty v-if="photos.length === 0" />

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
  </div>
</template>