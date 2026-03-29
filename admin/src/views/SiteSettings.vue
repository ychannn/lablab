<template>
  <div class="site-settings">
    <h2 class="page-title">站点设置</h2>
    <p class="hint">网站标题将显示在门户导航栏、页脚和浏览器标签页。</p>
    <div class="form-card">
      <div class="form-group">
        <label>网站标题</label>
        <input v-model="title" type="text" class="form-input" placeholder="实验室网站" maxlength="50" />
      </div>
      <div class="form-actions">
        <button type="button" class="btn btn-primary" :disabled="saving" @click="save">
          {{ saving ? '保存中…' : '保存' }}
        </button>
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
      title: '实验室网站',
      saving: false
    }
  },
  mounted() {
    this.fetchTitle()
  },
  methods: {
    async fetchTitle() {
      try {
        const data = await request('/config/admin/site-title')
        if (data.code === 200 && data.data != null) this.title = data.data
      } catch (e) {
        console.error(e)
      }
    },
    async save() {
      this.saving = true
      try {
        const data = await request('/config/admin/site-title', {
          method: 'PUT',
          body: JSON.stringify({ title: this.title || '实验室网站' })
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
.page-title { font-size: 24px; margin-bottom: 8px; color: #333; }
.hint { color: #666; font-size: 14px; margin-bottom: 24px; }
.form-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  padding: 24px;
  max-width: 560px;
}
.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 500; color: #333; }
.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}
.form-input:focus { border-color: #1890ff; outline: none; }
.form-actions { margin-top: 24px; }
.btn { padding: 8px 20px; border-radius: 4px; border: none; cursor: pointer; font-size: 14px; }
.btn-primary { background: #1890ff; color: #fff; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
