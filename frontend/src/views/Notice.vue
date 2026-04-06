<template>
  <div class="notice">
    <div class="container">
      <h2 class="page-title">公告</h2>
      <div class="filter-wrap">
        <form class="filter-row" @submit.prevent="onSearch">
          <label class="filter-label">关键词</label>
          <input v-model="filter.keyword" type="text" placeholder="标题/内容" class="filter-input filter-input-text" />
          <span class="filter-label time-label">时间</span>
          <div class="filter-time-group" ref="dtWrap">
            <div class="dt-wrap">
              <button type="button" class="dt-trigger" :class="{ filled: filter.timeStartDate }" @click.stop="dtpOpen = dtpOpen === 'start' ? null : 'start'" title="开始时间">
                <span class="dt-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span>
                <span class="dt-text">{{ startDisplay }}</span>
              </button>
              <div v-show="dtpOpen === 'start'" class="dt-popover" @click.stop>
                <div class="dt-popover-title">开始日期</div>
                <label class="date-only-wrap" for="notice-dtp-start" @click="openDatePicker">
                  <span class="date-only-text">{{ filter.timeStartDate ? dateLabel(filter.timeStartDate) : '请选择日期' }}</span>
                  <input id="notice-dtp-start" v-model="filter.timeStartDate" type="date" class="date-only-input" />
                </label>
                <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtp('start')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
              </div>
            </div>
            <span class="filter-sep">至</span>
            <div class="dt-wrap">
              <button type="button" class="dt-trigger" :class="{ filled: filter.timeEndDate }" @click.stop="dtpOpen = dtpOpen === 'end' ? null : 'end'" title="结束时间">
                <span class="dt-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span>
                <span class="dt-text">{{ endDisplay }}</span>
              </button>
              <div v-show="dtpOpen === 'end'" class="dt-popover" @click.stop>
                <div class="dt-popover-title">结束日期</div>
                <label class="date-only-wrap" for="notice-dtp-end" @click="openDatePicker">
                  <span class="date-only-text">{{ filter.timeEndDate ? dateLabel(filter.timeEndDate) : '请选择日期' }}</span>
                  <input id="notice-dtp-end" v-model="filter.timeEndDate" type="date" class="date-only-input" />
                </label>
                <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtp('end')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
              </div>
            </div>
          </div>
          <button type="submit" class="filter-btn filter-btn-query">查询</button>
          <button type="button" class="filter-btn filter-btn-clear" @click="onResetFilter" title="清空筛选">清空</button>
        </form>
      </div>
      <div class="notice-list">
        <div
          v-for="item in noticeList"
          :key="item.id"
          class="notice-item"
          @click="goToDetail(item.id)"
        >
          <div class="notice-date">{{ formatDate(item.time || item.createTime) }}</div>
          <div class="notice-content">
            <h3 class="notice-title">{{ item.title }}</h3>
            <p class="notice-summary">{{ (item.content || '').substring(0, 150) }}{{ (item.content || '').length > 150 ? '...' : '' }}</p>
          </div>
        </div>
      </div>
      <p v-if="!loading && noticeList.length === 0" class="empty-hint">暂无公告</p>
      <div v-if="loading" class="loading">加载中…</div>
      <div class="pagination" v-if="noticeTotal > 0">
        <button type="button" :disabled="noticePageNum <= 1" @click="fetchList(noticePageNum - 1)">上一页</button>
        <span class="page-info">第 {{ noticePageNum }} / {{ noticePages || 1 }} 页，共 {{ noticeTotal }} 条</span>
        <button type="button" :disabled="noticePageNum >= (noticePages || 1)" @click="fetchList(noticePageNum + 1)">下一页</button>
      </div>
    </div>
  </div>
</template>

<script>
import { apiBase } from '../api/config.js'
export default {
  name: 'Notice',
  data() {
    return {
      apiBase,
      pageSize: 10,
      noticeList: [],
      noticePageNum: 1,
      noticeTotal: 0,
      noticePages: 0,
      loading: true,
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
    this.fetchList(1)
    this.clickOutside = (e) => { if (this.dtpOpen && this.$refs.dtWrap && !this.$refs.dtWrap.contains(e.target)) this.dtpOpen = null }
    document.addEventListener('click', this.clickOutside)
  },
  beforeUnmount() {
    document.removeEventListener('click', this.clickOutside)
  },
  methods: {
    openDatePicker(e) {
      const input = e.currentTarget.querySelector('input[type="date"]')
      if (input && typeof input.showPicker === 'function') input.showPicker()
    },
    buildPageUrl(pageNum) {
      const params = new URLSearchParams()
      params.set('pageNum', String(pageNum))
      params.set('pageSize', String(this.pageSize))
      if (this.filter.keyword && this.filter.keyword.trim()) params.set('keyword', this.filter.keyword.trim())
      const timeStart = this.buildDateTime(this.filter.timeStartDate, this.filter.timeStartTime, '00:00')
      if (timeStart) params.set('timeStart', timeStart)
      const timeEnd = this.buildDateTime(this.filter.timeEndDate, this.filter.timeEndTime, '23:59')
      if (timeEnd) params.set('timeEnd', timeEnd)
      return `${this.apiBase}/notice/page?${params.toString()}`
    },
    buildDateTime(date, time, defaultTime) {
      if (!date || !date.trim()) return ''
      return (date + 'T' + (time && time.trim() ? time : defaultTime) + ':00').slice(0, 19)
    },
    dateLabel(d) {
      if (!d || d.length < 10) return ''
      return parseInt(d.slice(5, 7), 10) + '月' + parseInt(d.slice(8, 10), 10) + '日'
    },
    clearDtp(which) {
      if (which === 'start') { this.filter.timeStartDate = ''; this.filter.timeStartTime = '' }
      else { this.filter.timeEndDate = ''; this.filter.timeEndTime = '' }
      this.dtpOpen = null
    },
    onSearch() {
      this.fetchList(1)
    },
    onResetFilter() {
      this.filter = { keyword: '', timeStartDate: '', timeStartTime: '', timeEndDate: '', timeEndTime: '' }
      this.dtpOpen = null
      this.fetchList(1)
    },
    async fetchList(pageNum) {
      this.loading = true
      try {
        const res = await fetch(this.buildPageUrl(pageNum))
        const data = await res.json()
        if (data.code === 200 && data.data) {
          const p = data.data
          this.noticeList = p.records || []
          this.noticePageNum = p.current || 1
          this.noticeTotal = p.total || 0
          this.noticePages = p.pages || 0
        }
      } catch (e) {
        console.error('获取公告列表失败:', e)
      } finally {
        this.loading = false
      }
    },
    goToDetail(id) {
      this.$parent.navigate('detail', { type: 'notice', id })
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      const year = date.getFullYear()
      const month = date.getMonth() + 1
      const day = date.getDate()
      return `${year}年${month}月${day}日`
    }
  }
}
</script>

<style scoped>
.notice {
  min-height: 80vh;
  padding: 64px 0 80px;
  background-color: #f5f7ff;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 24px;
  color: #2c3e50;
  letter-spacing: 0.02em;
}

.filter-wrap {
  margin-bottom: 24px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e8eeff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}
.filter-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin: 0;
}
.filter-label { font-size: 14px; color: #5a6c7d; }
.filter-label.time-label { margin-left: 4px; }
.filter-input {
  padding: 10px 12px;
  border: 1px solid #e8eeff;
  border-radius: 8px;
  font-size: 14px;
}
.filter-input:focus {
  outline: none;
  border-color: #d6e0f0;
}
.filter-input-text { min-width: 160px; }
.filter-time-group { display: inline-flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.filter-sep { font-size: 13px; color: #8a9ba8; }
.dt-wrap { position: relative; }
.dt-trigger {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: 1px solid #e8eeff;
  border-radius: 8px;
  background: #fff;
  font-size: 14px;
  color: #5a6c7d;
  cursor: pointer;
  transition: border-color 0.2s, color 0.2s;
}
.dt-trigger:hover {
  border-color: #d6e0f0;
  color: #2c3e50;
}
.dt-trigger.filled {
  color: #2c3e50;
  border-color: #d6e0f0;
  background: #f5f7ff;
}

.dt-popover {
  position: absolute;
  top: 100%;
  left: 0;
  margin-top: 4px;
  padding: 12px;
  background: #fff;
  border: 1px solid #e8eeff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  z-index: 10;
}
.dt-popover-title { font-size: 12px; color: #8a9ba8; margin-bottom: 8px; }
.date-only-wrap {
  display: block;
  cursor: pointer;
  padding: 6px 10px;
  border: 1px solid #e8eeff;
  border-radius: 6px;
  min-height: 36px;
}
.date-only-text { font-size: 14px; color: #2c3e50; }
.date-only-input { position: absolute; opacity: 0; pointer-events: none; width: 0; height: 0; }
.dt-popover-actions { margin-top: 10px; display: flex; gap: 8px; justify-content: flex-end; }
.dt-btn-clear, .dt-btn-ok { padding: 6px 12px; font-size: 13px; border-radius: 6px; cursor: pointer; }
.dt-btn-clear { background: #f5f5f5; border: 1px solid #e8eeff; }
.dt-btn-ok { background: #fff; border: 1px solid #e8eeff; color: #5a6c7d; }
.filter-btn { padding: 10px 18px; border-radius: 8px; font-size: 14px; cursor: pointer; }
.filter-btn-query { background: #fff; border: 1px solid #e8eeff; color: #5a6c7d; }
.filter-btn-clear { background: #fff; border: 1px solid #e8eeff; color: #5a6c7d; }

.notice-list {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e8eeff;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}

.notice-item {
  display: flex;
  flex-direction: column;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item:hover {
  background-color: #f0f7ff;
}

.notice-content {
  flex: 1;
  margin-bottom: 12px;
}

.notice-title {
  font-size: 17px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #2c3e50;
}

.notice-summary {
  font-size: 14px;
  color: #5a6c7d;
  margin-bottom: 12px;
  line-height: 1.6;
}

.notice-date {
  text-align: left;
  color: #5a6c7d;
  font-size: 13px;
  font-weight: 500;
  padding: 8px 0;
  border-top: 1px solid #f0f0f0;
}

.empty-hint,
.loading {
  text-align: center;
  padding: 48px;
  color: #5a6c7d;
  font-size: 14px;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 28px;
  padding: 24px;
}
.pagination button {
  padding: 10px 18px;
  border: 1px solid #e8eeff;
  background: #fff;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: border-color 0.2s, color 0.2s;
}
.pagination button:hover:not(:disabled) {
  border-color: #165DFF;
  color: #165DFF;
}
.pagination button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}
.page-info {
  font-size: 14px;
  color: #5a6c7d;
}

@media (max-width: 768px) {
  .notice-item {
    flex-direction: column;
  }
  .notice-date {
    width: 100%;
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style>
