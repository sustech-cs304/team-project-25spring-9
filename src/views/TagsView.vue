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
import { ref, nextTick, computed } from 'vue'
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

// 关键状态
const isLoading = ref(false)
const error = ref(null)
const currentTag = ref(null)
const currentTagTitle = ref('Tags')
const isSelectMode = ref(false)
const selectedPhotos = ref([])
const viewMode = ref('grid')
const photoGallery = ref(null)
const tagGallery = ref(null)

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

// Handle refreshing tags
const handleRefreshTags = () => {
  photoGallery.value?.refreshPhotos()
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

      <!-- Tag Filters Bar -->
      <div class="bg-gradient-to-r from-blue-50 via-white to-purple-50 rounded-xl shadow p-3 mb-6 overflow-x-auto custom-scrollbar">
        <div class="flex flex-nowrap gap-2 min-w-0">
          <BaseButton
            :icon="mdiTagMultiple"
            :label="`All Photos${Array.isArray(allPhotos?.value) ? ` (${allPhotos.value.length})` : ''}`"
            :color="!currentTag ? 'info' : 'whiteDark'"
            :class="[
              'whitespace-nowrap rounded-full font-semibold shadow',
              !currentTag ? 'bg-gradient-to-r from-blue-400 to-purple-400 text-white' : ''
            ]"
            small
            @click="handleBackToTags"
          />
          <BaseButton
            v-for="group in tagGroups"
            :key="group.id"
            :icon="mdiTag"
            :label="`${group.name}${group.count !== undefined ? ` (${group.count})` : ''}`"
            :color="currentTag === group.id ? 'info' : 'whiteDark'"
            :class="[
              'whitespace-nowrap rounded-full font-semibold shadow',
              currentTag === group.id ? 'bg-gradient-to-r from-blue-400 to-purple-400 text-white' : 'bg-white'
            ]"
            small
            @click="() => selectTagGroup(group)"
          />
        </div>
      </div>

      <!-- Tag group gallery with新样式 -->
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
