<template>
  <div class="app">
    <nav class="navbar">
      <div class="container nav-inner">
        <div class="logo">{{ siteTitle }}</div>
        <div class="nav-right">
          <div class="search-wrap">
            <input
              v-model="searchQuery"
              type="text"
              class="nav-search"
              placeholder="站内搜索"
              @keyup.enter="onSearchSubmit"
            >
          </div>
          <ul class="nav-links">
            <li><a href="#" @click.prevent="navigate('home')" :class="{ active: currentPage === 'home' }">首页</a></li>
            <li><a href="#" @click.prevent="navigate('about')" :class="{ active: currentPage === 'about' }">实验室介绍</a></li>
            <li><a href="#" @click.prevent="navigate('news')" :class="{ active: currentPage === 'news' }">新闻</a></li>
            <li><a href="#" @click.prevent="navigate('notice')" :class="{ active: currentPage === 'notice' }">公告</a></li>
            <li><a href="#" @click.prevent="navigate('team')" :class="{ active: currentPage === 'team' }">师资队伍</a></li>
            <li><a href="#" @click.prevent="navigate('achievements')" :class="{ active: currentPage === 'achievements' }">科研成果</a></li>
            <li><a href="#" @click.prevent="navigate('contact')" :class="{ active: currentPage === 'contact' }">联系我们</a></li>
          </ul>
        </div>
      </div>
    </nav>

    <main class="main-content">
      <Home v-if="currentPage === 'home'" />
      <About v-else-if="currentPage === 'about'" />
      <News v-else-if="currentPage === 'news'" />
      <Notice v-else-if="currentPage === 'notice'" />
      <Team v-else-if="currentPage === 'team'" />
      <Achievements v-else-if="currentPage === 'achievements'" :initial-tab="initialParams.achievementsTab" />
      <Contact v-else-if="currentPage === 'contact'" />
      <Search
        v-else-if="currentPage === 'search'"
        :keyword="searchKeyword"
        @search="onSearchKeyword"
        @go="onSearchGo"
      />
      <Detail
        v-else-if="currentPage === 'detail'"
        :type="detailParams.type"
        :id="detailParams.id"
        @back="onDetailBack"
      />
    </main>

    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-links">
            <h4>联系我们</h4>
            <ul class="link-list">
              <li v-if="contact.address" class="contact-info">地址：{{ contact.address }}</li>
              <li v-if="contact.phone" class="contact-info">电话：{{ contact.phone }}</li>
              <li v-if="contact.email" class="contact-info">邮箱：{{ contact.email }}</li>
              <li v-if="contact.workTime" class="contact-info">工作日：{{ contact.workTime }}</li>
            </ul>
          </div>
          <div class="footer-copyright">
            <p>&copy; 2026 跨文化信息分析与智能决策重点实验室</p>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import { apiBase } from './api/config.js'
import Home from './views/Home.vue'
import About from './views/About.vue'
import News from './views/News.vue'
import Notice from './views/Notice.vue'
import Team from './views/Team.vue'
import Achievements from './views/Achievements.vue'
import Contact from './views/Contact.vue'
import Search from './views/Search.vue'
import Detail from './views/Detail.vue'

export default {
  name: 'App',
  components: {
    Home,
    About,
    News,
    Notice,
    Team,
    Achievements,
    Contact,
    Search,
    Detail
  },
  data() {
    return {
      currentPage: 'home',
      siteTitle: '实验室网站',
      searchQuery: '',
      searchKeyword: '',
      initialParams: {
        achievementsTab: null
      },
      detailParams: { type: '', id: null },
      contact: {
        address: '',
        phone: '',
        email: '',
        workTime: ''
      }
    }
  },
  created() {
    const h = this.readPathFromLocation()
    this.currentPage = h.page
    if (h.searchKeyword != null) {
      this.searchKeyword = h.searchKeyword
      this.searchQuery = h.searchKeyword
    }
    if (h.achievementsTab != null) this.initialParams = { achievementsTab: h.achievementsTab }
    if (h.detailType) this.detailParams = { type: h.detailType, id: h.detailId }
  },
  mounted() {
    this.fetchSiteTitle()
    this.fetchContact()
    window.addEventListener('popstate', this.onPopState)
  },
  beforeUnmount() {
    window.removeEventListener('popstate', this.onPopState)
  },
  watch: {
    siteTitle(title) {
      if (title) document.title = title
    }
  },
  methods: {
    readPathFromLocation() {
      const path = (typeof window !== 'undefined' ? window.location.pathname : '').replace(/^\//, '') || 'home'
      const parts = path.split('/').map(p => decodeURIComponent(p))
      let page = parts[0] || 'home'
      let remainingParts = parts
      if (page === 'home' && parts.length > 1) {
        page = parts[1]
        remainingParts = parts.slice(1)
      }
      const validPages = ['home', 'about', 'news', 'notice', 'team', 'achievements', 'contact', 'search', 'detail']
      const res = { page: validPages.includes(page) ? page : 'home', searchKeyword: null, achievementsTab: null, detailType: '', detailId: null }
      if (page === 'search' && remainingParts[1]) res.searchKeyword = remainingParts[1]
      if (page === 'achievements' && remainingParts[1]) res.achievementsTab = remainingParts[1]
      if (page === 'detail' && remainingParts[1] && remainingParts[2]) {
        res.detailType = remainingParts[1]
        const n = parseInt(remainingParts[2], 10)
        res.detailId = isNaN(n) ? null : n
      }
      return res
    },
    onPopState() {
      const h = this.readPathFromLocation()
      this.currentPage = h.page
      if (h.searchKeyword != null) {
        this.searchKeyword = h.searchKeyword
        this.searchQuery = h.searchKeyword
      }
      if (h.achievementsTab != null) this.initialParams = { achievementsTab: h.achievementsTab }
      if (h.detailType) this.detailParams = { type: h.detailType, id: h.detailId }
    },
    writePath(page, params) {
      if (typeof window === 'undefined') return
      let path = 'home/' + (page || 'home')
      if (page === 'search' && params && params.q) path += '/' + encodeURIComponent(params.q)
      if (page === 'achievements' && params && params.tab) path += '/' + encodeURIComponent(params.tab)
      if (page === 'detail' && params && params.type != null && params.id != null) path += '/' + encodeURIComponent(params.type) + '/' + params.id
      window.history.pushState({}, '', '/' + path)
    },
    async fetchSiteTitle() {
      try {
        const res = await fetch(apiBase + '/config/site-title')
        const data = await res.json()
        if (data.code === 200 && data.data) {
          this.siteTitle = data.data
          document.title = data.data
        }
      } catch (e) {
        console.error(e)
      }
    },
    async fetchContact() {
      try {
        const res = await fetch(apiBase + '/home')
        const data = await res.json()
        if (data.code === 200 && data.data && data.data.contact) {
          this.contact = data.data.contact
        }
      } catch (e) {
        console.error(e)
      }
    },
    navigate(page, params) {
      // 滚动到顶部
      window.scrollTo({ top: 0, behavior: 'smooth' })
      
      if (page === 'detail' && params && (params.type != null && params.id != null)) {
        this.currentPage = 'detail'
        this.detailParams = { type: params.type, id: params.id }
        this.writePath('detail', { type: params.type, id: params.id })
        return
      }
      this.currentPage = page
      if (params && typeof params === 'object') {
        this.initialParams = {
          achievementsTab: params.tab ?? params.achievementsTab ?? null
        }
        this.writePath(page, { tab: this.initialParams.achievementsTab })
      } else {
        this.initialParams = { achievementsTab: null }
        this.writePath(page)
      }
    },
    onSearchSubmit() {
      // 滚动到顶部
      window.scrollTo({ top: 0, behavior: 'smooth' })
      
      const q = (this.searchQuery || '').trim()
      if (!q) return
      this.searchKeyword = q
      this.currentPage = 'search'
      this.writePath('search', { q })
    },
    onSearchKeyword(keyword) {
      // 滚动到顶部
      window.scrollTo({ top: 0, behavior: 'smooth' })
      
      this.searchKeyword = keyword
      this.searchQuery = keyword
      this.currentPage = 'search'
      this.writePath('search', { q: keyword })
    },
    onSearchGo(payload) {
      // 滚动到顶部
      window.scrollTo({ top: 0, behavior: 'smooth' })
      
      if (payload.page === 'detail' && payload.type != null && payload.id != null) {
        this.detailParams = { type: payload.type, id: payload.id }
        this.currentPage = 'detail'
        this.writePath('detail', { type: payload.type, id: payload.id })
        return
      }
      this.initialParams = { achievementsTab: payload.tab ?? null }
      this.currentPage = payload.page
      this.writePath(payload.page, { tab: payload.tab })
    },
    onDetailBack(detailType) {
      // 滚动到顶部
      window.scrollTo({ top: 0, behavior: 'smooth' })
      
      if (detailType === 'news') {
        this.currentPage = 'news'
        this.writePath('news')
      } else if (detailType === 'scholar') {
        this.currentPage = 'team'
        this.writePath('team')
      } else if (['paper', 'project', 'award', 'direction'].includes(detailType)) {
        this.initialParams = { achievementsTab: detailType === 'award' ? 'award' : detailType }
        this.currentPage = 'achievements'
        this.writePath('achievements', { tab: this.initialParams.achievementsTab })
      } else {
        this.currentPage = 'home'
        this.writePath('home')
      }
    }
  }
}
</script>

<style>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background: linear-gradient(135deg, #0061a0 0%, #004a7a 100%);
  border-bottom: 1px solid var(--border, #d6e0f0);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 97, 160, 0.2);
}

.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.nav-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 72px;
  padding: 12px 0;
}

.logo {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.02em;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.search-wrap {
  flex-shrink: 0;
}

.nav-search {
  width: 160px;
  padding: 10px 16px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease;
  background-color: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.nav-search:focus {
  border-color: rgba(255, 255, 255, 0.8);
  width: 200px;
  background-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.2);
}

.nav-search::placeholder {
  color: rgba(255, 255, 255, 0.7);
}

.nav-links {
  list-style: none;
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-links a {
  display: block;
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
  border-radius: 8px;
  text-decoration: none;
  position: relative;
}

.nav-links a:hover {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-links a.active {
  color: #fff;
  font-weight: 600;
  background-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.nav-links a.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 16px;
  height: 3px;
  background: #fff;
  border-radius: 3px;
}

.main-content {
  flex: 1;
}

.footer {
  background: linear-gradient(135deg, #0061a0 0%, #004a7a 100%);
  color: rgba(255,255,255,0.9);
  padding: 32px 24px;
  font-size: 13px;
}

.footer-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.footer-links {
  text-align: center;
}

.footer-links h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #fff;
}

.link-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
}

.friend-link {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: color 0.3s ease;
  font-size: 14px;
}

.friend-link:hover {
  color: #fff;
  text-decoration: underline;
}

.contact-info {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  text-align: center;
}

.contact-link {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  transition: color 0.3s ease;
}

.contact-link:hover {
  color: #fff;
  text-decoration: underline;
}

.footer-copyright {
  text-align: center;
  margin-top: 16px;
}

.footer-copyright p {
  margin: 0;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

@media (max-width: 768px) {
  .link-list {
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }
}

@media (max-width: 1200px) {
  .container {
    padding: 0 20px;
  }
}

@media (max-width: 768px) {
  .nav-inner {
    flex-direction: column;
    padding: 16px 0;
    gap: 12px;
  }
  .nav-right {
    flex-direction: column;
    width: 100%;
  }
  .search-wrap {
    width: 100%;
  }
  .nav-search {
    width: 100%;
  }
  .nav-search:focus {
    width: 100%;
  }
  .nav-links {
    flex-wrap: wrap;
    justify-content: center;
  }
  .nav-links li {
    margin: 0;
  }
}
</style>