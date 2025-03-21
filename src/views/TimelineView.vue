<script setup>
import {
  mdiMonitorCellphone,
  mdiTableBorder,
  mdiGithub,
  mdiImageMultiple,
  mdiImageSearch,
  mdiViewList,
  mdiViewGrid,
  mdiViewGridOutline,
  mdiDotsVertical,
  mdiCheckboxMarked,
  mdiCheckboxBlankOutline,
  mdiCheckboxMultipleMarkedOutline,
  mdiCursorDefault,
  mdiPlayCircle,
  mdiCalendarMonth,
  mdiMovieOutline,
  mdiFilmstrip,
  mdiDownload,
  mdiShare,
  mdiPlayCircleOutline,
  mdiLoading,
  mdiVideo,
  mdiClose,
  mdiArrowLeft,
  mdiArrowRight,
  mdiInformation
} from '@mdi/js'
import SectionMain from '@/components/SectionMain.vue'
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

// Currently generating video status
const isGeneratingVideo = ref(false)

// Sample photo data (instead of full memories)
const photos = ref([
  { id: 1, name: 'Beach Sunset', src: 'https://picsum.photos/id/11/300/200', date: '2023-07-15', size: '2.4 MB', type: 'JPG' },
  { id: 2, name: 'Ocean View', src: 'https://picsum.photos/id/15/300/200', date: '2023-07-15', size: '3.1 MB', type: 'PNG' },
  { id: 3, name: 'Family Photo', src: 'https://picsum.photos/id/20/300/200', date: '2023-07-16', size: '1.8 MB', type: 'JPG' },
  { id: 4, name: 'Mountain View', src: 'https://picsum.photos/id/10/300/200', date: '2023-09-10', size: '2.9 MB', type: 'JPG' },
  { id: 5, name: 'Forest Path', src: 'https://picsum.photos/id/13/300/200', date: '2023-09-11', size: '2.2 MB', type: 'PNG' },
  { id: 6, name: 'City Skyline', src: 'https://picsum.photos/id/12/300/200', date: '2023-11-20', size: '4.0 MB', type: 'TIFF' },
  { id: 7, name: 'Street Art', src: 'https://picsum.photos/id/22/300/200', date: '2023-11-21', size: '2.1 MB', type: 'JPG' },
  { id: 8, name: 'Cafe Visit', src: 'https://picsum.photos/id/24/300/200', date: '2023-11-21', size: '1.7 MB', type: 'JPG' },
  { id: 9, name: 'Winter Landscape', src: 'https://picsum.photos/id/14/300/200', date: '2024-01-05', size: '2.5 MB', type: 'PNG' },
  { id: 10, name: 'Holiday Dinner', src: 'https://picsum.photos/id/25/300/200', date: '2024-01-06', size: '3.2 MB', type: 'JPG' }
])

// Generated timeline videos
const timelineVideos = ref([])

// Track modal state and current photo
const isModalOpen = ref(false)
const currentPhoto = ref(null)

// Filtered photos based on search query
const filteredPhotos = computed(() => {
  if (!searchQuery.value) return photos.value
  return photos.value.filter(photo =>
    photo.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    photo.date.includes(searchQuery.value)
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

// Method to generate timeline video from selected photos
const generateTimelineVideo = async () => {
  if (selectedPhotos.value.length === 0) return

  isGeneratingVideo.value = true

  try {
    // Get selected photo data
    const selectedPhotoData = selectedPhotos.value.map(id =>
      photos.value.find(photo => photo.id === id)
    ).filter(Boolean)

    console.log('Sending selected photos to server:', selectedPhotoData)

    // Simulate API request to server
    await new Promise(resolve => setTimeout(resolve, 2000))

    // Create a new timeline/memory video
    const newVideo = {
      id: timelineVideos.value.length + 1,
      title: `Timeline from ${new Date().toLocaleDateString()}`,
      date: new Date().toISOString().split('T')[0],
      description: `Generated from ${selectedPhotoData.length} photos`,
      coverImage: selectedPhotoData[0]?.src || '',
      photos: selectedPhotoData,
      videoGenerated: true,
      videoUrl: '#'
    }

    // Add to timeline videos
    timelineVideos.value.push(newVideo)

    // Show success notification
    alert('Timeline video generated successfully!')

    // Clear selections and exit select mode
    clearSelections()
    isSelectMode.value = false
  } catch (error) {
    console.error('Error generating timeline video:', error)
    alert('Failed to generate timeline video')
  } finally {
    isGeneratingVideo.value = false
  }
}

// Method to play a generated video
const playVideo = (video) => {
  alert(`Playing video: ${video.title}`)
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithButton :icon="mdiCalendarMonth" title="Timeline Generator" main>
        <!-- Toggle for select mode -->
        <BaseButton :icon="isSelectMode ? mdiCursorDefault : mdiCheckboxMultipleMarkedOutline"
          :label="isSelectMode ? 'View Mode' : 'Select Photos'" :color="isSelectMode ? 'info' : 'contrast'" small
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
          <BaseButton :icon="mdiViewList" :color="viewMode === 'list' ? 'info' : 'whiteDark'" small
            @click="setViewMode('list')" class="mr-1" title="List view" />
          <BaseButton :icon="mdiViewGridOutline" :color="viewMode === 'grid' ? 'info' : 'whiteDark'" small
            @click="setViewMode('grid')" class="mr-1" title="Grid view" />
        </div>
      </div>

      <!-- Photos Display Component -->
      <CardBox class="mb-6">
        <!-- List View -->
        <div v-if="viewMode === 'list'" class="overflow-x-auto">
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
                <td class="px-3 py-2">{{ photo.date }}</td>
                <td class="px-3 py-2">{{ photo.type }}</td>
                <td class="px-3 py-2">{{ photo.size }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Grid View (Default) -->
        <div v-else class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-3">
          <div v-for="photo in filteredPhotos" :key="photo.id"
            class="flex flex-col items-center relative hover:bg-gray-50 p-2 rounded"
            :class="{ 'ring-2 ring-blue-500': isSelectMode && isPhotoSelected(photo.id) }">
            <div v-if="isSelectMode" class="absolute top-3 left-3 z-10">
              <button @click.stop="togglePhotoSelection(photo.id)"
                class="bg-white bg-opacity-70 rounded-md p-0.5 text-gray-700 hover:text-blue-500">
                <svg class="w-5 h-5" viewBox="0 0 24 24">
                  <path fill="currentColor"
                    :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
                </svg>
              </button>
            </div>
            <img :src="photo.src" class="w-full h-32 object-cover rounded mb-2 cursor-pointer"
              @click="openPhotoModal(photo)" />
            <span class="text-center text-sm">{{ photo.name }}</span>
            <span class="text-center text-xs text-gray-500">{{ photo.date }}</span>
          </div>
        </div>

        <!-- Empty State -->
        <CardBoxComponentEmpty v-if="filteredPhotos.length === 0" />
      </CardBox>

      <!-- Generated Timeline Videos Section -->
      <div v-if="timelineVideos.length > 0" class="mt-8">
        <h3 class="text-lg font-medium mb-4">Generated Timeline Videos</h3>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          <div v-for="video in timelineVideos" :key="video.id"
            class="border rounded-lg overflow-hidden hover:shadow-md transition-shadow">
            <div class="relative">
              <img :src="video.coverImage" class="w-full h-48 object-cover" />
              <button @click="playVideo(video)"
                class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-30 hover:bg-opacity-50 transition-opacity">
                <svg class="w-12 h-12 text-white" viewBox="0 0 24 24">
                  <path fill="currentColor" :d="mdiPlayCircle" />
                </svg>
              </button>
            </div>
            <div class="p-4">
              <div class="flex justify-between items-center">
                <h3 class="text-lg font-medium">{{ video.title }}</h3>
                <div class="text-green-600">
                  <svg class="w-5 h-5" viewBox="0 0 24 24">
                    <path fill="currentColor" :d="mdiMovieOutline" />
                  </svg>
                </div>
              </div>
              <p class="text-sm text-gray-500 mb-2">{{ video.date }}</p>
              <p class="text-gray-700 mb-3 text-sm">{{ video.description }}</p>
              <div class="flex justify-between items-center">
                <span class="text-sm text-gray-500">{{ video.photos.length }} photos</span>
                <div class="flex">
                  <button class="text-blue-600 text-sm flex items-center mr-3">
                    <svg class="w-4 h-4 mr-1" viewBox="0 0 24 24">
                      <path fill="currentColor" :d="mdiDownload" />
                    </svg>
                    Download
                  </button>
                  <button class="text-blue-600 text-sm flex items-center">
                    <svg class="w-4 h-4 mr-1" viewBox="0 0 24 24">
                      <path fill="currentColor" :d="mdiShare" />
                    </svg>
                    Share
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="flex justify-between mt-6">
        <div class="flex">
          <BaseButton v-if="isSelectMode" :icon="isGeneratingVideo ? mdiLoading : mdiVideo"
            :label="isGeneratingVideo ? 'Generating...' : 'Generate Timeline Video'" color="info" rounded-full small
            :disabled="selectedPhotos.length === 0 || isGeneratingVideo" @click="generateTimelineVideo" />
        </div>
        <div v-if="isSelectMode && selectedPhotos.length > 0" class="flex items-center">
          <span class="mr-2 text-sm text-gray-700">{{ selectedPhotos.length }} photos selected</span>
          <BaseButton label="Clear selection" color="whiteDark" small @click="clearSelections" />
        </div>
      </div>

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
    </SectionMain>
  </LayoutAuthenticated>
</template>
