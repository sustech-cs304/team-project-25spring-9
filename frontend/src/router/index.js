import { createRouter, createWebHashHistory } from 'vue-router'
import Home from '@/views/HomeView.vue'
import RegisterView from '@/views/RegisterView.vue'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    meta: {
      title: 'Dashboard',
      requiresAuth: true,
    },
    path: '/',
    name: 'home',
    component: Home,
  },
  {
    // Document title tag
    // We combine it with defaultDocumentTitle set in `src/main.js` on router.afterEach hook
    meta: {
      title: 'Dashboard',
      requiresAuth: true,
    },
    path: '/dashboard',
    name: 'dashboard',
    component: Home,
  },
  {
    meta: {
      title: 'Photos',
      requiresAuth: true,
    },
    path: '/photos',
    name: 'photos',
    component: () => import('@/views/PhotosView.vue'),
  },
  {
    meta: {
      title: 'Albums',
      requiresAuth: true,
    },
    path: '/albums',
    name: 'albums',
    component: () => import('@/views/AlbumsView.vue'),
  },
  {
    meta: {
      title: 'Timeline',
      requiresAuth: true,
    },
    path: '/timeline',
    name: 'timeline',
    component: () => import('@/views/TimelineView.vue'),
  },
  {
    meta: {
      title: 'Forms',
      requiresAuth: true,
    },
    path: '/forms',
      name: 'forms',
    component: () => import('@/views/FormsView.vue'),
  },
  {
    meta: {
      title: 'Profile',
      requiresAuth: true,
    },
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/ProfileView.vue'),
  },
  {
    meta: {
      title: 'Style',
    },
    path: '/style',
    name: 'style',
    component: () => import('@/views/StyleView.vue'),
  },
  {
    meta: {
      title: 'Login',
    },
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: {
      title: 'User Registration'
    }
  },
  {
    meta: {
      title: 'Error',
    },
    path: '/error',
    name: 'error',
    component: () => import('@/views/ErrorView.vue'),
  },
  {
    meta: {
      title: 'Tags',
      requiresAuth: true
    },
    path: '/tags',
    name: 'tags',
    component: () => import('../views/TagsView.vue')
  },
  {
    meta: {
      title: 'Shared Photo'
    },
    path: '/share/:userId/:photoId',
    name: 'share',
    component: () => import('@/views/ShareView.vue')
  },
  {
    meta: {
      title: 'Shared Photo'
    },
    path: '/share/photo/:userId/:photoId',
    name: 'sharePhoto',
    component: () => import('@/views/ShareView.vue'),
  },
  {
    meta: {
      title: 'Shared Album'
    },
    path: '/share/album/:userId/:albumId',
    name: 'shareAlbum',
    component: () => import('@/views/ShareView.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { top: 0 }
  },
})

// Add navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth && !authStore.checkAuth()) {
    next('/login')
  } else {
    next()
  }
})

export default router
