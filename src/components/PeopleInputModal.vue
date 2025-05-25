<script setup>
import { ref, computed, watch } from 'vue'
import { mdiClose, mdiAccountPlus } from '@mdi/js'
import BaseButton from '@/components/BaseButton.vue'

const props = defineProps({
  show: Boolean,
  peoples: Array,
  title: {
    type: String,
    default: 'Add People Tag'
  },
  placeholder: {
    type: String,
    default: 'Enter people name'
  },
  initialValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['close', 'confirm'])

const inputValue = ref(props.initialValue)
const activeIndex = ref(-1) // 当前高亮的联想项
const isInputFocused = ref(false)
const suggestionUsed = ref(false)

// 过滤匹配的 peoples.nickname
const filteredPeoples = computed(() => {
  if (suggestionUsed.value) return []
  const list = props.peoples || []
  if (!inputValue.value.trim()) {
    return list.slice(0, 5)
  }
  return list.filter(
    p => p.nickname && p.nickname.toLowerCase().includes(inputValue.value.trim().toLowerCase())
  )
})

// 输入框回车或点击确认
const handleConfirm = () => {
  if (activeIndex.value >= 0 && filteredPeoples.value.length > 0) {
    inputValue.value = filteredPeoples.value[activeIndex.value].nickname
    suggestionUsed.value = true
    // 不直接提交，等待用户再次确认
    return
  }
  if (inputValue.value.trim()) {
    emit('confirm', inputValue.value.trim())
    inputValue.value = ''
    suggestionUsed.value = false
    emit('close')
  }
}

// 选择某个联想项
const selectSuggestion = (nickname) => {
  inputValue.value = nickname
  suggestionUsed.value = true
  activeIndex.value = -1
}

// 输入变化时重置高亮和提示
watch(inputValue, () => {
  activeIndex.value = -1
  suggestionUsed.value = false
})

// 键盘上下键选择
const handleKeydown = (e) => {
  if (!filteredPeoples.value.length) return
  if (e.key === 'ArrowDown') {
    e.preventDefault()
    activeIndex.value = (activeIndex.value + 1) % filteredPeoples.value.length
  } else if (e.key === 'ArrowUp') {
    e.preventDefault()
    activeIndex.value = (activeIndex.value - 1 + filteredPeoples.value.length) % filteredPeoples.value.length
  } else if (e.key === 'Enter') {
    handleConfirm()
  }
}

// 输入变化时重置高亮
watch(inputValue, () => {
  activeIndex.value = -1
})
</script>
<template>
  <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center backdrop-blur-sm bg-white/30">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-md p-6 border border-gray-200" @click.stop>
      <!-- Header -->
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-lg font-medium flex items-center gap-2">
          <svg class="w-6 h-6"><path fill="currentColor" :d="mdiAccountPlus" /></svg>
          {{ title }}
        </h3>
        <BaseButton :icon="mdiClose" color="whiteDark" small @click="$emit('close')" />
      </div>

      <!-- Input -->
      <div class="mb-4 relative">
        <input
          v-model="inputValue"
          type="text"
          class="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-300 focus:border-blue-300"
          :placeholder="placeholder"
          @keydown="handleKeydown"
          @focus="isInputFocused = true"
          @blur="isInputFocused = false"
        />
        <!-- 联想下拉 -->
        <ul
          v-if="isInputFocused && filteredPeoples.length && inputValue && inputValue.trim() && !suggestionUsed"
          class="absolute left-0 right-0 bg-white border border-gray-200 rounded shadow z-10 mt-1 max-h-40 overflow-auto"
        >
          <li
            v-for="(person, idx) in filteredPeoples"
            :key="person.peopleId"
            :class="[
              'px-3 py-2 cursor-pointer hover:bg-blue-100',
              { 'bg-blue-100': idx === activeIndex }
            ]"
            @mousedown.prevent="selectSuggestion(person.nickname)"
            @mouseenter="activeIndex = idx"
          >
            {{ person.nickname }}
          </li>
        </ul>
      </div>
      <!-- Actions -->
      <div class="flex justify-end gap-2">
        <BaseButton label="Cancel" color="whiteDark" @click="$emit('close')" />
        <BaseButton label="Confirm" color="info" @click="handleConfirm" />
      </div>
    </div>
  </div>
</template>