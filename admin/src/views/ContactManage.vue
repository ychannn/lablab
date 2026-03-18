<template>
  <div class="contact-manage">
    <h2 class="page-title">联系我们</h2>
    <p class="hint">门户「联系我们」页展示内容，可视化编辑后保存。</p>
    <div class="form-card">
      <div class="form-row">
        <div class="form-group">
          <label>实验室名称</label>
          <input v-model="form.labName" type="text" class="form-input" />
        </div>
        <div class="form-group">
          <label>邮编</label>
          <input v-model="form.zipCode" type="text" class="form-input" />
        </div>
      </div>
      <div class="form-group">
        <label>地址</label>
        <input v-model="form.address" type="text" class="form-input" />
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>电话</label>
          <input v-model="form.phone" type="text" class="form-input" />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input v-model="form.email" type="text" class="form-input" />
        </div>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>传真</label>
          <input v-model="form.fax" type="text" class="form-input" />
        </div>
        <div class="form-group">
          <label>工作时间</label>
          <input v-model="form.workTime" type="text" class="form-input" placeholder="如：周一至周五 9:00-17:00" />
        </div>
      </div>
      <div class="form-group">
        <label>微信公众号</label>
        <input v-model="form.wechat" type="text" class="form-input" />
      </div>
      <div class="form-group">
        <label>简介</label>
        <textarea v-model="form.introduction" class="form-textarea" rows="3"></textarea>
      </div>
      <div class="form-actions">
        <button type="button" class="btn btn-primary" :disabled="saving" @click="save">{{ saving ? '保存中…' : '保存' }}</button>
      </div>
    </div>
  </div>
</template>

<script>
import { request } from '../api/auth'

export default {
  name: 'ContactManage',
  data() {
    return {
      form: {
        labName: '',
        address: '',
        zipCode: '',
        phone: '',
        email: '',
        fax: '',
        workTime: '',
        wechat: '',
        introduction: ''
      },
      saving: false
    }
  },
  mounted() {
    this.fetchContact()
  },
  methods: {
    async fetchContact() {
      try {
        const data = await request('/api/config/contact')
        if (data.code === 200 && data.data) Object.assign(this.form, data.data)
      } catch (e) {
        console.error(e)
      }
    },
    async save() {
      this.saving = true
      try {
        const data = await request('/api/config/admin/contact', {
          method: 'PUT',
          body: JSON.stringify(this.form)
        })
        if (data.code === 200) {
          alert('保存成功')
        } else {
          alert(data.message || '保存失败')
        }
      } catch (e) {
        alert('保存失败')
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style scoped>
.contact-manage { padding: 0; }
.page-title { font-size: 24px; margin-bottom: 8px; color: #333; }
.hint { color: #666; font-size: 14px; margin-bottom: 24px; }
.form-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  padding: 24px;
  max-width: 720px;
}
.form-row { display: flex; gap: 20px; flex-wrap: wrap; }
.form-row .form-group { flex: 1; min-width: 200px; }
.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 500; color: #333; }
.form-input, .form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}
.form-textarea { resize: vertical; min-height: 80px; }
.form-input:focus, .form-textarea:focus { border-color: #1890ff; outline: none; }
.form-actions { margin-top: 24px; }
.btn { padding: 8px 20px; border-radius: 4px; border: none; cursor: pointer; font-size: 14px; }
.btn-primary { background: #1890ff; color: #fff; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
