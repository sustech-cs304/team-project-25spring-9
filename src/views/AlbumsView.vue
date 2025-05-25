<script setup>
import {
  mdiFolderMultiple,
  mdiFolderPlus,
  mdiFolderEdit,
  mdiFolderRemove,
  mdiImageMultiple,
  mdiImageMove,
  mdiCheckboxMultipleMarkedOutline,
  mdiCursorDefault,
  mdiRefresh,
  mdiArrowLeft,
  mdiCardPlus,
  mdiImagePlus,
  mdiImageRemove,
  mdiImageEdit,
  mdiDownload
} from '@mdi/js'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'
import BaseButton from '@/components/BaseButton.vue'
import PhotoGallery from '@/components/PhotoGallery.vue'
import PhotoEditor from '@/components/PhotoEditor.vue'
import AlbumsGallery from '@/components/AlbumsGallery.vue'
import { ref, computed, onMounted, nextTick } from 'vue'
import { useToast } from 'vue-toastification'
import { useMainStore } from '@/stores/main'

const toast = useToast()
const mainStore = useMainStore()

// Track state
const isLoading = ref(false)
const error = ref(null)
const currentAlbumId = ref(null)
const isSelectMode = ref(false)
const selectedPhotos = ref([])

// Viewing options
const viewMode = ref('grid')
const photoGallery = ref(null)
const albumsGallery = ref(null)

// New album dialog
const showNewAlbumDialog = ref(false)
const newAlbumName = ref("")
const newAlbumDescription = ref("")

// Move photos dialog
const showMoveDialog = ref(false)
const targetAlbumId = ref(null)

// Editor and uploader states
const showEditor = ref(false)
const editingPhoto = ref(null)

// 新增 currentAlbumTitle ref
const currentAlbumTitle = ref('Albums')

// 添加 albums ref 用于存储相册列表
const albums = ref([])

// Handler for clicking on an album
const handleOpenAlbum = (album) => {
  console.log('Opening album:', album) // Debug log
  currentAlbumId.value = album.id
  currentAlbumTitle.value = album.name === '__Unfiled__' ? 'Unfiled Photos' : album.name
  selectedPhotos.value = []
  nextTick(() => {
    photoGallery.value?.refreshPhotos()
  })
}

// Handler for deleting an album
const handleDeleteAlbum = async (albumId) => {
  if (!confirm(`Are you sure you want to delete this album? Photos will be moved to Unfiled.`)) {
    return
  }

  try {
    const params = new URLSearchParams({
      userId: mainStore.userId,
      albumId: albumId
    })

    const response = await fetch(`http://10.16.60.67:9090/album/delete?${params}`, {
      method: 'POST'
    })
    const result = await response.json()

    if (!result || result.msg !== 'ok') {
      throw new Error(result?.msg || 'Failed to delete album')
    }

    toast.success("Album deleted successfully")
    // Refresh albums through the ref
    albumsGallery.value?.refresh()

  } catch (err) {
    toast.error(`Failed to delete album: ${err.message}`)
  }
}

// Handler for going back to albums list
const handleBackToAlbums = () => {
  currentAlbumId.value = null
  currentAlbumTitle.value = 'Albums'  // 添加这一行
  selectedPhotos.value = []
}

// Handler for refreshing albums
const handleRefreshAlbums = () => {
  albumsGallery.value?.refresh()
}

// Toggle photo selection
const togglePhotoSelection = (photoId) => {
  if (selectedPhotos.value.includes(photoId)) {
    selectedPhotos.value = selectedPhotos.value.filter(id => id !== photoId);
  } else {
    selectedPhotos.value.push(photoId);
  }
};

// Toggle select mode
const toggleSelectMode = () => {
  isSelectMode.value = !isSelectMode.value;
  if (!isSelectMode.value) {
    selectedPhotos.value = [];
  }
};

// Clear selections
const clearSelections = () => {
  selectedPhotos.value = [];
};

// Move photos to another album
const movePhotosToAlbum = async () => {
  if (selectedPhotos.value.length === 0 || targetAlbumId.value === null) {
    toast.error("Please select photos and a target album")
    return
  }

  try {
    const movePromises = selectedPhotos.value.map(async photoId => {
      // 通过 PhotoGallery 获取照片信息
      const photo = photoGallery.value?.getPhotoById(photoId)
      if (!photo) return Promise.resolve()

      const params = new URLSearchParams({
        userId: mainStore.userId,
        imgId: photoId,
        albumId: targetAlbumId.value === "null" ? "" : targetAlbumId.value,
        name: photo.name || `Image ${photoId}`,
        pub: true
      })

      const response = await fetch(`http://10.16.60.67:9090/img/cname?${params}`, {
        method: 'GET'
      })
      const result = await response.json()
      if (!result || result.msg !== 'ok') {
        throw new Error(result?.msg || 'Failed to move photo')
      }
      return result
    })

    await Promise.all(movePromises)
    toast.success(`${selectedPhotos.value.length} photo(s) moved successfully`)
    showMoveDialog.value = false
    targetAlbumId.value = null
    selectedPhotos.value = []

    // 刷新相册和照片列表
    albumsGallery.value?.refresh()
    photoGallery.value?.refreshPhotos()

  } catch (err) {
    toast.error(`Failed to move photos: ${err.message}`)
  }
};

// Open move dialog
const openMoveDialog = async () => {
  if (selectedPhotos.value.length === 0) {
    toast.error("Please select photos to move")
    return
  }
  await loadAlbums()
  showMoveDialog.value = true
};

// Handle delete photos
const handleDelete = () => {
  photoGallery.value?.deletePhotos(selectedPhotos)
  selectedPhotos.value = []
  // 刷新相册和照片列表
  albumsGallery.value?.refresh()
}

// Handler for download photos
const handleDownload = () => {
  photoGallery.value?.downloadPhotos(selectedPhotos)
}

// Open editor
const openEditor = () => {
  if (selectedPhotos.value.length === 1) {
    openEditorWithPhoto(selectedPhotos.value[0])
  }
}

// Open editor with photo ID
const openEditorWithPhoto = (photoId) => {
  const photo = photoGallery.value?.getPhotoById(photoId)
  if (!photo) {
    toast.error("Photo not found")
    return
  }

  editingPhoto.value = photo
  showEditor.value = true
  isSelectMode.value = false
  selectedPhotos.value = []
}

// Close editor
const closeEditor = () => {
  showEditor.value = false
  editingPhoto.value = null
}

// Save edited photo
const saveEditedPhoto = (updatedPhoto) => {
  photoGallery.value?.uploadPhotos(updatedPhoto)
  closeEditor()
  // 刷新相册列表
  albumsGallery.value?.refresh()
}

// Add album create method
const createAlbum = async () => {
  if (!newAlbumName.value.trim()) {
    toast.error('Please enter album name')
    return
  }

  try {
    const params = new URLSearchParams({
      userId: mainStore.userId,
      names: newAlbumName.value.trim(),
      albumDescription: newAlbumDescription.value.trim() || ''
    })

    const response = await fetch(`http://10.16.60.67:9090/album/new?${params}`, {
      method: 'POST'
    })
    const result = await response.json()

    if (!result || result.msg !== 'ok') {
      throw new Error(result?.msg || 'Failed to create album')
    }

    toast.success('Album created successfully')
    showNewAlbumDialog.value = false
    newAlbumName.value = ''
    newAlbumDescription.value = ''

    // Refresh albums list
    albumsGallery.value?.refresh()

  } catch (err) {
    toast.error(`Failed to create album: ${err.message}`)
  }
}

// 添加 loadAlbums 方法
const loadAlbums = async () => {
  try {
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
    // 组装 albums 列表，未分类相册
    albums.value = [
      {
        id: -1,
        name: 'Unfiled Photos',
        photos: []
      },
      ...(result.data || []).map(album => ({
        id: album.albumId,
        name: album.albumName,
        photos: [] // 可根据需要填充照片数
      }))
    ]
  } catch (err) {
    toast.error('Failed to load albums')
    albums.value = []
  }
}

// 删除 onMounted(() => { fetchAlbums(); });

</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <!-- Update title section to use currentAlbumTitle -->
      <SectionTitleLineWithButton
        :icon="currentAlbumId ? mdiImageMultiple : mdiFolderMultiple"
        :title="currentAlbumTitle"
        main
      >
        <div class="flex">
          <BaseButton
            v-if="currentAlbumId"
            :icon="isSelectMode ? mdiCursorDefault : mdiCheckboxMultipleMarkedOutline"
            :label="isSelectMode ? 'View Mode' : 'Select Mode'"
            :color="isSelectMode ? 'info' : 'contrast'"
            small
            class="mr-2"
            @click="isSelectMode = !isSelectMode"
          />
          <!-- Add refresh button -->
          <BaseButton
            v-if="currentAlbumId"
            :icon="mdiRefresh"
            label="Refresh"
            color="info"
            small
            class="mr-2"
            @click="photoGallery?.refreshPhotos()"
          />
          <BaseButton
            v-if="currentAlbumId"
            :icon="mdiArrowLeft"
            label="Back to Albums"
            color="contrast"
            small
            @click="handleBackToAlbums"
          />
          <template v-else>
            <BaseButton
              :icon="mdiRefresh"
              label="Refresh"
              color="info"
              small
              class="mr-2"
              @click="handleRefreshAlbums"
            />
            <BaseButton
              :icon="mdiFolderPlus"
              label="New Album"
              color="info"
              small
              @click="showNewAlbumDialog = true"
            />
          </template>
        </div>
      </SectionTitleLineWithButton>

      <!-- Album List -->
      <AlbumsGallery
        v-if="currentAlbumId === null"
        ref="albumsGallery"
        @open-album="handleOpenAlbum"
        @delete-album="handleDeleteAlbum"
      />

      <!-- Photo Gallery -->
      <div v-else>
        <CardBox class="mb-6">
          <PhotoGallery
            ref="photoGallery"
            :key="String(currentAlbumId)"
            :album-id="currentAlbumId"
            :initial-view-mode="viewMode"
            :available-view-modes="['details', 'grid', 'large', 'small']"
            :is-select-mode="isSelectMode"
            :selected-photo-ids="selectedPhotos"
            :show-actions="true"
            :use-api-data="true"
            :user-id="mainStore.userId"
            @select-photo="togglePhotoSelection"
            @update:viewMode="mode => viewMode = mode"
            @photo-edit="openEditorWithPhoto"
          />
        </CardBox>

        <!-- Action buttons for photo management within album -->
        <div class="mb-4 flex justify-between">
          <div class="flex gap-2">
            <!-- Change Upload button to pass currentAlbumId -->
            <BaseButton
              :icon="mdiImagePlus"
              label="Upload"
              color="info"
              small
              @click="$refs.photoGallery.initiateUpload(currentAlbumId)"
            />

            <template v-if="isSelectMode">
              <!-- Show move button in select mode when photos are selected -->
              <BaseButton v-if="selectedPhotos.length > 0" :icon="mdiImageMove" label="Move to Album" color="success"
                small @click="openMoveDialog" />

              <!-- Delete button -->
              <BaseButton :icon="mdiImageRemove" label="Remove" color="danger" small
                :disabled="selectedPhotos.length === 0" @click="handleDelete" />

              <!-- Download button -->
              <BaseButton :icon="mdiDownload" label="Download" color="success" small
                :disabled="selectedPhotos.length === 0" @click="handleDownload" />

              <!-- Edit button -->
              <BaseButton :icon="mdiImageEdit" label="Edit" color="info" small :disabled="selectedPhotos.length !== 1"
                @click="openEditor" />
            </template>
          </div>

          <div v-if="isSelectMode && selectedPhotos.length > 0" class="flex items-center">
            <span class="mr-2 text-sm text-gray-700">{{ selectedPhotos.length }} selected</span>
            <BaseButton label="Clear selection" color="whiteDark" small @click="clearSelections" />
          </div>
        </div>
      </div>

      <!-- New Album Dialog -->
      <div v-if="showNewAlbumDialog" class="fixed inset-0 bg-opacity-50 backdrop-blur-sm flex items-center justify-center z-50">
        <div class="bg-white rounded-lg p-6 max-w-md w-full" @click.stop>
          <h3 class="text-xl font-medium mb-4">Create New Album</h3>

          <div class="mb-4">
            <label class="block text-gray-700 text-sm font-medium mb-2">Album Name</label>
            <input v-model="newAlbumName" type="text"
              class="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-300"/>
          </div>
          <div class="mb-4">
            <label class="block text-gray-700 text-sm font-medium mb-2">Album Description</label>
            <input v-model="newAlbumDescription" type="text"
              class="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-300"/>
          </div>

          <div class="flex justify-end gap-2">
            <BaseButton label="Cancel" color="whiteDark" @click="showNewAlbumDialog = false" />
            <BaseButton :icon="mdiFolderPlus" label="Create" color="info" @click="createAlbum" />
          </div>
        </div>
      </div>

      <!-- Move Photos Dialog -->
      <div v-if="showMoveDialog" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg p-6 max-w-md w-full" @click.stop>
          <h3 class="text-xl font-medium mb-4">Move {{ selectedPhotos.length }} Photo(s) to Album</h3>

          <div class="mb-4">
            <label class="block text-gray-700 text-sm font-medium mb-2">Select Destination Album</label>
            <select v-model="targetAlbumId"
              class="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-300">
              <option value="" disabled selected>Select an album</option>
              <option v-for="album in albums" :key="album.id || 'unfiled'"
                :value="album.id === null ? 'null' : album.id">
                {{ album.name }}
              </option>
            </select>
          </div>

          <div class="flex justify-end gap-2">
            <BaseButton label="Cancel" color="whiteDark" @click="showMoveDialog = false" />
            <BaseButton :icon="mdiImageMove" label="Move" color="success" @click="movePhotosToAlbum" />
          </div>
        </div>
      </div>

      <!-- Photo Editor Modal -->
      <PhotoEditor v-if="showEditor" :photo="editingPhoto" @save="saveEditedPhoto" @close="closeEditor" />
    </SectionMain>
  </LayoutAuthenticated>
</template>

<style scoped>
/* Add any component-specific styles here */
</style>
