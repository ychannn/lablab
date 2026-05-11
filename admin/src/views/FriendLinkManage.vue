<template>
  <div class="friend-link-manage">
    <h2 class="page-title">友情链接管理</h2>
    
    <!-- 添加友情链接 -->
    <div class="form-card">
      <h3 class="form-title">添加友情链接</h3>
      <form @submit.prevent="addLink">
        <div class="form-row">
          <div class="form-group">
            <label>链接名称</label>
            <input v-model="newLink.name" type="text" class="form-input" placeholder="链接名称" maxlength="50" required />
          </div>
          <div class="form-group">
            <label>链接地址</label>
            <input v-model="newLink.url" type="url" class="form-input" placeholder="https://example.com" maxlength="200" required />
          </div>
        </div>
        <button type="submit" class="btn btn-primary" :disabled="adding">
          {{ adding ? '添加中…' : '添加' }}
        </button>
      </form>
    </div>

    <!-- 友情链接列表 -->
    <div class="list-card">
      <h3 class="list-title">友情链接列表</h3>
      <table class="link-table">
        <thead>
          <tr>
            <th>序号</th>
            <th>链接名称</th>
            <th>链接地址</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(link, index) in links" :key="link.id || index">
            <td>{{ index + 1 }}</td>
            <td>{{ link.name }}</td>
            <td><a :href="link.url" target="_blank" class="link-url">{{ link.url }}</a></td>
            <td>
              <button type="button" class="btn-edit" @click="editLink(link)">编辑</button>
              <button type="button" class="btn-delete" @click="deleteLink(link)">删除</button>
            </td>
          </tr>
          <tr v-if="links.length === 0">
            <td colspan="4" class="empty-cell">暂无友情链接</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 编辑弹窗 -->
    <div v-if="editingLink" class="modal-mask" @click.self="cancelEdit">
      <div class="modal-edit">
        <h3>编辑友情链接</h3>
        <form @submit.prevent="saveEdit">
          <div class="form-group">
            <label>链接名称</label>
            <input v-model="editingLink.name" type="text" class="form-input" placeholder="链接名称" maxlength="50" required />
          </div>
          <div class="form-group">
            <label>链接地址</label>
            <input v-model="editingLink.url" type="url" class="form-input" placeholder="https://example.com" maxlength="200" required />
          </div>
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="cancelEdit">取消</button>
            <button type="submit" class="btn-submit" :disabled="saving">{{ saving ? '保存中…' : '保存' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { request } from '../api/auth'

export default {
  name: 'FriendLinkManage',
  data() {
    return {
      links: [],
      newLink: {
        name: '',
        url: ''
      },
      editingLink: null,
      adding: false,
      saving: false
    }
  },
  mounted() {
    this.fetchLinks()
  },
  methods: {
    async fetchLinks() {
      try {
        const data = await request('/config/friend-links')
        if (data.code === 200) {
          this.links = data.data || []
        }
      } catch (e) {
        console.error(e)
      }
    },
    async addLink() {
      this.adding = true
      try {
        const data = await request('/config/friend-links', {
          method: 'POST',
          body: JSON.stringify(this.newLink)
        })
        if (data.code === 200) {
          alert('添加成功')
          this.newLink = { name: '', url: '' }
          this.fetchLinks()
        } else {
          alert(data.message || '添加失败')
        }
      } catch (e) {
        alert(e.message || '添加失败')
      } finally {
        this.adding = false
      }
    },
    editLink(link) {
      this.editingLink = { ...link }
    },
    cancelEdit() {
      this.editingLink = null
    },
    async saveEdit() {
      this.saving = true
      try {
        const data = await request('/config/friend-links', {
          method: 'PUT',
          body: JSON.stringify(this.editingLink)
        })
        if (data.code === 200) {
          alert('保存成功')
          this.editingLink = null
          this.fetchLinks()
        } else {
          alert(data.message || '保存失败')
        }
      } catch (e) {
        alert(e.message || '保存失败')
      } finally {
        this.saving = false
      }
    },
    async deleteLink(link) {
      if (!confirm('确定要删除这个友情链接吗？')) return
      try {
        const data = await request(`/config/friend-links/${link.id}`, {
          method: 'DELETE'
        })
        if (data.code === 200) {
          alert('删除成功')
          this.fetchLinks()
        } else {
          alert(data.message || '删除失败')
        }
      } catch (e) {
        alert(e.message || '删除失败')
      }
    }
  }
}
</script>

<style scoped>
.friend-link-manage { padding: 0; }
.page-title { font-size: 24px; margin-bottom: 24px; color: #333; }

.form-card, .list-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  padding: 24px;
  margin-bottom: 24px;
}

.form-title, .list-title {
  font-size: 18px;
  margin-bottom: 16px;
  color: #333;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.form-group {
  flex: 1;
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-input:focus {
  border-color: #1890ff;
  outline: none;
}

.btn {
  padding: 8px 20px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary {
  background: #1890ff;
  color: #fff;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.link-table {
  width: 100%;
  border-collapse: collapse;
}

.link-table th,
.link-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e8e8e8;
}

.link-table th {
  background: #fafafa;
  font-weight: 600;
  color: #333;
}

.link-url {
  color: #1890ff;
  text-decoration: none;
  font-size: 13px;
}

.link-url:hover {
  text-decoration: underline;
}

.btn-edit, .btn-delete {
  padding: 4px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  margin-right: 8px;
}

.btn-edit {
  background: #f5f5f5;
  color: #666;
}

.btn-edit:hover {
  background: #e8e8e8;
}

.btn-delete {
  background: #fff;
  color: #f5222d;
  border: 1px solid #ffccc7;
}

.btn-delete:hover {
  background: #fff2f0;
}

.empty-cell {
  text-align: center;
  color: #999;
  padding: 32px;
}

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
  width: 400px;
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

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>