<script setup>
import {
  mdiMonitorCellphone,
  mdiTableBorder,
  mdiTableOff,
  mdiGithub,
  mdiImageMultiple,
  mdiImagePlus,
  mdiImageRemove,
  mdiImageSearch,
  mdiImageEdit,
  mdiViewList,
  mdiViewGrid,
  mdiViewGridOutline,
  mdiViewCompactOutline,
  mdiDotsVertical
} from '@mdi/js'
import SectionMain from '@/components/SectionMain.vue'
import NotificationBar from '@/components/NotificationBar.vue'
import TableSampleClients from '@/components/TableSampleClients.vue'
import CardBox from '@/components/CardBox.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'
import BaseButton from '@/components/BaseButton.vue'
import CardBoxComponentEmpty from '@/components/CardBoxComponentEmpty.vue'
import { ref, computed } from 'vue'

// Define view mode and search functionality
const viewMode = ref('grid')
const searchQuery = ref('')

// Sample photo data
const photos = ref([
  { id: 1, name: 'Mountain View', src: 'https://picsum.photos/id/10/300/200', size: '2.4 MB', date: '2023-09-15', type: 'JPG' },
  { id: 2, name: 'Beach Sunset', src: 'https://picsum.photos/id/11/300/200', size: '3.1 MB', date: '2023-10-02', type: 'PNG' },
  { id: 3, name: 'City Skyline', src: 'https://picsum.photos/id/12/300/200', size: '1.8 MB', date: '2023-11-20', type: 'JPG' },
  { id: 4, name: 'Forest Path', src: 'https://picsum.photos/id/13/300/200', size: '2.9 MB', date: '2024-01-05', type: 'JPG' },
  { id: 5, name: 'Desert Landscape', src: 'https://picsum.photos/id/14/300/200', size: '2.2 MB', date: '2024-02-18', type: 'PNG' },
  { id: 6, name: 'Ocean Waves', src: 'https://picsum.photos/id/15/300/200', size: '4.0 MB', date: '2024-03-10', type: 'TIFF' },
])

// Filtered photos based on search query
const filteredPhotos = computed(() => {
  if (!searchQuery.value) return photos.value
  return photos.value.filter(photo =>
    photo.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

// Method to change view mode
const setViewMode = (mode) => {
  viewMode.value = mode
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithButton :icon="mdiImageMultiple" title="Photos" main>

      </SectionTitleLineWithButton>

      <!-- Search Bar -->
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
          <BaseButton :icon="mdiViewList" :color="viewMode === 'details' ? 'info' : 'whiteDark'" small
            @click="setViewMode('details')" class="mr-1" title="Details view" />
          <BaseButton :icon="mdiViewGrid" :color="viewMode === 'large' ? 'info' : 'whiteDark'" small
            @click="setViewMode('large')" class="mr-1" title="Large icons" />
          <BaseButton :icon="mdiViewGridOutline" :color="viewMode === 'grid' ? 'info' : 'whiteDark'" small
            @click="setViewMode('grid')" class="mr-1" title="Medium icons" />
          <BaseButton :icon="mdiViewCompactOutline" :color="viewMode === 'small' ? 'info' : 'whiteDark'" small
            @click="setViewMode('small')" title="Small icons" />
        </div>
      </div>

      <!-- Photo Display Component -->
      <CardBox class="mb-6">
        <!-- Details View (Table-like) -->
        <div v-if="viewMode === 'details'" class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="border-b">
                <th class="px-3 py-2 text-left">Preview</th>
                <th class="px-3 py-2 text-left">Name</th>
                <th class="px-3 py-2 text-left">Type</th>
                <th class="px-3 py-2 text-left">Size</th>
                <th class="px-3 py-2 text-left">Modified Date</th>
                <th class="px-3 py-2 text-left">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="photo in filteredPhotos" :key="photo.id" class="border-b hover:bg-gray-50">
                <td class="px-3 py-2">
                  <img :src="photo.src" class="h-12 w-16 object-cover rounded" />
                </td>
                <td class="px-3 py-2">{{ photo.name }}</td>
                <td class="px-3 py-2">{{ photo.type }}</td>
                <td class="px-3 py-2">{{ photo.size }}</td>
                <td class="px-3 py-2">{{ photo.date }}</td>
                <td class="px-3 py-2">
                  <BaseButton :icon="mdiDotsVertical" small color="lightDark" />
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Large Grid View -->
        <div v-else-if="viewMode === 'large'" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          <div v-for="photo in filteredPhotos" :key="photo.id"
            class="flex flex-col items-center hover:bg-gray-50 p-2 rounded">
            <img :src="photo.src" class="w-full h-40 object-cover rounded mb-2" />
            <span class="text-center">{{ photo.name }}</span>
          </div>
        </div>

        <!-- Medium Grid View (Default) -->
        <div v-else-if="viewMode === 'grid'" class="grid grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-3">
          <div v-for="photo in filteredPhotos" :key="photo.id"
            class="flex flex-col items-center hover:bg-gray-50 p-2 rounded">
            <img :src="photo.src" class="w-full h-24 object-cover rounded mb-1" />
            <span class="text-center text-sm">{{ photo.name }}</span>
          </div>
        </div>

        <!-- Small Grid View -->
        <div v-else class="grid grid-cols-4 md:grid-cols-6 lg:grid-cols-10 gap-2">
          <div v-for="photo in filteredPhotos" :key="photo.id"
            class="flex flex-col items-center hover:bg-gray-50 p-1 rounded">
            <img :src="photo.src" class="w-full h-16 object-cover rounded mb-1" />
            <span class="text-center text-xs truncate w-full">{{ photo.name }}</span>
          </div>
        </div>

        <!-- Empty State -->
        <CardBoxComponentEmpty v-if="filteredPhotos.length === 0" />
      </CardBox>

      <!-- Action Buttons -->
      <div class="flex">
        <BaseButton :icon="mdiImagePlus" label="Add" color="contrast" rounded-full small class="mr-2" />
        <BaseButton :icon="mdiImageRemove" label="Remove" color="contrast" rounded-full small class="mx-2" />
        <BaseButton :icon="mdiImageEdit" label="Edit" color="contrast" rounded-full small class="ml-2" />
      </div>
    </SectionMain>
  </LayoutAuthenticated>
</template>
