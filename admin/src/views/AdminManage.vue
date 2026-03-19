<template>
  <div class="crud-page">
    <h2 class="page-title">管理员管理</h2>
    <p class="page-desc">仅超级管理员可管理子管理员（添加、移除）。子管理员无法访问本页。</p>
    <div class="toolbar">
      <button type="button" class="btn btn-primary" @click="openAdd">添加管理员</button>
    </div>
    <table class="data-table">
      <thead>
        <tr><th>ID</th><th>用户名</th><th>角色</th><th>绑定邮箱</th><th width="100">操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="row in list" :key="row.id">
          <td>{{ row.id }}</td>
          <td>{{ row.username }}</td>
          <td>{{ roleLabel(row.role) }}</td>
          <td>{{ row.email || '—' }}</td>
          <td>
            <button type="button" class="btn-link danger" :disabled="isSelf(row) || isSuperAdmin(row)" @click="remove(row)" :title="isSelf(row) ? '不能移除自己' : isSuperAdmin(row) ? '不能移除超级管理员' : '移除'">
              移除
            </button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="showModal" class="modal-mask" @click.self="showModal = false">
      <div class="modal">
        <h3>添加管理员</h3>
        <div class="form-group">
          <label>用户名</label>
          <input v-model="form.username" type="text" class="form-input" placeholder="登录账号" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="form.password" type="password" class="form-input" placeholder="6-32位" />
        </div>
        <div class="form-group">
          <label>角色</label>
          <select v-model="form.role" class="form-input" disabled title="仅支持添加操作员，系统仅允许一个超级管理员">
            <option value="operator">操作员</option>
          </select>
          <p class="form-hint">系统仅允许一个超级管理员，此处只能添加操作员。</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn" @click="showModal = false">取消</button>
          <button type="button" class="btn btn-primary" :disabled="saving" @click="submit">{{ saving ? '提交中…' : '添加' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { request } from '../api/auth'

export default {
  name: 'AdminManage',
  props: {
    currentUser: { type: Object, default: null }
  },
  data() {
    return {
      list: [],
      showModal: false,
      saving: false,
      form: { username: '', password: '', role: 'operator' }
    }
  },
  mounted() {
    this.load()
  },
  methods: {
    roleLabel(role) {
      return role === 'admin' ? '超级管理员' : role === 'operator' ? '操作员' : role || '—'
    },
    isSelf(row) {
      const info = this.currentUser
      return info && info.id === row.id
    },
    isSuperAdmin(row) {
      return row.role === 'admin'
    },
    async load() {
      try {
        const data = await request('/admin/list')
        if (data.code === 200 && Array.isArray(data.data)) this.list = data.data
      } catch (e) {
        alert(e.message || '加载失败')
      }
    },
    openAdd() {
      this.form = { username: '', password: '', role: 'operator' }
      this.showModal = true
    },
    async submit() {
      const { username, password, role } = this.form
      if (!username || !username.trim()) {
        alert('请输入用户名')
        return
      }
      if (!password || password.length < 6 || password.length > 32) {
        alert('密码长度为6-32位')
        return
      }
      this.saving = true
      try {
        await request('/admin/add', {
          method: 'POST',
          body: JSON.stringify({ username: username.trim(), password, role })
        })
        alert('添加成功')
        this.showModal = false
        this.load()
      } catch (e) {
        alert(e.message || '添加失败')
      } finally {
        this.saving = false
      }
    },
    async remove(row) {
      if (this.isSelf(row)) {
        alert('不能移除自己')
        return
      }
      if (this.isSuperAdmin(row)) {
        alert('不能移除超级管理员')
        return
      }
      if (!confirm(`确定移除管理员「${row.username}」？移除后该账号将无法登录。`)) return
      try {
        await request('/admin/' + row.id, { method: 'DELETE' })
        this.load()
      } catch (e) {
        alert(e.message || '移除失败')
      }
    }
  }
}
</script>

<style scoped>
.crud-page { padding: 0; }
.page-title { font-size: 24px; margin-bottom: 8px; color: #333; }
.page-desc { font-size: 13px; color: #666; margin-bottom: 20px; }
.toolbar { margin-bottom: 16px; }
.btn { padding: 8px 16px; border: 1px solid #d9d9d9; border-radius: 6px; background: #fff; cursor: pointer; font-size: 14px; }
.btn-primary { background: #1890ff; color: #fff; border-color: #1890ff; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.data-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.data-table th, .data-table td { padding: 12px 16px; text-align: left; border-bottom: 1px solid #f0f0f0; }
.data-table th { background: #fafafa; font-weight: 600; color: #333; }
.btn-link { background: none; border: none; color: #1890ff; cursor: pointer; padding: 0; font-size: 14px; }
.btn-link:hover { text-decoration: underline; }
.btn-link.danger { color: #ff4d4f; }
.btn-link:disabled { color: #bbb; cursor: not-allowed; text-decoration: none; }
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: #fff; border-radius: 8px; padding: 24px; min-width: 360px; }
.modal h3 { margin: 0 0 20px; font-size: 18px; }
.form-group { margin-bottom: 16px; }
.form-group label { display: block; margin-bottom: 6px; font-size: 14px; color: #333; }
.form-input { width: 100%; padding: 8px 12px; border: 1px solid #d9d9d9; border-radius: 6px; font-size: 14px; box-sizing: border-box; }
.form-hint { margin: 6px 0 0; font-size: 12px; color: #999; }
.modal-footer { margin-top: 24px; display: flex; justify-content: flex-end; gap: 12px; }
</style>
