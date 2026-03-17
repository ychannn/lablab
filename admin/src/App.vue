<template>
  <div class="admin-app">
    <Login v-if="showLogin" @login-success="showLogin = false" />
    <template v-else>
      <header class="admin-header">
        <div class="header-inner">
          <h1 class="logo">后台管理</h1>
          <div class="header-actions">
            <a :href="portalUrl" class="link-portal" target="_blank" rel="noopener">访问门户</a>
            <button type="button" class="btn-logout" @click="logout">退出</button>
          </div>
        </div>
      </header>
      <div class="admin-body">
        <aside class="admin-sidebar">
          <nav class="sidebar-nav">
            <div class="nav-group">站点配置</div>
            <a v-for="item in menus" :key="item.key" href="javascript:;" :class="{ active: currentMenu === item.key }" @click.prevent="currentMenu = item.key">{{ item.label }}</a>
            <div class="nav-group">业务管理</div>
            <a v-for="item in crudMenus" :key="item.key" href="javascript:;" :class="{ active: currentMenu === item.key }" @click.prevent="currentMenu = item.key">{{ item.label }}</a>
          </nav>
        </aside>
        <main class="admin-main">
          <SiteSettings v-show="currentMenu === 'site'" />
          <BannerManage v-show="currentMenu === 'banner'" />
          <ContactManage v-show="currentMenu === 'contact'" />
          <LabIntroManage v-show="currentMenu === 'lab-intro'" />
          <LabNewsCrud v-show="currentMenu === 'lab-news'" />
          <PublicationCrud v-show="currentMenu === 'publication'" />
          <TopicProjectCrud v-show="currentMenu === 'topic-project'" />
          <AchievementCrud v-show="currentMenu === 'achievement'" />
          <DirectionCrud v-show="currentMenu === 'direction'" />
          <ScholarCrud v-show="currentMenu === 'scholar'" />
          <AreaCrud v-show="currentMenu === 'area'" />
        </main>
      </div>
    </template>
  </div>
</template>

<script>
import { isLoggedIn, setToken } from './api/auth'
import Login from './views/Login.vue'
import SiteSettings from './views/SiteSettings.vue'
import BannerManage from './views/BannerManage.vue'
import ContactManage from './views/ContactManage.vue'
import LabIntroManage from './views/LabIntroManage.vue'
import LabNewsCrud from './views/crud/LabNewsCrud.vue'
import PublicationCrud from './views/crud/PublicationCrud.vue'
import TopicProjectCrud from './views/crud/TopicProjectCrud.vue'
import AchievementCrud from './views/crud/AchievementCrud.vue'
import DirectionCrud from './views/crud/DirectionCrud.vue'
import ScholarCrud from './views/crud/ScholarCrud.vue'
import AreaCrud from './views/crud/AreaCrud.vue'

export default {
  name: 'AdminApp',
  components: {
    Login,
    SiteSettings,
    BannerManage,
    ContactManage,
    LabIntroManage,
    LabNewsCrud,
    PublicationCrud,
    TopicProjectCrud,
    AchievementCrud,
    DirectionCrud,
    ScholarCrud,
    AreaCrud
  },
  data() {
    return {
      showLogin: !isLoggedIn(),
      portalUrl: 'http://localhost:5173',
      currentMenu: 'site',
      menus: [
        { key: 'site', label: '站点设置' },
        { key: 'banner', label: '首页轮播图' },
        { key: 'contact', label: '联系我们' },
        { key: 'lab-intro', label: '实验室介绍' }
      ],
      crudMenus: [
        { key: 'lab-news', label: '新闻公告' },
        { key: 'publication', label: '论文发表' },
        { key: 'topic-project', label: '课题项目' },
        { key: 'achievement', label: '成果奖项' },
        { key: 'direction', label: '研究方向' },
        { key: 'scholar', label: '师资队伍' },
        { key: 'area', label: '区域管理' }
      ]
    }
  },
  mounted() {
    window.addEventListener('admin-logout', this.onLogout)
  },
  beforeUnmount() {
    window.removeEventListener('admin-logout', this.onLogout)
  },
  methods: {
    logout() {
      setToken(null)
      this.showLogin = true
    },
    onLogout() {
      this.showLogin = true
    }
  }
}
</script>

<style>
.admin-app { min-height: 100vh; display: flex; flex-direction: column; background: #f0f2f5; }
.admin-header {
  background: #001529;
  color: #fff;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.header-inner {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
}
.admin-header .logo { font-size: 18px; font-weight: 600; }
.header-actions { display: flex; align-items: center; gap: 16px; }
.link-portal { color: #fff; opacity: 0.85; text-decoration: none; font-size: 14px; }
.link-portal:hover { opacity: 1; }
.btn-logout { background: transparent; border: 1px solid rgba(255,255,255,0.5); color: #fff; padding: 4px 12px; border-radius: 4px; cursor: pointer; font-size: 14px; }
.btn-logout:hover { background: rgba(255,255,255,0.1); }

.admin-body { flex: 1; display: flex; overflow: hidden; }
.admin-sidebar {
  width: 220px;
  background: #fff;
  box-shadow: 2px 0 8px rgba(0,0,0,0.06);
  overflow-y: auto;
}
.sidebar-nav { padding: 16px 0; }
.nav-group { padding: 12px 24px 4px; font-size: 12px; color: #999; }
.sidebar-nav a {
  display: block;
  padding: 10px 24px;
  color: #333;
  text-decoration: none;
  font-size: 14px;
  transition: background 0.2s;
}
.sidebar-nav a:hover { background: #f5f5f5; }
.sidebar-nav a.active { background: #e6f7ff; color: #1890ff; font-weight: 500; }

.admin-main { flex: 1; padding: 24px; overflow-y: auto; }
</style>
