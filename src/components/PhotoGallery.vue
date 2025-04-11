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
  mdiDownload
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
    // required: true,
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
  'photos-loaded'
])

// Internal state for the component
const viewMode = ref(props.initialViewMode)
const searchQuery = ref('')

// Track modal state and current photo
const isModalOpen = ref(false)
const currentPhoto = ref(null)

// Test Photos
const propPhotos = ref(props.photos)

// API data states
const apiPhotos = ref([])
const loading = ref(false)
const error = ref(null)

// Fetch images from API
const fetchPhotos = async () => {
  if (!props.useApiData) return

  loading.value = true
  error.value = null

  try {
    // Fetch image metadata from first API endpoint
    const response = await fetch('http://10.16.60.67:9090/img/all?userId=' + props.userId)
    const result = await response.json()

    if (result && result.data) {
      // Filter by userId if specified
      let imageData = result.data
      if (props.userId) {
        imageData = imageData.filter(img => img.userId === props.userId)
      }

      // Transform data to match our component's expected format
      apiPhotos.value = imageData.map(img => ({
        id: img.imgId,
        name: img.imgName || `Image ${img.imgId}`,
        src: `http://10.16.60.67:9000/softwareeng/upload-img/${img.imgId}.jpeg`,
        type: img.imgType || 'JPEG',
        size: img.imgSize ? `${(parseInt(img.imgSize) / 1024).toFixed(2)} KB` : 'Unknown',
        date: img.createTime || new Date().toISOString().split('T')[0],
        userId: img.userId
      }))

      // Emit the loaded photos to parent
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
  //todo: generate new id from API
  let idCounter = 6
  return () => props.useApiData ? undefined : ++idCounter
})()

const uploadPhotos = (file) => {
  // const file = file
  if (file) {
    if (!file.type.startsWith('image/')) {
      toast.error('Please upload an image file')
      return
    }
    const reader = new FileReader()
    reader.onload = (e) => {
      const newPhoto = {
        id: generateNewId(),
        name: file.name,
        src: e.target.result,
        size: `${(file.size / (1024 * 1024)).toFixed(1)} MB`,
        date: new Date().toISOString().split('T')[0],
        type: file.type.split('/')[1].toUpperCase()
      }
      if (!props.useApiData) {
        propPhotos.value.push(newPhoto)
      }
      else {
        //todo: upload to API
      }
      
      toast.success('Image uploaded successfully')
    }
    reader.onerror = () => {
      toast.error('Error reading file')
    }
    reader.readAsDataURL(file)
  }

}

const deletePhotos = (selectedIds) => {
  const count = selectedIds.value.length
  if(!props.useApiData) {
    propPhotos.value = propPhotos.value.filter(photo => !selectedIds.value.includes(photo.id))
  }
  else {
    //todo: delete from API
  }
  toast.success(`${count} photo(s) deleted`)
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

      URL.revokeObjectURL(objectUrl); // 清理内存
    } catch (err) {
      console.error(`下载失败: ${photo.name}`, err);
      toast.error(`Failed to download ${photo.name}`);
    }
  }

  toast.success(`Successfully downloaded ${photosToDownload.length} photo(s)`);
}

// Determine which photos to use - API or props
const displayPhotos = computed(() => {
  return props.useApiData ? apiPhotos.value : propPhotos.value
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

// Method to handle action button click with menu
const handleActionClick = (photo, event) => {
  // 防止冒泡
  event?.stopPropagation()
  
  actionMenuPhoto.value = photo
  if (event) {
    // 计算菜单位置
    const rect = event.target.getBoundingClientRect()
    actionMenuPosition.value = {
      x: rect.left,
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
      // TODO: Implement edit functionality
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
  fetchPhotos()
}

defineExpose({
  refreshPhotos,
  uploadPhotos,
  deletePhotos,
  downloadPhotos
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
              <BaseButton :icon="mdiDotsVertical" small color="lightDark" @click="handleActionClick(photo, $event)" />
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

            <!-- 添加操作按钮组 -->
            <div class="flex items-center gap-2">
              <button 
                v-for="(action, index) in [
                  { icon: mdiImageEdit, label: 'Edit', value: 'edit' },
                  { icon: mdiDelete, label: 'Delete', value: 'delete' },
                  { icon: mdiDownload, label: 'Download', value: 'download' }
                ]"
                :key="index"
                class="p-1 rounded-full hover:bg-gray-200 flex items-center"
                @click="handleMenuAction(action.value)">
                <svg class="w-6 h-6" viewBox="0 0 24 24">
                  <path fill="currentColor" :d="action.icon" />
                </svg>
              </button>
              
              <button class="p-1 rounded-full hover:bg-gray-200" @click="closePhotoModal">
                <svg class="w-6 h-6" viewBox="0 0 24 24">
                  <path fill="currentColor" :d="mdiClose" />
                </svg>
              </button>
            </div>
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

    <!-- Action Menu -->
    <div v-if="showActionMenu" 
      class="fixed z-50 bg-white rounded-lg shadow-lg py-2 min-w-[150px]"
      :style="`left: ${actionMenuPosition.x}px; top: ${actionMenuPosition.y}px`"
      @click.stop>
      <button 
        v-for="(action, index) in [
          { icon: mdiImageEdit, label: 'Edit', value: 'edit' },
          { icon: mdiDelete, label: 'Delete', value: 'delete' },
          { icon: mdiDownload, label: 'Download', value: 'download' }
        ]"
        :key="index"
        class="w-full px-4 py-2 flex items-center hover:bg-gray-100 text-left"
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
</style>
