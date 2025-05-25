<script setup>
import {
  mdiTag,
  mdiTagMultiple,
  mdiTagRemove,
  mdiTagPlus,
  mdiRefresh,
  mdiDeleteOutline,
  mdiExport,
  mdiFilterVariant,
  mdiClose,
  mdiImageEdit,
  mdiDelete,
  mdiDownload,
  mdiCheckboxMultipleMarkedOutline,
  mdiCursorDefault,
  mdiImageSearch,
  mdiViewList,
  mdiViewGrid,
  mdiViewGridOutline,
  mdiViewCompactOutline,
  mdiImageMultiple,
  mdiArrowLeft,
} from '@mdi/js'

import { ref, computed, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import { useMainStore } from '@/stores/main'

import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'
import BaseButton from '@/components/BaseButton.vue'
import PhotoGallery from '@/components/PhotoGallery.vue'
import AlbumsGallery from '@/components/AlbumsGallery.vue'

const toast = useToast()
const mainStore = useMainStore()

// State
const isLoading = ref(false)
const error = ref(null)
const photos = ref([])
const selectedTag = ref(null)
const viewMode = ref('grid')
const isSelectMode = ref(false)
const selectedPhotos = ref([])
const currentTagGroup = ref(null)
const photoGallery = ref(null)

// Computed: Group photos by tag
const photosByTag = computed(() => {
  const groupedPhotos = new Map()

  groupedPhotos.set('untagged', {
    name: 'Untagged',
    photos: photos.value.filter(p => !p.tags?.length)
  })

  photos.value.forEach(photo => {
    photo.tags?.forEach(tag => {
      if (!groupedPhotos.has(tag)) {
        groupedPhotos.set(tag, { name: tag, photos: [] })
      }
      groupedPhotos.get(tag).photos.push(photo)
    })
  })

  return Array.from(groupedPhotos.values()).sort((a, b) => b.photos.length - a.photos.length)
})

const tagAlbums = computed(() => {
  return photosByTag.value.map(group => ({
    id: group.name,
    name: group.name === 'untagged' ? 'Untagged Photos' : group.name,
    description: group.name === 'untagged' ? 'Photos without any tags' : `Photos tagged with "${group.name}"`,
    photos: group.photos,
    coverImage: group.photos[0]?.src || null,
    isTagGroup: true,
    count: group.photos.length,
    style: group.name === 'untagged' ? 'opacity-70' : ''
  }))
})

// Fetch and format photos
const fetchPhotos = async () => {
  isLoading.value = true
  error.value = null
  try {
    const params = new URLSearchParams({ userId: mainStore.userId })
    const response = await fetch(`http://10.16.60.67:9090/img/all?${params}`)
    const result = await response.json()
    if (!result?.data) throw new Error('Failed to fetch photos')

    photos.value = result.data.map(photo => ({
      id: photo.imgId,
      name: photo.imgName || `Image ${photo.imgId}`,
      src: `http://10.16.60.67:9000/softwareeng/upload-img/${photo.imgId}.jpeg`,
      date: photo.imgDate,
      displayDate: formatDate(photo.imgDate),
      tags: photo.tags || [],
      size: 'Unknown',
      type: photo.imgType || 'JPEG',
      peoples: photo.peoples,
      location: photo.imgPos,
      desc: photo.imgDescribtion || '',
      userId: photo.userId
    }))
  } catch (err) {
    error.value = `Failed to fetch photos: ${err.message}`
    toast.error(error.value)
  } finally {
    isLoading.value = false
  }
}

// Utils
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
}

// Tag/group view switching
const selectTagGroup = (group) => {
  // 从标签组获取完整的album信息
  const targetAlbum = group.isTagGroup
    ? group
    : tagAlbums.value.find(album => album.id === group.name)

  if (targetAlbum) {
    currentTagGroup.value = targetAlbum
    selectedTag.value = targetAlbum.id
    selectedPhotos.value = []
  }
}

const backToTags = () => {
  currentTagGroup.value = null
  selectedTag.value = null
  selectedPhotos.value = []
}

// Filtering
const filteredPhotos = computed(() => {
  if (!selectedTag.value) return photos.value
  return selectedTag.value === 'untagged'
    ? photos.value.filter(p => !p.tags?.length)
    : photos.value.filter(p => p.tags?.includes(selectedTag.value))
})

// Selection logic (mimic PhotosView)
const togglePhotoSelection = (photoId) => {
  if (selectedPhotos.value.includes(photoId)) {
    selectedPhotos.value = selectedPhotos.value.filter(id => id !== photoId)
  } else {
    selectedPhotos.value.push(photoId)
  }
}

const toggleSelectMode = () => {
  isSelectMode.value = !isSelectMode.value
  if (!isSelectMode.value) {
    clearSelections()
  }
}

const clearSelections = () => {
  selectedPhotos.value = []
}

// Actions (delegate to PhotoGallery)
const handleDelete = () => {
  photoGallery.value?.deletePhotos(selectedPhotos)
  selectedPhotos.value = []
}
const handleDownload = () => {
  photoGallery.value?.downloadPhotos(selectedPhotos)
}

onMounted(fetchPhotos)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithButton
        :icon="currentTagGroup ? mdiImageMultiple : mdiTagMultiple"
        :title="currentTagGroup ? currentTagGroup.name : 'Tags'"
        main
        class="mb-6"
      >
        <div class="flex gap-3">
          <BaseButton
            v-if="currentTagGroup"
            :icon="isSelectMode ? mdiCursorDefault : mdiCheckboxMultipleMarkedOutline"
            :label="isSelectMode ? 'View Mode' : 'Select Mode'"
            :color="isSelectMode ? 'info' : 'contrast'"
            small
            class="transition-all duration-200"
            @click="toggleSelectMode"
          />
          <BaseButton
            v-if="currentTagGroup"
            :icon="mdiArrowLeft"
            label="Back to Tags"
            color="contrast"
            class="shadow hover:shadow-lg transition-all duration-200 transform hover:-translate-y-0.5"
            small
            @click="backToTags"
          />
          <BaseButton
            v-else
            :icon="mdiRefresh"
            label="Refresh"
            color="info"
            class="shadow hover:shadow-lg transition-all duration-200 transform hover:-translate-y-0.5"
            small
            @click="fetchPhotos"
          />
        </div>
      </SectionTitleLineWithButton>

      <div v-if="!isLoading && !error" class="space-y-6">
        <!-- Tag summary -->
        <CardBox class="overflow-hidden bg-white shadow-lg">
          <div class="flex flex-wrap gap-2 p-4 max-h-36 overflow-y-auto custom-scrollbar">
            <BaseButton
              :icon="mdiFilterVariant"
              :label="`All Photos (${photos.length})`"
              :color="!selectedTag ? 'info' : 'whiteDark'"
              :class="{'shadow transform hover:-translate-y-0.5': !selectedTag}"
              class="transition-all duration-200"
              small
              @click="backToTags"
            />
            <BaseButton
              v-for="group in photosByTag"
              :key="group.name"
              :icon="mdiTag"
              :label="`${group.name === 'untagged' ? 'Untagged' : group.name} (${group.photos.length})`"
              :color="selectedTag === group.name ? 'info' : 'whiteDark'"
              :class="{
                'opacity-75 hover:opacity-100': group.photos.length === 0,
                'shadow transform hover:-translate-y-0.5': selectedTag === group.name
              }"
              class="transition-all duration-200"
              small
              @click="selectTagGroup(group)"
            />
          </div>
        </CardBox>

        <!-- Tag group gallery -->
        <div v-if="!currentTagGroup"
          class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <CardBox
            v-for="album in tagAlbums"
            :key="album.id"
            class="cursor-pointer transition-all duration-300 transform hover:-translate-y-2 hover:shadow-xl bg-white"
            :class="album.style"
            @click="selectTagGroup(album)"
          >
            <div class="relative aspect-[4/3] overflow-hidden rounded-t-lg">
              <img
                v-if="album.coverImage"
                :src="album.coverImage"
                class="absolute inset-0 w-full h-full object-cover rounded-t-lg"
                alt="Album cover"
              />
              <div v-else class="absolute inset-0 bg-gray-50 flex items-center justify-center">
                <svg class="w-16 h-16 text-gray-300" viewBox="0 0 24 24">
                  <path fill="currentColor" :d="mdiImageMultiple" />
                </svg>
              </div>
              <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent opacity-0 hover:opacity-100 transition-opacity duration-300"/>
              <div class="absolute bottom-3 right-3 bg-black/80 text-white px-4 py-1.5 rounded-full text-sm font-medium backdrop-blur-sm">
                {{ album.photos.length }} photos
              </div>
            </div>
            <div class="p-5">
              <h3 class="text-lg font-semibold mb-2 flex items-center gap-2">
                <svg class="w-5 h-5 text-blue-500" viewBox="0 0 24 24">
                  <path fill="currentColor" :d="mdiTag" />
                </svg>
                {{ album.name }}
              </h3>
              <p class="text-sm text-gray-600">{{ album.description }}</p>
            </div>
          </CardBox>
        </div>

        <!-- PhotoGallery layout (no upload/edit, mimic PhotosView) -->
        <div v-else>
          <CardBox class="mb-4">
            <PhotoGallery
              ref="photoGallery"
              :key="selectedTag"
              :photos="filteredPhotos"
              :initial-view-mode="viewMode"
              :available-view-modes="['details', 'grid', 'large', 'small']"
              :is-select-mode="isSelectMode"
              :selected-photo-ids="selectedPhotos"
              :show-actions="true"
              class="p-2 shadow-none"
              @select-photo="togglePhotoSelection"
              @update:viewMode="mode => viewMode = mode"
            />
          </CardBox>
          <div class="flex justify-between mb-6">
            <div class="flex gap-2">
              <BaseButton
                v-if="isSelectMode"
                :icon="mdiDeleteOutline"
                label="Remove"
                color="danger"
                rounded-full
                small
                :disabled="selectedPhotos.length === 0"
                @click="handleDelete"
              />
              <BaseButton
                v-if="isSelectMode"
                :icon="mdiDownload"
                label="Download"
                color="success"
                rounded-full
                small
                :disabled="selectedPhotos.length === 0"
                @click="handleDownload"
              />
            </div>
            <div v-if="isSelectMode && selectedPhotos.length > 0" class="flex items-center">
              <span class="mr-2 text-sm text-gray-700">{{ selectedPhotos.length }} selected</span>
              <BaseButton label="Clear selection" color="whiteDark" small @click="clearSelections" />
            </div>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex justify-center items-center py-16">
        <div class="animate-spin rounded-full h-14 w-14 border-4 border-blue-500 border-t-transparent shadow-lg"/>
      </div>

      <!-- Error State -->
      <div v-if="error" class="bg-red-50 border-l-4 border-red-500 text-red-700 p-6 rounded-lg shadow-lg">
        <div class="flex items-center mb-2">
          <svg class="w-5 h-5 mr-2" viewBox="0 0 24 24">
            <path fill="currentColor" :d="mdiClose" />
          </svg>
          <p class="font-bold">Error</p>
        </div>
        <p>{{ error }}</p>
        <BaseButton label="Retry" color="danger" small class="mt-3" @click="fetchPhotos" />
      </div>
    </SectionMain>
  </LayoutAuthenticated>
</template>

<style scoped>
.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: #93c5fd #f8fafc;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: #f8fafc;
  border-radius: 4px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #93c5fd;
  border-radius: 4px;
  transition: all 0.2s;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background-color: #60a5fa;
}
</style>
