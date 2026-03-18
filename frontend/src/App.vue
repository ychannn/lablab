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
            <li><a href="#" @click.prevent="navigate('home')">首页</a></li>
            <li><a href="#" @click.prevent="navigate('about')">实验室介绍</a></li>
            <li><a href="#" @click.prevent="navigate('news')">新闻公告</a></li>
            <li><a href="#" @click.prevent="navigate('team')">师资队伍</a></li>
            <li><a href="#" @click.prevent="navigate('achievements')">科研成果</a></li>
            <li><a href="#" @click.prevent="navigate('contact')">联系我们</a></li>
          </ul>
        </div>
      </div>
    </nav>

    <main class="main-content">
      <Home v-if="currentPage === 'home'" />
      <About v-else-if="currentPage === 'about'" />
      <News v-else-if="currentPage === 'news'" />
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
        <p>&copy; 2026 {{ siteTitle }}</p>
      </div>
    </footer>
  </div>
</template>

<script>
import Home from './views/Home.vue'
import About from './views/About.vue'
import News from './views/News.vue'
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
      detailParams: { type: '', id: null }
    }
  },
  created() {
    const h = this.readHashFromLocation()
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
    window.addEventListener('hashchange', this.onHashChange)
  },
  beforeUnmount() {
    window.removeEventListener('hashchange', this.onHashChange)
  },
  watch: {
    siteTitle(title) {
      if (title) document.title = title
    }
  },
  methods: {
    readHashFromLocation() {
      const hash = (typeof window !== 'undefined' ? window.location.hash : '').replace(/^#/, '') || 'home'
      const parts = hash.split('/').map(p => decodeURIComponent(p))
      const page = parts[0] || 'home'
      const validPages = ['home', 'about', 'news', 'team', 'achievements', 'contact', 'search', 'detail']
      const res = { page: validPages.includes(page) ? page : 'home', searchKeyword: null, achievementsTab: null, detailType: '', detailId: null }
      if (page === 'search' && parts[1]) res.searchKeyword = parts[1]
      if (page === 'achievements' && parts[1]) res.achievementsTab = parts[1]
      if (page === 'detail' && parts[1] && parts[2]) {
        res.detailType = parts[1]
        const n = parseInt(parts[2], 10)
        res.detailId = isNaN(n) ? null : n
      }
      return res
    },
    onHashChange() {
      const h = this.readHashFromLocation()
      this.currentPage = h.page
      if (h.searchKeyword != null) {
        this.searchKeyword = h.searchKeyword
        this.searchQuery = h.searchKeyword
      }
      if (h.achievementsTab != null) this.initialParams = { achievementsTab: h.achievementsTab }
      if (h.detailType) this.detailParams = { type: h.detailType, id: h.detailId }
    },
    writeHash(page, params) {
      if (typeof window === 'undefined') return
      let hash = page || 'home'
      if (page === 'search' && params && params.q) hash += '/' + encodeURIComponent(params.q)
      if (page === 'achievements' && params && params.tab) hash += '/' + encodeURIComponent(params.tab)
      if (page === 'detail' && params && params.type != null && params.id != null) hash += '/' + encodeURIComponent(params.type) + '/' + params.id
      window.location.hash = hash
    },
    async fetchSiteTitle() {
      try {
        const res = await fetch('http://localhost:8080/api/config/site-title')
        const data = await res.json()
        if (data.code === 200 && data.data) {
          this.siteTitle = data.data
          document.title = data.data
        }
      } catch (e) {
        console.error(e)
      }
    },
    navigate(page, params) {
      if (page === 'detail' && params && (params.type != null && params.id != null)) {
        this.currentPage = 'detail'
        this.detailParams = { type: params.type, id: params.id }
        this.writeHash('detail', { type: params.type, id: params.id })
        return
      }
      this.currentPage = page
      if (params && typeof params === 'object') {
        this.initialParams = {
          achievementsTab: params.tab ?? params.achievementsTab ?? null
        }
        this.writeHash(page, { tab: this.initialParams.achievementsTab })
      } else {
        this.initialParams = { achievementsTab: null }
        this.writeHash(page)
      }
    },
    onSearchSubmit() {
      const q = (this.searchQuery || '').trim()
      if (!q) return
      this.searchKeyword = q
      this.currentPage = 'search'
      this.writeHash('search', { q })
    },
    onSearchKeyword(keyword) {
      this.searchKeyword = keyword
      this.searchQuery = keyword
      this.currentPage = 'search'
      this.writeHash('search', { q: keyword })
    },
    onSearchGo(payload) {
      if (payload.page === 'detail' && payload.type != null && payload.id != null) {
        this.detailParams = { type: payload.type, id: payload.id }
        this.currentPage = 'detail'
        this.writeHash('detail', { type: payload.type, id: payload.id })
        return
      }
      this.initialParams = { achievementsTab: payload.tab ?? null }
      this.currentPage = payload.page
      this.writeHash(payload.page, { tab: payload.tab })
    },
    onDetailBack(detailType) {
      if (detailType === 'news') {
        this.currentPage = 'news'
        this.writeHash('news')
      } else if (detailType === 'scholar') {
        this.currentPage = 'team'
        this.writeHash('team')
      } else if (['paper', 'project', 'award', 'direction'].includes(detailType)) {
        this.initialParams = { achievementsTab: detailType === 'award' ? 'award' : detailType }
        this.currentPage = 'achievements'
        this.writeHash('achievements', { tab: this.initialParams.achievementsTab })
      } else {
        this.currentPage = 'home'
        this.writeHash('home')
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
  background-color: #fff;
  border-bottom: 1px solid var(--border, #dde8e4);
  position: sticky;
  top: 0;
  z-index: 100;
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
  min-height: 64px;
}

.logo {
  font-size: 20px;
  font-weight: 600;
  color: var(--accent, #2d9d78);
  letter-spacing: 0.02em;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-wrap {
  flex-shrink: 0;
}

.nav-search {
  width: 140px;
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 13px;
  outline: none;
  transition: border-color 0.2s, width 0.2s;
}

.nav-search:focus {
  border-color: var(--accent, #2d9d78);
  width: 180px;
}

.nav-search::placeholder {
  color: #999;
}

.nav-links {
  list-style: none;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-links a {
  display: block;
  padding: 10px 16px;
  color: var(--text-muted, #5a6c7d);
  font-size: 14px;
  font-weight: 500;
  transition: color 0.2s;
}

.nav-links a:hover {
  color: var(--accent, #2d9d78);
}

.main-content {
  flex: 1;
}

.footer {
  background: linear-gradient(135deg, #2d9d78 0%, #248f6a 100%);
  color: rgba(255,255,255,0.9);
  text-align: center;
  padding: 32px 24px;
  font-size: 13px;
}

.footer p {
  margin: 0;
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