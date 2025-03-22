import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref(false)
  const token = ref(localStorage.getItem('token') || null)
  
  function login(credentials) {
    // For a real implementation, you would make an API call here
    return new Promise((resolve) => {
      // Simulate API delay
      setTimeout(() => {
        isAuthenticated.value = true
        token.value = 'demo-token'
        localStorage.setItem('token', token.value)
        resolve(true)
      }, 500)
    })
  }
  
  function logout() {
    isAuthenticated.value = false
    token.value = null
    localStorage.removeItem('token')
  }
  
  function checkAuth() {
    return !!token.value
  }
  
  return { isAuthenticated, token, login, logout, checkAuth }
})