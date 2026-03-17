<template>
  <div class="crud-page">
    <h2 class="page-title">师资队伍</h2>
    <div class="toolbar"><button type="button" class="btn btn-primary" @click="openAdd">新增</button></div>
    <table class="data-table">
      <thead>
        <tr><th>ID</th><th>姓名</th><th>领域</th><th>级别</th><th>照片</th><th width="140">操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="row in list" :key="row.id">
          <td>{{ row.id }}</td>
          <td>{{ row.name }}</td>
          <td>{{ areaName(row.area) }}</td>
          <td>{{ row.rank }}</td>
          <td>
            <img v-if="row.photo" :src="imageUrl(row.photo)" class="thumb" alt="" />
            <span v-else class="no-photo">—</span>
          </td>
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
        <h3>{{ editId ? "编辑" : "新增" }}学者</h3>
        <div class="form-group">
          <label>姓名</label>
          <input v-model="form.name" type="text" class="form-input" />
        </div>
        <div class="form-group">
          <label>领域ID</label>
          <input v-model.number="form.areaId" type="number" class="form-input" />
        </div>
        <div class="form-group">
          <label>级别</label>
          <input v-model.number="form.rank" type="number" class="form-input" />
        </div>
        <div class="form-group">
          <label>照片</label>
          <div class="photo-upload">
            <input ref="photoInput" type="file" accept="image/*" class="hidden" @change="onPhotoChange" />
            <button type="button" class="btn btn-secondary" @click="$refs.photoInput.click()">
              {{ form.photo ? "更换图片" : "上传图片" }}
            </button>
            <span v-if="uploading" class="upload-status">上传中…</span>
            <div v-if="form.photo" class="photo-preview">
              <img :src="imageUrl(form.photo)" alt="" />
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn" @click="showModal = false">取消</button>
          <button type="button" class="btn btn-primary" :disabled="saving" @click="submit">{{ saving ? "保存中…" : "保存" }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { request, API_BASE } from "../../api/auth"

export default {
  name: "ScholarCrud",
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
      uploading: false,
      form: { name: "", areaId: 0, rank: 0, photo: "", introList: [], paperList: [], projectList: [] }
    }
  },
  mounted() { this.load(1) },
  methods: {
    imageUrl(url) {
      if (!url) return ""
      return url.startsWith("http") ? url : API_BASE + url
    },
    areaName(id) { return "区域" + id },
    async load(p) {
      const data = await request("/team/scholar/page?pageNum=" + p + "&pageSize=" + this.pageSize)
      if (data.code === 200 && data.data) {
        this.list = data.data.records || []
        this.pageNum = data.data.current || 1
        this.total = data.data.total || 0
        this.pages = data.data.pages || 0
      } else {
        this.list = []
      }
    },
    openAdd() {
      this.editId = null
      this.form = { name: "", areaId: 0, rank: 0, photo: "", introList: [], paperList: [], projectList: [] }
      this.showModal = true
    },
    openEdit(row) {
      this.editId = row.id
      this.form = {
        name: row.name || "",
        areaId: row.area || 0,
        rank: row.rank || 0,
        photo: row.photo || "",
        introList: [],
        paperList: [],
        projectList: []
      }
      this.showModal = true
    },
    async onPhotoChange(e) {
      const file = e.target.files && e.target.files[0]
      e.target.value = ""
      if (!file) return
      this.uploading = true
      try {
        const formData = new FormData()
        formData.append("file", file)
        formData.append("type", "scholar")
        const token = (typeof localStorage !== "undefined" && localStorage.getItem("admin_token")) || ""
        const headers = {}
        if (token) headers["Authorization"] = "Bearer " + token
        const res = await fetch(API_BASE + "/api/config/admin/upload", {
          method: "POST",
          headers,
          body: formData
        })
        const data = await res.json()
        if (data.code === 200 && data.data) {
          this.form.photo = data.data
        } else {
          alert(data.message || "上传失败")
        }
      } catch (err) {
        alert(err.message || "上传失败")
      } finally {
        this.uploading = false
      }
    },
    async submit() {
      this.saving = true
      try {
        const payload = {
          name: this.form.name,
          areaId: this.form.areaId,
          rank: this.form.rank,
          photo: this.form.photo || "",
          introList: this.form.introList || [],
          paperList: this.form.paperList || [],
          projectList: this.form.projectList || []
        }
        if (this.editId) {
          payload.id = this.editId
          await request("/team/scholar/update", { method: "PUT", body: JSON.stringify(payload) })
        } else {
          await request("/team/scholar/add", { method: "POST", body: JSON.stringify(payload) })
        }
        this.showModal = false
        this.load(this.pageNum)
      } catch (e) { alert(e.message || "操作失败") }
      finally { this.saving = false }
    },
    async remove(id) {
      if (!confirm("确定删除？")) return
      try {
        await request("/team/scholar/delete/" + id, { method: "DELETE" })
        this.load(this.pageNum)
      } catch (e) { alert(e.message || "删除失败") }
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
.thumb { width: 36px; height: 36px; object-fit: cover; border-radius: 4px; }
.no-photo { color: #999; }
.btn-link { background: none; border: none; color: #1890ff; cursor: pointer; padding: 0 8px; }
.btn-link.danger { color: #ff4d4f; }
.pagination { margin-top: 16px; display: flex; align-items: center; gap: 16px; }
.pagination button { padding: 6px 12px; border: 1px solid #d9d9d9; background: #fff; border-radius: 4px; cursor: pointer; }
.pagination button:disabled { opacity: 0.5; cursor: not-allowed; }
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: #fff; border-radius: 8px; padding: 24px; min-width: 400px; max-width: 90vw; max-height: 90vh; overflow-y: auto; }
.modal h3 { margin-bottom: 20px; }
.form-group { margin-bottom: 16px; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 500; }
.form-input { width: 100%; padding: 8px 12px; border: 1px solid #d9d9d9; border-radius: 4px; }
.hidden { display: none; }
.photo-upload { display: flex; flex-wrap: wrap; align-items: center; gap: 12px; }
.photo-upload .btn-secondary { background: #f0f0f0; color: #333; }
.upload-status { font-size: 14px; color: #666; }
.photo-preview img { width: 80px; height: 80px; object-fit: cover; border-radius: 4px; border: 1px solid #e8e8e8; }
.modal-footer { margin-top: 24px; display: flex; justify-content: flex-end; gap: 12px; }
.btn { padding: 8px 16px; border-radius: 4px; border: none; cursor: pointer; }
.btn-primary { background: #1890ff; color: #fff; }
</style>
