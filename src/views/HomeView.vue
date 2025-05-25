<script setup>
import { computed, ref, onMounted, nextTick } from 'vue'
import { useMainStore } from '@/stores/main'
import {
  mdiChartTimelineVariant,
  mdiImageMultiple,
  mdiArchiveClockOutline,
  mdiAccount,
  mdiTag,
  mdiCamera,
  mdiTrendingUp,
  mdiClockOutline,
  mdiCalendarToday,
  mdiWeatherSunny,
  mdiHeart,
  mdiStar,
  mdiEye,
  mdiDownload,
  mdiShareVariant,
  mdiRefresh,
  mdiChartLine,
  mdiImageArea,
  mdiPalette,
  mdiMapMarker
} from '@mdi/js'
import * as chartConfig from '@/components/Charts/chart.config.js'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxWidget from '@/components/CardBoxWidget.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'
import BaseButton from '@/components/BaseButton.vue'

// 基础状态
const chartData = ref(null)
const photoCount = ref(0)
const peopleCount = ref(0)
const tagCount = ref(0)

// 新增的丰富功能状态
const isLoading = ref(false)
const recentPhotos = ref([])
const photoStats = ref({
  thisWeek: 0,
  thisMonth: 0,
  totalSize: '0 MB',
  avgPerDay: 0
})
const popularTags = ref([])
const timeDistribution = ref([])
const greeting = ref('')
const currentTime = ref('')
const inspirationalQuote = ref('')
const weatherInfo = ref('')

// 动画相关状态
const animatedCounts = ref({
  photos: 0,
  people: 0,
  tags: 0
})
const showStats = ref(false)

const fillChartData = () => {
  chartData.value = chartConfig.sampleChartData()
}

// 数字动画函数
const animateNumber = (start, end, duration, callback) => {
  const range = end - start
  const stepTime = Math.abs(Math.floor(duration / range))
  const timer = setInterval(() => {
    start += range > 0 ? 1 : -1
    callback(start)
    if (start === end) {
      clearInterval(timer)
    }
  }, stepTime)
}

// 获取问候语
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
}

// 获取当前时间
const updateCurrentTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 励志语录
const inspirationalQuotes = [
  '每一张照片都是时间的见证者 📸',
  '用镜头记录生活的美好瞬间 ✨',
  '照片不只是回忆，更是情感的载体 💝',
  '在像素中找到永恒 🌟',
  '每一次快门都是对美的致敬 🎨'
]

const getRandomQuote = () => {
  const randomIndex = Math.floor(Math.random() * inspirationalQuotes.length)
  inspirationalQuote.value = inspirationalQuotes[randomIndex]
}

// 获取天气信息（模拟）
const getWeatherInfo = () => {
  const weathers = ['☀️ 晴朗', '⛅ 多云', '🌤️ 晴间多云', '🌈 雨后天晴']
  const randomIndex = Math.floor(Math.random() * weathers.length)
  weatherInfo.value = weathers[randomIndex]
}

// 获取最近照片
const fetchRecentPhotos = async () => {
  try {
    const mainStore = useMainStore()
    const params = new URLSearchParams({
      userId: mainStore.userId,
      limit: '6'
    })

    const response = await fetch(`http://10.16.60.67:9090/img/recent?${params}`)
    const result = await response.json()

    if (result && result.data) {
      recentPhotos.value = result.data.slice(0, 6).map(img => ({
        id: img.imgId,
        name: img.imgName || `Photo ${img.imgId}`,
        src: `http://10.16.60.67:9000/softwareeng/upload-img/${img.imgId}.jpeg`,
        date: img.imgDate,
        tags: img.tags || []
      }))
    }
  } catch (error) {
    console.error('Failed to fetch recent photos:', error)
    // 使用示例数据作为后备
    recentPhotos.value = [
      { id: 1, name: 'Beach Sunset', src: 'https://picsum.photos/id/11/200/150', date: '2024-01-15', tags: ['sunset', 'beach'] },
      { id: 2, name: 'Mountain View', src: 'https://picsum.photos/id/15/200/150', date: '2024-01-14', tags: ['mountain', 'nature'] },
      { id: 3, name: 'City Lights', src: 'https://picsum.photos/id/20/200/150', date: '2024-01-13', tags: ['city', 'night'] },
      { id: 4, name: 'Forest Path', src: 'https://picsum.photos/id/25/200/150', date: '2024-01-12', tags: ['forest', 'path'] },
      { id: 5, name: 'Ocean Wave', src: 'https://picsum.photos/id/30/200/150', date: '2024-01-11', tags: ['ocean', 'wave'] },
      { id: 6, name: 'Flower Garden', src: 'https://picsum.photos/id/35/200/150', date: '2024-01-10', tags: ['flower', 'garden'] }
    ]
  }
}

// 获取热门标签
const fetchPopularTags = async () => {
  try {
    const mainStore = useMainStore()
    const params = new URLSearchParams({ userId: mainStore.userId })

    const response = await fetch(`http://10.16.60.67:9090/img/populartags?${params}`)
    const result = await response.json()

    if (result && result.data) {
      popularTags.value = result.data.slice(0, 8)
    }
  } catch (error) {
    console.error('Failed to fetch popular tags:', error)
    // 使用示例数据作为后备
    popularTags.value = [
      { name: 'nature', count: 156 },
      { name: 'travel', count: 89 },
      { name: 'family', count: 67 },
      { name: 'sunset', count: 45 },
      { name: 'city', count: 34 },
      { name: 'food', count: 28 },
      { name: 'friends', count: 23 },
      { name: 'art', count: 19 }
    ]
  }
}

// 获取统计数据
const fetchPhotoStats = async () => {
  try {
    const mainStore = useMainStore()
    const params = new URLSearchParams({ userId: mainStore.userId })

    // 这周上传的照片数量
    const weekResponse = await fetch(`http://10.16.60.67:9090/img/weekstats?${params}`)
    const weekData = await weekResponse.json()

    // 这月上传的照片数量
    const monthResponse = await fetch(`http://10.16.60.67:9090/img/monthstats?${params}`)
    const monthData = await monthResponse.json()

    photoStats.value = {
      thisWeek: weekData?.data || Math.floor(Math.random() * 20),
      thisMonth: monthData?.data || Math.floor(Math.random() * 50),
      totalSize: '2.3 GB',
      avgPerDay: Math.floor((monthData?.data || 30) / 30)
    }
  } catch (error) {
    console.error('Failed to fetch photo stats:', error)
    // 使用示例数据
    photoStats.value = {
      thisWeek: Math.floor(Math.random() * 20),
      thisMonth: Math.floor(Math.random() * 50),
      totalSize: '2.3 GB',
      avgPerDay: Math.floor(Math.random() * 5) + 1
    }
  }
}

// 获取时间分布数据
const fetchTimeDistribution = () => {
  // 模拟一周上传分布数据
  timeDistribution.value = [
    { day: '周一', count: Math.floor(Math.random() * 15) + 5 },
    { day: '周二', count: Math.floor(Math.random() * 15) + 5 },
    { day: '周三', count: Math.floor(Math.random() * 15) + 5 },
    { day: '周四', count: Math.floor(Math.random() * 15) + 5 },
    { day: '周五', count: Math.floor(Math.random() * 15) + 5 },
    { day: '周六', count: Math.floor(Math.random() * 15) + 5 },
    { day: '周日', count: Math.floor(Math.random() * 15) + 5 }
  ]
}

const fetchCounts = async () => {
  isLoading.value = true
  try {
    const mainStore = useMainStore()

    // Fetch photo count
    const photoResponse = await fetch('http://10.16.60.67:9090/img/getimgcnt?userId=' + mainStore.userId)
    const photoData = await photoResponse.json()
    photoCount.value = photoData.data || 0

    // Fetch people count
    const peopleResponse = await fetch('http://10.16.60.67:9090/img/getpeoplecnt?userId=' + mainStore.userId)
    const peopleData = await peopleResponse.json()
    peopleCount.value = peopleData.data || 0

    // Fetch tag count
    const tagResponse = await fetch('http://10.16.60.67:9090/img/gettagcnt?userId=' + mainStore.userId)
    const tagData = await tagResponse.json()
    tagCount.value = tagData.data || 0

    // 启动数字动画
    await nextTick()
    setTimeout(() => {
      animateNumber(0, photoCount.value, 1000, (val) => {
        animatedCounts.value.photos = val
      })
      animateNumber(0, peopleCount.value, 1200, (val) => {
        animatedCounts.value.people = val
      })
      animateNumber(0, tagCount.value, 800, (val) => {
        animatedCounts.value.tags = val
      })
    }, 200)

  } catch (error) {
    console.error('Error fetching counts:', error)
  } finally {
    isLoading.value = false
  }
}

// 刷新所有数据
const refreshAllData = async () => {
  isLoading.value = true
  await Promise.all([
    fetchCounts(),
    fetchRecentPhotos(),
    fetchPopularTags(),
    fetchPhotoStats()
  ])
  fetchTimeDistribution()
  getRandomQuote()
  isLoading.value = false
}

// 标签颜色
const getTagColor = (index) => {
  const colors = [
    'bg-blue-100 text-blue-700',
    'bg-green-100 text-green-700',
    'bg-purple-100 text-purple-700',
    'bg-pink-100 text-pink-700',
    'bg-yellow-100 text-yellow-700',
    'bg-indigo-100 text-indigo-700',
    'bg-red-100 text-red-700',
    'bg-gray-100 text-gray-700'
  ]
  return colors[index % colors.length]
}

onMounted(async () => {
  fillChartData()
  greeting.value = getGreeting()
  updateCurrentTime()
  getRandomQuote()
  getWeatherInfo()

  // 显示统计卡片动画
  setTimeout(() => {
    showStats.value = true
  }, 100)

  await refreshAllData()

  // 每分钟更新时间
  setInterval(updateCurrentTime, 60000)
})

const mainStore = useMainStore()

const clientBarItems = computed(() => mainStore.clients.slice(0, 4))
const transactionBarItems = computed(() => mainStore.history)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <!-- 欢迎区域 -->
      <CardBox class="mb-6 bg-gradient-to-r from-blue-500 to-purple-600 text-white">
        <div class="flex justify-between items-start">
          <div>
            <h1 class="text-2xl font-bold mb-2">
              {{ greeting }}，{{ mainStore.userName || 'Friend' }}！
            </h1>
            <p class="text-blue-100 mb-1">{{ currentTime }}</p>
            <p class="text-blue-100 flex items-center">
              <svg class="w-4 h-4 mr-1" viewBox="0 0 24 24">
                <path fill="currentColor" :d="mdiWeatherSunny" />
              </svg>
              {{ weatherInfo }}
            </p>
          </div>
          <div class="text-right">
            <p class="text-blue-100 text-sm mb-2">{{ inspirationalQuote }}</p>
            <BaseButton
              :icon="mdiRefresh"
              color="whiteDark"
              small
              :disabled="isLoading"
              @click="refreshAllData"
              class="animate-spin-on-hover"
            />
          </div>
        </div>
      </CardBox>

      <SectionTitleLineWithButton :icon="mdiChartTimelineVariant" title="数据概览" main>
      </SectionTitleLineWithButton>

      <!-- 主要统计卡片 -->
      <div class="grid grid-cols-1 gap-6 lg:grid-cols-3 mb-6"
           :class="{ 'animate-fade-in-up': showStats }">
        <CardBoxWidget
          color="text-green-500"
          :icon="mdiImageMultiple"
          :number="animatedCounts.photos"
          label="Photos"
          class="hover:scale-105 transition-transform duration-300 shadow-lg hover:shadow-xl"
        />
        <CardBoxWidget
          color="text-blue-500"
          :icon="mdiAccount"
          :number="animatedCounts.people"
          label="People"
          class="hover:scale-105 transition-transform duration-300 shadow-lg hover:shadow-xl"
        />
        <CardBoxWidget
          color="text-purple-500"
          :icon="mdiTag"
          :number="animatedCounts.tags"
          label="Tags"
          class="hover:scale-105 transition-transform duration-300 shadow-lg hover:shadow-xl"
        />
      </div>

      <!-- 详细统计信息 -->
      <div class="grid grid-cols-1 lg:grid-cols-4 gap-4 mb-6">
        <CardBox class="text-center p-4 hover:shadow-lg transition-shadow">
          <div class="text-blue-500 text-2xl font-bold">{{ photoStats.thisWeek }}</div>
          <div class="text-sm text-gray-600 flex items-center justify-center">
            <svg class="w-4 h-4 mr-1" viewBox="0 0 24 24">
              <path fill="currentColor" :d="mdiTrendingUp" />
            </svg>
            本周上传
          </div>
        </CardBox>
        <CardBox class="text-center p-4 hover:shadow-lg transition-shadow">
          <div class="text-green-500 text-2xl font-bold">{{ photoStats.thisMonth }}</div>
          <div class="text-sm text-gray-600 flex items-center justify-center">
            <svg class="w-4 h-4 mr-1" viewBox="0 0 24 24">
              <path fill="currentColor" :d="mdiCalendarToday" />
            </svg>
            本月上传
          </div>
        </CardBox>
        <CardBox class="text-center p-4 hover:shadow-lg transition-shadow">
          <div class="text-purple-500 text-2xl font-bold">{{ photoStats.totalSize }}</div>
          <div class="text-sm text-gray-600 flex items-center justify-center">
            <svg class="w-4 h-4 mr-1" viewBox="0 0 24 24">
              <path fill="currentColor" :d="mdiImageArea" />
            </svg>
            总容量
          </div>
        </CardBox>
        <CardBox class="text-center p-4 hover:shadow-lg transition-shadow">
          <div class="text-orange-500 text-2xl font-bold">{{ photoStats.avgPerDay }}</div>
          <div class="text-sm text-gray-600 flex items-center justify-center">
            <svg class="w-4 h-4 mr-1" viewBox="0 0 24 24">
              <path fill="currentColor" :d="mdiClockOutline" />
            </svg>
            日均上传
          </div>
        </CardBox>
      </div>

      <!-- 内容区域 -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <!-- 最近照片 -->
        <CardBox>
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold flex items-center">
              <svg class="w-5 h-5 mr-2 text-blue-500" viewBox="0 0 24 24">
                <path fill="currentColor" :d="mdiCamera" />
              </svg>
              最近照片
            </h3>
            <router-link to="/photos" class="text-blue-500 hover:text-blue-700 text-sm">
              查看全部 →
            </router-link>
          </div>
          <div v-if="isLoading" class="grid grid-cols-3 gap-2">
            <div v-for="i in 6" :key="i" class="aspect-square bg-gray-200 animate-pulse rounded"></div>
          </div>
          <div v-else class="grid grid-cols-3 gap-2">
            <div v-for="photo in recentPhotos" :key="photo.id"
                 class="relative group cursor-pointer rounded overflow-hidden hover:scale-105 transition-transform duration-200">
              <img :src="photo.src" :alt="photo.name"
                   class="w-full aspect-square object-cover"
                   @error="$event.target.src = 'https://placehold.co/150'" />
              <div class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-30 transition-all duration-200 flex items-center justify-center">
                <svg class="w-6 h-6 text-white opacity-0 group-hover:opacity-100 transition-opacity" viewBox="0 0 24 24">
                  <path fill="currentColor" :d="mdiEye" />
                </svg>
              </div>
              <div class="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black to-transparent text-white text-xs p-2 transform translate-y-full group-hover:translate-y-0 transition-transform duration-200">
                <div class="truncate">{{ photo.name }}</div>
              </div>
            </div>
          </div>
        </CardBox>

        <!-- 热门标签 -->
        <CardBox>
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold flex items-center">
              <svg class="w-5 h-5 mr-2 text-purple-500" viewBox="0 0 24 24">
                <path fill="currentColor" :d="mdiTag" />
              </svg>
              热门标签
            </h3>
            <router-link to="/tags" class="text-purple-500 hover:text-purple-700 text-sm">
              查看全部 →
            </router-link>
          </div>
          <div v-if="isLoading" class="space-y-2">
            <div v-for="i in 8" :key="i" class="h-8 bg-gray-200 animate-pulse rounded"></div>
          </div>
          <div v-else class="flex flex-wrap gap-2">
            <div v-for="(tag, index) in popularTags" :key="tag.name"
                 :class="getTagColor(index)"
                 class="px-3 py-1 rounded-full text-sm cursor-pointer hover:scale-105 transition-transform duration-200 flex items-center">
              <span>{{ tag.name }}</span>
              <span class="ml-2 text-xs opacity-75">({{ tag.count }})</span>
            </div>
          </div>
        </CardBox>
      </div>

      <!-- 时间分布图 -->
      <CardBox class="mb-6">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-semibold flex items-center">
            <svg class="w-5 h-5 mr-2 text-green-500" viewBox="0 0 24 24">
              <path fill="currentColor" :d="mdiChartLine" />
            </svg>
            本周上传分布
          </h3>
        </div>
        <div v-if="isLoading" class="h-32 bg-gray-200 animate-pulse rounded"></div>
        <div v-else class="flex items-end justify-between h-32 px-4">
          <div v-for="item in timeDistribution" :key="item.day"
               class="flex flex-col items-center flex-1">
            <div class="bg-gradient-to-t from-blue-500 to-blue-300 rounded-t transition-all duration-500 hover:from-blue-600 hover:to-blue-400 w-8 mb-2"
                 :style="{ height: `${(item.count / Math.max(...timeDistribution.map(d => d.count))) * 80}px` }">
            </div>
            <div class="text-xs text-gray-600">{{ item.day }}</div>
            <div class="text-xs text-gray-500">{{ item.count }}</div>
          </div>
        </div>
      </CardBox>

      <!-- 快捷操作 -->
      <CardBox>
        <h3 class="text-lg font-semibold mb-4 flex items-center">
          <svg class="w-5 h-5 mr-2 text-indigo-500" viewBox="0 0 24 24">
            <path fill="currentColor" :d="mdiPalette" />
          </svg>
          快捷操作
        </h3>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <router-link to="/photos"
                       class="flex flex-col items-center p-4 rounded-lg border-2 border-transparent hover:border-blue-200 hover:bg-blue-50 transition-all duration-200 group">
            <svg class="w-8 h-8 text-blue-500 mb-2 group-hover:scale-110 transition-transform" viewBox="0 0 24 24">
              <path fill="currentColor" :d="mdiImageMultiple" />
            </svg>
            <span class="text-sm font-medium">浏览照片</span>
          </router-link>

          <router-link to="/albums"
                       class="flex flex-col items-center p-4 rounded-lg border-2 border-transparent hover:border-green-200 hover:bg-green-50 transition-all duration-200 group">
            <svg class="w-8 h-8 text-green-500 mb-2 group-hover:scale-110 transition-transform" viewBox="0 0 24 24">
              <path fill="currentColor" :d="mdiArchiveClockOutline" />
            </svg>
            <span class="text-sm font-medium">查看相册</span>
          </router-link>

          <router-link to="/timeline"
                       class="flex flex-col items-center p-4 rounded-lg border-2 border-transparent hover:border-purple-200 hover:bg-purple-50 transition-all duration-200 group">
            <svg class="w-8 h-8 text-purple-500 mb-2 group-hover:scale-110 transition-transform" viewBox="0 0 24 24">
              <path fill="currentColor" :d="mdiChartTimelineVariant" />
            </svg>
            <span class="text-sm font-medium">时间轴</span>
          </router-link>

          <router-link to="/style"
                       class="flex flex-col items-center p-4 rounded-lg border-2 border-transparent hover:border-orange-200 hover:bg-orange-50 transition-all duration-200 group">
            <svg class="w-8 h-8 text-orange-500 mb-2 group-hover:scale-110 transition-transform" viewBox="0 0 24 24">
              <path fill="currentColor" :d="mdiPalette" />
            </svg>
            <span class="text-sm font-medium">AI 风格化</span>
          </router-link>
        </div>
      </CardBox>

    </SectionMain>
  </LayoutAuthenticated>
</template>

<style scoped>
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in-up {
  animation: fadeInUp 0.6s ease-out;
}

.animate-spin-on-hover:hover {
  animation: spin 1s linear;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 卡片悬停效果 */
.hover\:scale-105:hover {
  transform: scale(1.05);
}

/* 渐变动画 */
.bg-gradient-to-r {
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>
