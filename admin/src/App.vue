<template>
  <div class="admin-app">
    <Login v-if="showLogin" @login-success="showLogin = false" />
    <template v-else>
      <header class="admin-header">
        <div class="header-inner">
          <h1 class="logo">后台管理</h1>
          <div class="header-actions">
            <a :href="portalUrl" class="link-portal" target="_blank" rel="noopener">访问门户</a>
            <button type="button" class="btn-header" @click="openPasswordModal">修改密码</button>
            <button type="button" class="btn-logout" @click="logout">退出</button>
          </div>
        </div>
      </header>
      <div class="admin-body">
        <aside class="admin-sidebar">
          <nav class="sidebar-nav">
            <div class="nav-group">站点配置</div>
            <a v-for="item in menus" :key="item.key" :href="'#' + item.key" :class="{ active: currentMenu === item.key }" @click.prevent="setMenu(item.key)">{{ item.label }}</a>
            <div class="nav-group">业务管理</div>
            <a v-for="item in crudMenus" :key="item.key" :href="'#' + item.key" :class="{ active: currentMenu === item.key }" @click.prevent="setMenu(item.key)">{{ item.label }}</a>
            <div v-if="currentUser && currentUser.role === 'admin'" class="nav-group">系统</div>
            <a v-if="currentUser && currentUser.role === 'admin'" :href="'#admins'" :class="{ active: currentMenu === 'admins' }" @click.prevent="setMenu('admins')">管理员管理</a>
          </nav>
        </aside>
        <main class="admin-main">
          <SiteSettings v-show="currentMenu === 'site'" />
          <BannerManage v-show="currentMenu === 'banner'" />
          <ContactManage v-show="currentMenu === 'contact'" />
          <LabIntroManage v-show="currentMenu === 'lab-intro'" />
          <LabNewsCrud v-show="currentMenu === 'lab-news'" />
          <NoticeCrud v-show="currentMenu === 'notice'" />
          <PublicationCrud v-show="currentMenu === 'publication'" />
          <TopicProjectCrud v-show="currentMenu === 'topic-project'" />
          <AchievementCrud v-show="currentMenu === 'achievement'" />
          <DirectionCrud v-show="currentMenu === 'direction'" />
          <ScholarCrud v-show="currentMenu === 'scholar'" />
          <AreaCrud v-show="currentMenu === 'area'" />
          <AdminManage v-show="currentMenu === 'admins'" :current-user="currentUser" />
        </main>
      </div>

      <!-- 修改密码弹窗（绑定/换绑邮箱需验证码，修改密码仅支持邮箱验证码） -->
      <div v-if="showPasswordModal" class="modal-mask" @click.self="showPasswordModal = false">
        <div class="modal-password">
          <h3>修改密码</h3>
          <!-- 未绑定邮箱：先绑定（需验证码） -->
          <template v-if="!passwordModalLoading && currentAdmin && !currentAdmin.email">
            <p class="modal-desc">请先绑定邮箱，验证码将发送到您填写的邮箱。</p>
            <form @submit.prevent="bindEmail">
              <div class="form-group">
                <label>邮箱</label>
                <input v-model="bindEmailInput" type="email" placeholder="用于接收验证码的邮箱" class="form-input" required />
              </div>
              <div class="form-group form-group-code">
                <label>验证码</label>
                <input v-model="bindCodeInput" type="text" placeholder="6位验证码" class="form-input" maxlength="8" required />
                <button type="button" class="btn-code" :disabled="bindCodeCooldown > 0" @click="sendBindCode">
                  {{ bindCodeCooldown > 0 ? bindCodeCooldown + '秒后重发' : '获取验证码' }}
                </button>
              </div>
              <div class="modal-actions">
                <button type="button" class="btn-cancel" @click="showPasswordModal = false">取消</button>
                <button type="submit" class="btn-submit" :disabled="bindEmailSubmitting">{{ bindEmailSubmitting ? '绑定中…' : '绑定' }}</button>
              </div>
            </form>
          </template>
          <!-- 已绑定：换绑 或 修改密码 -->
          <template v-else-if="!passwordModalLoading && currentAdmin && currentAdmin.email">
            <!-- 换绑邮箱（需验证码） -->
            <template v-if="showRebindForm">
              <p class="modal-desc">验证码将发送到您填写的新邮箱。</p>
              <form @submit.prevent="bindEmail">
                <div class="form-group">
                  <label>新邮箱</label>
                  <input v-model="bindEmailInput" type="email" placeholder="新邮箱" class="form-input" required />
                </div>
                <div class="form-group form-group-code">
                  <label>验证码</label>
                  <input v-model="bindCodeInput" type="text" placeholder="6位验证码" class="form-input" maxlength="8" required />
                  <button type="button" class="btn-code" :disabled="bindCodeCooldown > 0" @click="sendBindCode">
                    {{ bindCodeCooldown > 0 ? bindCodeCooldown + '秒后重发' : '获取验证码' }}
                  </button>
                </div>
                <div class="modal-actions">
                  <button type="button" class="btn-cancel" @click="showRebindForm = false">取消换绑</button>
                  <button type="submit" class="btn-submit" :disabled="bindEmailSubmitting">{{ bindEmailSubmitting ? '换绑中…' : '确认换绑' }}</button>
                </div>
              </form>
            </template>
            <!-- 修改密码（仅支持邮箱验证码） -->
            <template v-else>
              <p class="modal-desc">当前绑定：{{ currentAdmin.email }}。<button type="button" class="btn-link-inline" @click="showRebindForm = true">换绑邮箱</button></p>
              <p class="modal-desc">验证码将发送到绑定邮箱，未配置 SMTP 时请查看后端控制台。</p>
              <form @submit.prevent="submitChangePassword">
              <div class="form-group form-group-code">
                <label>验证码</label>
                <input v-model="passwordForm.code" type="text" placeholder="6位验证码" class="form-input" maxlength="8" required />
                <button type="button" class="btn-code" :disabled="codeCooldown > 0" @click="sendCode">
                  {{ codeCooldown > 0 ? codeCooldown + '秒后重发' : '获取验证码' }}
                </button>
              </div>
              <div class="form-group">
                <label>新密码</label>
                <input v-model="passwordForm.newPassword" type="password" placeholder="6-32位" class="form-input" required />
              </div>
              <div class="form-group">
                <label>确认密码</label>
                <input v-model="passwordForm.confirmPassword" type="password" placeholder="再次输入新密码" class="form-input" required />
              </div>
              <div class="modal-actions">
                <button type="button" class="btn-cancel" @click="showPasswordModal = false">取消</button>
                <button type="submit" class="btn-submit" :disabled="passwordSubmitting">{{ passwordSubmitting ? '提交中…' : '确认修改' }}</button>
              </div>
            </form>
            </template>
          </template>
          <p v-else class="modal-desc">{{ passwordModalLoading ? '加载中…' : '获取管理员信息失败，请关闭重试' }}</p>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { isLoggedIn, setToken, request } from './api/auth'
import Login from './views/Login.vue'
import SiteSettings from './views/SiteSettings.vue'
import BannerManage from './views/BannerManage.vue'
import ContactManage from './views/ContactManage.vue'
import LabIntroManage from './views/LabIntroManage.vue'
import LabNewsCrud from './views/crud/LabNewsCrud.vue'
import NoticeCrud from './views/crud/NoticeCrud.vue'
import PublicationCrud from './views/crud/PublicationCrud.vue'
import TopicProjectCrud from './views/crud/TopicProjectCrud.vue'
import AchievementCrud from './views/crud/AchievementCrud.vue'
import DirectionCrud from './views/crud/DirectionCrud.vue'
import ScholarCrud from './views/crud/ScholarCrud.vue'
import AreaCrud from './views/crud/AreaCrud.vue'
import AdminManage from './views/AdminManage.vue'

const MENU_KEYS = ['site', 'banner', 'contact', 'lab-intro', 'lab-news', 'notice', 'publication', 'topic-project', 'achievement', 'direction', 'scholar', 'area', 'admins']

export default {
  name: 'AdminApp',
  components: {
    Login,
    SiteSettings,
    BannerManage,
    ContactManage,
    LabIntroManage,
    LabNewsCrud,
    NoticeCrud,
    PublicationCrud,
    TopicProjectCrud,
    AchievementCrud,
    DirectionCrud,
    ScholarCrud,
    AreaCrud,
    AdminManage
  },
  data() {
    const hash = typeof window !== 'undefined' ? (window.location.hash || '').replace(/^#/, '') : ''
    const initialMenu = MENU_KEYS.includes(hash) ? hash : 'site'
    return {
      showLogin: !isLoggedIn(),
      portalUrl: 'http://localhost:5173',
      currentUser: null,
      currentMenu: initialMenu,
      showPasswordModal: false,
      passwordModalLoading: false,
      currentAdmin: null,
      showRebindForm: false,
      bindEmailInput: '',
      bindCodeInput: '',
      bindCodeCooldown: 0,
      bindCodeTimer: null,
      bindEmailSubmitting: false,
      passwordForm: { code: '', newPassword: '', confirmPassword: '' },
      passwordSubmitting: false,
      codeCooldown: 0,
      codeTimer: null,
      menus: [
        { key: 'site', label: '站点设置' },
        { key: 'banner', label: '首页轮播图' },
        { key: 'contact', label: '联系我们' },
        { key: 'lab-intro', label: '实验室介绍' }
      ],
      crudMenus: [
        { key: 'lab-news', label: '新闻公告' },
        { key: 'notice', label: '公告' },
        { key: 'publication', label: '论文发表' },
        { key: 'topic-project', label: '课题项目' },
        { key: 'achievement', label: '成果奖项' },
        { key: 'direction', label: '研究方向' },
        { key: 'scholar', label: '师资队伍' },
        { key: 'area', label: '领域管理' }
      ]
    }
  },
  mounted() {
    window.addEventListener('admin-logout', this.onLogout)
    window.addEventListener('hashchange', this.onHashChange)
    this.syncHashToMenu()
    if (!this.showLogin) this.fetchCurrentUser()
  },
  watch: {
    showLogin(v) {
      if (!v) this.fetchCurrentUser()
    }
  },
  beforeUnmount() {
    window.removeEventListener('admin-logout', this.onLogout)
    window.removeEventListener('hashchange', this.onHashChange)
    if (this.codeTimer) clearInterval(this.codeTimer)
    if (this.bindCodeTimer) clearInterval(this.bindCodeTimer)
  },
  methods: {
    async fetchCurrentUser() {
      try {
        const res = await request('/admin/info')
        const admin = (res && res.data !== undefined) ? res.data : (res && res.result !== undefined) ? res.result : res
        this.currentUser = admin && typeof admin === 'object' ? { id: admin.id, username: admin.username, role: admin.role, email: admin.email } : null
      } catch (_) {
        this.currentUser = null
      }
    },
    async openPasswordModal() {
      this.passwordForm = { code: '', newPassword: '', confirmPassword: '' }
      this.bindEmailInput = ''
      this.bindCodeInput = ''
      this.showRebindForm = false
      this.currentAdmin = null
      this.codeCooldown = 0
      this.bindCodeCooldown = 0
      if (this.codeTimer) { clearInterval(this.codeTimer); this.codeTimer = null }
      if (this.bindCodeTimer) { clearInterval(this.bindCodeTimer); this.bindCodeTimer = null }
      this.showPasswordModal = true
      this.passwordModalLoading = true
      try {
        const res = await request('/admin/info')
        const admin = (res && res.data !== undefined) ? res.data : (res && res.result !== undefined) ? res.result : res
        this.currentAdmin = admin && typeof admin === 'object' ? admin : null
      } catch (e) {
        this.currentAdmin = null
        alert(e.message || '获取管理员信息失败')
      } finally {
        this.passwordModalLoading = false
      }
    },
    sendBindCode() {
      const email = (this.bindEmailInput || '').trim()
      if (!email) {
        alert('请先填写邮箱')
        return
      }
      request('/admin/send-bind-email-code', { method: 'POST', body: JSON.stringify({ email }) })
        .then(() => {
          alert('验证码已发送，请查收（未配置 SMTP 时请查看后端控制台）')
          this.bindCodeCooldown = 60
          this.bindCodeTimer = setInterval(() => {
            this.bindCodeCooldown--
            if (this.bindCodeCooldown <= 0 && this.bindCodeTimer) {
              clearInterval(this.bindCodeTimer)
              this.bindCodeTimer = null
            }
          }, 1000)
        })
        .catch(e => alert(e.message || '发送失败'))
    },
    async bindEmail() {
      const email = (this.bindEmailInput || '').trim()
      const code = (this.bindCodeInput || '').trim()
      if (!email) {
        alert('请填写邮箱')
        return
      }
      if (!code) {
        alert('请填写验证码')
        return
      }
      this.bindEmailSubmitting = true
      try {
        await request('/admin/bind-email', { method: 'POST', body: JSON.stringify({ email, code }) })
        alert(this.currentAdmin && this.currentAdmin.email ? '换绑成功' : '绑定成功')
        this.currentAdmin = { ...this.currentAdmin, email }
        this.showRebindForm = false
        this.bindCodeInput = ''
      } catch (e) {
        alert(e.message || '绑定失败')
      } finally {
        this.bindEmailSubmitting = false
      }
    },
    async sendCode() {
      try {
        await request('/admin/send-email-code', { method: 'POST', body: JSON.stringify({}) })
        alert('验证码已发送到绑定邮箱，请查收（未配置 SMTP 时请查看后端控制台）')
        this.codeCooldown = 60
        this.codeTimer = setInterval(() => {
          this.codeCooldown--
          if (this.codeCooldown <= 0 && this.codeTimer) {
            clearInterval(this.codeTimer)
            this.codeTimer = null
          }
        }, 1000)
      } catch (e) {
        alert(e.message || '发送失败')
      }
    },
    async submitChangePassword() {
      const { code, newPassword, confirmPassword } = this.passwordForm
      if (newPassword !== confirmPassword) {
        alert('两次输入的密码不一致')
        return
      }
      if (newPassword.length < 6 || newPassword.length > 32) {
        alert('密码长度为6-32位')
        return
      }
      this.passwordSubmitting = true
      try {
        await request('/admin/change-password-by-email', {
          method: 'POST',
          body: JSON.stringify({ code: code.trim(), newPassword })
        })
        alert('密码已修改，请重新登录')
        this.showPasswordModal = false
        this.passwordForm = { code: '', newPassword: '', confirmPassword: '' }
        this.logout()
      } catch (e) {
        alert(e.message || '修改失败')
      } finally {
        this.passwordSubmitting = false
      }
    },
    setMenu(key) {
      this.currentMenu = key
      window.location.hash = key
    },
    onHashChange() {
      this.syncHashToMenu()
    },
    syncHashToMenu() {
      const hash = (window.location.hash || '').replace(/^#/, '')
      if (MENU_KEYS.includes(hash)) this.currentMenu = hash
    },
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
.btn-header { background: transparent; border: 1px solid rgba(255,255,255,0.5); color: #fff; padding: 4px 12px; border-radius: 4px; cursor: pointer; font-size: 14px; }
.btn-header:hover { background: rgba(255,255,255,0.1); }

.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-password { background: #fff; border-radius: 8px; padding: 24px; width: 90%; max-width: 420px; }
.modal-password h3 { margin: 0 0 8px; font-size: 18px; }
.modal-desc { margin: 0 0 20px; font-size: 13px; color: #666; }
.modal-password .form-group { margin-bottom: 16px; }
.modal-password .form-group label { display: block; margin-bottom: 6px; font-size: 13px; color: #333; }
.modal-password .form-input { width: 100%; padding: 8px 12px; border: 1px solid #d9d9d9; border-radius: 6px; font-size: 14px; box-sizing: border-box; }
.form-group-code { display: flex; gap: 8px; align-items: flex-end; }
.form-group-code .form-input { flex: 1; }
.btn-code { flex-shrink: 0; padding: 8px 14px; font-size: 13px; border: 1px solid #1890ff; color: #1890ff; background: #fff; border-radius: 6px; cursor: pointer; }
.btn-code:hover:not(:disabled) { background: #e6f7ff; }
.btn-code:disabled { opacity: 0.6; cursor: not-allowed; }
.modal-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px; }
.btn-cancel { padding: 8px 16px; font-size: 14px; color: #666; background: #fff; border: 1px solid #d9d9d9; border-radius: 6px; cursor: pointer; }
.btn-submit { padding: 8px 16px; font-size: 14px; color: #fff; background: #1890ff; border: none; border-radius: 6px; cursor: pointer; }
.btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-link-inline { background: none; border: none; color: #1890ff; cursor: pointer; padding: 0; font-size: inherit; text-decoration: underline; }
.btn-link-inline:hover { color: #40a9ff; }
</style>
