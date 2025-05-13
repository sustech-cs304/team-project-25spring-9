<script setup>
import {
  mdiFolderMultiple,
  mdiFolderRemove,
  mdiImageMultiple,
  mdiRefresh
} from '@mdi/js'
import CardBox from '@/components/CardBox.vue'
import BaseButton from '@/components/BaseButton.vue'
import { ref } from 'vue'

// Define props
const props = defineProps({
  albums: {
    type: Array,
    required: true
  },
  isLoading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: null
  }
})

// Define emits
const emit = defineEmits([
  'open-album',
  'delete-album',
  'refresh-albums'
])

// Handler for album card click
const handleAlbumClick = (album) => {
  emit('open-album', album)
}

// Handler for album delete
const handleAlbumDelete = (event, albumId) => {
  event.stopPropagation()
  emit('delete-album', albumId)
}
</script>

<template>
  <!-- Loading state -->
  <div v-if="isLoading" class="flex justify-center items-center py-12">
    <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-500"></div>
  </div>

  <!-- Error state -->
  <div v-else-if="error" class="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-4">
    <p class="font-bold">Error</p>
    <p>{{ error }}</p>
    <BaseButton label="Retry" color="danger" small class="mt-2" @click="$emit('refresh-albums')" />
  </div>

  <!-- Albums Grid -->
  <CardBox v-else class="mb-6">
    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
      <div v-for="album in albums" :key="album.id || 'unfiled'"
        class="cursor-pointer hover:scale-105 transition-transform duration-200" @click="handleAlbumClick(album)">
        <div class="bg-white rounded-lg shadow-md overflow-hidden border border-gray-200">
          <!-- Album Cover -->
          <div class="h-40 bg-gray-200 relative">
            <img v-if="album.coverImage" :src="album.coverImage" class="w-full h-full object-cover" alt="Album cover" />
            <div v-else class="flex items-center justify-center h-full bg-gray-100">
              <svg class="w-16 h-16 text-gray-400" viewBox="0 0 24 24">
                <path fill="currentColor" :d="mdiImageMultiple" />
              </svg>
            </div>

            <!-- Photo count badge -->
            <div class="absolute bottom-2 right-2 bg-black bg-opacity-60 text-white px-2 py-1 rounded text-sm">
              {{ album.photos.length }} photos
            </div>
          </div>

          <!-- Album info -->
          <div class="p-4">
            <div class="flex justify-between items-center">
              <h3 class="font-medium text-gray-900 truncate">{{ album.name }}</h3>

              <!-- Actions (not applicable for Unfiled) -->
              <button v-if="album.id !== null" @click.stop="handleAlbumDelete($event, album.id)"
                class="text-gray-500 hover:text-red-500" title="Delete album">
                <svg class="w-5 h-5" viewBox="0 0 24 24">
                  <path fill="currentColor" :d="mdiFolderRemove" />
                </svg>
              </button>
            </div>
            <p v-if="album.description" class="text-sm text-gray-600 mt-1 truncate">{{ album.description }}</p>
          </div>
        </div>
      </div>
    </div>
  </CardBox>
</template>
