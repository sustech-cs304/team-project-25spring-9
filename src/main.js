import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useMainStore } from '@/stores/main.js'
import { useAuthStore } from '@/stores/auth.js'

import './css/main.css'

// Init Pinia
const pinia = createPinia()

// Create Vue app
createApp(App).use(router).use(pinia).mount('#app')

// Init stores
const mainStore = useMainStore(pinia)
const authStore = useAuthStore(pinia)

// 可选：添加路由守卫检查登录状态
router.beforeEach((to, from, next) => {
  const isAuthenticated = authStore.isAuthenticated

  // 如果路由需要认证且用户未登录
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

// Default title tag
const defaultDocumentTitle = 'Smart Album'

// Set document title from route meta
router.afterEach((to) => {
  document.title = to.meta?.title
    ? `${to.meta.title} — ${defaultDocumentTitle}`
    : defaultDocumentTitle
})
