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
import PhotoUploader from '@/components/PhotoUploader.vue'
import AlbumsGallery from '@/components/AlbumsGallery.vue'
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
const newAlbumDescription = ref("")

// Move photos dialog
const showMoveDialog = ref(false)
const targetAlbumId = ref(null)

// Editor and uploader states
const showEditor = ref(false)
const showUploader = ref(false)
const editingPhoto = ref(null)

// Initialize albums from API data
// Initialize albums from API data
const fetchAlbums = async () => {
  isLoading.value = true;
  error.value = null;

  try {
    // Step 1: Fetch album metadata first
    const albumParams = new URLSearchParams({
      userId: mainStore.userId.toString()
    });

    const albumResponse = await fetch(`http://10.16.60.67:9090/album/list?${albumParams}`, {
      method: 'POST'
    });
    const albumResult = await albumResponse.json();

    if (!albumResult || albumResult.msg !== 'ok') {
      throw new Error('Failed to fetch albums');
    }

    // Create initial album map with metadata
    const albumsMap = new Map();

    // Add "Unfiled" album
    albumsMap.set(null, {
      id: null,
      name: '__Unfiled__',
      description: 'Photos not in any album',
      photos: [],
      coverImage: null
    });

    // Add all albums from the API response
    if (albumResult.data && albumResult.data.length > 0) {
      albumResult.data.forEach(album => {
        albumsMap.set(album.albumId, {
          id: album.albumId,
          name: album.albumName,
          description: album.albumDescribtion || '',
          photos: [],
          coverImage: null
        });
      });
    }

    // Step 2: Fetch all photos
    const photoParams = new URLSearchParams({
      userId: mainStore.userId
    });

    const photoResponse = await fetch(`http://10.16.60.67:9090/img/all?${photoParams}`);
    const photoResult = await photoResponse.json();

    if (!photoResult || !photoResult.data) {
      throw new Error('Failed to fetch photos data');
    }

    // Step 3: Group photos by album_id
    photoResult.data.forEach(photo => {
      const albumId = photo.albumId || null;

      // Ensure the album exists in our map (just in case)
      if (!albumsMap.has(albumId) && albumId !== null) {
        // Try to find this album in the albumResult data first
        const albumDetails = albumResult.data?.find(album => album.albumId.toString() === albumId.toString());

        albumsMap.set(albumId, {
          id: albumId,
          name: albumDetails ? albumDetails.albumName : `Album ${albumId}`,
          description: albumDetails ? albumDetails.albumDescribtion || '' : '',
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
        displayDate: formatDate(photo.imgDate),
        tags: photo.tags || [],
        size: 'Unknown',
        type: photo.imgType || 'JPEG',
        peoples: photo.peoples,
        location: photo.imgPos,
        desc: photo.imgDescribtion || "",
        userId: photo.userId
      };

      album.photos.push(photoObj);

      // Set first photo as cover if none exists
      if (!album.coverImage) {
        album.coverImage = photoObj.src;
      }
    });

    // Step 4: Convert map to array and sort
    albums.value = Array.from(albumsMap.values())
      // .filter(album => album.id === null || album.photos.length > 0) // Only show albums with photos or the Unfiled album
      .sort((a, b) => {
        // Keep Unfiled at the end
        if (a.id === null) return 1;
        if (b.id === null) return -1;
        // Sort alphabetically by name
        return a.name.localeCompare(b.name);
      });

  } catch (err) {
    error.value = `Failed to load albums: ${err.message}`;
    console.error(error.value);
    toast.error(error.value);
  } finally {
    isLoading.value = false;
  }
};

// Format date function (copied from PhotoGallery)
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const options = {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  }
  return date.toLocaleDateString('en-US', options)
}

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
      names: newAlbumName.value.trim(),
      albumDescription: newAlbumDescription.value.trim() // Default description
    });

    const response = await fetch(`http://10.16.60.67:9090/album/new?${params}`, {
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
      // Find the photo object to get additional properties
      const photo = currentAlbum.value.photos.find(p => p.id === photoId);
      if (!photo) return Promise.resolve(); // Skip if photo not found

      const params = new URLSearchParams({
        userId: mainStore.userId.toString(),
        imgId: photoId.toString(),
        albumId: targetAlbumId.value === "null" ? "" : targetAlbumId.value,
        name: photo.name || `Image ${photoId}`, // Use existing name or generate one
        pub: true // Default to public
      });

      return fetch(`http://10.16.60.67:9090/img/cname?${params}`, {
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

// Upload photos handler
const handleUpload = (file) => {
  if (!file) return;

  const formData = new FormData();
  formData.append('files', file);

  // Set album ID for the upload if we're in an album
  const albumId = currentAlbum.value?.id === null ? "" : currentAlbum.value?.id;
  const currentDate = new Date().toISOString().slice(0, 19).replace('T', ' ');

  const params = new URLSearchParams({
    imgDate: currentDate,
    imgName: file.name,
    userId: mainStore.userId.toString(),
    pub: true,
    albumId: albumId
  });

  fetch(`http://10.16.60.67:9090/img/add?${params}`, {
    method: 'POST',
    body: formData
  })
    .then(response => response.json())
    .then(result => {
      if (!result.msg || result.msg !== 'ok') {
        throw new Error(result.msg || 'Upload failed');
      }
      toast.success('Image uploaded successfully');
      // Refresh the current view
      fetchAlbums().then(() => {
        if (currentAlbum.value) {
          // If we're in an album view, update to show the refreshed album
          const updatedAlbum = albums.value.find(a =>
            (a.id === null && currentAlbum.value.id === null) ||
            a.id === currentAlbum.value.id
          );
          if (updatedAlbum) {
            currentAlbum.value = updatedAlbum;
          }
        }
      });
    })
    .catch(error => {
      console.error('Upload error:', error);
      toast.error(`Failed to upload image: ${error.message}`);
    });

  showUploader.value = false;
};

// Delete photos
const handleDelete = () => {
  if (selectedPhotos.value.length === 0) {
    toast.error("Please select photos to delete");
    return;
  }

  if (!confirm(`Are you sure you want to delete ${selectedPhotos.value.length} selected photo(s)?`)) {
    return;
  }

  const deletePromises = selectedPhotos.value.map(photoId => {
    const photo = currentAlbum.value.photos.find(p => p.id === photoId);
    if (!photo) return Promise.resolve();

    const params = new URLSearchParams({
      userId: mainStore.userId.toString(),
      imgId: photoId.toString()
    });

    return fetch(`http://10.16.60.67:9090/img/delete?${params}`, {
      method: 'GET'
    })
      .then(response => response.json())
      .then(result => {
        if (!result || result.msg !== 'ok') {
          throw new Error(result?.msg || 'Failed to delete photo');
        }
        return result;
      });
  });

  Promise.all(deletePromises)
    .then(() => {
      toast.success(`${selectedPhotos.value.length} photo(s) deleted`);
      selectedPhotos.value = [];
      fetchAlbums().then(() => {
        if (currentAlbum.value) {
          const updatedAlbum = albums.value.find(a =>
            (a.id === null && currentAlbum.value.id === null) ||
            a.id === currentAlbum.value.id
          );
          if (updatedAlbum) {
            currentAlbum.value = updatedAlbum;
          }
        }
      });
    })
    .catch(error => {
      console.error('Delete error:', error);
      toast.error(`Failed to delete photos: ${error.message}`);
    });
};

// Download photos
const handleDownload = () => {
  if (selectedPhotos.value.length === 0) return;

  const photosToDownload = selectedPhotos.value.map(id =>
    currentAlbum.value.photos.find(p => p.id === id)
  ).filter(Boolean);

  photosToDownload.forEach(async (photo) => {
    try {
      const res = await fetch(photo.src, { mode: 'cors' });
      const blob = await res.blob();
      const objectUrl = URL.createObjectURL(blob);

      const link = document.createElement('a');
      link.href = objectUrl;
      link.download = photo.name || 'download';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      URL.revokeObjectURL(objectUrl);
    } catch (err) {
      console.error(`Download failed: ${photo.name}`, err);
      toast.error(`Failed to download ${photo.name}`);
    }
  });

  toast.success(`Successfully downloaded ${photosToDownload.length} photo(s)`);
};

// Open editor
const openEditor = () => {
  if (selectedPhotos.value.length === 1) {
    openEditorWithPhoto(selectedPhotos.value[0]);
  }
};

// Open editor with photo ID
const openEditorWithPhoto = (photoId) => {
  const photo = currentAlbum.value.photos.find(p => p.id === photoId);
  if (!photo) {
    toast.error("Photo not found");
    return;
  }

  editingPhoto.value = photo;
  showEditor.value = true;
  isSelectMode.value = false;
  selectedPhotos.value = [];
};

// Close editor
const closeEditor = () => {
  showEditor.value = false;
  editingPhoto.value = null;
};

// Save edited photo
const saveEditedPhoto = (updatedPhoto) => {
  // API call to save the photo would go here
  toast.success("Photo updated successfully");
  closeEditor();
  fetchAlbums().then(() => {
    if (currentAlbum.value) {
      const updatedAlbum = albums.value.find(a =>
        (a.id === null && currentAlbum.value.id === null) ||
        a.id === currentAlbum.value.id
      );
      if (updatedAlbum) {
        currentAlbum.value = updatedAlbum;
      }
    }
  });
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
          <BaseButton v-if="currentAlbum" :icon="isSelectMode ? mdiCursorDefault : mdiCheckboxMultipleMarkedOutline"
            :label="isSelectMode ? 'View Mode' : 'Select Mode'" :color="isSelectMode ? 'info' : 'contrast'" small
            class="mr-2" @click="toggleSelectMode" />
          <BaseButton v-if="currentAlbum" :icon="mdiArrowLeft" label="Back to Albums" color="contrast" small
            @click="backToAlbums" />
          <template v-else>
            <BaseButton :icon="mdiRefresh" label="Refresh" color="info" small class="mr-2" @click="fetchAlbums" />
            <BaseButton :icon="mdiFolderPlus" label="New Album" color="info" small @click="showNewAlbumDialog = true" />
          </template>
        </div>
      </SectionTitleLineWithButton>

      <!-- Album View (using the new AlbumsGallery component) -->
      <AlbumsGallery v-if="!currentAlbum" :albums="albums" :isLoading="isLoading" :error="error" @open-album="openAlbum"
        @delete-album="deleteAlbum" @refresh-albums="fetchAlbums" />

      <!-- Album Contents View (when album is selected) -->
      <div v-else>
        <!-- Show photos in current album -->
        <CardBox class="mb-6">
          <PhotoGallery ref="photoGallery" :photos="currentAlbum.photos" :initial-view-mode="viewMode"
            :available-view-modes="['details', 'grid', 'large', 'small']" :is-select-mode="isSelectMode"
            :selected-photo-ids="selectedPhotos" :show-actions="true" @select-photo="togglePhotoSelection"
            @update:viewMode="mode => viewMode = mode" @photo-edit="openEditorWithPhoto" />
        </CardBox>

        <!-- Action buttons for photo management within album -->
        <div class="mb-4 flex justify-between">
          <div class="flex gap-2">
            <!-- Upload button -->
            <BaseButton :icon="mdiImagePlus" label="Upload" color="info" small @click="showUploader = true" />

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
      <div v-if="showNewAlbumDialog" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
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

      <!-- Photo Uploader Modal -->
      <PhotoUploader :show="showUploader" @close="showUploader = false" @upload="handleUpload" />

      <!-- Photo Editor Modal -->
      <PhotoEditor v-if="showEditor" :photo="editingPhoto" @save="saveEditedPhoto" @close="closeEditor" />
    </SectionMain>
  </LayoutAuthenticated>
</template>

<style scoped>
/* Add any component-specific styles here */
</style>
