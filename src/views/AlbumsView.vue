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
  mdiCardPlus
} from '@mdi/js'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'
import BaseButton from '@/components/BaseButton.vue'
import PhotoGallery from '@/components/PhotoGallery.vue'
import { ref, computed, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import { useMainStore } from '@/stores/main'

const toast = useToast()
const mainStore = useMainStore()

// Track state
const isLoading = ref(false)
const error = ref(null)
const albums = ref([])
const currentAlbum = ref(null)
const isSelectMode = ref(false)
const selectedPhotos = ref([])

// Viewing options
const viewMode = ref('grid')

// Create a reference to the PhotoGallery component
const photoGallery = ref(null)

// New album dialog
const showNewAlbumDialog = ref(false)
const newAlbumName = ref("")

// Move photos dialog
const showMoveDialog = ref(false)
const targetAlbumId = ref(null)

// Initialize albums from API data
const fetchAlbums = async () => {
  isLoading.value = true;
  error.value = null;

  try {
    // Fetch all photos first
    const params = new URLSearchParams({
      userId: mainStore.userId.toString()
    });

    const response = await fetch(`http://10.16.60.67:9090/img/all?${params}`);
    const result = await response.json();

    if (!result || !result.data) {
      throw new Error('Failed to fetch photos data');
    }

    // Group photos by album_id
    const albumsMap = new Map();

    // Add "Unfiled" album
    albumsMap.set(null, {
      id: null,
      name: '__Unfiled__',
      photos: [],
      coverImage: null
    });

    // Process photos and group by album_id
    result.data.forEach(photo => {
      const albumId = photo.album_id || null;

      if (!albumsMap.has(albumId)) {
        albumsMap.set(albumId, {
          id: albumId,
          name: `Album ${albumId}`,
          photos: [],
          coverImage: null
        });
      }

      const album = albumsMap.get(albumId);
      const photoObj = {
        id: photo.imgId,
        name: photo.imgName || `Image ${photo.imgId}`,
        src: `http://10.16.60.67:9000/softwareeng/upload-img/${photo.imgId}.jpeg`,
        album_id: albumId,
        date: photo.imgDate,
        tags: photo.tags || []
      };

      album.photos.push(photoObj);

      // Set first photo as cover if none exists
      if (!album.coverImage) {
        album.coverImage = photoObj.src;
      }
    });

    // Convert map to array and sort
    albums.value = Array.from(albumsMap.values()).sort((a, b) => {
      // Keep Unfiled at the end
      if (a.id === null) return 1;
      if (b.id === null) return -1;
      return a.id - b.id;
    });

  } catch (err) {
    error.value = `Failed to load albums: ${err.message}`;
    console.error(error.value);
    toast.error(error.value);
  } finally {
    isLoading.value = false;
  }
};

// Open an album to view its photos
const openAlbum = (album) => {
  currentAlbum.value = album;
  selectedPhotos.value = [];
};

// Go back to albums view
const backToAlbums = () => {
  currentAlbum.value = null;
  selectedPhotos.value = [];
};

// Create a new album
const createAlbum = async () => {
  if (!newAlbumName.value.trim()) {
    toast.error("Please enter a valid album name");
    return;
  }

  try {
    const params = new URLSearchParams({
      userId: mainStore.userId.toString(),
      albumName: newAlbumName.value.trim()
    });

    const response = await fetch(`http://10.16.60.67:9090/album/add?${params}`, {
      method: 'POST'
    });

    const result = await response.json();

    if (!result || result.msg !== 'ok') {
      throw new Error(result?.msg || 'Failed to create album');
    }

    toast.success(`Album "${newAlbumName.value}" created successfully`);
    showNewAlbumDialog.value = false;
    newAlbumName.value = "";

    // Refresh albums
    fetchAlbums();

  } catch (err) {
    toast.error(`Failed to create album: ${err.message}`);
  }
};

// Delete an album
const deleteAlbum = async (albumId) => {
  if (!confirm(`Are you sure you want to delete this album? Photos will be moved to Unfiled.`)) {
    return;
  }

  try {
    const params = new URLSearchParams({
      userId: mainStore.userId.toString(),
      albumId: albumId.toString()
    });

    const response = await fetch(`http://10.16.60.67:9090/album/delete?${params}`, {
      method: 'GET'
    });

    const result = await response.json();

    if (!result || result.msg !== 'ok') {
      throw new Error(result?.msg || 'Failed to delete album');
    }

    toast.success("Album deleted successfully");

    // Refresh albums
    fetchAlbums();

  } catch (err) {
    toast.error(`Failed to delete album: ${err.message}`);
  }
};

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
    toast.error("Please select photos and a target album");
    return;
  }

  try {
    const movePromises = selectedPhotos.value.map(photoId => {
      const params = new URLSearchParams({
        userId: mainStore.userId.toString(),
        imgId: photoId.toString(),
        albumId: targetAlbumId.value === "null" ? "" : targetAlbumId.value
      });

      return fetch(`http://10.16.60.67:9090/img/move?${params}`, {
        method: 'GET'
      })
        .then(response => response.json())
        .then(result => {
          if (!result || result.msg !== 'ok') {
            throw new Error(result?.msg || 'Failed to move photo');
          }
          return result;
        });
    });

    await Promise.all(movePromises);

    toast.success(`${selectedPhotos.value.length} photo(s) moved successfully`);
    showMoveDialog.value = false;
    targetAlbumId.value = null;
    selectedPhotos.value = [];

    // Refresh albums
    fetchAlbums();

  } catch (err) {
    toast.error(`Failed to move photos: ${err.message}`);
  }
};

// Open move dialog
const openMoveDialog = () => {
  if (selectedPhotos.value.length === 0) {
    toast.error("Please select photos to move");
    return;
  }

  showMoveDialog.value = true;
};

// Initialize data on component mount
onMounted(() => {
  fetchAlbums();
});
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <!-- Title section with navigation when in album view -->
      <SectionTitleLineWithButton :icon="currentAlbum ? mdiImageMultiple : mdiFolderMultiple"
        :title="currentAlbum ? currentAlbum.name : 'Albums'" main>
        <div class="flex">
          <BaseButton v-if="currentAlbum" :icon="mdiArrowLeft" label="Back to Albums" color="contrast" small
            @click="backToAlbums" />
          <template v-else>
            <BaseButton :icon="mdiRefresh" label="Refresh" color="whiteDark" small class="mr-2" @click="fetchAlbums" />
            <BaseButton :icon="mdiFolderPlus" label="New Album" color="info" small @click="showNewAlbumDialog = true" />
          </template>
        </div>
      </SectionTitleLineWithButton>

      <!-- Loading state -->
      <div v-if="isLoading" class="flex justify-center items-center py-12">
        <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-500"></div>
      </div>

      <!-- Error state -->
      <div v-else-if="error" class="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-4">
        <p class="font-bold">Error</p>
        <p>{{ error }}</p>
        <BaseButton label="Retry" color="danger" small class="mt-2" @click="fetchAlbums" />
      </div>

      <!-- Album View -->
      <div v-else-if="!currentAlbum" class="space-y-6">
        <!-- Albums Grid -->
        <CardBox class="mb-6">
          <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
            <div v-for="album in albums" :key="album.id || 'unfiled'"
              class="cursor-pointer hover:scale-105 transition-transform duration-200" @click="openAlbum(album)">
              <div class="bg-white rounded-lg shadow-md overflow-hidden border border-gray-200">
                <!-- Album Cover -->
                <div class="h-40 bg-gray-200 relative">
                  <img v-if="album.coverImage" :src="album.coverImage" class="w-full h-full object-cover"
                    alt="Album cover" />
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
                    <button v-if="album.id !== null" @click.stop="deleteAlbum(album.id)"
                      class="text-gray-500 hover:text-red-500" title="Delete album">
                      <svg class="w-5 h-5" viewBox="0 0 24 24">
                        <path fill="currentColor" :d="mdiFolderRemove" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </CardBox>
      </div>

      <!-- Album Contents View (when album is selected) -->
      <div v-else>
        <!-- Action buttons for photo management within album -->
        <div class="mb-4 flex justify-between">
          <div class="flex gap-2">
            <BaseButton :icon="isSelectMode ? mdiCursorDefault : mdiCheckboxMultipleMarkedOutline"
              :label="isSelectMode ? 'View Mode' : 'Select Mode'" :color="isSelectMode ? 'info' : 'contrast'" small
              @click="toggleSelectMode" />

            <!-- Show move button in select mode when photos are selected -->
            <BaseButton v-if="isSelectMode && selectedPhotos.length > 0" :icon="mdiImageMove" label="Move to Album"
              color="success" small @click="openMoveDialog" />
          </div>

          <div v-if="isSelectMode && selectedPhotos.length > 0" class="flex items-center">
            <span class="mr-2 text-sm text-gray-700">{{ selectedPhotos.length }} selected</span>
            <BaseButton label="Clear selection" color="whiteDark" small @click="clearSelections" />
          </div>
        </div>

        <!-- Show photos in current album -->
        <CardBox class="mb-6">
          <PhotoGallery ref="photoGallery" :photos="currentAlbum.photos" :initial-view-mode="viewMode"
            :available-view-modes="['details', 'grid', 'large', 'small']" :is-select-mode="isSelectMode"
            :selected-photo-ids="selectedPhotos" show-actions="true" @select-photo="togglePhotoSelection"
            @update:viewMode="mode => viewMode = mode" />
        </CardBox>
      </div>

      <!-- New Album Dialog -->
      <div v-if="showNewAlbumDialog" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg p-6 max-w-md w-full" @click.stop>
          <h3 class="text-xl font-medium mb-4">Create New Album</h3>

          <div class="mb-4">
            <label class="block text-gray-700 text-sm font-medium mb-2">Album Name</label>
            <input v-model="newAlbumName" type="text"
              class="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-300"
              @keyup.enter="createAlbum" />
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
                {{ album.name }} ({{ album.photos.length }} photos)
              </option>
            </select>
          </div>

          <div class="flex justify-end gap-2">
            <BaseButton label="Cancel" color="whiteDark" @click="showMoveDialog = false" />
            <BaseButton :icon="mdiImageMove" label="Move" color="success" @click="movePhotosToAlbum" />
          </div>
        </div>
      </div>
    </SectionMain>
  </LayoutAuthenticated>
</template>

<style scoped>
/* Add any component-specific styles here */
</style>
