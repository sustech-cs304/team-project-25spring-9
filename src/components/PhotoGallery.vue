<script setup>
import {
  mdiCheckboxMarked,
  mdiCheckboxBlankOutline,
  mdiDotsVertical,
  mdiClose,
  mdiArrowLeft,
  mdiArrowRight,
  mdiInformation,
  mdiImageSearch,
  mdiViewList,
  mdiViewGrid,
  mdiViewGridOutline,
  mdiViewCompactOutline
} from '@mdi/js'
import CardBoxComponentEmpty from '@/components/CardBoxComponentEmpty.vue'
import BaseButton from '@/components/BaseButton.vue'
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'

// Define props for the component
const props = defineProps({
  photos: {
    type: Array,
    required: true
  },
  initialViewMode: {
    type: String,
    default: 'grid',
    validator: (value) => ['details', 'grid', 'large', 'small'].includes(value)
  },
  availableViewModes: {
    type: Array,
    default: () => ['details', 'grid', 'large', 'small']
  },
  isSelectMode: {
    type: Boolean,
    default: false
  },
  selectedPhotoIds: {
    type: Array,
    default: () => []
  },
  showActions: {
    type: Boolean,
    default: true
  },
  useApiData: {
    type: Boolean,
    default: false
  },
  userId: {
    type: Number,
    default: null
  }
})

// Define emits
const emit = defineEmits([
  'select-photo',
  'view-photo',
  'action-click',
  'update:viewMode',
  'filter',
  'photos-loaded'
])

// Internal state for the component
const viewMode = ref(props.initialViewMode)
const searchQuery = ref('')

// Track modal state and current photo
const isModalOpen = ref(false)
const currentPhoto = ref(null)

// API data states
const apiPhotos = ref([])
const loading = ref(false)
const error = ref(null)

// Advanced search state
const showAdvancedSearch = ref(false)
const advancedFilters = ref({
  dateRange: { start: '', end: '' },
  location: '',
  tags: [],
  author: ''
})

// Add applied filter state
const appliedFilters = ref({
  query: '',
  dateRange: { start: '', end: '' },
  location: '',
  tags: [],
  author: ''
})

// Add temporary filter state to store unapplied changes
const tempFilters = ref({
  dateRange: { start: '', end: '' },
  location: '',
  tags: [],
  author: ''
})

// Change placeholder text when advanced search is active
const searchPlaceholder = computed(() => {
  return showAdvancedSearch.value 
    ? 'Search by name, type, or any field...' 
    : 'Quick search photos...'
})

function formatFileSize(bytes) {
  if (!bytes) return 'Unknown';
  if (bytes < 1024) return `${bytes} B`;
  
  const units = ['KB', 'MB', 'GB', 'TB'];
  let size = bytes;
  let unitIndex = -1;
  
  do {
    size /= 1024;
    unitIndex++;
  } while (size >= 1024 && unitIndex < units.length - 1);

  return `${size.toFixed(1)} ${units[unitIndex]}`;
}

async function getImageFileSizeFromUrl(url) {
  try {
    const response = await fetch(url, { method: 'HEAD' });
    if (!response.ok) throw new Error('Failed to fetch image size');
    const size = response.headers.get('content-length');
    return size ? formatFileSize(parseInt(size)) : 'Unknown';
  } catch (error) {
    console.error('Error getting image size:', error);
    return 'Unknown';
  }
}

// Fetch images from API
const fetchPhotos = async () => {
  if (!props.useApiData) return

  loading.value = true
  error.value = null

  try {
    const response = await fetch('http://10.16.60.67:9090/img/all?userId=' + props.userId)
    const result = await response.json()

    if (result && result.data) {
      let imageData = result.data
      if (props.userId) {
        imageData = imageData.filter(img => img.userId === props.userId)
      }

      // Transform data and fetch sizes
      const transformedData = await Promise.all(imageData.map(async img => {
        const imgUrl = `http://10.16.60.67:9000/softwareeng/upload-img/${img.imgId}.jpeg`;
        const size = await getImageFileSizeFromUrl(imgUrl);
        
        return {
          id: img.imgId,
          name: img.imgName || `Image ${img.imgId}`,
          pub: img.pub,
          src: imgUrl,
          type: img.imgType || 'JPEG',
          size: size,
          date: img.imgDate || new Date().toISOString().split('T')[0],
          userId: img.userId,
          tags: img.tags,
          peoples: img.peoples,
          desc: img.imgDescribtion || ""
        };
      }));

      apiPhotos.value = transformedData;
      emit('photos-loaded', apiPhotos.value)
    }
  } catch (err) {
    error.value = 'Failed to fetch photos: ' + err.message
    console.error(error.value)
  } finally {
    loading.value = false
  }
}

const generateNewId = (() => {
  // Generate new id from API
  let idCounter = 6
  return () => props.useApiData ? undefined : ++idCounter
})()

const uploadPhotos = (file) => {
  if (file) {
    if (!file.type.startsWith('image/')) {
      toast.error('Please upload an image file')
      return
    }

    if (!props.useApiData) {
      // Local upload logic
      const reader = new FileReader()
      reader.onload = (e) => {
        const newPhoto = {
          id: generateNewId(),
          name: file.name,
          src: e.target.result,
          size: formatFileSize(file.size),
          date: new Date().toISOString().split('T')[0],
          type: file.type.split('/')[1].toUpperCase()
        }
        propPhotos.value.push(newPhoto)
        toast.success('Image uploaded successfully')
      }
      reader.onerror = () => {
        toast.error('Error reading file')
      }
      reader.readAsDataURL(file)
    } else {
      // API upload logic
      const formData = new FormData()
      formData.append('files', file)

      const currentDate = new Date().toISOString().slice(0, 19).replace('T', ' ')
      const params = new URLSearchParams({
        imgDate: currentDate,
        imgName: file.name,
        userId: props.userId.toString(),
        pub: true
      })

      fetch(`http://10.16.60.67:9090/img/add?${params}`, {
        method: 'POST',
        body: formData
      })
        .then(response => response.json())
        .then(result => {
          if (!result.msg || result.msg !== 'ok') {
            throw new Error(result.msg || 'Upload failed')
          }
          toast.success('Image uploaded successfully')
          fetchPhotos() // Refresh the photo list
        })
        .catch(error => {
          console.error('Upload error:', error)
          toast.error(`Failed to upload image: ${error.message}`)
        })
    }
  }
}

const deletePhotos = (selectedIds) => {
  const photosToDelete = selectedIds.value.map(id => 
    displayPhotos.value.find(p => p.id === id)
  ).filter(Boolean);

  if (!props.useApiData) {
    propPhotos.value = propPhotos.value.filter(photo => !selectedIds.value.includes(photo.id))
  } else {
    // Delete from API
    const deletePromises = photosToDelete.map(photo => {
      const params = new URLSearchParams({
        userId: photo.userId.toString(),
        imgId: photo.id.toString()
      })
      
      return fetch(`http://10.16.60.67:9090/img/delete?${params}`, {
        method: 'GET'
      })
      .then(response => response.json())
      .then(result => {
        if (!result.msg || result.msg !== 'ok') {
          throw new Error(result.msg || 'Delete failed')
        }
        return result
      })
    })

    Promise.all(deletePromises)
      .then(() => {
        toast.success(`${photosToDelete.length} photo(s) deleted`)
        fetchPhotos() // Refresh the photo list
      })
      .catch(error => {
        console.error('Delete error:', error)
        toast.error(`Failed to delete photos: ${error.message}`)
      })
    return
  }
  toast.success(`${photosToDelete.length} photo(s) deleted`)
}

const downloadPhotos = async (selectedIds) => {
  if (selectedIds.value.length === 0) return;

  const photosToDownload = selectedIds.value.map(id =>
    displayPhotos.value.find(p => p.id === id)
  ).filter(Boolean); 

  for (const photo of photosToDownload) {
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

      URL.revokeObjectURL(objectUrl); // Free memory
    } catch (err) {
      console.error(`Download failed: ${photo.name}`, err);
      toast.error(`Failed to download ${photo.name}`);
    }
  }

  toast.success(`Successfully downloaded ${photosToDownload.length} photo(s)`);
}

// Determine which photos to use - API or props
const displayPhotos = computed(() => {
  return props.useApiData ? apiPhotos.value : props.photos
})

// Filtered photos based on search query
const filteredPhotos = computed(() => {
  if (!searchQuery.value) return displayPhotos.value

  const query = searchQuery.value.toLowerCase()
  return displayPhotos.value.filter(photo => {
    return photo.name.toLowerCase().includes(query) ||
      (photo.date && photo.date.includes(query)) ||
      (photo.type && photo.type.toLowerCase().includes(query))
  })
})

// Emit filtered photos whenever they change
watch(filteredPhotos, (newFilteredPhotos) => {
  emit('filter', newFilteredPhotos)
})

// Method to check if a photo is selected
const isPhotoSelected = (photoId) => {
  return props.selectedPhotoIds.includes(photoId)
}

// Method to toggle photo selection
const togglePhotoSelection = (photoId) => {
  emit('select-photo', photoId)
}

// Method to change view mode
const setViewMode = (mode) => {
  viewMode.value = mode
  emit('update:viewMode', mode)
}

// Method to open the full image modal
const openPhotoModal = (photo) => {
  currentPhoto.value = photo
  isModalOpen.value = true
  emit('view-photo', photo)
}

// Method to close the modal
const closePhotoModal = () => {
  isModalOpen.value = false
  currentPhoto.value = null
}

// Methods to navigate between photos in the modal
const viewNextPhoto = () => {
  if (!currentPhoto.value) return

  const currentIndex = filteredPhotos.value.findIndex(p => p.id === currentPhoto.value.id)
  if (currentIndex < filteredPhotos.value.length - 1) {
    currentPhoto.value = filteredPhotos.value[currentIndex + 1]
  } else {
    // Wrap around to the first photo
    currentPhoto.value = filteredPhotos.value[0]
  }
}

const viewPreviousPhoto = () => {
  if (!currentPhoto.value) return

  const currentIndex = filteredPhotos.value.findIndex(p => p.id === currentPhoto.value.id)
  if (currentIndex > 0) {
    currentPhoto.value = filteredPhotos.value[currentIndex - 1]
  } else {
    // Wrap around to the last photo
    currentPhoto.value = filteredPhotos.value[filteredPhotos.value.length - 1]
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
  if (props.useApiData) {
    fetchPhotos()
  }
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})

// Method to handle action button click
const handleActionClick = (photo) => {
  emit('action-click', photo)
}

// Method to refresh API photos
const refreshPhotos = () => {
  fetchPhotos()
}

const getPhotoById = (id) => {
  return displayPhotos.value.find(photo => photo.id === id)
}

defineExpose({
  refreshPhotos,
  uploadPhotos,
  deletePhotos,
  downloadPhotos,
  getPhotoById
})
</script>

<template>
  <div>
    <!-- Search Bar and View Mode Switcher -->
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
        <BaseButton v-if="availableViewModes.includes('details')" :icon="mdiViewList"
          :color="viewMode === 'details' ? 'info' : 'whiteDark'" small @click="setViewMode('details')" class="mr-1"
          title="Details view" />

        <BaseButton v-if="availableViewModes.includes('large')" :icon="mdiViewGrid"
          :color="viewMode === 'large' ? 'info' : 'whiteDark'" small @click="setViewMode('large')" class="mr-1"
          title="Large icons" />

        <BaseButton v-if="availableViewModes.includes('grid')" :icon="mdiViewGridOutline"
          :color="viewMode === 'grid' ? 'info' : 'whiteDark'" small @click="setViewMode('grid')" class="mr-1"
          title="Medium icons" />

        <BaseButton v-if="availableViewModes.includes('small')" :icon="mdiViewCompactOutline"
          :color="viewMode === 'small' ? 'info' : 'whiteDark'" small @click="setViewMode('small')"
          title="Small icons" />
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-12">
      <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-500"></div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-4">
      <p class="font-bold">Error</p>
      <p>{{ error }}</p>
      <BaseButton label="Retry" color="danger" small class="mt-2" @click="refreshPhotos" />
    </div>

    <!-- Rest of the template remains the same -->
    <!-- Details View (Table-like) -->
    <div v-if="viewMode === 'details'" class="overflow-x-auto">
      <!-- Table structure... -->
      <table class="w-full">
        <thead>
          <tr class="border-b">
            <th v-if="isSelectMode" class="px-3 py-2 text-left">Select</th>
            <th class="px-3 py-2 text-left">Preview</th>
            <th class="px-3 py-2 text-left">Name</th>
            <th class="px-3 py-2 text-left">Type</th>
            <th class="px-3 py-2 text-left">Size</th>
            <th class="px-3 py-2 text-left">Modified Date</th>
            <th v-if="showActions" class="px-3 py-2 text-left">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="photo in filteredPhotos" :key="photo.id" class="border-b hover:bg-gray-100 dark:hover:bg-gray-700"
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
            <td class="px-3 py-2">{{ photo.type }}</td>
            <td class="px-3 py-2">{{ photo.size }}</td>
            <td class="px-3 py-2">{{ photo.date }}</td>
            <td v-if="showActions" class="px-3 py-2">
              <BaseButton :icon="mdiDotsVertical" small color="lightDark" @click="handleActionClick(photo)" />
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Large Grid View -->
    <!-- Same as before -->
    <div v-else-if="viewMode === 'large'" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
      <div v-for="photo in filteredPhotos" :key="photo.id"
        class="flex flex-col items-center relative hover:bg-gray-100 dark:hover:bg-gray-700 p-2 rounded"
        :class="{ 'ring-2 ring-blue-500': isSelectMode && isPhotoSelected(photo.id) }">
        <div v-if="isSelectMode" class="absolute top-4 left-4 z-10">
          <button @click.stop="togglePhotoSelection(photo.id)"
            class="bg-white bg-opacity-70 rounded-md p-1 text-gray-700 hover:text-blue-500">
            <svg class="w-5 h-5" viewBox="0 0 24 24">
              <path fill="currentColor" :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
            </svg>
          </button>
        </div>
        <img :src="photo.src" class="w-full h-40 object-cover rounded mb-2 cursor-pointer"
          @click="openPhotoModal(photo)" />
        <span class="text-center">{{ photo.name }}</span>
      </div>
    </div>

    <!-- Medium Grid View (Default) -->
    <!-- Same as before -->
    <div v-else-if="viewMode === 'grid'" class="grid grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-3">
      <div v-for="photo in filteredPhotos" :key="photo.id"
        class="flex flex-col items-center relative hover:bg-gray-100 dark:hover:bg-gray-700 p-2 rounded"
        :class="{ 'ring-2 ring-blue-500': isSelectMode && isPhotoSelected(photo.id) }">
        <div v-if="isSelectMode" class="absolute top-3 left-3 z-10">
          <button @click.stop="togglePhotoSelection(photo.id)"
            class="bg-white bg-opacity-70 rounded-md p-0.5 text-gray-700 hover:text-blue-500">
            <svg class="w-4 h-4" viewBox="0 0 24 24">
              <path fill="currentColor" :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
            </svg>
          </button>
        </div>
        <img :src="photo.src" class="w-full h-24 object-cover rounded mb-1 cursor-pointer"
          @click="openPhotoModal(photo)" />
        <span class="text-center text-sm">{{ photo.name }}</span>
      </div>
    </div>

    <!-- Small Grid View -->
    <!-- Same as before -->
    <div v-else class="grid grid-cols-4 md:grid-cols-6 lg:grid-cols-10 gap-2">
      <div v-for="photo in filteredPhotos" :key="photo.id"
        class="flex flex-col items-center relative hover:bg-gray-100 dark:hover:bg-gray-700 p-1 rounded"
        :class="{ 'ring-1 ring-blue-500': isSelectMode && isPhotoSelected(photo.id) }">
        <div v-if="isSelectMode" class="absolute top-2 left-2 z-10">
          <button @click.stop="togglePhotoSelection(photo.id)"
            class="bg-white bg-opacity-70 rounded-md p-0.5 text-gray-700 hover:text-blue-500">
            <svg class="w-3 h-3" viewBox="0 0 24 24">
              <path fill="currentColor" :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
            </svg>
          </button>
        </div>
        <img :src="photo.src" class="w-full h-16 object-cover rounded mb-1 cursor-pointer"
          @click="openPhotoModal(photo)" />
        <span class="text-center text-xs truncate w-full">{{ photo.name }}</span>
      </div>
    </div>

    <!-- Empty State -->
    <CardBoxComponentEmpty v-if="filteredPhotos.length === 0" />

    <!-- Photo Modal -->
    <!-- Same as before -->
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
  </div>
</template>
