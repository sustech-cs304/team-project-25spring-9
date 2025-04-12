import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  // 根据是否有 token 初始化认证状态
  const isAuthenticated = ref(!!token.value)

  async function login(credentials) {
    try {
      // Build the URL with query parameters
      const url = new URL('http://10.16.60.67:9091/user/signin')
      url.searchParams.append('userName', credentials.username)
      url.searchParams.append('userPassword', credentials.password)

      // Execute the GET request
      const response = await fetch(url, { method: 'GET' })

      // Parse response JSON
      const result = await response.json()

      // Check if the API returns a successful status code in the JSON response
      if (result.code === 200) {
        // Set a demo token if needed; the API doesn't provide one
        token.value = 'demo-token'
        isAuthenticated.value = true
        localStorage.setItem('token', token.value)
        return result
      } else {
        // The API indicates an error, so throw an error with the message returned
        throw new Error(result.msg || 'Login failed')
      }
    } catch (error) {
      // Propagate the error so that the component can catch it and display a message
      throw error
    }
  }

  function logout() {
    isAuthenticated.value = false
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo') // 清除用户信息
  }

  function checkAuth() {
    return !!token.value
  }

  return { isAuthenticated, token, login, logout, checkAuth }
})
