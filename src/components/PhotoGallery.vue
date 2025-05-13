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
  mdiViewCompactOutline,
  mdiImageEdit,
  mdiDelete,
  mdiDownload,
  mdiChevronDown,
  mdiFilterVariant,
  mdiFilterVariantRemove,
  mdiCalendarMonth,
  mdiTag,
  mdiMapMarker,
  mdiAccount,
  mdiMagnify,
} from '@mdi/js'
import CardBoxComponentEmpty from '@/components/CardBoxComponentEmpty.vue'
import BaseButton from '@/components/BaseButton.vue'
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useToast } from 'vue-toastification'

const toast = useToast()

// Define props for the component
const props = defineProps({
  photos: {
    type: Array,
    default: () => [
      { id: 1, name: 'Mountain View', src: 'https://picsum.photos/id/10/300/200', size: '2.4 MB', date: '2023-09-15', type: 'JPG' },
      { id: 2, name: 'Beach Sunset', src: 'https://picsum.photos/id/11/300/200', size: '3.1 MB', date: '2023-10-02', type: 'PNG' },
      { id: 3, name: 'City Skyline', src: 'https://picsum.photos/id/12/300/200', size: '1.8 MB', date: '2023-11-20', type: 'JPG' },
      { id: 4, name: 'Forest Path', src: 'https://picsum.photos/id/13/300/200', size: '2.9 MB', date: '2024-01-05', type: 'JPG' },
      { id: 5, name: 'Desert Landscape', src: 'https://picsum.photos/id/14/300/200', size: '2.2 MB', date: '2024-02-18', type: 'PNG' },
      { id: 6, name: 'Ocean Waves', src: 'https://picsum.photos/id/15/300/200', size: '4.0 MB', date: '2024-03-10', type: 'TIFF' }
    ]
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
  'photos-loaded',
  'photo-edit'
])

// Internal state for the component
const viewMode = ref(props.initialViewMode)
const searchQuery = ref('')

// Track modal state and current photo
const isModalOpen = ref(false)
const currentPhoto = ref(null)

// Method to close the modal
const closePhotoModal = () => {
  isModalOpen.value = false
  currentPhoto.value = null
}

// Test Photos
const propPhotos = ref(props.photos)

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
  peoples: ''
})

// Add applied filter state
const appliedFilters = ref({
  query: '',
  dateRange: { start: '', end: '' },
  location: '',
  tags: [],
  peoples: ''
})

// Add temporary filter state to store unapplied changes
const tempFilters = ref({
  dateRange: { start: '', end: '' },
  location: '',
  tags: [],
  peoples: ''
})

// Add tag input state management
const newTagInput = ref('')

// Handle tag input
const handleTagInput = (event) => {
  if (event.key === 'Enter' || event.key === ',') {
    event.preventDefault()
    const tag = newTagInput.value.trim().toLowerCase()
    if (tag && !tempFilters.value.tags.includes(tag)) {
      tempFilters.value.tags.push(tag)
    }
    newTagInput.value = ''
  } else if (event.key === 'Backspace' && !newTagInput.value) {
    tempFilters.value.tags.pop()
  }
}

// Remove tag
const removeTag = (index) => {
  tempFilters.value.tags.splice(index, 1)
}

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

// Add date formatter function
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

// Fetch images from API
const fetchPhotos = async () => {
  if (!props.useApiData) return

  loading.value = true
  error.value = null

  try {
    const params = new URLSearchParams({
      userId: props.userId.toString()
    })

    if (appliedFilters.value.query) {
      params.append('imgName', appliedFilters.value.query)
    }
    if (appliedFilters.value.dateRange.start) {
      params.append('startDate', appliedFilters.value.dateRange.start)
    }
    if (appliedFilters.value.dateRange.end) {
      params.append('endDate', appliedFilters.value.dateRange.end)
    }
    if (appliedFilters.value.location) {
      params.append('imgPos', appliedFilters.value.location)
    }
    if (appliedFilters.value.peoples) {
      params.append('peoples', appliedFilters.value.peoples)
    }
    if (appliedFilters.value.tags?.length > 0) {
      params.append('tags', appliedFilters.value.tags.join(','))
    }

    const response = await fetch(`http://10.16.60.67:9090/img/all?${params}`)
    const result = await response.json()

    if (result && result.data) {
      const transformedData = await Promise.all(result.data.map(async img => {
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
          displayDate: formatDate(img.imgDate) || formatDate(new Date()),
          userId: img.userId,
          tags: img.tags,
          peoples: img.peoples,
          location: img.imgPos,
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
  let idCounter = 6
  return () => props.useApiData ? undefined : ++idCounter
})()

// Add upload queue
const uploadingPhotos = ref([])

// Modify upload method
const uploadPhotos = (file) => {
  searchQuery.value = ''
  tempFilters.value = {
    dateRange: { start: '', end: '' },
    location: '',
    tags: [],
    peoples: ''
  }
  appliedFilters.value = {
    query: '',
    dateRange: { start: '', end: '' },
    location: '',
    tags: [],
    peoples: ''
  }
  advancedFilters.value = {
    dateRange: { start: '', end: '' },
    location: '',
    tags: [],
    peoples: ''
  }
  showAdvancedSearch.value = false
  fetchPhotos()
  if (file) {
    if (!file.type.startsWith('image/')) {
      toast.error('Please upload an image file')
      return
    }

    const localUrl = URL.createObjectURL(file)
    const currentDate = new Date().toISOString().slice(0, 19).replace('T', ' ')
    const tempPhoto = {
      id: `temp_${Date.now()}`,
      name: file.name,
      src: localUrl,
      size: formatFileSize(file.size),
      date: currentDate.split(' ')[0],
      type: file.type.split('/')[1].toUpperCase(),
      isUploading: true,
      tempUrl: localUrl
    }

    if (!props.useApiData) {
      propPhotos.value.push(tempPhoto)
      toast.success('Image uploaded successfully')
      URL.revokeObjectURL(localUrl)
      return
    }

    uploadingPhotos.value.push(tempPhoto)

    const formData = new FormData()
    formData.append('files', file)

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
        uploadingPhotos.value = uploadingPhotos.value.filter(p => p.id !== tempPhoto.id)
        URL.revokeObjectURL(localUrl)
        toast.success('Image uploaded successfully')
        fetchPhotos()
      })
      .catch(error => {
        const failedPhoto = uploadingPhotos.value.find(p => p.id === tempPhoto.id)
        if (failedPhoto) {
          failedPhoto.isUploading = false
          failedPhoto.uploadFailed = true
        }
        console.error('Upload error:', error)
        toast.error(`Failed to upload image: ${error.message}`)
      })
  }
}

// Method to clear failed uploads from queue
const clearUploadFailedQueue = () => {
  uploadingPhotos.value = uploadingPhotos.value.filter(photo => {
    if (photo.uploadFailed) {
      if (photo.tempUrl) {
        URL.revokeObjectURL(photo.tempUrl)
      }
      return false
    }
    return true
  })
}

// Method to clear entire upload queue
const clearUploadQueue = () => {
  uploadingPhotos.value.forEach(photo => {
    if (photo.tempUrl) {
      URL.revokeObjectURL(photo.tempUrl)
    }
  })
  uploadingPhotos.value = []
}

// Modify display photos computed property
const displayPhotos = computed(() => {
  if (props.useApiData) {
    return [...uploadingPhotos.value, ...apiPhotos.value]
  }
  return propPhotos.value
})

// Clean up memory when component unmounts
onUnmounted(() => {
  clearUploadQueue()
})

const deletePhotos = (selectedIds) => {
  const photosToDelete = selectedIds.value.map(id =>
    displayPhotos.value.find(p => p.id === id)
  ).filter(Boolean);

  if (!props.useApiData) {
    propPhotos.value = propPhotos.value.filter(photo => !selectedIds.value.includes(photo.id))
  } else {
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
        fetchPhotos()
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

      URL.revokeObjectURL(objectUrl);
    } catch (err) {
      console.error(`Download failed: ${photo.name}`, err);
      toast.error(`Failed to download ${photo.name}`);
    }
  }

  toast.success(`Successfully downloaded ${photosToDownload.length} photo(s)`);
}

// Enhanced filtered photos computed
const filteredPhotos = computed(() => {
  return displayPhotos.value
})

// Modify apply filters method
const applyFilters = () => {
  clearUploadQueue()

  advancedFilters.value = { ...tempFilters.value }
  appliedFilters.value = {
    query: searchQuery.value || '',
    dateRange: {
      start: tempFilters.value.dateRange.start || '',
      end: tempFilters.value.dateRange.end || ''
    },
    location: tempFilters.value.location || '',
    tags: tempFilters.value.tags?.length ? tempFilters.value.tags : [],
    peoples: tempFilters.value.peoples || ''
  }
  fetchPhotos()
}

// Clear all filter conditions
const clearAdvancedFilters = () => {
  searchQuery.value = ''
  tempFilters.value = {
    dateRange: { start: '', end: '' },
    location: '',
    tags: [],
    peoples: ''
  }
  applyFilters()
}

// Emit filtered photos whenever they change
watch(filteredPhotos, (newFilteredPhotos) => {
  emit('filter', newFilteredPhotos)
})

// Method to check if a photo is selected
const isPhotoSelected = (photoId) => {
  return props.selectedPhotoIds.includes(photoId)
}

// Method to check if a photo can be selected
const canSelectPhoto = (photo) => {
  return !photo.isUploading && !photo.uploadFailed
}

// Modify toggle selection method
const togglePhotoSelection = (photoId) => {
  const photo = displayPhotos.value.find(p => p.id === photoId)
  if (!photo || !canSelectPhoto(photo)) return
  emit('select-photo', photoId)
}

// Method to change view mode
const setViewMode = (mode) => {
  viewMode.value = mode
  emit('update:viewMode', mode)
}

// Modify photo modal opening method
const openPhotoModal = (photo) => {
  if (photo.uploadFailed) {
    toast.error('Cannot preview failed upload')
    return
  }

  currentPhoto.value = photo
  isModalOpen.value = true
  emit('view-photo', photo)
}

// Modify photo index lookup method
const getPhotoIndex = (photoId) => {
  const allPhotos = displayPhotos.value
  return allPhotos.findIndex(p => p.id === photoId)
}

const viewNextPhoto = () => {
  if (!currentPhoto.value) return

  const currentIndex = getPhotoIndex(currentPhoto.value.id)
  const allPhotos = displayPhotos.value

  if (currentIndex < allPhotos.length - 1) {
    currentPhoto.value = allPhotos[currentIndex + 1]
  } else {
    currentPhoto.value = allPhotos[0]
  }
}

const viewPreviousPhoto = () => {
  if (!currentPhoto.value) return

  const currentIndex = getPhotoIndex(currentPhoto.value.id)
  const allPhotos = displayPhotos.value

  if (currentIndex > 0) {
    currentPhoto.value = allPhotos[currentIndex - 1]
  } else {
    currentPhoto.value = allPhotos[allPhotos.length - 1]
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
  document.addEventListener('click', closeActionMenu)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
  document.removeEventListener('click', closeActionMenu)
})

// Action menu state
const showActionMenu = ref(false)
const actionMenuPhoto = ref(null)
const actionMenuPosition = ref({ x: 0, y: 0 })

// Modify action click handler
const handleActionClick = (photo, event) => {
  if (photo.uploadFailed) {
    toast.error('Cannot perform actions on failed uploads')
    return
  }

  if (photo.isUploading) {
    toast.info('Photo is still uploading. Only preview is available.')
    return
  }

  event?.stopPropagation()
  actionMenuPhoto.value = photo
  if (event) {
    const rect = event.target.getBoundingClientRect()
    const menuWidth = 150
    const spaceRight = window.innerWidth - rect.right

    actionMenuPosition.value = {
      x: spaceRight > menuWidth ? rect.left : rect.right - menuWidth,
      y: rect.bottom + window.scrollY
    }
  }
  showActionMenu.value = true
}

// Handle menu item click
const handleMenuAction = (action) => {
  if (isModalOpen.value) {
    actionMenuPhoto.value = currentPhoto.value
  }
  if (!actionMenuPhoto.value) return

  switch (action) {
    case 'edit':
      closePhotoModal()
      emit('photo-edit', actionMenuPhoto.value.id)
      break
    case 'delete':
      deletePhotos({ value: [actionMenuPhoto.value.id] })
      break
    case 'download':
      downloadPhotos({ value: [actionMenuPhoto.value.id] })
      break
  }
  showActionMenu.value = false
  actionMenuPhoto.value = null

  if (isModalOpen.value && action == 'delete') {
    closePhotoModal()
  }
}

// Close menu when clicking outside
const closeActionMenu = () => {
  showActionMenu.value = false
  actionMenuPhoto.value = null
}

// Method to refresh API photos
const refreshPhotos = () => {
  clearUploadQueue()
  fetchPhotos()
}

const getPhotoById = (id) => {
  return displayPhotos.value.find(photo => photo.id === id)
}

// Set fixed color classes for tags
const tagColorClasses = [
  'bg-indigo-100 text-indigo-700',
  'bg-emerald-100 text-emerald-700',
  'bg-amber-100 text-amber-700',
  'bg-rose-100 text-rose-700',
  'bg-purple-100 text-purple-700',
  'bg-sky-100 text-sky-700',
  'bg-orange-100 text-orange-700'
]

// Get color for tag by index
const getTagColor = (index) => {
  return tagColorClasses[index % tagColorClasses.length]
}

// Handle tag click
const handleTagClick = (tag) => {
  tempFilters.value.tags = [tag]
  showAdvancedSearch.value = true
  closePhotoModal()
  applyFilters()
}

const addNewTag = async (photo) => {
  const newTag = prompt('请输入新标签:').trim();
   if (!newTag) return;

  if (photo.tags.includes(newTag)) {
    toast.error(`标签 "${newTag}" 已存在`);
    return;
  }

  try {
    const params = new URLSearchParams({
        userId: photo.userId.toString(),
        imgId: photo.id.toString(),
        tag: newTag
    })
    const response = await fetch(`http://10.16.60.67:9090/imgtag/add?${params}`, {
      method: 'POST'
    });

    const result = await response.json();

    if (result.msg === 'ok') {
      photo.tags.push(newTag);
      toast.success(`标签 "${newTag}" 已成功添加`);
    } else {
      throw new Error(result.msg || '添加标签失败');
    }
  } catch (error) {
    console.error('添加标签失败:', error);
    toast.error(`添加标签失败: ${error.message}`);
  }
};

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
    <!-- Enhanced Search Bar -->
    <div class="mb-6 space-y-4">
      <div class="flex items-center gap-3">
        <!-- Search input with integrated button -->
        <div class="relative flex-grow max-w-xl flex shadow-sm">
          <input v-model="searchQuery" type="text" :placeholder="searchPlaceholder"
            class="w-full py-2 pl-10 pr-4 border rounded-l-lg focus:outline-none focus:ring-2 focus:ring-blue-300 focus:border-blue-300 transition-all"
            @keyup.enter="applyFilters" />
          <div class="absolute left-3 top-2 text-gray-500">
            <svg class="w-5 h-5" viewBox="0 0 24 24">
              <path fill="currentColor" :d="mdiImageSearch" />
            </svg>
          </div>
          <BaseButton :icon="mdiMagnify" color="info" class="rounded-l-none px-4" @click="applyFilters" />
        </div>

        <!-- Filter toggle button -->
        <BaseButton :icon="showAdvancedSearch ? mdiFilterVariantRemove : mdiFilterVariant"
          :color="showAdvancedSearch ? 'info' : 'whiteDark'"
          :title="showAdvancedSearch ? 'Hide filters' : 'Show filters'" class="shadow-sm"
          @click="showAdvancedSearch = !showAdvancedSearch" />

        <!-- View Mode Switcher -->
        <div class="flex border rounded-lg shadow-sm overflow-hidden">
          <BaseButton v-if="availableViewModes.includes('details')" :icon="mdiViewList"
            :color="viewMode === 'details' ? 'info' : 'whiteDark'" @click="setViewMode('details')"
            class="rounded-none border-r last:border-r-0" title="Details view" />

          <BaseButton v-if="availableViewModes.includes('large')" :icon="mdiViewGrid"
            :color="viewMode === 'large' ? 'info' : 'whiteDark'" @click="setViewMode('large')"
            class="rounded-none border-r last:border-r-0" title="Large icons" />

          <BaseButton v-if="availableViewModes.includes('grid')" :icon="mdiViewGridOutline"
            :color="viewMode === 'grid' ? 'info' : 'whiteDark'" @click="setViewMode('grid')"
            class="rounded-none border-r last:border-r-0" title="Medium icons" />

          <BaseButton v-if="availableViewModes.includes('small')" :icon="mdiViewCompactOutline"
            :color="viewMode === 'small' ? 'info' : 'whiteDark'" @click="setViewMode('small')"
            class="rounded-none border-r last:border-r-0" title="Small icons" />
        </div>
      </div>

      <!-- Advanced Search Panel -->
      <div v-if="showAdvancedSearch" class="p-5 bg-white border rounded-lg shadow-sm animate-fade-in">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <!-- Date Range -->
          <div class="space-y-2">
            <label class="text-sm font-medium flex items-center text-gray-700">
              <svg class="w-6 h-6 mr-1">
                <path fill="currentColor" :d="mdiCalendarMonth" />
              </svg>
              <span>Date Range</span>
            </label>
            <div class="flex gap-2 items-center">
              <input v-model="tempFilters.dateRange.start" type="date"
                class="flex-1 px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-300 focus:border-blue-300"
                placeholder="From date" />
              <span class="text-gray-500">to</span>
              <input v-model="tempFilters.dateRange.end" type="date"
                class="flex-1 px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-300 focus:border-blue-300"
                placeholder="To date" />
            </div>
          </div>

          <!-- Location -->
          <div class="space-y-2">
            <label class="text-sm font-medium flex items-center text-gray-700">
              <svg class="w-6 h-6 mr-1">
                <path fill="currentColor" :d="mdiMapMarker" />
              </svg>
              <span>Location</span>
            </label>
            <input v-model="tempFilters.location" type="text"
              class="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-300 focus:border-blue-300"
              placeholder="Enter location name" />
          </div>

          <!-- Tags -->
          <div class="space-y-2">
            <label class="text-sm font-medium flex items-center text-gray-700">
              <svg class="w-6 h-6 mr-1">
                <path fill="currentColor" :d="mdiTag" />
              </svg>
              <span>Tags</span>
            </label>
            <div class="flex flex-wrap gap-2 px-3 py-2 border border-gray-700 rounded focus-within:ring-2 focus-within:ring-blue-300 focus-within:border-blue-300">
              <span v-for="(tag, index) in tempFilters.tags"
                    :key="index"
                    :class="getTagColor(index)"
                    class="px-2 py-1 rounded-full flex items-center gap-1 text-sm">
                {{ tag }}
                <button @click="removeTag(index)"
                        class="hover:text-red-500 focus:outline-none"
                        type="button">
                  ×
                </button>
              </span>
              <input v-model="newTagInput"
                    type="text"
                    class="flex-1 min-w-[120px] border-0 p-0 focus:outline-none focus:ring-0 bg-transparent"
                    placeholder="Type tag and press Enter"
                    @keydown="handleTagInput" />
            </div>
          </div>

          <!-- Peoples -->
          <div class="space-y-2">
            <label class="text-sm font-medium flex items-center text-gray-700">
              <svg class="w-6 h-6 mr-1"><path fill="currentColor" :d="mdiAccount" /></svg>
              <span>Peoples</span>
            </label>
            <input
              v-model="tempFilters.peoples"
              type="text"
              class="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-300 focus:border-blue-300"
              placeholder="Search by people names"
            />
          </div>
        </div>

        <!-- Actions -->
        <div class="mt-4 flex justify-end gap-2">
          <BaseButton label="Reset Filters" color="whiteDark" small @click="clearAdvancedFilters" />
          <BaseButton label="Apply Filters" color="success" small @click="applyFilters" />
        </div>
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

    <!-- Details View (Table-like) -->
    <div v-if="viewMode === 'details'" class="overflow-x-auto">
      <table class="w-full">
        <thead>
          <tr class="border-b">
            <th v-if="isSelectMode" class="px-3 py-2 text-left">Select</th>
            <th class="px-3 py-2 text-left">Preview</th>
            <th class="px-3 py-2 text-left">Name</th>
            <th class="px-3 py-2 text-left">Size</th>
            <th class="px-3 py-2 text-left">Modified Date</th>
            <th class="px-3 py-2 text-left">Tags</th>
            <th v-if="showActions" class="px-3 py-2 text-right">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="photo in filteredPhotos" :key="photo.id"
              class="border-b hover:bg-gray-100 dark:hover:bg-gray-700 group"
              :class="{ 'bg-blue-50': isSelectMode && isPhotoSelected(photo.id) }">
            <!-- Add select column -->
            <td v-if="isSelectMode" class="px-3 py-2 w-10">
              <button @click.stop="togglePhotoSelection(photo.id)"
                      :class="{ 'opacity-50 cursor-not-allowed': !canSelectPhoto(photo) }"
                      class="text-gray-500 hover:text-blue-500"
                      :disabled="!canSelectPhoto(photo)">
                <svg class="w-5 h-5" viewBox="0 0 24 24">
                  <path fill="currentColor"
                    :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
                </svg>
              </button>
            </td>

            <!-- Preview column with upload indicator -->
            <td class="px-3 py-2 w-20 relative">
              <div class="relative">
                <img :src="photo.src"
                     class="h-12 w-16 object-cover rounded cursor-pointer"
                     @click="openPhotoModal(photo)" />
                <!-- Add upload indicator inside preview cell -->
                <div v-if="photo.isUploading"
                     class="absolute inset-0 flex items-center justify-center">
                  <div class="w-8 h-8 rounded-full bg-white/80 flex items-center justify-center shadow-lg">
                    <div class="animate-spin rounded-full h-5 w-5 border-2 border-blue-500 border-t-transparent"></div>
                  </div>
                </div>
              </div>
            </td>

            <!-- Name column with status -->
            <td class="px-3 py-2">
              <div class="flex flex-col">
                <span class="truncate max-w-[200px]" :title="photo.name">{{ photo.name }}</span>
                <span v-if="photo.isUploading" class="text-xs text-blue-500">Uploading...</span>
                <span v-if="photo.uploadFailed" class="text-xs text-red-500">Upload failed</span>
              </div>
            </td>

            <!-- Regular columns -->
            <td class="px-3 py-2">{{ photo.size }}</td>
            <td class="px-3 py-2">{{ photo.displayDate }}</td>

            <!-- Tags column -->
            <td class="px-3 py-2 whitespace-nowrap">
              <div class="flex gap-1 overflow-x-auto">
                <span v-for="(tag, index) in photo.tags" :key="tag"
                      class="px-2 py-0.5 text-xs rounded whitespace-nowrap cursor-pointer hover:opacity-80"
                      :class="getTagColor(index)"
                      @click.stop="handleTagClick(tag)">
                  {{ tag }}
                </span>
                <button class="px-2 py-0.5 text-xs rounded bg-gray-200 hover:bg-gray-300 cursor-pointer"
                    @click.stop="addNewTag(photo)">
                    +
                </button>
              </div>
            </td>

            <!-- Actions column -->
            <td v-if="showActions" class="px-3 py-2 text-right w-10">
              <BaseButton
                v-if="!photo.isUploading && !photo.uploadFailed"
                :icon="mdiDotsVertical"
                small
                color="lightDark"
                class="opacity-0 group-hover:opacity-100 transition-opacity"
                @click="handleActionClick(photo, $event)"
              />
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Large Grid View -->
    <div v-else-if="viewMode === 'large'" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
      <div v-for="photo in filteredPhotos" :key="photo.id"
        class="relative flex flex-col items-center"
        :class="[
          'hover:bg-gray-100 dark:hover:bg-gray-700 p-2 rounded',
          { 'ring-2 ring-blue-500': isSelectMode && isPhotoSelected(photo.id) }
        ]"
      >
        <!-- Modify upload indicator to be less intrusive -->
        <div v-if="photo.isUploading"
             class="absolute inset-0 flex items-center justify-center z-10">
          <div class="w-10 h-10 rounded-full bg-white/80 flex items-center justify-center shadow-lg">
            <div class="animate-spin rounded-full h-6 w-6 border-2 border-blue-500 border-t-transparent"></div>
          </div>
        </div>

        <!-- Add upload failed indicator -->
        <div v-if="photo.uploadFailed"
             class="absolute top-2 right-2 text-red-500"
             title="Upload failed">
          ⚠️
        </div>

        <div v-if="isSelectMode" class="absolute top-4 left-4 z-10">
          <button @click.stop="togglePhotoSelection(photo.id)"
            :class="{ 'opacity-50 cursor-not-allowed': !canSelectPhoto(photo) }"
            class="bg-white bg-opacity-70 rounded-md p-1 text-gray-700 hover:text-blue-500"
            :disabled="!canSelectPhoto(photo)">
            <svg class="w-5 h-5" viewBox="0 0 24 24">
              <path fill="currentColor" :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
            </svg>
          </button>
        </div>
        <img :src="photo.src" class="w-full h-40 object-cover rounded mb-2 cursor-pointer"
          @click="openPhotoModal(photo)" />
        <span class="text-center truncate w-full block" :title="photo.name">{{ photo.name }}</span>
      </div>
    </div>

    <!-- Medium Grid View (Default) -->
    <div v-else-if="viewMode === 'grid'" class="grid grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-3">
      <div v-for="photo in filteredPhotos" :key="photo.id"
        class="relative flex flex-col items-center"
        :class="[
          'hover:bg-gray-100 dark:hover:bg-gray-700 p-2 rounded',
          { 'ring-2 ring-blue-500': isSelectMode && isPhotoSelected(photo.id) }
        ]"
      >
        <!-- Modify upload indicator -->
        <div v-if="photo.isUploading"
             class="absolute inset-0 flex items-center justify-center z-10">
          <div class="w-8 h-8 rounded-full bg-white/80 flex items-center justify-center shadow-lg">
            <div class="animate-spin rounded-full h-5 w-5 border-2 border-blue-500 border-t-transparent"></div>
          </div>
        </div>

        <!-- Add upload failed indicator -->
        <div v-if="photo.uploadFailed"
             class="absolute top-2 right-2 text-red-500"
             title="Upload failed">
          ⚠️
        </div>

        <div v-if="isSelectMode" class="absolute top-3 left-3 z-10">
          <button @click.stop="togglePhotoSelection(photo.id)"
            :class="{ 'opacity-50 cursor-not-allowed': !canSelectPhoto(photo) }"
            class="bg-white bg-opacity-70 rounded-md p-0.5 text-gray-700 hover:text-blue-500"
            :disabled="!canSelectPhoto(photo)">
            <svg class="w-4 h-4" viewBox="0 0 24 24">
              <path fill="currentColor" :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
            </svg>
          </button>
        </div>
        <img :src="photo.src" class="w-full h-24 object-cover rounded mb-1 cursor-pointer"
          @click="openPhotoModal(photo)" />
        <span class="text-center text-sm truncate w-full block" :title="photo.name">{{ photo.name }}</span>
      </div>
    </div>

    <!-- Small Grid View -->
    <div v-else class="grid grid-cols-4 md:grid-cols-6 lg:grid-cols-10 gap-2">
      <div v-for="photo in filteredPhotos" :key="photo.id"
        class="relative flex flex-col items-center"
        :class="[
          'hover:bg-gray-100 dark:hover:bg-gray-700 p-1 rounded',
          { 'ring-1 ring-blue-500': isSelectMode && isPhotoSelected(photo.id) }
        ]"
      >
        <!-- Modify upload indicator -->
        <div v-if="photo.isUploading"
             class="absolute inset-0 flex items-center justify-center z-10">
          <div class="w-6 h-6 rounded-full bg-white/80 flex items-center justify-center shadow-lg">
            <div class="animate-spin rounded-full h-4 w-4 border-2 border-blue-500 border-t-transparent"></div>
          </div>
        </div>

        <!-- Add upload failed indicator -->
        <div v-if="photo.uploadFailed"
             class="absolute top-2 right-2 text-red-500"
             title="Upload failed">
          ⚠️
        </div>

        <div v-if="isSelectMode" class="absolute top-2 left-2 z-10">
          <button @click.stop="togglePhotoSelection(photo.id)"
            :class="{ 'opacity-50 cursor-not-allowed': !canSelectPhoto(photo) }"
            class="bg-white bg-opacity-70 rounded-md p-0.5 text-gray-700 hover:text-blue-500"
            :disabled="!canSelectPhoto(photo)">
            <svg class="w-3 h-3" viewBox="0 0 24 24">
              <path fill="currentColor" :d="isPhotoSelected(photo.id) ? mdiCheckboxMarked : mdiCheckboxBlankOutline" />
            </svg>
          </button>
        </div>
        <img :src="photo.src" class="w-full h-16 object-cover rounded mb-1 cursor-pointer"
          @click="openPhotoModal(photo)" />
        <span class="text-center text-xs truncate w-full block" :title="photo.name">{{ photo.name }}</span>
      </div>
    </div>

    <!-- Empty State -->
    <CardBoxComponentEmpty v-if="filteredPhotos.length === 0" />

    <!-- Photo Modal -->
    <div v-if="isModalOpen && currentPhoto"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-75"
      @click="closePhotoModal">
      <div class="max-w-5xl w-full mx-4 relative" @click.stop>
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

            <div class="text-lg font-medium break-all">{{ currentPhoto.name }}</div>

            <!-- Modify action button display logic -->
            <div class="flex items-center gap-2">
              <template v-if="showActions && !currentPhoto.uploadFailed">
                <button v-for="(action, index) in [
                  { icon: mdiImageEdit, label: 'Edit', value: 'edit', disabled: currentPhoto.isUploading },
                  { icon: mdiDelete, label: 'Delete', value: 'delete', disabled: currentPhoto.isUploading },
                  { icon: mdiDownload, label: 'Download', value: 'download', disabled: currentPhoto.isUploading }
                ]" :key="index"
                  class="p-1 rounded-full hover:bg-gray-200 flex items-center"
                  :class="{ 'opacity-50 cursor-not-allowed': action.disabled }"
                  @click="!action.disabled && handleMenuAction(action.value)"
                  :title="action.disabled ? 'Not available while uploading' : action.label">
                  <svg class="w-6 h-6" viewBox="0 0 24 24">
                    <path fill="currentColor" :d="action.icon" />
                  </svg>
                </button>
              </template>
              <button class="p-1 rounded-full hover:bg-gray-200" @click="closePhotoModal">
                <svg class="w-6 h-6" viewBox="0 0 24 24">
                  <path fill="currentColor" :d="mdiClose" />
                </svg>
              </button>
            </div>
          </div>

          <!-- Photo -->
          <div class="flex justify-center bg-black p-2">
            <img :src="currentPhoto.src"
                 class="max-h-[70vh] max-w-full object-contain"
                 :class="{ 'opacity-70': currentPhoto.isUploading }"
                 alt="Full size preview" />
          </div>

          <!-- Enhanced Photo Details -->
          <div class="p-4 bg-white">
            <div class="flex items-start">
              <svg class="w-5 h-5 text-gray-500 mr-2 mt-0.5" viewBox="0 0 24 24">
                <path fill="currentColor" :d="mdiInformation" />
              </svg>
              <div class="flex-1">
                <div class="mb-1"><span class="font-medium">Size:</span> {{ currentPhoto.size }}</div>
                <div class="mb-1"><span class="font-medium">Date:</span> {{ currentPhoto.displayDate }}</div>
                <div v-if="currentPhoto.tags?.length" class="flex flex-wrap gap-2">
                  <span v-for="(tag, index) in currentPhoto.tags" :key="tag"
                        class="px-2 py-1 rounded cursor-pointer hover:opacity-80"
                        :class="getTagColor(index)"
                        @click="handleTagClick(tag)">
                    {{ tag }}
                  </span>
                </div>
                <div v-if="currentPhoto.desc" class="mt-2">
                  <span class="font-medium">Description:</span>
                  <p class="mt-1 text-gray-600">{{ currentPhoto.desc }}</p>
                </div>
                <!-- Add upload status if applicable -->
                <div v-if="currentPhoto.isUploading" class="mt-2 text-blue-500">
                  Upload in progress...
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Action Menu -->
    <div v-if="showActionMenu"
      class="absolute bg-white rounded-lg shadow-lg py-2 min-w-[150px]"
      :style="`left: ${actionMenuPosition.x}px; top: ${actionMenuPosition.y}px`"
      @click.stop>
      <button v-for="(action, index) in [
        { icon: mdiImageEdit, label: 'Edit', value: 'edit' },
        { icon: mdiDelete, label: 'Delete', value: 'delete' },
        { icon: mdiDownload, label: 'Download', value: 'download' }
      ]" :key="index" class="w-full px-4 py-2 flex items-center hover:bg-gray-100 text-left"
        @click="handleMenuAction(action.value)">
        <svg class="w-5 h-5 mr-2" viewBox="0 0 24 24">
          <path fill="currentColor" :d="action.icon" />
        </svg>
        {{ action.label }}
      </button>
    </div>

  </div>
</template>

<style scoped>
.action-menu-enter-active,
.action-menu-leave-active {
  transition: opacity 0.2s;
}

.action-menu-enter-from,
.action-menu-leave-to {
  opacity: 0;
}

.animate-fade-in {
  animation: fadeIn 0.2s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
