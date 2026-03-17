<template>
  <div class="crud-page">
    <h2 class="page-title">研究方向</h2>
    <div class="toolbar"><button type="button" class="btn btn-primary" @click="openAdd">新增</button></div>
    <table class="data-table">
      <thead>
        <tr><th>ID</th><th>标题</th><th>内容</th><th width="140">操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="row in list" :key="row.id">
          <td>{{ row.id }}</td>
          <td>{{ row.title }}</td>
          <td>{{ (row.content || '').slice(0, 50) }}{{ (row.content || '').length > 50 ? '…' : '' }}</td>
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
        <h3>{{ editId ? '编辑' : '新增' }}研究方向</h3>
        <div class="form-group">
          <label>标题</label>
          <input v-model="form.title" type="text" class="form-input" />
        </div>
        <div class="form-group">
          <label>内容</label>
          <textarea v-model="form.content" class="form-input" rows="5"></textarea>
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
  name: 'DirectionCrud',
  data() {
    return {
      list: [], pageNum: 1, pageSize: 10, total: 0, pages: 0,
      showModal: false, editId: null, saving: false,
      form: { title: '', content: '' }
    }
  },
  mounted() { this.load(1) },
  methods: {
    async load(p) {
      const data = await request(`/research/direction/page?pageNum=${p}&pageSize=${this.pageSize}`)
      if (data.code === 200 && data.data) {
        this.list = data.data.records || []
        this.pageNum = data.data.current || 1
        this.total = data.data.total || 0
        this.pages = data.data.pages || 0
      }
    },
    openAdd() {
      this.editId = null
      this.form = { title: '', content: '' }
      this.showModal = true
    },
    openEdit(row) {
      this.editId = row.id
      this.form = { title: row.title || '', content: row.content || '' }
      this.showModal = true
    },
    async submit() {
      this.saving = true
      try {
        const payload = { title: this.form.title, content: this.form.content }
        if (this.editId) {
          payload.id = this.editId
          await request('/research/direction/update', { method: 'PUT', body: JSON.stringify(payload) })
        } else {
          await request('/research/direction/add', { method: 'POST', body: JSON.stringify(payload) })
        }
        this.showModal = false
        this.load(this.pageNum)
      } catch (e) { alert(e.message || '操作失败') }
      finally { this.saving = false }
    },
    async remove(id) {
      if (!confirm('确定删除？')) return
      try {
        await request(`/research/direction/delete/${id}`, { method: 'DELETE' })
        this.load(this.pageNum)
      } catch (e) { alert(e.message || '删除失败') }
    }
  }
}
</script>

<style scoped>
.crud-page { padding: 0; }
.page-title { font-size: 24px; margin-bottom: 20px; color: #333; }
.toolbar { margin-bottom: 16px; }
.data-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.data-table th, .data-table td { padding: 12px 16px; text-align: left; border-bottom: 1px solid #f0f0f0; }
.data-table th { background: #fafafa; font-weight: 500; }
.btn-link { background: none; border: none; color: #1890ff; cursor: pointer; padding: 0 8px; }
.btn-link.danger { color: #ff4d4f; }
.pagination { margin-top: 16px; display: flex; align-items: center; gap: 16px; }
.pagination button { padding: 6px 12px; border: 1px solid #d9d9d9; background: #fff; border-radius: 4px; cursor: pointer; }
.pagination button:disabled { opacity: 0.5; cursor: not-allowed; }
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: #fff; border-radius: 8px; padding: 24px; min-width: 400px; max-width: 90vw; }
.modal h3 { margin-bottom: 20px; }
.form-group { margin-bottom: 16px; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 500; }
.form-input { width: 100%; padding: 8px 12px; border: 1px solid #d9d9d9; border-radius: 4px; }
.modal-footer { margin-top: 24px; display: flex; justify-content: flex-end; gap: 12px; }
.btn { padding: 8px 16px; border-radius: 4px; border: none; cursor: pointer; }
.btn-primary { background: #1890ff; color: #fff; }
</style>
