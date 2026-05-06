<template>
  <div class="site-settings">
    <!-- 网站标题 -->
    <div class="section">
      <h2 class="section-title">网站标题</h2>
      <p class="hint">网站标题将显示在门户导航栏、页脚和浏览器标签页。</p>
      <div class="form-card">
        <div class="form-group">
          <label>网站标题</label>
          <input v-model="config.siteTitle" type="text" class="form-input" placeholder="实验室网站" maxlength="50" />
        </div>
      </div>
    </div>

    <!-- 标题配置（按模块分组） -->
    <div class="section">
      <h2 class="section-title">页面标题配置</h2>
      <p class="hint">配置前台各页面和模块显示的标题文字。点击编辑图标可修改。</p>
      
      <!-- 首页模块 -->
      <div class="form-card">
        <h3 class="module-title">首页</h3>
        <div class="title-list">
          <div class="title-item">
            <span class="title-label">首页轮播图</span>
            <span class="title-value">{{ config.homeCarouselTitle || '新闻动态' }}</span>
            <button type="button" class="btn-edit-title" @click="editTitle('homeCarouselTitle')">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/>
              </svg>
            </button>
          </div>
          <div class="title-item">
            <span class="title-label">校园风景</span>
            <span class="title-value">{{ config.campusTitle || '校园风景' }}</span>
            <button type="button" class="btn-edit-title" @click="editTitle('campusTitle')">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/>
              </svg>
            </button>
          </div>
          <div class="title-item">
            <span class="title-label">实验室动态</span>
            <span class="title-value">{{ config.labNewsTitle || '实验室动态' }}</span>
            <button type="button" class="btn-edit-title" @click="editTitle('labNewsTitle')">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/>
              </svg>
            </button>
          </div>
          <div class="title-item">
            <span class="title-label">公告通知</span>
            <span class="title-value">{{ config.noticeTitle || '公告通知' }}</span>
            <button type="button" class="btn-edit-title" @click="editTitle('noticeTitle')">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/>
              </svg>
            </button>
          </div>
          <div class="title-item">
            <span class="title-label">友情链接</span>
            <span class="title-value">{{ config.friendLinkTitle || '友情链接' }}</span>
            <button type="button" class="btn-edit-title" @click="editTitle('friendLinkTitle')">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- 实验室介绍模块 -->
      <div class="form-card">
        <h3 class="module-title">实验室介绍</h3>
        <div class="title-list">
          <div class="title-item">
            <span class="title-label">实验室环境</span>
            <span class="title-value">{{ config.labEnvTitle || '实验室环境' }}</span>
            <button type="button" class="btn-edit-title" @click="editTitle('labEnvTitle')">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 保存按钮 -->
    <div class="save-section">
      <button type="button" class="btn btn-primary btn-large" :disabled="saving" @click="save">
        {{ saving ? '保存中…' : '保存所有配置' }}
      </button>
    </div>

    <!-- 编辑标题弹窗 -->
    <div v-if="editingKey" class="modal-mask" @click.self="cancelEdit">
      <div class="modal-edit">
        <h3>编辑标题</h3>
        <div class="form-group">
          <label>{{ getTitleLabel(editingKey) }}</label>
          <input v-model="editValue" type="text" class="form-input" placeholder="请输入标题" maxlength="20" />
        </div>
        <div class="modal-actions">
          <button type="button" class="btn-cancel" @click="cancelEdit">取消</button>
          <button type="button" class="btn-submit" @click="saveEdit">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { request } from '../api/auth'

export default {
  name: 'SiteSettings',
  data() {
    return {
      saving: false,
      editingKey: null,
      editValue: '',
      config: {
        siteTitle: '实验室网站',
        homeCarouselTitle: '新闻动态',
        campusTitle: '校园风景',
        labNewsTitle: '实验室动态',
        noticeTitle: '公告通知',
        friendLinkTitle: '友情链接',
        labEnvTitle: '实验室环境'
      },
      titleLabels: {
        homeCarouselTitle: '首页轮播图',
        campusTitle: '校园风景',
        labNewsTitle: '实验室动态',
        noticeTitle: '公告通知',
        friendLinkTitle: '友情链接',
        labEnvTitle: '实验室环境'
      }
    }
  },
  mounted() {
    this.fetchConfig()
  },
  methods: {
    async fetchConfig() {
      try {
        const data = await request('/config/admin/all')
        if (data.code === 200 && data.data) {
          Object.assign(this.config, data.data)
        }
      } catch (e) {
        console.error(e)
      }
    },
    editTitle(key) {
      this.editingKey = key
      this.editValue = this.config[key] || ''
    },
    cancelEdit() {
      this.editingKey = null
      this.editValue = ''
    },
    saveEdit() {
      if (this.editingKey && this.config.hasOwnProperty(this.editingKey)) {
        this.config[this.editingKey] = this.editValue
      }
      this.cancelEdit()
    },
    getTitleLabel(key) {
      return this.titleLabels[key] || key
    },
    async save() {
      this.saving = true
      try {
        const data = await request('/config/admin/all', {
          method: 'PUT',
          body: JSON.stringify(this.config)
        })
        if (data.code === 200) alert('保存成功')
        else alert(data.message || '保存失败')
      } catch (e) {
        alert(e.message || '保存失败')
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style scoped>
.site-settings { padding: 0; }
.section { margin-bottom: 32px; }
.section-title { font-size: 22px; margin-bottom: 8px; color: #333; }
.hint { color: #666; font-size: 14px; margin-bottom: 16px; }

.form-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  padding: 24px;
  margin-bottom: 16px;
}

.module-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;
  color: #333;
}

.title-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.title-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
  transition: background 0.2s;
}

.title-item:hover {
  background: #f0f0f0;
}

.title-label {
  width: 120px;
  font-size: 14px;
  color: #666;
  flex-shrink: 0;
}

.title-value {
  flex: 1;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.btn-edit-title {
  padding: 6px;
  border: none;
  background: none;
  color: #999;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.btn-edit-title:hover {
  background: #e8e8e8;
  color: #1890ff;
}

.form-group { margin-bottom: 16px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 500; color: #333; }
.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}
.form-input:focus { border-color: #1890ff; outline: none; }

.btn { 
  padding: 8px 20px; 
  border-radius: 4px; 
  border: none; 
  cursor: pointer; 
  font-size: 14px; 
}
.btn-primary { background: #1890ff; color: #fff; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-large { padding: 12px 32px; font-size: 16px; }

.save-section { margin-top: 32px; padding-top: 24px; border-top: 1px solid #e8e8e8; }

/* 弹窗样式 */
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-edit {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  width: 360px;
  max-width: 90%;
}

.modal-edit h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  color: #333;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.btn-cancel {
  padding: 8px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: #fff;
  color: #666;
  cursor: pointer;
}

.btn-submit {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  background: #1890ff;
  color: #fff;
  cursor: pointer;
}
</style>