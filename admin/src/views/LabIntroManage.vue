<template>
  <div class="lab-intro-manage">
    <h2 class="page-title">实验室介绍</h2>
    <p class="hint">门户「实验室介绍」页展示内容，可视化编辑后保存。</p>
    
    <!-- 基本信息 -->
    <div class="form-card">
      <h3 class="section-label">基本信息</h3>
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
        <label>实验室 Logo</label>
        <div class="upload-row">
          <input ref="logoInput" type="file" accept="image/*" class="hidden" @change="e => onImageUpload(e, 'logo')" />
          <button type="button" class="btn btn-secondary" @click="$refs.logoInput.click()">{{ form.logo ? '更换图片' : '上传图片' }}</button>
          <span v-if="uploadingLogo" class="upload-status">上传中…</span>
          <div v-if="form.logo" class="thumb-wrap"><img :src="imageUrl(form.logo)" alt="Logo" class="thumb-img" /></div>
        </div>
      </div>
    </div>

    <!-- 实验室负责人 -->
    <div class="form-card">
      <div class="section-header">
        <h3 class="section-label">实验室负责人</h3>
        <button type="button" class="btn btn-secondary btn-sm" @click="addDirector">+ 添加负责人</button>
      </div>
      <div class="directors-list">
        <div v-for="(director, index) in form.directors" :key="index" class="director-card">
          <div class="director-header">
            <span class="director-title">负责人 {{ index + 1 }}</span>
            <button v-if="form.directors.length > 1" type="button" class="btn btn-remove-sm" @click="removeDirector(index)">删除</button>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>姓名</label>
              <input v-model="director.name" type="text" class="form-input" placeholder="姓名" maxlength="20" />
            </div>
            <div class="form-group">
              <label>职称</label>
              <input v-model="director.title" type="text" class="form-input" placeholder="职称" maxlength="30" />
            </div>
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input v-model="director.email" type="email" class="form-input" placeholder="邮箱" maxlength="50" />
          </div>
          <div class="form-group">
            <label>简介</label>
            <textarea v-model="director.intro" class="form-textarea" rows="3" placeholder="简介" maxlength="200"></textarea>
          </div>
          <div class="form-group">
            <label>照片</label>
            <div class="upload-row">
              <input :ref="'photoInput' + index" type="file" accept="image/*" class="hidden" @change="e => onDirectorPhotoUpload(e, index)" />
              <button type="button" class="btn btn-secondary btn-sm" @click="triggerPhotoUpload($event, index)">{{ director.photo ? '更换照片' : '上传照片' }}</button>
              <span v-if="uploadingPhotos[index]" class="upload-status">上传中…</span>
              <div v-if="director.photo" class="thumb-wrap"><img :src="imageUrl(director.photo)" alt="照片" class="thumb-img" /></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 研究方向和荣誉资质 -->
    <div class="form-card">
      <h3 class="section-label">研究方向与荣誉资质</h3>
      <div class="form-group">
        <label>研究方向（每行一项）</label>
        <textarea v-model="researchAreasText" class="form-textarea" rows="3" placeholder="方向一&#10;方向二"></textarea>
      </div>
      <div class="form-group">
        <label>荣誉资质（每行一项）</label>
        <textarea v-model="honorsText" class="form-textarea" rows="3" placeholder="荣誉一&#10;荣誉二"></textarea>
      </div>
    </div>

    <!-- 实验室环境图片 -->
    <div class="form-card">
      <h3 class="section-label">实验室环境图片</h3>
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

    <!-- 保存按钮 -->
    <div class="form-actions">
      <button type="button" class="btn btn-primary btn-large" :disabled="saving" @click="save">{{ saving ? '保存中…' : '保存' }}</button>
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
        establishedDate: '',
        logo: '',
        directors: [
          { name: '', title: '', email: '', intro: '', photo: '' }
        ],
        researchAreas: [],
        honors: [],
        photos: []
      },
      researchAreasText: '',
      honorsText: '',
      saving: false,
      uploadingLogo: false,
      uploadingPhotos: {}
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
      this.uploadingLogo = true
      try {
        const formData = new FormData()
        formData.append('file', file)
        const data = await request('/config/admin/upload', { method: 'POST', body: formData })
        if (data.code === 200 && data.data) this.form[field] = data.data
        else alert(data.message || '上传失败')
      } catch (err) {
        alert(err.message || '上传失败')
      } finally {
        this.uploadingLogo = false
      }
    },
    triggerPhotoUpload(event, index) {
      const input = event.target.previousElementSibling
      if (input && input.tagName === 'INPUT' && input.type === 'file') {
        input.click()
      }
    },
    async onDirectorPhotoUpload(e, index) {
      const file = e.target.files?.[0]
      e.target.value = ''
      if (!file) return
      this.uploadingPhotos[index] = true
      try {
        const formData = new FormData()
        formData.append('file', file)
        const data = await request('/config/admin/upload', { method: 'POST', body: formData })
        if (data.code === 200 && data.data) {
          this.form.directors[index].photo = data.data
        } else {
          alert(data.message || '上传失败')
        }
      } catch (err) {
        alert(err.message || '上传失败')
      } finally {
        this.uploadingPhotos[index] = false
      }
    },
    async onPhotosUpload(e) {
      const file = e.target.files?.[0]
      e.target.value = ''
      if (!file) return
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
      }
    },
    async fetchIntro() {
      try {
        const data = await request('/config/lab-intro')
        if (data.code === 200 && data.data) {
          Object.assign(this.form, data.data)
          // 确保负责人数组存在
          if (!this.form.directors || this.form.directors.length === 0) {
            this.form.directors = [{ name: '', title: '', email: '', intro: '', photo: '' }]
          }
          this.researchAreasText = (this.form.researchAreas || []).join('\n')
          this.honorsText = (this.form.honors || []).join('\n')
        }
      } catch (e) {
        console.error(e)
      }
    },
    addDirector() {
      this.form.directors.push({ name: '', title: '', email: '', intro: '', photo: '' })
    },
    removeDirector(index) {
      if (this.form.directors.length > 1) {
        if (confirm('确定要删除这位负责人吗？')) {
          this.form.directors.splice(index, 1)
        }
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
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;
}

.section-label {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.section-header .section-label {
  margin: 0;
}

.form-row { display: flex; gap: 20px; flex-wrap: wrap; }
.form-row .form-group { flex: 1; min-width: 200px; }
.form-group { margin-bottom: 16px; }
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

.btn { padding: 8px 20px; border-radius: 4px; border: none; cursor: pointer; font-size: 14px; }
.btn-primary { background: #1890ff; color: #fff; }
.btn-secondary { background: #f0f0f0; color: #333; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-sm { padding: 4px 12px; font-size: 12px; }
.btn-large { padding: 12px 32px; font-size: 16px; }
.btn-remove-sm {
  padding: 4px 12px;
  background: #f5222d;
  color: #fff;
  font-size: 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.btn-remove-sm:hover { background: #ff4d4f; }

.hidden { display: none; }
.upload-row { display: flex; flex-wrap: wrap; align-items: center; gap: 12px; }
.upload-status { font-size: 14px; color: #666; }
.thumb-wrap .thumb-img { width: 80px; height: 80px; object-fit: cover; border-radius: 4px; border: 1px solid #e8e8e8; }

.directors-list { margin-top: 16px; }
.director-card {
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
  margin-bottom: 12px;
}
.director-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.director-title { font-weight: 500; color: #333; }

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
.btn-remove:hover { background: #ff4d4f; }

.empty-photos {
  color: #666;
  font-size: 14px;
  margin: 12px 0;
  padding: 16px;
  background-color: #f5f5f5;
  border-radius: 4px;
  text-align: center;
}

.form-actions { margin-top: 32px; padding-top: 24px; border-top: 1px solid #e8e8e8; }
</style>