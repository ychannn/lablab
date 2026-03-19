<template>
  <div class="crud-page">
    <h2 class="page-title">新闻</h2>
    <div class="toolbar">
      <div class="filter-wrap">
        <form class="filter-row" @submit.prevent="onSearch">
          <label class="filter-label">关键词</label>
          <input v-model="filter.keyword" type="text" placeholder="标题/内容" class="filter-input filter-input-text" />
          <span class="filter-label time-label">时间</span>
          <div class="filter-time-group" ref="dtWrap">
            <div class="dt-wrap">
              <button type="button" class="dt-trigger" :class="{ filled: filter.timeStartDate }" @click.stop="dtpOpen = dtpOpen === 'start' ? null : 'start'" title="开始时间">
                <span class="dt-icon" aria-hidden="true"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span>
                <span class="dt-text">{{ startDisplay }}</span>
              </button>
              <div v-show="dtpOpen === 'start'" class="dt-popover" @click.stop>
                <div class="dt-popover-title">开始日期</div>
                <label class="date-only-wrap" for="labnews-dtp-start" @click="openDatePicker">
                  <span class="date-only-text">{{ filter.timeStartDate ? dateLabel(filter.timeStartDate) : '请选择日期' }}</span>
                  <input id="labnews-dtp-start" v-model="filter.timeStartDate" type="date" class="date-only-input" />
                </label>
                <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtp('start')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
              </div>
            </div>
            <span class="filter-sep">至</span>
            <div class="dt-wrap">
              <button type="button" class="dt-trigger" :class="{ filled: filter.timeEndDate }" @click.stop="dtpOpen = dtpOpen === 'end' ? null : 'end'" title="结束时间">
                <span class="dt-icon" aria-hidden="true"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span>
                <span class="dt-text">{{ endDisplay }}</span>
              </button>
              <div v-show="dtpOpen === 'end'" class="dt-popover" @click.stop>
                <div class="dt-popover-title">结束日期</div>
                <label class="date-only-wrap" for="labnews-dtp-end" @click="openDatePicker">
                  <span class="date-only-text">{{ filter.timeEndDate ? dateLabel(filter.timeEndDate) : '请选择日期' }}</span>
                  <input id="labnews-dtp-end" v-model="filter.timeEndDate" type="date" class="date-only-input" />
                </label>
                <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtp('end')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
              </div>
            </div>
          </div>
          <button type="submit" class="btn btn-query">查询</button>
          <button type="button" class="btn btn-clear" @click="onResetFilter" title="清空筛选">清空</button>
        </form>
      </div>
      <button type="button" class="btn btn-primary" @click="openAdd">新增</button>
    </div>
    <table class="data-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>标题</th>
          <th>内容</th>
          <th>时间</th>
          <th width="140">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in list" :key="row.id">
          <td>{{ row.id }}</td>
          <td>{{ row.title }}</td>
          <td class="content-cell">{{ (row.content || '').slice(0, 80) }}{{ (row.content || '').length > 80 ? '…' : '' }}</td>
          <td>{{ formatTime(row.time) }}</td>
          <td>
            <button type="button" class="btn-link" @click="openEdit(row)">编辑</button>
            <button type="button" class="btn-link danger" @click="remove(row.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="total > 0" class="pagination">
      <button type="button" :disabled="pageNum <= 1" @click="load(pageNum - 1)">上一页</button>
      <span>第 {{ pageNum }} / {{ pages || 1 }} 页，共 {{ total }} 条</span>
      <button type="button" :disabled="pageNum >= (pages || 1)" @click="load(pageNum + 1)">下一页</button>
    </div>

    <div v-if="showModal" class="modal-mask" @click.self="showModal = false">
      <div class="modal">
        <h3>{{ editId ? '编辑' : '新增' }}新闻</h3>
        <div class="form-group">
          <label>标题</label>
          <input v-model="form.title" type="text" class="form-input" />
        </div>
        <div class="form-group content-group">
          <label>内容</label>
          <textarea v-model="form.content" class="form-input content-field" rows="8"></textarea>
        </div>
        <div class="form-group">
          <label>封面图（仅一张）</label>
          <div class="images-upload">
            <div v-if="form.imageUrl" class="image-item">
              <img :src="imageFullUrl(form.imageUrl)" alt="" />
              <button type="button" class="image-remove" @click="form.imageUrl = ''">删除</button>
            </div>
            <label class="image-add">
              <input ref="fileInput" type="file" accept="image/*" @change="onImageSelect" style="display:none" />
              <span>{{ form.imageUrl ? '更换图片' : '+ 上传图片' }}</span>
            </label>
          </div>
        </div>
        <div class="form-group">
          <label>时间</label>
          <input v-model="form.time" type="datetime-local" class="form-input" />
        </div>
        <div class="modal-footer">
          <button type="button" class="btn" @click="showModal = false">取消</button>
          <button type="button" class="btn btn-primary" :disabled="saving" @click="submit">{{ saving ? '保存中…' : '保存' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { request } from '../../api/auth'

export default {
  name: 'LabNewsCrud',
  data() {
    return {
      list: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      pages: 0,
      showModal: false,
      editId: null,
      saving: false,
      form: { title: '', content: '', time: '', imageUrl: '' },
      filter: { keyword: '', timeStartDate: '', timeStartTime: '', timeEndDate: '', timeEndTime: '' },
      dtpOpen: null
    }
  },
  computed: {
    startDisplay() {
      const d = this.filter.timeStartDate
      if (!d || !d.trim()) return '开始'
      return this.dateLabel(d)
    },
    endDisplay() {
      const d = this.filter.timeEndDate
      if (!d || !d.trim()) return '结束'
      return this.dateLabel(d)
    }
  },
  mounted() {
    this.load(1)
    this.clickOutside = (e) => {
      if (this.dtpOpen && this.$refs.dtWrap && !this.$refs.dtWrap.contains(e.target)) this.dtpOpen = null
    }
    document.addEventListener('click', this.clickOutside)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.clickOutside)
  },
  methods: {
    openDatePicker(e) {
      const input = e.currentTarget.querySelector('input[type="date"]')
      if (input && typeof input.showPicker === 'function') input.showPicker()
    },
    formatTime(t) {
      if (!t) return ''
      return t.replace('T', ' ').slice(0, 19)
    },
    buildPageQuery(p) {
      const params = new URLSearchParams()
      params.set('pageNum', String(p))
      params.set('pageSize', String(this.pageSize))
      if (this.filter.keyword && this.filter.keyword.trim()) params.set('keyword', this.filter.keyword.trim())
      const timeStart = this.buildDateTime(this.filter.timeStartDate, this.filter.timeStartTime, '00:00')
      if (timeStart) params.set('timeStart', timeStart)
      const timeEnd = this.buildDateTime(this.filter.timeEndDate, this.filter.timeEndTime, '23:59')
      if (timeEnd) params.set('timeEnd', timeEnd)
      return `/lab-news/page?${params.toString()}`
    },
    async load(p) {
      const data = await request(this.buildPageQuery(p))
      if (data.code === 200 && data.data) {
        this.list = data.data.records || []
        this.pageNum = data.data.current || 1
        this.total = data.data.total || 0
        this.pages = data.data.pages || 0
      }
    },
    onSearch() {
      this.load(1)
    },
    dateLabel(d) {
      if (!d || d.length < 10) return ''
      return parseInt(d.slice(5, 7), 10) + '月' + parseInt(d.slice(8, 10), 10) + '日'
    },
    buildDateTime(date, time, defaultTime) {
      if (!date || !date.trim()) return ''
      return (date + 'T' + (time && time.trim() ? time : defaultTime) + ':00').slice(0, 19)
    },
    clearDtp(which) {
      if (which === 'start') { this.filter.timeStartDate = ''; this.filter.timeStartTime = '' }
      else { this.filter.timeEndDate = ''; this.filter.timeEndTime = '' }
      this.dtpOpen = null
    },
    onResetFilter() {
      this.filter = { keyword: '', timeStartDate: '', timeStartTime: '', timeEndDate: '', timeEndTime: '' }
      this.dtpOpen = null
      this.load(1)
    },
    openAdd() {
      this.editId = null
      this.form = { title: '', content: '', time: new Date().toISOString().slice(0, 16), imageUrl: '' }
      this.showModal = true
    },
    openEdit(row) {
      this.editId = row.id
      this.form = {
        title: row.title || '',
        content: row.content || '',
        time: (row.time || '').slice(0, 16),
        imageUrl: row.imageUrl || ''
      }
      this.showModal = true
    },
    imageFullUrl(url) {
      if (!url) return ''
      const base = (typeof import.meta !== 'undefined' && import.meta.env && import.meta.env.VITE_API_BASE) ? import.meta.env.VITE_API_BASE : 'http://localhost:8080'
      return url.startsWith('http') ? url : base + (url.startsWith('/') ? '' : '') + url
    },
    async onImageSelect(e) {
      const file = e.target && e.target.files && e.target.files[0]
      if (!file) return
      const fd = new FormData()
      fd.append('file', file)
      try {
        const res = await request('/api/config/admin/upload?type=news', { method: 'POST', body: fd })
        if (res.code === 200 && res.data) {
          this.form.imageUrl = res.data
        } else {
          alert(res.message || '上传失败')
        }
      } catch (err) {
        alert(err.message || '上传失败')
      }
      e.target.value = ''
    },
    async submit() {
      this.saving = true
      try {
        const payload = {
          title: this.form.title,
          content: this.form.content,
          time: this.form.time ? this.form.time + ':00' : null,
          imageUrl: this.form.imageUrl && this.form.imageUrl.trim() ? this.form.imageUrl.trim() : null
        }
        if (this.editId) {
          payload.id = this.editId
          await request('/lab-news/update', { method: 'PUT', body: JSON.stringify(payload) })
        } else {
          await request('/lab-news/add', { method: 'POST', body: JSON.stringify(payload) })
        }
        this.showModal = false
        this.load(this.pageNum)
      } catch (e) {
        alert(e.message || '操作失败')
      } finally {
        this.saving = false
      }
    },
    async remove(id) {
      if (!confirm('确定删除？')) return
      try {
        await request(`/lab-news/delete/${id}`, { method: 'DELETE' })
        this.load(this.pageNum)
      } catch (e) {
        alert(e.message || '删除失败')
      }
    }
  }
}
</script>

<style scoped>
.crud-page { padding: 0; }
.page-title { font-size: 24px; margin-bottom: 20px; color: #333; }
.toolbar { margin-bottom: 16px; display: flex; flex-wrap: wrap; align-items: center; gap: 12px; }
.filter-wrap { flex: 1; background: #fafafa; border: 1px solid #e8e8e8; border-radius: 8px; padding: 12px 16px; }
.filter-row { display: flex; flex-wrap: wrap; align-items: center; gap: 10px; margin: 0; }
.filter-label { font-size: 13px; color: #666; white-space: nowrap; }
.filter-label.time-label { margin-left: 8px; }
.filter-input { padding: 8px 10px; border: 1px solid #d9d9d9; border-radius: 6px; font-size: 13px; }
.filter-input-text { min-width: 160px; }
.filter-date { width: 132px; }
.filter-time { width: 88px; }
.filter-time-group { display: inline-flex; align-items: center; gap: 6px; flex-wrap: wrap; }
.filter-sep { font-size: 12px; color: #999; }
.dt-wrap { position: relative; display: inline-block; }
.dt-trigger {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 6px 10px;
  min-width: 72px; max-width: 100px;
  border: 1px solid #d9d9d9; border-radius: 6px;
  background: #fff; color: #595959;
  font-size: 12px; cursor: pointer;
  transition: border-color 0.2s, color 0.2s;
}
.dt-trigger:hover { border-color: #1890ff; color: #1890ff; }
.dt-trigger.filled { color: #1890ff; border-color: #91d5ff; background: #e6f7ff; }
.dt-icon { flex-shrink: 0; display: inline-flex; }
.dt-text { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.dt-popover {
  position: absolute; left: 0; top: calc(100% + 4px); z-index: 1000;
  min-width: 200px; padding: 12px;
  background: #fff; border: 1px solid #e8e8e8; border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.12);
}
.dt-popover-title { font-size: 12px; color: #8c8c8c; margin-bottom: 8px; }
.date-only-wrap { position: relative; padding: 8px 12px; border: 1px solid #d9d9d9; border-radius: 6px; height: 40px; display: flex; align-items: center; cursor: pointer; margin-bottom: 10px; }
.date-only-text { color: #333; font-size: 13px; pointer-events: none; }
.date-only-input { position: absolute; left: 0; top: 0; width: 100%; height: 100%; opacity: 0; cursor: pointer; z-index: 1; font-size: 16px; }
.dt-popover-actions { display: flex; justify-content: flex-end; gap: 8px; }
.dt-btn-clear { padding: 4px 10px; font-size: 12px; color: #666; background: #fff; border: 1px solid #d9d9d9; border-radius: 4px; cursor: pointer; }
.dt-btn-ok { padding: 4px 12px; font-size: 12px; color: #fff; background: #1890ff; border: none; border-radius: 4px; cursor: pointer; }
.btn-query { padding: 8px 16px; background: #1890ff; color: #fff; border: none; border-radius: 6px; cursor: pointer; font-size: 13px; }
.btn-clear { padding: 8px 12px; background: #fff; color: #666; border: 1px solid #d9d9d9; border-radius: 6px; cursor: pointer; font-size: 13px; }
.btn-clear:hover { color: #ff4d4f; border-color: #ff4d4f; }
.data-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.data-table th, .data-table td { padding: 12px 16px; text-align: left; border-bottom: 1px solid #f0f0f0; }
.data-table th { background: #fafafa; font-weight: 500; }
.btn-link { background: none; border: none; color: #1890ff; cursor: pointer; padding: 0 8px; }
.btn-link.danger { color: #ff4d4f; }
.pagination { margin-top: 16px; display: flex; align-items: center; gap: 16px; }
.pagination button { padding: 6px 12px; border: 1px solid #d9d9d9; background: #fff; border-radius: 4px; cursor: pointer; }
.pagination button:disabled { opacity: 0.5; cursor: not-allowed; }
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: 16px; }
.modal { background: #fff; border-radius: 8px; padding: 28px; min-width: 900px; width: 94vw; max-width: 1200px; min-height: 480px; max-height: 92vh; overflow-y: auto; }
.modal h3 { margin-bottom: 24px; font-size: 20px; }
.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 500; font-size: 14px; }
.form-input { width: 100%; padding: 10px 14px; border: 1px solid #d9d9d9; border-radius: 4px; font-size: 14px; }
.content-group .content-field { width: 65%; min-width: 300px; resize: vertical; min-height: 120px; }
.images-upload { display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-start; }
.image-item { position: relative; width: 100px; height: 100px; border: 1px solid #e8e8e8; border-radius: 8px; overflow: hidden; }
.image-item img { width: 100%; height: 100%; object-fit: cover; }
.image-remove { position: absolute; bottom: 0; left: 0; right: 0; padding: 4px; font-size: 12px; background: rgba(0,0,0,0.6); color: #fff; border: none; cursor: pointer; }
.image-add { width: 100px; height: 100px; border: 1px dashed #d9d9d9; border-radius: 8px; display: flex; align-items: center; justify-content: center; cursor: pointer; color: #666; font-size: 13px; }
.image-add:hover { border-color: #1890ff; color: #1890ff; }
.content-cell { max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.modal-footer { margin-top: 28px; padding-top: 20px; border-top: 1px solid #f0f0f0; display: flex; justify-content: flex-end; gap: 12px; }
.btn { padding: 10px 20px; border-radius: 4px; border: none; cursor: pointer; font-size: 14px; }
.btn-primary { background: #1890ff; color: #fff; }
</style>
