<script setup>
import {
  mdiTagMultiple,
  mdiRefresh,
  mdiArrowLeft,
  mdiCheckboxMultipleMarkedOutline,
  mdiCursorDefault,
  mdiDownload,
  mdiImageEdit,
  mdiDelete,  // 增加需要的图标
  mdiImageMultiple
} from '@mdi/js'
import { ref, nextTick, computed, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import { useMainStore } from '@/stores/main'

// 添加必要的组件导入
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'
import BaseButton from '@/components/BaseButton.vue'
import PhotoGallery from '@/components/PhotoGallery.vue'
import PhotoEditor from '@/components/PhotoEditor.vue'
import TagGallery from '@/components/TagGallery.vue'

const toast = useToast()
const mainStore = useMainStore()

// 修改状态管理
const isLoading = ref(false)
const error = ref(null)
const currentTag = ref(null)
const currentTagTitle = ref('Tags')
const isSelectMode = ref(false)
const selectedPhotos = ref([])
const viewMode = ref('grid')
const photoGallery = ref(null)
const tagGallery = ref(null)
const allPhotos = ref([])
const tagGroups = ref([])

// 修改初始化数据方法，移除缓存逻辑
const initializeData = async () => {
  isLoading.value = true
  error.value = null

  try {
    const params = new URLSearchParams({ userId: mainStore.userId })
    const response = await fetch(`http://10.16.60.67:9090/img/all?${params}`)
    const result = await response.json()

    if (!result?.data) throw new Error('Failed to fetch photos')

    // 直接更新照片数据
    allPhotos.value = result.data.map(img => ({
      id: img.imgId,
      src: `http://10.16.60.67:9000/softwareeng/upload-img/${img.imgId}.jpeg`,
      tags: img.tags || []
    }))

    // 生成标签组
    const tagMap = new Map()
    allPhotos.value.forEach(photo => {
      if (Array.isArray(photo.tags)) {
        photo.tags.forEach(tag => {
          if (!tagMap.has(tag)) {
            tagMap.set(tag, {
              id: tag,
              name: tag,
              count: 0,
              photos: []
            })
          }
          const group = tagMap.get(tag)
          group.count++
          group.photos.push(photo)
        })
      }
    })

    // 更新标签组数据
    tagGroups.value = Array.from(tagMap.values())
      .sort((a, b) => b.count - a.count)

  } catch (err) {
    error.value = err.message
    toast.error(`Failed to load data: ${err.message}`)
  } finally {
    isLoading.value = false
  }
}

// Selection logic
const togglePhotoSelection = (photoId) => {
  if (selectedPhotos.value.includes(photoId)) {
    selectedPhotos.value = selectedPhotos.value.filter(id => id !== photoId)
  } else {
    selectedPhotos.value.push(photoId)
  }
}

const toggleSelectMode = () => {
  isSelectMode.value = !isSelectMode.value
  if (!isSelectMode.value) clearSelections()
}

const clearSelections = () => {
  selectedPhotos.value = []
}

// Photo actions
const handleDelete = () => {
  photoGallery.value?.deletePhotos(selectedPhotos)
  selectedPhotos.value = []
  // 刷新标签列表
  tagGallery.value?.refresh()
}

const handleDownload = () => {
  photoGallery.value?.downloadPhotos(selectedPhotos)
}

// Editor handling
const showEditor = ref(false)
const editingPhoto = ref(null)

const openEditorWithPhoto = (photoId) => {
  const photo = photoGallery.value?.getPhotoById(photoId)
  if (!photo) return

  editingPhoto.value = photo
  showEditor.value = true
  isSelectMode.value = false
  selectedPhotos.value = []
}

const closeEditor = () => {
  showEditor.value = false
  editingPhoto.value = null
}

const saveEditedPhoto = (updatedPhoto) => {
  photoGallery.value?.uploadPhotos(updatedPhoto)
  closeEditor()
  tagGallery.value?.refresh()
}

const filteredPhotos = computed(() => {
  if (!currentTag.value) return allPhotos.value
  return allPhotos.value.filter(p => Array.isArray(p.tags) && p.tags.includes(currentTag.value))
})

// 修改 handleRefreshTags 方法
const handleRefreshTags = () => {
  initializeData()
}

// 修改 handlePhotosLoaded 方法
const handlePhotosLoaded = (photos) => {
  // 当加载新照片时重新获取所有数据
  initializeData()
}

// Replace selectTagGroup to use cached data
const selectTagGroup = (group) => {
  // 从最新的 tagGroups 中查找
  const targetGroup = tagGroups.value.find(g => g.id === group.id)
  if (targetGroup) {
    currentTag.value = targetGroup.id
    currentTagTitle.value = targetGroup.name
    selectedPhotos.value = []
    nextTick(() => {
      photoGallery.value?.refreshPhotos()
    })
  }
}

// Add onMounted to initialize data
onMounted(() => {
  initializeData()
})

// Handle opening a tag group
const handleOpenTag = (tag) => {
  currentTag.value = tag.id
  currentTagTitle.value = tag.name
  selectedPhotos.value = []
  nextTick(() => {
    photoGallery.value?.refreshPhotos()
  })
}

// Handle going back to tag list
const handleBackToTags = () => {
  currentTag.value = null
  currentTagTitle.value = 'Tags'
  selectedPhotos.value = []
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithButton
        :icon="currentTag ? mdiImageMultiple : mdiTagMultiple"
        :title="currentTagTitle"
        main
      >
        <div class="flex">
          <BaseButton
            v-if="currentTag"
            :icon="isSelectMode ? mdiCursorDefault : mdiCheckboxMultipleMarkedOutline"
            :label="isSelectMode ? 'View Mode' : 'Select Mode'"
            :color="isSelectMode ? 'info' : 'contrast'"
            small
            class="mr-2"
            @click="toggleSelectMode"
          />
          <BaseButton
            v-if="currentTag"
            :icon="mdiRefresh"
            label="Refresh"
            color="info"
            small
            class="mr-2"
            @click="photoGallery?.refreshPhotos()"
          />
          <BaseButton
            v-if="currentTag"
            :icon="mdiArrowLeft"
            label="Back to Tags"
            color="contrast"
            small
            @click="handleBackToTags"
          />
          <BaseButton
            v-else
            :icon="mdiRefresh"
            label="Refresh"
            color="info"
            small
            class="mr-2"
            @click="handleRefreshTags"
          />
        </div>
      </SectionTitleLineWithButton>

      <!-- Add Tag Navigation Bar - using cached data -->
      <div class="mb-6">
        <div class="bg-gradient-to-r from-blue-50 via-white to-purple-50 rounded-xl shadow p-3 overflow-x-auto">
          <div class="flex flex-nowrap gap-2 min-w-0">
            <BaseButton
              :icon="mdiTagMultiple"
              :label="`All Photos (${allPhotos.length})`"
              :color="!currentTag ? 'info' : 'whiteDark'"
              :class="[
                'whitespace-nowrap font-medium shadow transition-all duration-200',
                !currentTag ? 'bg-gradient-to-r from-blue-400 to-purple-400 text-white' : 'bg-white'
              ]"
              rounded
              small
              @click="handleBackToTags"
            />
            <BaseButton
              v-for="group in tagGroups"
              :key="group.id"
              :icon="mdiTag"
              :label="`${group.name} (${group.count})`"
              :color="currentTag === group.id ? 'info' : 'whiteDark'"
              :class="[
                'whitespace-nowrap font-medium shadow transition-all duration-200',
                currentTag === group.id
                  ? 'bg-gradient-to-r from-blue-400 to-purple-400 text-white'
                  : 'bg-white hover:bg-gray-50'
              ]"
              rounded
              small
              @click="selectTagGroup(group)"
            />
          </div>
        </div>
      </div>

      <!-- Tag group gallery with new styling -->
      <TagGallery
        v-if="!currentTag"
        ref="tagGallery"
        @select-tag-group="handleOpenTag"
      />

      <!-- Photo Gallery for Selected Tag -->
      <div v-else>
        <CardBox class="mb-6">
          <PhotoGallery
            ref="photoGallery"
            :key="currentTag"
            :initial-view-mode="viewMode"
            :available-view-modes="['details', 'grid', 'large', 'small']"
            :is-select-mode="isSelectMode"
            :selected-photo-ids="selectedPhotos"
            :show-actions="true"
            :use-api-data="true"
            :user-id="mainStore.userId"
            :filter-tags="[currentTag]"
            class="p-2 shadow-none"
            @select-photo="togglePhotoSelection"
            @update:viewMode="mode => viewMode = mode"
            @photo-edit="openEditorWithPhoto"
            @photos-loaded="handlePhotosLoaded"
          />
        </CardBox>

        <!-- Action Buttons -->
        <div class="flex justify-between mb-6">
          <div class="flex gap-2">
            <template v-if="isSelectMode">
              <BaseButton
                :icon="mdiDelete"
                label="Remove"
                color="danger"
                rounded-full
                small
                :disabled="selectedPhotos.length === 0"
                @click="handleDelete"
              />
              <BaseButton
                :icon="mdiDownload"
                label="Download"
                color="success"
                rounded-full
                small
                :disabled="selectedPhotos.length === 0"
                @click="handleDownload"
              />
              <BaseButton
                :icon="mdiImageEdit"
                label="Edit"
                color="info"
                rounded-full
                small
                :disabled="selectedPhotos.length !== 1"
                @click="openEditorWithPhoto(selectedPhotos[0])"
              />
            </template>
          </div>
          <div v-if="isSelectMode && selectedPhotos.length > 0" class="flex items-center">
            <span class="mr-2 text-sm text-gray-700">{{ selectedPhotos.length }} selected</span>
            <BaseButton label="Clear selection" color="whiteDark" small @click="clearSelections" />
          </div>
        </div>
      </div>

      <!-- Photo Editor Modal -->
      <PhotoEditor
        v-if="showEditor"
        :photo="editingPhoto"
        @save="saveEditedPhoto"
        @close="closeEditor"
      />
    </SectionMain>
  </LayoutAuthenticated>
</template>

<style scoped>
/* Add horizontal scrollbar styling */
.overflow-x-auto {
  scrollbar-width: thin;
  scrollbar-color: #93c5fd #f8fafc;
}

.overflow-x-auto::-webkit-scrollbar {
  height: 6px;
}

.overflow-x-auto::-webkit-scrollbar-track {
  background: #f8fafc;
  border-radius: 4px;
}

.overflow-x-auto::-webkit-scrollbar-thumb {
  background-color: #93c5fd;
  border-radius: 4px;
  transition: all 0.2s;
}

.overflow-x-auto::-webkit-scrollbar-thumb:hover {
  background-color: #60a5fa;
}

.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: #a5b4fc #f8fafc;
}
.custom-scrollbar::-webkit-scrollbar {
  height: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: #f8fafc;
  border-radius: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #a5b4fc;
  border-radius: 4px;
  transition: all 0.2s;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background-color: #818cf8;
}
</style>
