import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useMainStore = defineStore('main', () => {
  // 从 localStorage 获取初始用户信息 (如果有的话)
  const savedUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

  const userName = ref(savedUserInfo.name || '')
  const userEmail = ref(savedUserInfo.email || '')
  const userId = ref(savedUserInfo.id || null)

  const userAvatar = computed(
    () =>
      `https://api.dicebear.com/9.x/lorelei-neutral/svg?seed=${userName.value.replace(
        /[^a-z0-9]+/gi,
        '-',
      )}`,
  )

  const isFieldFocusRegistered = ref(false)

  const clients = ref([])
  const history = ref([])

  function setUser(payload) {
    if (payload.name) {
      userName.value = payload.name
    }
    if (payload.email) {
      userEmail.value = payload.email
    }
    if (payload.id) {
      userId.value = payload.id
    }

    // 保存用户信息到 localStorage
    localStorage.setItem(
      'userInfo',
      JSON.stringify({
        name: userName.value,
        email: userEmail.value,
        id: userId.value,
      }),
    )
  }

  function fetchSampleClients() {
    axios
      .get(`data-sources/clients.json?v=3`)
      .then((result) => {
        clients.value = result?.data?.data
      })
      .catch((error) => {
        alert(error.message)
      })
  }

  function fetchSampleHistory() {
    axios
      .get(`data-sources/history.json`)
      .then((result) => {
        history.value = result?.data?.data
      })
      .catch((error) => {
        alert(error.message)
      })
  }

  return {
    userName,
    userEmail,
    userId,
    userAvatar,
    isFieldFocusRegistered,
    clients,
    history,
    setUser,
    fetchSampleClients,
    fetchSampleHistory,
  }
})
