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
        <label>实验室 Logo</label>
        <div class="upload-row">
          <input ref="logoInput" type="file" accept="image/*" class="hidden" @change="e => onImageUpload(e, 'logo')" />
          <button type="button" class="btn btn-secondary" @click="$refs.logoInput.click()">{{ form.logo ? '更换图片' : '上传图片' }}</button>
          <span v-if="uploadingLogo" class="upload-status">上传中…</span>
          <div v-if="form.logo" class="thumb-wrap"><img :src="imageUrl(form.logo)" alt="Logo" class="thumb-img" /></div>
        </div>
      </div>
      <div class="form-group">
        <label>负责人照片</label>
        <div class="upload-row">
          <input ref="leaderPhotoInput" type="file" accept="image/*" class="hidden" @change="e => onImageUpload(e, 'leaderPhoto')" />
          <button type="button" class="btn btn-secondary" @click="$refs.leaderPhotoInput.click()">{{ form.leaderPhoto ? '更换图片' : '上传图片' }}</button>
          <span v-if="uploadingLeader" class="upload-status">上传中…</span>
          <div v-if="form.leaderPhoto" class="thumb-wrap"><img :src="imageUrl(form.leaderPhoto)" alt="负责人" class="thumb-img" /></div>
        </div>
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
        <label>实验室图片</label>
        <div class="upload-row">
          <input ref="photosInput" type="file" accept="image/*" class="hidden" @change="onPhotosUpload" />
          <button type="button" class="btn btn-secondary" @click="$refs.photosInput.click()">上传图片（可多张）</button>
          <span v-if="uploadingPhotos" class="upload-status">上传中…</span>
        </div>
        <div v-if="form.photos && form.photos.length" class="photos-preview">
          <div v-for="(photo, index) in form.photos" :key="index" class="photo-preview-item">
            <img :src="imageUrl(photo)" alt="实验室图片" class="photo-preview-img" />
            <button type="button" class="btn btn-remove" @click="removePhoto(index)">删除</button>
          </div>
        </div>
        <p v-else class="empty-photos">暂无实验室图片，请点击上传按钮添加</p>
      </div>
      <div class="form-actions">
        <button type="button" class="btn btn-primary" :disabled="saving" @click="save">{{ saving ? '保存中…' : '保存' }}</button>
      </div>
    </div>
  </div>
</template>

<script>
import { request, API_BASE } from '../api/auth'

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
      saving: false,
      uploadingLogo: false,
      uploadingLeader: false,
      uploadingPhotos: false
    }
  },
  mounted() {
    this.fetchIntro()
  },
  methods: {
    imageUrl(url) {
      if (!url) return ''
      return url.startsWith('http') ? url : url
    },
    async onImageUpload(e, field) {
      const file = e.target.files?.[0]
      e.target.value = ''
      if (!file) return
      const key = field === 'logo' ? 'uploadingLogo' : 'uploadingLeader'
      this[key] = true
      try {
        const formData = new FormData()
        formData.append('file', file)
        const data = await request('/config/admin/upload', { method: 'POST', body: formData })
        if (data.code === 200 && data.data) this.form[field] = data.data
        else alert(data.message || '上传失败')
      } catch (err) {
        alert(err.message || '上传失败')
      } finally {
        this[key] = false
      }
    },
    async onPhotosUpload(e) {
      const file = e.target.files?.[0]
      e.target.value = ''
      if (!file) return
      this.uploadingPhotos = true
      try {
        const formData = new FormData()
        formData.append('file', file)
        const data = await request('/config/admin/upload', { method: 'POST', body: formData })
        if (data.code === 200 && data.data) {
          if (!this.form.photos) {
            this.form.photos = []
          }
          this.form.photos.push(data.data)
        } else alert(data.message || '上传失败')
      } catch (err) {
        alert(err.message || '上传失败')
      } finally {
        this.uploadingPhotos = false
      }
    },
    async fetchIntro() {
      try {
        const data = await request('/config/lab-intro')
        if (data.code === 200 && data.data) {
          Object.assign(this.form, data.data)
          this.researchAreasText = (this.form.researchAreas || []).join('\n')
          this.honorsText = (this.form.honors || []).join('\n')
        }
      } catch (e) {
        console.error(e)
      }
    },
    removePhoto(index) {
      if (confirm('确定要删除这张图片吗？')) {
        this.form.photos.splice(index, 1)
      }
    },
    async save() {
      this.form.researchAreas = this.researchAreasText.split(/\n/).map(s => s.trim()).filter(Boolean)
      this.form.honors = this.honorsText.split(/\n/).map(s => s.trim()).filter(Boolean)
      // 确保photos是数组
      if (!this.form.photos) {
        this.form.photos = []
      }
      this.saving = true
      try {
        const data = await request('/config/admin/lab-intro', {
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
.btn-secondary { background: #f0f0f0; color: #333; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.hidden { display: none; }
.upload-row { display: flex; flex-wrap: wrap; align-items: center; gap: 12px; }
.upload-status { font-size: 14px; color: #666; }
.thumb-wrap .thumb-img { width: 80px; height: 80px; object-fit: cover; border-radius: 4px; border: 1px solid #e8e8e8; }
.photos-list { margin-top: 8px; }
.photos-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin: 12px 0;
}
.photo-preview-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.photo-preview-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #e8e8e8;
}
.btn-remove {
  padding: 4px 12px;
  background: #f5222d;
  color: #fff;
  font-size: 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.btn-remove:hover {
  background: #ff4d4f;
}
.empty-photos {
  color: #666;
  font-size: 14px;
  margin: 12px 0;
  padding: 16px;
  background-color: #f5f5f5;
  border-radius: 4px;
  text-align: center;
}
</style>
