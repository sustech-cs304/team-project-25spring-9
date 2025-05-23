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
import html2canvas_pro from 'html2canvas-pro' // Add this import

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
const generateTimelineView = async () => {
  if (selectedPhotos.value.length === 0) return

  isGeneratingVideo.value = true // reusing the loading state variable

  try {
    // Get selected photo data from either API photos or sample photos
    const photosSource = useApiData.value ? apiPhotos.value : photos.value
    const selectedPhotoData = selectedPhotos.value.map(id =>
      photosSource.find(photo => photo.id === id)
    ).filter(Boolean)

    // Sort photos by date
    const sortedPhotos = [...selectedPhotoData].sort((a, b) =>
      new Date(a.date) - new Date(b.date)
    )

    // Assign positions (above/below) alternating for visual appeal
    const photosWithPosition = sortedPhotos.map((photo, index) => ({
      ...photo,
      position: index % 2 === 0 ? 'above' : 'below'
    }))

    console.log('Creating timeline with photos:', photosWithPosition)

    // Create a new timeline
    const newTimeline = {
      id: timelineVideos.value.length + 1, // reusing the same array
      title: `Timeline from ${new Date().toLocaleDateString()}`,
      date: new Date().toISOString().split('T')[0],
      description: `Timeline with ${photosWithPosition.length} photos`,
      coverImage: photosWithPosition[0]?.src || '',
      photos: photosWithPosition,
      isTimeline: true
    }

    // Add to timelines array
    timelineVideos.value.push(newTimeline)

    // Show success notification
    alert('Timeline created successfully!')

    // Clear selections and exit select mode
    clearSelections()
    isSelectMode.value = false
  } catch (error) {
    console.error('Error generating timeline:', error)
    alert('Failed to create timeline')
  } finally {
    isGeneratingVideo.value = false
  }
}

// Method to play a generated video
const playVideo = (video) => {
  alert(`Playing video: ${video.title}`)
}

async function downloadTimeline(timeline) {
  const original = document.getElementById(`timeline-${timeline.id}`);
  if (!original) throw new Error('Timeline element not found');

  // 1) Clone & size it to its full scrollable dimensions
  const clone = original.cloneNode(true);
  const fullW = original.scrollWidth;
  const fullH = original.scrollHeight;

  Object.assign(clone.style, {
    position: 'absolute',
    top: '0',
    left: '0',
    width: `${fullW}px`,
    height: `${fullH}px`,
    backgroundColor: '#fff',
    overflow: 'visible',
  });
  document.body.appendChild(clone);

  try {
    // 2) Pass width/height/windowWidth/windowHeight to html2canvas_pro
    const canvas = await html2canvas_pro(clone, {
      backgroundColor: '#ffffff',
      scale: 2,
      useCORS: true,
      allowTaint: true,
      width: fullW,
      height: fullH,
      windowWidth: fullW,
      windowHeight: fullH,
      scrollX: 0,
      scrollY: 0,
    });

    // 3) Download it
    const dataUrl = canvas.toDataURL('image/png');
    const link = document.createElement('a');
    link.download = `Timeline_${timeline.title.replace(/\s+/g, '_')}.png`;
    link.href = dataUrl;
    link.click();
  } catch (err) {
    console.error('Error downloading timeline:', err);
    alert('Failed to download timeline: ' + err.message);
  } finally {
    clone.remove();
  }
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithButton :icon="mdiCalendarMonth" title="Timeline Generator" main>
        <div class="flex">
          <BaseButton v-if="useApiData" :icon="mdiRefresh" tooltip="Refresh Photos"
            :color="isSelectMode ? 'info' : 'contrast'" small class="mr-2" label="Refresh"
            @click="$refs.photoGallery.refreshPhotos()" />
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
            :label="isGeneratingVideo ? 'Generating...' : 'Generate Timeline'" color="info" rounded-full small
            :disabled="selectedPhotos.length === 0 || isGeneratingVideo" @click="generateTimelineView" />
        </div>
        <div v-if="isSelectMode && selectedPhotos.length > 0" class="flex items-center">
          <span class="mr-2 text-sm text-gray-700">{{ selectedPhotos.length }} photos selected</span>
          <BaseButton label="Clear selection" color="whiteDark" small @click="clearSelections" />
        </div>
      </div>

      <!-- Generated Timelines Section -->
      <div v-if="timelineVideos.length > 0" class="mt-8">
        <h3 class="text-lg font-medium mb-4">Generated Timelines</h3>

        <div v-for="timeline in timelineVideos" :key="timeline.id" class="mb-10 border rounded-lg p-6 bg-white">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-medium">{{ timeline.title }}</h3>
            <div class="flex">
              <button class="text-blue-600 text-sm flex items-center mr-3" @click="downloadTimeline(timeline)">
                <svg class="w-4 h-4 mr-1" viewBox="0 0 24 24">
                  <path fill="currentColor" :d="mdiDownload" />
                </svg>
                Download
              </button>
            </div>
          </div>

          <p class="text-sm text-gray-500 mb-1">{{ timeline.date }}</p>
          <p class="text-gray-700 mb-4 text-sm">{{ timeline.description }}</p>

          <!-- Timeline Visualization -->
          <div class="relative overflow-x-auto" :id="`timeline-${timeline.id}`">
            <!-- Timeline points with photos -->
            <div class="relative py-32 min-h-[400px]"
              :style="{ minWidth: `${Math.max(100, timeline.photos.length * 100)}px` }">

              <!-- Center line - moved inside to ensure it spans the full width -->
              <div class="absolute left-0 right-0 h-1 bg-gray-300 top-1/2 transform -translate-y-1/2"
                :style="{ width: '100%' }"></div>

              <div v-for="(photo, index) in timeline.photos" :key="photo.id" class="absolute transform" :style="{
                left: `${(index / (timeline.photos.length - 1) * (80) + 5)}%`,
                transform: 'translateX(-30%)'
              }" :class="photo.position === 'above' ? 'bottom-1/2 mb-8' : 'top-1/2 mt-8'">
                <!-- Photo container -->
                <div :class="[
                  'flex flex-col items-center',
                  photo.position === 'above' ? 'origin-bottom' : 'origin-top'
                ]">
                  <!-- Date indicator for below-timeline photos (appears above photo) -->
                  <div v-if="photo.position === 'below'" class="mb-2 text-xs text-gray-600 font-medium">
                    {{ new Date(photo.date).toLocaleDateString() }}
                  </div>

                  <!-- Photo -->
                  <div class="relative group">
                    <img :src="photo.src" :alt="photo.name || 'Timeline photo'"
                      class="w-32 h-32 object-cover rounded border-2 border-white shadow-md"
                      @error="$event.target.src = 'https://placehold.co/150'" />
                    <div
                      class="absolute inset-0 bg-black opacity-0 hover:opacity-20 transition-opacity rounded pointer-events-none">
                    </div>
                  </div>

                  <!-- Date indicator for above-timeline photos (appears below photo) -->
                  <div v-if="photo.position === 'above'" class="mt-2 text-xs text-gray-600 font-medium">
                    {{ new Date(photo.date).toLocaleDateString() }}
                  </div>

                  <!-- Connector line to timeline -->
                  <div class="absolute h-6 w-0.5 bg-gray-400"
                    :class="photo.position === 'above' ? 'bottom-0 -mb-8' : 'top-0 -mt-8'"></div>

                  <!-- Timeline dot -->
                  <div class="absolute w-3 h-3 bg-blue-500 rounded-full transform -translate-x-[0.5px]"
                    :class="photo.position === 'above' ? 'bottom-0 -mb-3.5' : 'top-0 -mt-3'"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </SectionMain>
  </LayoutAuthenticated>
</template>
