<template>
  <div class="crud-page">
    <h2 class="page-title">课题项目</h2>
    <div class="toolbar">
      <div class="filter-wrap">
        <div class="filter-row">
          <label class="filter-label">关键词</label>
          <input v-model="filter.keyword" type="text" placeholder="内容" class="filter-input filter-input-text" />
          <span class="filter-label time-label">开始</span>
          <div class="filter-time-group" ref="dtWrap">
            <div class="dt-wrap">
              <button type="button" class="dt-trigger" :class="{ filled: filter.startStartDate }" @click.stop="dtpOpen = dtpOpen === 'startStart' ? null : 'startStart'" title="起">
                <span class="dt-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span>
                <span class="dt-text">{{ filter.startStartDate ? dateLabel(filter.startStartDate) : '起' }}</span>
              </button>
              <div v-show="dtpOpen === 'startStart'" class="dt-popover" @click.stop>
                <div class="dt-popover-title">开始日期·起</div>
                <label class="date-only-wrap" for="topic-dtp-ss" @click="openDatePicker">
                  <span class="date-only-text">{{ filter.startStartDate ? dateLabel(filter.startStartDate) : '请选择日期' }}</span>
                  <input id="topic-dtp-ss" v-model="filter.startStartDate" type="date" class="date-only-input" />
                </label>
                <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtp('startStart')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
              </div>
            </div>
            <span class="filter-sep">至</span>
            <div class="dt-wrap">
              <button type="button" class="dt-trigger" :class="{ filled: filter.startEndDate }" @click.stop="dtpOpen = dtpOpen === 'startEnd' ? null : 'startEnd'" title="止">
                <span class="dt-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span>
                <span class="dt-text">{{ filter.startEndDate ? dateLabel(filter.startEndDate) : '止' }}</span>
              </button>
              <div v-show="dtpOpen === 'startEnd'" class="dt-popover" @click.stop>
                <div class="dt-popover-title">开始日期·止</div>
                <label class="date-only-wrap" for="topic-dtp-se" @click="openDatePicker">
                  <span class="date-only-text">{{ filter.startEndDate ? dateLabel(filter.startEndDate) : '请选择日期' }}</span>
                  <input id="topic-dtp-se" v-model="filter.startEndDate" type="date" class="date-only-input" />
                </label>
                <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtp('startEnd')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
              </div>
            </div>
          </div>
          <span class="filter-label time-label">结束</span>
          <div class="filter-time-group" ref="dtWrap2">
            <div class="dt-wrap">
              <button type="button" class="dt-trigger" :class="{ filled: filter.endStartDate }" @click.stop="dtpOpen = dtpOpen === 'endStart' ? null : 'endStart'" title="起">
                <span class="dt-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span>
                <span class="dt-text">{{ filter.endStartDate ? dateLabel(filter.endStartDate) : '起' }}</span>
              </button>
              <div v-show="dtpOpen === 'endStart'" class="dt-popover" @click.stop>
                <div class="dt-popover-title">结束日期·起</div>
                <label class="date-only-wrap" for="topic-dtp-es" @click="openDatePicker">
                  <span class="date-only-text">{{ filter.endStartDate ? dateLabel(filter.endStartDate) : '请选择日期' }}</span>
                  <input id="topic-dtp-es" v-model="filter.endStartDate" type="date" class="date-only-input" />
                </label>
                <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtp('endStart')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
              </div>
            </div>
            <span class="filter-sep">至</span>
            <div class="dt-wrap">
              <button type="button" class="dt-trigger" :class="{ filled: filter.endEndDate }" @click.stop="dtpOpen = dtpOpen === 'endEnd' ? null : 'endEnd'" title="止">
                <span class="dt-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span>
                <span class="dt-text">{{ filter.endEndDate ? dateLabel(filter.endEndDate) : '止' }}</span>
              </button>
              <div v-show="dtpOpen === 'endEnd'" class="dt-popover" @click.stop>
                <div class="dt-popover-title">结束日期·止</div>
                <label class="date-only-wrap" for="topic-dtp-ee" @click="openDatePicker">
                  <span class="date-only-text">{{ filter.endEndDate ? dateLabel(filter.endEndDate) : '请选择日期' }}</span>
                  <input id="topic-dtp-ee" v-model="filter.endEndDate" type="date" class="date-only-input" />
                </label>
                <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtp('endEnd')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
              </div>
            </div>
          </div>
          <button type="button" class="btn btn-query" @click="onSearch">查询</button>
          <button type="button" class="btn btn-clear" @click="onResetFilter" title="清空筛选">清空</button>
        </div>
      </div>
      <button type="button" class="btn btn-primary" @click="openAdd">新增</button>
    </div>
    <table class="data-table">
      <thead>
        <tr><th>ID</th><th>内容</th><th>开始时间</th><th>结束时间</th><th width="140">操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="row in list" :key="row.id">
          <td>{{ row.id }}</td>
          <td>{{ (row.content || "").slice(0, 40) }}{{ (row.content || "").length > 40 ? "…" : "" }}</td>
          <td>{{ formatTime(row.startTime) }}</td>
          <td>{{ formatTime(row.endTime) }}</td>
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
        <h3>{{ editId ? "编辑" : "新增" }}课题项目</h3>
        <div class="form-group content-group"><label>内容</label><textarea v-model="form.content" class="form-input content-field" rows="8"></textarea></div>
        <div class="form-group"><label>开始时间</label><input v-model="form.startTime" type="datetime-local" class="form-input" /></div>
        <div class="form-group"><label>结束时间</label><input v-model="form.endTime" type="datetime-local" class="form-input" /></div>
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
  name: "TopicProjectCrud",
  data() {
    return {
      list: [], pageNum: 1, pageSize: 10, total: 0, pages: 0,
      showModal: false, editId: null, saving: false,
      form: { content: "", startTime: "", endTime: "" },
      filter: { keyword: "", startStartDate: "", startStartTime: "", startEndDate: "", startEndTime: "", endStartDate: "", endStartTime: "", endEndDate: "", endEndTime: "" },
      dtpOpen: null
    }
  },
  mounted() {
    this.load(1)
    this.clickOutside = (e) => {
      const wrap = this.$refs.dtWrap && this.$refs.dtWrap.contains(e.target)
      const wrap2 = this.$refs.dtWrap2 && this.$refs.dtWrap2.contains(e.target)
      if (this.dtpOpen && !wrap && !wrap2) this.dtpOpen = null
    }
    document.addEventListener('click', this.clickOutside)
  },
  beforeDestroy() { document.removeEventListener('click', this.clickOutside) },
  methods: {
    openDatePicker(e) {
      const input = e.currentTarget.querySelector('input[type="date"]')
      if (input && typeof input.showPicker === 'function') input.showPicker()
    },
    dateLabel(d) {
      if (!d || d.length < 10) return ''
      return parseInt(d.slice(5, 7), 10) + '月' + parseInt(d.slice(8, 10), 10) + '日'
    },
    clearDtp(which) {
      const m = { startStart: ['startStartDate','startStartTime'], startEnd: ['startEndDate','startEndTime'], endStart: ['endStartDate','endStartTime'], endEnd: ['endEndDate','endEndTime'] }
      const [dKey, tKey] = m[which] || []
      if (dKey) { this.filter[dKey] = ''; this.filter[tKey] = '' }
      this.dtpOpen = null
    },
    formatTime(t) { return t ? (t.replace("T", " ").slice(0, 19)) : "" },
    buildPageQuery(p) {
      const params = new URLSearchParams()
      params.set("pageNum", String(p))
      params.set("pageSize", String(this.pageSize))
      if (this.filter.keyword && this.filter.keyword.trim()) params.set("keyword", this.filter.keyword.trim())
      const buildDt = (d, t, def) => (!d || !d.trim()) ? "" : (d + "T" + (t && t.trim() ? t : def) + ":00").slice(0, 19)
      const s = buildDt(this.filter.startStartDate, this.filter.startStartTime, "00:00"); if (s) params.set("startTimeStart", s)
      const e = buildDt(this.filter.startEndDate, this.filter.startEndTime, "23:59"); if (e) params.set("startTimeEnd", e)
      const s2 = buildDt(this.filter.endStartDate, this.filter.endStartTime, "00:00"); if (s2) params.set("endTimeStart", s2)
      const e2 = buildDt(this.filter.endEndDate, this.filter.endEndTime, "23:59"); if (e2) params.set("endTimeEnd", e2)
      return "/topic-project/page?" + params.toString()
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
    onSearch() { this.load(1) },
    onResetFilter() {
      this.filter = { keyword: "", startStartDate: "", startStartTime: "", startEndDate: "", startEndTime: "", endStartDate: "", endStartTime: "", endEndDate: "", endEndTime: "" }
      this.dtpOpen = null
      this.load(1)
    },
    openAdd() {
      this.editId = null
      const now = new Date().toISOString().slice(0, 16)
      this.form = { content: "", startTime: now, endTime: now }
      this.showModal = true
    },
    openEdit(row) {
      this.editId = row.id
      this.form = { content: row.content || "", startTime: (row.startTime || "").slice(0, 16), endTime: (row.endTime || "").slice(0, 16) }
      this.showModal = true
    },
    async submit() {
      this.saving = true
      try {
        const payload = { content: this.form.content, startTime: this.form.startTime ? this.form.startTime + ":00" : null, endTime: this.form.endTime ? this.form.endTime + ":00" : null }
        if (this.editId) { payload.id = this.editId; await request("/topic-project/update", { method: "PUT", body: JSON.stringify(payload) }) }
        else { await request("/topic-project/add", { method: "POST", body: JSON.stringify(payload) }) }
        this.showModal = false
        this.load(this.pageNum)
      } catch (e) { alert(e.message || "操作失败") }
      finally { this.saving = false }
    },
    async remove(id) {
      if (!confirm("确定删除？")) return
      try { await request("/topic-project/delete/" + id, { method: "DELETE" }); this.load(this.pageNum) }
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
.filter-row { display: flex; flex-wrap: wrap; align-items: center; gap: 10px; }
.filter-label { font-size: 13px; color: #666; }
.filter-label.time-label { margin-left: 8px; }
.filter-input { padding: 8px 10px; border: 1px solid #d9d9d9; border-radius: 6px; font-size: 13px; }
.filter-input-text { min-width: 160px; }
.filter-time-group { display: inline-flex; align-items: center; gap: 6px; flex-wrap: wrap; }
.filter-sep { font-size: 12px; color: #999; }
.dt-wrap { position: relative; display: inline-block; }
.dt-trigger { display: inline-flex; align-items: center; gap: 4px; padding: 6px 10px; min-width: 64px; max-width: 90px; border: 1px solid #d9d9d9; border-radius: 6px; background: #fff; color: #595959; font-size: 12px; cursor: pointer; }
.dt-trigger:hover { border-color: #1890ff; color: #1890ff; }
.dt-trigger.filled { color: #1890ff; border-color: #91d5ff; background: #e6f7ff; }
.dt-icon { flex-shrink: 0; display: inline-flex; }
.dt-text { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.dt-popover { position: absolute; left: 0; top: calc(100% + 4px); z-index: 1000; min-width: 200px; padding: 12px; background: #fff; border: 1px solid #e8e8e8; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.12); }
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
.modal-footer { margin-top: 28px; padding-top: 20px; border-top: 1px solid #f0f0f0; display: flex; justify-content: flex-end; gap: 12px; }
.btn { padding: 10px 20px; border-radius: 4px; border: none; cursor: pointer; font-size: 14px; }
.btn-primary { background: #1890ff; color: #fff; }
</style>
