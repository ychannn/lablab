<template>
  <div class="lab-intro-manage">
    <h2 class="page-title">实验室介绍</h2>
    <p class="hint">门户「实验室介绍」页展示内容，可视化编辑后保存。</p>
    <div class="form-card">
      <div class="form-row">
        <div class="form-group">
          <label>实验室名称</label>
          <input v-model="form.labName" type="text" class="form-input" />
        </div>
        <div class="form-group">
          <label>英文名称</label>
          <input v-model="form.englishName" type="text" class="form-input" />
        </div>
      </div>
      <div class="form-group">
        <label>成立时间</label>
        <input v-model="form.establishedDate" type="text" class="form-input" placeholder="如：2020年" />
      </div>
      <div class="form-group">
        <label>实验室简介</label>
        <textarea v-model="form.introduction" class="form-textarea" rows="5"></textarea>
      </div>
      <div class="form-group">
        <label>负责人姓名</label>
        <input v-model="form.leaderName" type="text" class="form-input" />
      </div>
      <div class="form-group">
        <label>负责人简介</label>
        <textarea v-model="form.leaderIntroduction" class="form-textarea" rows="4"></textarea>
      </div>
      <div class="form-group">
        <label>实验室 Logo URL</label>
        <input v-model="form.logo" type="text" class="form-input" placeholder="图片地址" />
      </div>
      <div class="form-group">
        <label>负责人照片 URL</label>
        <input v-model="form.leaderPhoto" type="text" class="form-input" placeholder="图片地址" />
      </div>
      <div class="form-group">
        <label>研究方向（每行一项）</label>
        <textarea v-model="researchAreasText" class="form-textarea" rows="3" placeholder="方向一&#10;方向二"></textarea>
      </div>
      <div class="form-group">
        <label>荣誉资质（每行一项）</label>
        <textarea v-model="honorsText" class="form-textarea" rows="3" placeholder="荣誉一&#10;荣誉二"></textarea>
      </div>
      <div class="form-group">
        <label>实验室图片 URL 列表（每行一项）</label>
        <textarea v-model="photosText" class="form-textarea" rows="2" placeholder="https://..."></textarea>
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
  name: 'LabIntroManage',
  data() {
    return {
      form: {
        labName: '',
        englishName: '',
        introduction: '',
        leaderName: '',
        leaderIntroduction: '',
        establishedDate: '',
        logo: '',
        leaderPhoto: '',
        researchAreas: [],
        honors: [],
        photos: []
      },
      researchAreasText: '',
      honorsText: '',
      photosText: '',
      saving: false
    }
  },
  mounted() {
    this.fetchIntro()
  },
  methods: {
    async fetchIntro() {
      try {
        const data = await request('/api/config/lab-intro')
        if (data.code === 200 && data.data) {
          Object.assign(this.form, data.data)
          this.researchAreasText = (this.form.researchAreas || []).join('\n')
          this.honorsText = (this.form.honors || []).join('\n')
          this.photosText = (this.form.photos || []).join('\n')
        }
      } catch (e) {
        console.error(e)
      }
    },
    async save() {
      this.form.researchAreas = this.researchAreasText.split(/\n/).map(s => s.trim()).filter(Boolean)
      this.form.honors = this.honorsText.split(/\n/).map(s => s.trim()).filter(Boolean)
      this.form.photos = this.photosText.split(/\n/).map(s => s.trim()).filter(Boolean)
      this.saving = true
      try {
        const data = await request('/api/config/admin/lab-intro', {
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
.lab-intro-manage { padding: 0; }
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
.form-textarea { resize: vertical; min-height: 60px; }
.form-input:focus, .form-textarea:focus { border-color: #1890ff; outline: none; }
.form-actions { margin-top: 24px; }
.btn { padding: 8px 20px; border-radius: 4px; border: none; cursor: pointer; font-size: 14px; }
.btn-primary { background: #1890ff; color: #fff; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
