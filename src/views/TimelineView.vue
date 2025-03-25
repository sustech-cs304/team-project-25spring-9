<script setup>
import {
  mdiCheckboxMultipleMarkedOutline,
  mdiCursorDefault,
  mdiPlayCircle,
  mdiCalendarMonth,
  mdiMovieOutline,
  mdiDownload,
  mdiShare,
  mdiVideo,
  mdiLoading,
  mdiRefresh
} from '@mdi/js'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'
import BaseButton from '@/components/BaseButton.vue'
import PhotoGallery from '@/components/PhotoGallery.vue'
import { useMainStore } from '@/stores/main'
import { ref } from 'vue'

// Track if we're in select mode
const isSelectMode = ref(false)

// Track selected photos
const selectedPhotos = ref([])

// Track filtered photos
const displayedPhotos = ref([])

// Track current view mode
const currentViewMode = ref('grid')

// Currently generating video status
const isGeneratingVideo = ref(false)

// API settings
const useApiData = ref(true)
// Replace with actual user ID or get from authentication
const mainStore = useMainStore()
const userId = ref(mainStore.userId)

// Sample photo data (fallback if API fails)
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
  { id: 10, name: 'Holiday Dinner', src: 'https://picsum.photos/id/25/300/200', date: '2024-01-06', size: '3.2 MB', type: 'JPG' },
])

// API fetched photos
const apiPhotos = ref([])

// Generated timeline videos
const timelineVideos = ref([])

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

// Method to handle filtered photos from the component
const handleFilteredPhotos = (filteredPhotos) => {
  displayedPhotos.value = filteredPhotos
}

// Method to handle photos loaded from API
const handlePhotosLoaded = (loadedPhotos) => {
  apiPhotos.value = loadedPhotos
}

// Method to handle view mode changes
const handleViewModeChange = (mode) => {
  currentViewMode.value = mode
}

// Method to generate timeline video from selected photos
const generateTimelineVideo = async () => {
  if (selectedPhotos.value.length === 0) return

  isGeneratingVideo.value = true

  try {
    // Get selected photo data from either API photos or sample photos
    const photosSource = useApiData.value ? apiPhotos.value : photos.value
    const selectedPhotoData = selectedPhotos.value.map(id =>
      photosSource.find(photo => photo.id === id)
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
        <div class="flex">
          <BaseButton v-if="useApiData" :icon="mdiRefresh" tooltip="Refresh Photos" :color="isSelectMode ? 'info' : 'contrast'"
            small class="mr-2" label="Refresh" @click="$refs.photoGallery.refreshPhotos()" />
          <BaseButton :icon="isSelectMode ? mdiCursorDefault : mdiCheckboxMultipleMarkedOutline"
            :label="isSelectMode ? 'View Mode' : 'Select Mode'" :color="isSelectMode ? 'info' : 'contrast'" small
            @click="toggleSelectMode" />
        </div>
      </SectionTitleLineWithButton>

      <!-- Photos Display Component with integrated search and view controls -->
      <CardBox class="mb-6">
        <PhotoGallery ref="photoGallery" :photos="photos" :initial-view-mode="currentViewMode"
          :available-view-modes="['details', 'grid', 'large', 'small']" :is-select-mode="isSelectMode"
          :selected-photo-ids="selectedPhotos" :show-actions="false" :use-api-data="useApiData" :userId="userId"
          @select-photo="togglePhotoSelection" @filter="handleFilteredPhotos" @update:viewMode="handleViewModeChange"
          @photos-loaded="handlePhotosLoaded" />
      </CardBox>

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

    </SectionMain>
  </LayoutAuthenticated>
</template>
