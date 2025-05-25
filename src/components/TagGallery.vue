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

  <!-- Tags Grid: 更加卡片化和色彩化，与 PhotoGallery 区分 -->
  <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
    <div
      v-for="(group, idx) in tagGroups"
      :key="group.id"
      class="rounded-2xl shadow-lg border-2 border-blue-200 bg-gradient-to-br from-blue-50 via-white to-purple-50 hover:shadow-2xl hover:scale-[1.03] transition-all cursor-pointer relative overflow-hidden"
      @click="$emit('select-tag-group', group)"
    >
      <div class="relative h-40">
        <img
          v-if="group.coverImage"
          :src="group.coverImage"
          class="absolute inset-0 w-full h-full object-cover rounded-t-2xl"
          alt="Tag cover"
        />
        <div v-else class="absolute inset-0 bg-gradient-to-br from-blue-100 to-purple-100 flex items-center justify-center">
          <svg class="w-16 h-16 text-blue-300" viewBox="0 0 24 24">
            <path fill="currentColor" :d="mdiImageMultiple" />
          </svg>
        </div>
        <div class="absolute inset-0 bg-gradient-to-t from-black/40 to-transparent rounded-t-2xl"/>
        <div class="absolute top-3 left-3 bg-white/80 text-blue-700 px-3 py-1 rounded-full text-xs font-semibold shadow">
          <svg class="w-4 h-4 inline-block mr-1" viewBox="0 0 24 24">
            <path fill="currentColor" :d="mdiTag" />
          </svg>
          {{ group.name }}
        </div>
        <div class="absolute bottom-3 right-3 bg-blue-600 text-white px-3 py-1 rounded-full text-xs font-semibold shadow">
          {{ group.count }} photos
        </div>
      </div>
      <div class="p-5">
        <p class="text-sm text-blue-900 font-medium mb-1 truncate">{{ group.description }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 让 TagGallery 卡片更有辨识度 */
.grid > div {
  min-height: 15rem;
}
</style>
