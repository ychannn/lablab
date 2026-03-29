<template>
  <div class="banner-manage">
    <div class="container">
      <h2 class="page-title">首页轮播图管理</h2>
      <p class="hint">门户按当前顺序倒序取前 5 张展示，可拖动调整顺序。上传后会自动保存；拖动排序后请点击「保存顺序」。</p>

      <div class="upload-area">
        <input
          ref="fileInput"
          type="file"
          accept="image/*"
          class="file-input"
          @change="onFileChange"
        />
        <button type="button" class="btn btn-upload" @click="$refs.fileInput.click()">
          上传图片
        </button>
        <span v-if="uploading" class="upload-status">上传中…</span>
        <span v-if="uploadError" class="upload-error">{{ uploadError }}</span>
      </div>

      <div class="banner-list" v-if="list.length">
        <div
          v-for="(item, index) in list"
          :key="item.imageUrl + '-' + index"
          class="banner-card"
          :class="{ dragging: draggedIndex === index }"
          draggable="true"
          @dragstart="onDragStart($event, index)"
          @dragend="onDragEnd"
          @dragover.prevent="onDragOver($event, index)"
          @drop.prevent="onDrop(index)"
        >
          <span class="card-order">{{ index + 1 }}</span>
          <div class="card-preview" :style="{ backgroundImage: 'url(' + imageUrl(item.imageUrl) + ')' }"></div>
          <div class="card-actions">
            <button type="button" class="btn btn-small btn-danger" @click="remove(index)">删除</button>
          </div>
        </div>
      </div>
      <div v-else class="empty-tip">暂无轮播图，请先上传图片。</div>

      <div class="actions">
        <button type="button" class="btn btn-primary" :disabled="saving" @click="save">
          {{ saving ? '保存中…' : '保存顺序' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { request, API_BASE } from '../api/auth'

export default {
  name: 'BannerManage',
  data() {
    return {
      list: [],
      draggedIndex: null,
      uploading: false,
      uploadError: '',
      saving: false
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    imageUrl(url) {
      if (!url) return ''
      return url.startsWith('http') ? url : url
    },
    async fetchList() {
      try {
        const data = await request('/config/admin/banner')
        if (data.code === 200 && Array.isArray(data.data)) this.list = data.data
      } catch (e) {
        console.error(e)
      }
    },
    async onFileChange(e) {
      const file = e.target.files?.[0]
      e.target.value = ''
      if (!file) return
      this.uploadError = ''
      this.uploading = true
      try {
        const form = new FormData()
        form.append('file', file)
        const data = await request('/config/admin/upload', { method: 'POST', body: form })
        if (data.code === 200 && data.data) {
          this.list.unshift({ imageUrl: data.data, title: '', link: '', sort: 0 })
          await this.persistBannerList()
        } else {
          this.uploadError = data.message || '上传失败'
        }
      } catch (err) {
        this.uploadError = err.message || '上传失败'
      } finally {
        this.uploading = false
      }
    },
    remove(index) {
      this.list.splice(index, 1)
    },
    onDragStart(e, index) {
      this.draggedIndex = index
      e.dataTransfer.effectAllowed = 'move'
      e.dataTransfer.setData('text/plain', index)
    },
    onDragEnd() {
      this.draggedIndex = null
    },
    onDragOver(e) {
      if (this.draggedIndex === null) return
      e.dataTransfer.dropEffect = 'move'
    },
    onDrop(toIndex) {
      if (this.draggedIndex === null || this.draggedIndex === toIndex) return
      const item = this.list.splice(this.draggedIndex, 1)[0]
      this.list.splice(toIndex, 0, item)
      this.draggedIndex = null
    },
    async persistBannerList() {
      try {
        const data = await request('/config/admin/banner', {
          method: 'PUT',
          body: JSON.stringify(this.list)
        })
        if (data.code !== 200) {
          this.uploadError = data.message || '保存列表失败'
        }
      } catch (e) {
        this.uploadError = e.message || '保存列表失败'
      }
    },
    async save() {
      this.saving = true
      try {
        const data = await request('/config/admin/banner', {
          method: 'PUT',
          body: JSON.stringify(this.list)
        })
        if (data.code === 200) {
          alert('保存成功')
          this.fetchList()
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
.banner-manage { padding: 0; }
.page-title { font-size: 24px; margin-bottom: 8px; color: #333; }
.hint { color: #666; font-size: 14px; margin-bottom: 24px; }
.upload-area { margin-bottom: 24px; }
.file-input { display: none; }
.btn {
  padding: 8px 16px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 14px;
}
.btn-upload { background: #1890ff; color: #fff; }
.btn-upload:hover { background: #40a9ff; }
.btn-primary { background: #1890ff; color: #fff; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-small { padding: 4px 10px; font-size: 12px; }
.btn-danger { background: #ff4d4f; color: #fff; }
.upload-status, .upload-error { margin-left: 12px; font-size: 14px; }
.upload-error { color: #ff4d4f; }
.banner-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}
.banner-card {
  position: relative;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
  cursor: grab;
  transition: box-shadow 0.2s;
}
.banner-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.banner-card.dragging { opacity: 0.6; cursor: grabbing; }
.card-order {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 1;
  background: rgba(0,0,0,0.6);
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.card-preview {
  width: 100%;
  padding-top: 56.25%;
  background-size: cover;
  background-position: center;
}
.card-actions { padding: 8px; text-align: right; }
.empty-tip { color: #999; margin-bottom: 24px; }
.actions { margin-top: 16px; }
</style>
