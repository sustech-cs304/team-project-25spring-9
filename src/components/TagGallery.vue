<script setup>
import { ref, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import { useMainStore } from '@/stores/main'
import { mdiTag, mdiImageMultiple } from '@mdi/js'
import CardBox from '@/components/CardBox.vue'

const mainStore = useMainStore()
const toast = useToast()

// State
const isLoading = ref(false)
const error = ref(null)
const tagGroups = ref([])

const emit = defineEmits(['select-tag-group'])

// Fetch tag groups
const fetchTagGroups = async () => {
  isLoading.value = true
  error.value = null
  
  try {
    const params = new URLSearchParams({ userId: mainStore.userId })
    
    // 获取所有照片以分析标签
    const response = await fetch(`http://10.16.60.67:9090/img/all?${params}`)
    const result = await response.json()
    
    if (!result?.data) throw new Error('Failed to fetch photos')
    
    // 分析标签分组
    const tagMap = new Map()
    const photos = result.data || []
    
    // 处理已标记照片
    photos.forEach(photo => {
      if (!photo.tags?.length) return
      
      photo.tags.forEach(tag => {
        if (!tagMap.has(tag)) {
          tagMap.set(tag, {
            id: tag,
            name: tag,
            description: `Photos tagged with "${tag}"`,
            photos: [],
            coverImage: null
          })
        }
        const group = tagMap.get(tag)
        group.photos.push(photo)
        if (!group.coverImage) {
          group.coverImage = `http://10.16.60.67:9000/softwareeng/upload-img/${photo.imgId}.jpeg`
        }
      })
    })
    
    tagGroups.value = Array.from(tagMap.values())
      .map(group => ({
        ...group,
        count: group.photos.length
      }))
      .sort((a, b) => b.count - a.count)
      
  } catch (err) {
    error.value = err.message
    toast.error(`Failed to load tags: ${err.message}`)
  } finally {
    isLoading.value = false
  }
}

// Initial load
onMounted(fetchTagGroups)

// Expose refresh method
defineExpose({
  refresh: fetchTagGroups
})
</script>

<template>
  <!-- Loading State -->
  <div v-if="isLoading" class="flex justify-center items-center py-12">
    <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-500"></div>
  </div>

  <!-- Error State -->
  <div v-else-if="error" class="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-4">
    <p class="font-bold">Error</p>
    <p>{{ error }}</p>
    <BaseButton label="Retry" color="danger" small class="mt-2" @click="fetchTagGroups" />
  </div>

  <!-- Tags Grid -->
  <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
    <CardBox
      v-for="group in tagGroups"
      :key="group.id"
      class="cursor-pointer transition-all duration-300 transform hover:-translate-y-2 hover:shadow-xl bg-white"
      @click="$emit('select-tag-group', group)"
    >
      <div class="relative aspect-[4/3] overflow-hidden rounded-t-lg">
        <img
          v-if="group.coverImage"
          :src="group.coverImage"
          class="absolute inset-0 w-full h-full object-cover rounded-t-lg"
          alt="Tag cover"
        />
        <div v-else class="absolute inset-0 bg-gray-50 flex items-center justify-center">
          <svg class="w-16 h-16 text-gray-300" viewBox="0 0 24 24">
            <path fill="currentColor" :d="mdiImageMultiple" />
          </svg>
        </div>
        <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent opacity-0 hover:opacity-100 transition-opacity duration-300"/>
        <div class="absolute bottom-3 right-3 bg-black/80 text-white px-4 py-1.5 rounded-full text-sm font-medium backdrop-blur-sm">
          {{ group.count }} photos
        </div>
      </div>
      <div class="p-5">
        <h3 class="text-lg font-semibold mb-2 flex items-center gap-2">
          <svg class="w-5 h-5 text-blue-500" viewBox="0 0 24 24">
            <path fill="currentColor" :d="mdiTag" />
          </svg>
          {{ group.name }}
        </h3>
        <p class="text-sm text-gray-600">{{ group.description }}</p>
      </div>
    </CardBox>
  </div>
</template>
