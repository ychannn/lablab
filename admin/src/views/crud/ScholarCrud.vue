<template>
  <div class="crud-page">
    <h2 class="page-title">师资队伍</h2>
    <div class="toolbar">
      <div class="filter-wrap">
        <form class="filter-row" @submit.prevent="onSearch">
          <label class="filter-label">关键词</label>
          <input v-model="filter.keyword" type="text" placeholder="姓名/邮箱" class="filter-input filter-input-text" />
          <label class="filter-label">领域</label>
          <select v-model.number="filter.areaId" class="filter-input filter-select">
            <option :value="0">全部领域</option>
            <option v-for="a in areaList" :key="a.id" :value="a.id">{{ a.title }}</option>
          </select>
          <label class="filter-label">职级</label>
          <select v-model.number="filter.rank" class="filter-input filter-select">
            <option :value="0">全部职级</option>
            <option v-for="r in rankOptions" :key="r.code" :value="r.code">{{ r.label }}</option>
          </select>
          <button type="submit" class="btn btn-query">查询</button>
          <button type="button" class="btn btn-clear" @click="onResetFilter" title="清空筛选">清空</button>
        </form>
      </div>
      <button type="button" class="btn btn-primary" @click="openAdd">新增</button>
    </div>
    <table class="data-table">
      <thead>
        <tr><th>ID</th><th>姓名</th><th>领域</th><th>职级</th><th>照片</th><th width="140">操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="row in list" :key="row.id">
          <td>{{ row.id }}</td>
          <td>{{ row.name }}</td>
          <td>{{ row.areaName || areaName(row.area) }}</td>
          <td>{{ row.rankLabel || row.rank }}</td>
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
          <label>领域</label>
          <select v-model.number="form.areaId" class="form-input">
            <option v-for="a in areaList" :key="a.id" :value="a.id">{{ a.title }}</option>
            <option v-if="!areaList.length" :value="0">请先添加领域</option>
          </select>
        </div>
        <div class="form-group">
          <label>职级</label>
          <select v-model.number="form.rank" class="form-input">
            <option v-for="r in rankOptions" :key="r.code" :value="r.code">{{ r.label }}</option>
          </select>
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
        <div class="form-group">
          <label>个人简介</label>
          <div class="list-edit">
            <div v-for="(item, idx) in form.introList" :key="'intro-' + idx" class="list-row">
              <textarea v-model="item.content" class="form-input" rows="2" placeholder="一段简介"></textarea>
              <button type="button" class="btn-link danger" @click="form.introList.splice(idx, 1)">删除</button>
            </div>
            <button type="button" class="btn btn-secondary" @click="form.introList.push({ content: '' })">+ 添加一条</button>
          </div>
        </div>
        <div class="form-group">
          <label>论文发表</label>
          <div class="list-edit">
            <div v-for="(item, idx) in form.paperList" :key="'paper-' + idx" class="list-row">
              <input v-model="item.content" type="text" class="form-input" placeholder="论文标题/内容" />
              <button type="button" class="btn-link danger" @click="form.paperList.splice(idx, 1)">删除</button>
            </div>
            <button type="button" class="btn btn-secondary" @click="form.paperList.push({ content: '' })">+ 添加一条</button>
          </div>
        </div>
        <div class="form-group">
          <label>科研项目</label>
          <div class="list-edit">
            <div v-for="(item, idx) in form.projectList" :key="'project-' + idx" class="list-row">
              <input v-model="item.content" type="text" class="form-input" placeholder="项目内容" />
              <button type="button" class="btn-link danger" @click="form.projectList.splice(idx, 1)">删除</button>
            </div>
            <button type="button" class="btn btn-secondary" @click="form.projectList.push({ content: '' })">+ 添加一条</button>
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
      areaList: [],
      rankOptions: [],
      filter: { keyword: "", areaId: 0, rank: 0 },
      form: { name: "", areaId: 0, rank: 1, photo: "", introList: [], paperList: [], projectList: [] }
    }
  },
  mounted() {
    this.loadAreas()
    this.loadRanks()
    this.load(1)
  },
  methods: {
    imageUrl(url) {
      if (!url) return ""
      return url.startsWith("http") ? url : url
    },
    areaName(id) { return this.areaList.find((a) => a.id === id)?.title ?? "领域" + (id || "") },
    async loadAreas() {
      try {
        const data = await request("/area/list")
        if (data.code === 200 && Array.isArray(data.data)) this.areaList = data.data
      } catch (e) { this.areaList = [] }
    },
    async loadRanks() {
      try {
        const data = await request("/team/scholar/ranks")
        if (data.code === 200 && Array.isArray(data.data)) this.rankOptions = data.data
        else this.rankOptions = []
      } catch (e) { this.rankOptions = [] }
    },
    buildPageQuery(p) {
      const params = new URLSearchParams()
      params.set("pageNum", String(p))
      params.set("pageSize", String(this.pageSize))
      if (this.filter.keyword && this.filter.keyword.trim()) params.set("keyword", this.filter.keyword.trim())
      if (this.filter.areaId > 0) params.set("areaId", String(this.filter.areaId))
      if (this.filter.rank > 0) params.set("rank", String(this.filter.rank))
      return "/team/scholar/page?" + params.toString()
    },
    async load(p) {
      const data = await request(this.buildPageQuery(p))
      if (data.code === 200 && data.data) {
        this.list = data.data.records || []
        this.pageNum = data.data.current || 1
        this.total = data.data.total || 0
        this.pages = data.data.pages || 0
      } else {
        this.list = []
      }
    },
    onSearch() { this.load(1) },
    onResetFilter() { this.filter = { keyword: "", areaId: 0, rank: 0 }; this.load(1) },
    openAdd() {
      this.editId = null
      this.form = { name: "", areaId: 0, rank: 1, photo: "", introList: [], paperList: [], projectList: [] }
      this.showModal = true
    },
    async openEdit(row) {
      this.editId = row.id
      this.form = {
        name: row.name || "",
        areaId: row.area ?? row.areaId ?? 0,
        rank: row.rank ?? 0,
        photo: row.photo || "",
        introList: [],
        paperList: [],
        projectList: []
      }
      this.showModal = true
      try {
        const data = await request("/team/scholar/details/" + row.id)
        if (data.code === 200 && data.data) {
          const d = data.data
          this.form.name = d.name || this.form.name
          this.form.areaId = d.area ?? this.form.areaId
          this.form.rank = d.rank ?? this.form.rank
          this.form.photo = d.photo || this.form.photo
          this.form.introList = (d.intro || []).map(i => ({ content: i.content || "" }))
          this.form.paperList = (d.paperList || []).map(p => ({ content: p.content || "" }))
          this.form.projectList = (d.projectList || []).map(p => ({ content: p.content || "" }))
        }
      } catch (e) {
        console.error("加载学者详情失败", e)
      }
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
        const res = await fetch(API_BASE + "/config/admin/upload", {
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
          introList: (this.form.introList || []).filter(i => (i.content || "").trim() !== "").map(i => ({ content: (i.content || "").trim() })),
          paperList: (this.form.paperList || []).filter(p => (p.content || "").trim() !== "").map(p => ({ content: (p.content || "").trim() })),
          projectList: (this.form.projectList || []).filter(p => (p.content || "").trim() !== "").map(p => ({ content: (p.content || "").trim() }))
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
.toolbar { margin-bottom: 16px; display: flex; flex-wrap: wrap; align-items: center; gap: 12px; }
.filter-wrap { flex: 1; background: #fafafa; border: 1px solid #e8e8e8; border-radius: 8px; padding: 12px 16px; }
.filter-row { display: flex; flex-wrap: wrap; align-items: center; gap: 10px; margin: 0; }
.filter-label { font-size: 13px; color: #666; }
.filter-input { padding: 8px 10px; border: 1px solid #d9d9d9; border-radius: 6px; font-size: 13px; }
.filter-input-text { min-width: 140px; }
.filter-select { min-width: 100px; }
.btn-query { padding: 8px 16px; background: #1890ff; color: #fff; border: none; border-radius: 6px; cursor: pointer; font-size: 13px; }
.btn-clear { padding: 8px 12px; background: #fff; color: #666; border: 1px solid #d9d9d9; border-radius: 6px; cursor: pointer; font-size: 13px; }
.btn-clear:hover { color: #ff4d4f; border-color: #ff4d4f; }
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
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: 16px; }
.modal { background: #fff; border-radius: 8px; padding: 28px; min-width: 900px; width: 94vw; max-width: 1200px; min-height: 480px; max-height: 92vh; overflow-y: auto; }
.modal h3 { margin-bottom: 24px; font-size: 20px; }
.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 500; font-size: 14px; }
.form-input { width: 100%; padding: 10px 14px; border: 1px solid #d9d9d9; border-radius: 4px; font-size: 14px; }
.hidden { display: none; }
.photo-upload { display: flex; flex-wrap: wrap; align-items: center; gap: 12px; }
.photo-upload .btn-secondary { background: #f0f0f0; color: #333; }
.upload-status { font-size: 14px; color: #666; }
.photo-preview img { width: 80px; height: 80px; object-fit: cover; border-radius: 4px; border: 1px solid #e8e8e8; }
.list-edit { border: 1px solid #e8e8e8; border-radius: 6px; padding: 12px; background: #fafafa; }
.list-row { display: flex; gap: 12px; align-items: flex-start; margin-bottom: 10px; }
.list-row .form-input { flex: 1; min-width: 0; }
.list-row textarea.form-input { min-height: 60px; resize: vertical; }
.list-row:last-of-type { margin-bottom: 0; }
.list-edit .btn-secondary { margin-top: 10px; }
.modal-footer { margin-top: 28px; padding-top: 20px; border-top: 1px solid #f0f0f0; display: flex; justify-content: flex-end; gap: 12px; }
.btn { padding: 10px 20px; border-radius: 4px; border: none; cursor: pointer; font-size: 14px; }
.btn-primary { background: #1890ff; color: #fff; }
</style>
