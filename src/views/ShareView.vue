<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { mdiDownload, mdiImageMultiple, mdiArrowLeft } from '@mdi/js'
import BaseButton from '@/components/BaseButton.vue'

const route = useRoute()
const router = useRouter()
const toast = useToast()

// Add parentAlbumId state
const parentAlbumId = ref(null)

// State
const photo = ref(null)
const album = ref(null)
const albumPhotos = ref([])
const loading = ref(true)
const error = ref(null)

// 控制单张照片视图的显示
const showPhotoView = ref(false)

// Computed
const isAlbumView = computed(() => route.name === 'shareAlbum')

// Fetch album data
const fetchAlbum = async () => {
  const { userId, albumId } = route.params

  if (!userId || !albumId) {
    error.value = 'Invalid URL parameters'
    return
  }

  try {
    parentAlbumId.value = albumId
    const albumParams = new URLSearchParams({ userId, albumId })
    const albumResponse = await fetch(`http://10.16.60.67:9090/album/list?${albumParams}`, {
      method: 'POST'
    })
    const albumResult = await albumResponse.json()

    if (!albumResult?.data?.[0]) {
      throw new Error('Album not found')
    }

    album.value = albumResult.data[0]

    // 修改照片获取参数，添加 pub=true
    const photoParams = new URLSearchParams({
      userId,
      albumId: albumId === '-1' ? '' : albumId,
      pub: true // 添加此条件
    })
    const photoResponse = await fetch(`http://10.16.60.67:9090/img/all?${photoParams}`)
    const photoResult = await photoResponse.json()

    if (photoResult?.data) {
      albumPhotos.value = photoResult.data.map(img => ({
        id: img.imgId,
        name: img.imgName || `Image ${img.imgId}`,
        src: `http://10.16.60.67:9000/softwareeng/upload-img/${img.imgId}.jpeg`,
        date: img.imgDate,
        tags: img.tags || [],
        peoples: img.peoples || [],
        location: img.imgPos
      }))
    }
  } catch (err) {
    error.value = err.message
    toast.error(`Failed to load album: ${err.message}`)
  } finally {
    loading.value = false
  }
}

const fetchPhoto = async () => {
  const { userId, photoId } = route.params

  if (!userId || !photoId) {
    error.value = 'Invalid URL parameters'
    return
  }

  try {
    const params = new URLSearchParams({
      userId: userId,
      imgId: photoId,
      pub: true  // 添加此条件
    })

    const response = await fetch(`http://10.16.60.67:9090/img/all?${params}`)
    const result = await response.json()

    if (!result?.data?.[0]) {
      throw new Error('Photo not found')
    }

    const photoData = result.data[0]
    // 检查照片是否公开
    if (!photoData.pub) {
      throw new Error('This photo is private')
    }

    photo.value = {
      id: photoData.imgId,
      name: photoData.imgName || `Image ${photoData.imgId}`,
      src: `http://10.16.60.67:9000/softwareeng/upload-img/${photoData.imgId}.jpeg`,
      date: photoData.imgDate,
      tags: photoData.tags || [],
      peoples: photoData.peoples || [],
      location: photoData.imgPos
    }
    showPhotoView.value = true // 显示单张照片视图
  } catch (err) {
    error.value = err.message
    toast.error(`Failed to load photo: ${err.message}`)
    showPhotoView.value = false
  } finally {
    loading.value = false
  }
}

const downloadPhoto = async () => {
  if (!photo.value?.src) return

  try {
    const response = await fetch(photo.value.src)
    const blob = await response.blob()
    const url = URL.createObjectURL(blob)

    const link = document.createElement('a')
    link.href = url
    link.download = photo.value.name || 'photo'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)

    toast.success('Photo downloaded successfully')
  } catch (err) {
    toast.error(`Failed to download photo: ${err.message}`)
  }
}

// Add back to album handler
const backToAlbum = () => {
  if (!parentAlbumId.value) return

  showPhotoView.value = false // 关闭单张照片视图
  router.push({
    name: 'shareAlbum',
    params: {
      userId: route.params.userId,
      albumId: parentAlbumId.value
    }
  })
}

// Navigate to photo view
const viewPhoto = (photoId) => {
  showPhotoView.value = false // 先关闭当前 photoView
  router.push({
    name: 'sharePhoto',
    params: {
      userId: route.params.userId,
      photoId: photoId,
      albumId: album.value?.albumId
    }
  })
}

onMounted(() => {
  if (isAlbumView.value) {
    fetchAlbum()
    showPhotoView.value = false
  } else {
    // 如果是从相册进入照片视图，从路由参数获取 albumId
    const { albumId } = route.params
    if (albumId) {
      parentAlbumId.value = albumId
    }
    fetchPhoto()
  }
})

// Add route watcher
watch(
  () => route.params,
  () => {
    loading.value = true
    error.value = null
    if (isAlbumView.value) {
      fetchAlbum()
      showPhotoView.value = false
    } else {
      fetchPhoto()
    }
  },
  { deep: true }
)
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-100 via-white to-purple-100 py-10 px-2">
    <div class="max-w-5xl mx-auto">
      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-24">
        <div class="animate-spin rounded-full h-16 w-16 border-4 border-blue-400 border-t-transparent"></div>
      </div>

      <!-- Error State -->
      <div v-else-if="error"
        class="bg-red-50 border-l-4 border-red-500 p-6 rounded-xl shadow text-lg text-red-700 text-center">
        <p>{{ error }}</p>
      </div>

      <!-- Single Photo View -->
      <div v-else-if="photo && showPhotoView" class="space-y-8">
        <div class="bg-white rounded-2xl shadow-xl overflow-hidden border border-blue-100">
          <!-- Header with Navigation and Actions -->
          <div class="px-8 py-6 flex flex-col md:flex-row md:justify-between md:items-center bg-gradient-to-r from-blue-50 to-indigo-50 border-b">
            <div class="flex items-center gap-4 mb-3 md:mb-0">
              <BaseButton
                v-if="parentAlbumId"
                :icon="mdiArrowLeft"
                :color="'whiteDark'"
                class="hover:bg-white/80 transition-colors"
                @click="backToAlbum"
              />
              <div>
                <h1 class="text-2xl font-bold text-gray-800 mb-1">{{ photo.name }}</h1>
                <p class="text-sm text-gray-500">
                  <span v-if="photo.date">{{ new Date(photo.date).toLocaleDateString() }}</span>
                  <span v-if="photo.location"> · {{ photo.location }}</span>
                </p>
              </div>
            </div>
            <BaseButton
              :icon="mdiDownload"
              label="Download"
              color="info"
              class="shadow-sm"
              @click="downloadPhoto"
            />
          </div>

          <!-- Photo Display -->
          <div class="relative bg-gray-900 flex justify-center items-center min-h-[400px] py-8">
            <img
              :src="photo.src"
              :alt="photo.name"
              class="h-[60vh] w-auto object-contain rounded-lg shadow-lg border-4 border-white"
              style="background: linear-gradient(135deg, #e0e7ff 0%, #f3e8ff 100%);"
            />
          </div>

          <!-- Photo Metadata -->
          <div class="p-8 space-y-6 bg-white">
            <!-- Tags Section -->
            <div v-if="photo.tags?.length" class="space-y-2">
              <h3 class="text-sm font-semibold text-blue-700 uppercase tracking-wider">Tags</h3>
              <div class="flex flex-wrap gap-2">
                <span
                  v-for="tag in photo.tags"
                  :key="tag"
                  class="px-3 py-1 bg-blue-50 text-blue-700 rounded-full text-sm font-medium border border-blue-200 hover:bg-blue-100 transition-colors"
                >
                  {{ tag }}
                </span>
              </div>
            </div>

            <!-- People Section -->
            <div v-if="photo.peoples?.length" class="space-y-2">
              <h3 class="text-sm font-semibold text-purple-700 uppercase tracking-wider">People</h3>
              <div class="flex flex-wrap gap-2">
                <span
                  v-for="person in photo.peoples"
                  :key="person"
                  class="px-3 py-1 bg-purple-50 text-purple-700 rounded-full text-sm font-medium border border-purple-200 hover:bg-purple-100 transition-colors"
                >
                  {{ person }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Album View -->
      <div v-else-if="isAlbumView && album" class="space-y-8">
        <div class="bg-white rounded-2xl shadow-xl border border-blue-100 p-8">
          <div class="flex items-center gap-3 mb-6">
            <svg class="w-8 h-8 text-blue-600" viewBox="0 0 24 24">
              <path fill="currentColor" :d="mdiImageMultiple" />
            </svg>
            <h1 class="text-3xl font-bold text-gray-800">
              {{ album.albumName === '__Unfiled__' ? 'Unfiled Photos' : album.albumName }}
            </h1>
          </div>
          <p v-if="album.albumDescription" class="text-gray-600 mb-2">
            {{ album.albumDescription }}
          </p>
          <p class="text-sm text-gray-500 mb-6">
            {{ albumPhotos.length }} photos
          </p>
          <!-- Photos Grid -->
          <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
            <div v-for="photo in albumPhotos"
                 :key="photo.id"
                 class="aspect-square bg-gradient-to-br from-blue-50 to-purple-50 rounded-xl shadow-md overflow-hidden hover:shadow-xl transition-shadow cursor-pointer flex items-center justify-center"
                 @click="viewPhoto(photo.id)">
              <img :src="photo.src"
                   :alt="photo.name"
                   class="w-full h-full object-cover transition-transform duration-200 hover:scale-105" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
