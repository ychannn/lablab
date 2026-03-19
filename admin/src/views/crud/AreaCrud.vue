<template>
  <div class="crud-page">
    <h2 class="page-title">领域管理</h2>
    <div class="toolbar">
      <div class="filter-wrap">
        <form class="filter-row" @submit.prevent="onSearch">
          <label class="filter-label">关键词</label>
          <input v-model="filter.keyword" type="text" placeholder="领域名称" class="filter-input filter-input-text" />
          <button type="submit" class="btn btn-query">查询</button>
          <button type="button" class="btn btn-clear" @click="onResetFilter" title="清空筛选">清空</button>
        </form>
      </div>
      <button type="button" class="btn btn-primary" @click="openAdd">新增</button>
    </div>
    <table class="data-table">
      <thead>
        <tr><th>ID</th><th>领域名称</th><th width="140">操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="row in list" :key="row.id">
          <td>{{ row.id }}</td>
          <td>{{ row.title }}</td>
          <td>
            <button type="button" class="btn-link" @click="openEdit(row)">编辑</button>
            <button type="button" class="btn-link danger" @click="remove(row.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="showModal" class="modal-mask" @click.self="showModal = false">
      <div class="modal">
        <h3>{{ editId ? "编辑" : "新增" }}领域</h3>
        <div class="form-group"><label>领域名称</label><input v-model="form.title" type="text" class="form-input" /></div>
        <div class="modal-footer">
          <button type="button" class="btn" @click="showModal = false">取消</button>
          <button type="button" class="btn btn-primary" :disabled="saving" @click="submit">{{ saving ? "保存中…" : "保存" }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { request } from "../../api/auth"

export default {
  name: "AreaCrud",
  data() {
    return {
      list: [],
      showModal: false,
      editId: null,
      saving: false,
      form: { title: "" },
      filter: { keyword: "" }
    }
  },
  mounted() { this.load() },
  methods: {
    async load() {
      const url = this.filter.keyword && this.filter.keyword.trim()
        ? "/area/list?keyword=" + encodeURIComponent(this.filter.keyword.trim())
        : "/area/list"
      const data = await request(url)
      if (data.code === 200 && Array.isArray(data.data)) this.list = data.data
    },
    onSearch() { this.load() },
    onResetFilter() { this.filter = { keyword: "" }; this.load() },
    openAdd() { this.editId = null; this.form = { title: "" }; this.showModal = true },
    openEdit(row) { this.editId = row.id; this.form = { title: row.title || "" }; this.showModal = true },
    async submit() {
      this.saving = true
      try {
        const payload = { title: this.form.title }
        if (this.editId) { payload.id = this.editId; await request("/area/update", { method: "PUT", body: JSON.stringify(payload) }) }
        else { await request("/area/add", { method: "POST", body: JSON.stringify(payload) }) }
        this.showModal = false
        this.load()
      } catch (e) { alert(e.message || "操作失败") }
      finally { this.saving = false }
    },
    async remove(id) {
      if (!confirm("确定删除？")) return
      try { await request("/area/delete/" + id, { method: "DELETE" }); this.load() }
      catch (e) { alert(e.message || "删除失败") }
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
.filter-input-text { min-width: 180px; }
.btn-query { padding: 8px 16px; background: #1890ff; color: #fff; border: none; border-radius: 6px; cursor: pointer; font-size: 13px; }
.btn-clear { padding: 8px 12px; background: #fff; color: #666; border: 1px solid #d9d9d9; border-radius: 6px; cursor: pointer; font-size: 13px; }
.btn-clear:hover { color: #ff4d4f; border-color: #ff4d4f; }
.data-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.data-table th, .data-table td { padding: 12px 16px; text-align: left; border-bottom: 1px solid #f0f0f0; }
.data-table th { background: #fafafa; font-weight: 500; }
.btn-link { background: none; border: none; color: #1890ff; cursor: pointer; padding: 0 8px; }
.btn-link.danger { color: #ff4d4f; }
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: 16px; }
.modal { background: #fff; border-radius: 8px; padding: 28px; min-width: 900px; width: 94vw; max-width: 1200px; min-height: 480px; max-height: 92vh; overflow-y: auto; }
.modal h3 { margin-bottom: 24px; font-size: 20px; }
.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 500; font-size: 14px; }
.form-input { width: 100%; padding: 10px 14px; border: 1px solid #d9d9d9; border-radius: 4px; font-size: 14px; }
.modal-footer { margin-top: 28px; padding-top: 20px; border-top: 1px solid #f0f0f0; display: flex; justify-content: flex-end; gap: 12px; }
.btn { padding: 10px 20px; border-radius: 4px; border: none; cursor: pointer; font-size: 14px; }
.btn-primary { background: #1890ff; color: #fff; }
</style>
