<script setup>
import {
  mdiFolderMultiple,
  mdiFolderRemove,
  mdiImageMultiple,
  mdiRefresh
} from '@mdi/js'
import CardBox from '@/components/CardBox.vue'
import BaseButton from '@/components/BaseButton.vue'
import { useMainStore } from '@/stores/main'
import { useToast } from 'vue-toastification'
import { ref, onMounted } from 'vue'

const mainStore = useMainStore()
const toast = useToast()

// Define props for hide controls
const props = defineProps({
  hideDelete: {
    type: Boolean,
    default: false
  },
  hideActions: {
    type: Boolean,
    default: false
  }
})

// Define emits
const emit = defineEmits(['open-album', 'delete-album', 'refresh-albums'])

// Remove props and use internal state
const albums = ref([])
const isLoading = ref(false)
const error = ref(null)

// Fetch albums
const fetchAlbums = async () => {
  isLoading.value = true
  error.value = null

  try {
    // 1. 获取相册列表
    const params = new URLSearchParams({
      userId: mainStore.userId
    })
    const response = await fetch(`http://10.16.60.67:9090/album/list?${params}`, {
      method: 'POST'
    })
    const result = await response.json()

    if (!result || result.msg !== 'ok') {
      throw new Error('Failed to fetch albums')
    }

    // 2. 获取所有照片以计算数量和封面
    const photoResponse = await fetch(`http://10.16.60.67:9090/img/all?${params}`)
    const photoResult = await photoResponse.json()

    // 创建相册Map用于统计照片
    const albumPhotosMap = new Map()
    const albumCoverMap = new Map()

    // 处理照片数据
    if (photoResult && photoResult.data) {
      photoResult.data.forEach(photo => {
        const albumId = photo.albumId || null
        if (!albumPhotosMap.has(albumId)) {
          albumPhotosMap.set(albumId, 0)
        }
        albumPhotosMap.set(albumId, albumPhotosMap.get(albumId) + 1)

        // 设置相册封面(使用每个相册的第一张照片)
        if (!albumCoverMap.has(albumId)) {
          albumCoverMap.set(albumId,
            `http://10.16.60.67:9000/softwareeng/upload-img/${photo.imgId}.jpeg`)
        }
      })
    }

    // 3. 构建最终相册列表
    albums.value = [
      // 未分类相册
      {
        id: -1,
        name: '__Unfiled__',
        description: 'Photos not in any album',
        coverImage: albumCoverMap.get(null),
        photoCount: albumPhotosMap.get(null) || 0
      },
      // 其他相册
      ...(result.data || []).map(album => ({
        id: album.albumId,
        name: album.albumName,
        description: album.albumDescribtion || '',
        coverImage: albumCoverMap.get(album.albumId),
        photoCount: albumPhotosMap.get(album.albumId) || 0
      }))
    ].sort((a, b) => {
      if (a.id === null) return 1
      if (b.id === null) return -1
      return a.name.localeCompare(b.name)
    })

  } catch (err) {
    error.value = err.message
    toast.error(`Failed to load albums: ${err.message}`)
  } finally {
    isLoading.value = false
  }
}

onMounted(fetchAlbums)

defineExpose({
  refresh: fetchAlbums
})

// Handler for album card click
const handleAlbumClick = (album) => {
  console.log('Album clicked:', album) // Debug log
  emit('open-album', {
    id: album.id,
    name: album.name,
    description: album.description
  })
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
      <div v-for="album in albums"
           :key="album.id || 'unfiled'"
           class="cursor-pointer hover:scale-105 transition-transform duration-200"
           @click="handleAlbumClick(album)"
      >
        <div class="bg-white rounded-lg shadow-md overflow-hidden border border-gray-200">
          <!-- Album Cover -->
          <div class="h-40 bg-gray-200 relative">
            <img v-if="album.coverImage"
                 :src="album.coverImage"
                 class="w-full h-full object-cover"
                 alt="Album cover" />
            <div v-else class="flex items-center justify-center h-full bg-gray-100">
              <svg class="w-16 h-16 text-gray-400" viewBox="0 0 24 24">
                <path fill="currentColor" :d="mdiImageMultiple" />
              </svg>
            </div>

            <!-- Photo count badge -->
            <div class="absolute bottom-2 right-2 bg-black bg-opacity-60 text-white px-2 py-1 rounded text-sm">
              {{ album.photoCount }} photos
            </div>
          </div>

          <!-- Album info -->
          <div class="p-4">
            <div class="flex justify-between items-center">
              <h3 class="font-medium text-gray-900 truncate">{{ album.name }}</h3>
              <button v-if="album.id !== null && !props.hideDelete && !props.hideActions"
                @click.stop="handleAlbumDelete($event, album.id)"
                class="text-gray-500 hover:text-red-500"
                title="Delete album">
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
