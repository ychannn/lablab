<template>
  <div class="news">
    <div class="container">
      <h2 class="page-title">新闻公告</h2>
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
                <label class="date-only-wrap" for="news-dtp-start" @click="openDatePicker">
                  <span class="date-only-text">{{ filter.timeStartDate ? dateLabel(filter.timeStartDate) : '请选择日期' }}</span>
                  <input id="news-dtp-start" v-model="filter.timeStartDate" type="date" class="date-only-input" />
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
                <label class="date-only-wrap" for="news-dtp-end" @click="openDatePicker">
                  <span class="date-only-text">{{ filter.timeEndDate ? dateLabel(filter.timeEndDate) : '请选择日期' }}</span>
                  <input id="news-dtp-end" v-model="filter.timeEndDate" type="date" class="date-only-input" />
                </label>
                <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtp('end')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
              </div>
            </div>
          </div>
          <button type="submit" class="filter-btn filter-btn-query">查询</button>
          <button type="button" class="filter-btn filter-btn-clear" @click="onResetFilter" title="清空筛选">清空</button>
        </form>
      </div>
      <!-- 新闻列表 -->
      <div class="news-list">
        <div
          v-for="news in newsList"
          :key="news.id"
          class="news-item"
          @click="goToDetail(news.id)"
        >
          <div class="news-date">{{ formatDate(news.time) }}</div>
          <div class="news-content">
            <h3 class="news-title">{{ news.title }}</h3>
            <p class="news-summary">{{ (news.content || '').substring(0, 150) }}{{ (news.content || '').length > 150 ? '...' : '' }}</p>
          </div>
        </div>
      </div>
      <div class="pagination" v-if="newsTotal > 0">
        <button type="button" :disabled="newsPageNum <= 1" @click="fetchNewsList(newsPageNum - 1)">上一页</button>
        <span class="page-info">第 {{ newsPageNum }} / {{ newsPages || 1 }} 页，共 {{ newsTotal }} 条</span>
        <button type="button" :disabled="newsPageNum >= (newsPages || 1)" @click="fetchNewsList(newsPageNum + 1)">下一页</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'News',
  data() {
    return {
      apiBase: 'http://localhost:8080',
      pageSize: 10,
      newsList: [],
      newsPageNum: 1,
      newsTotal: 0,
      newsPages: 0,
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
    this.fetchNewsList(1)
    this.clickOutside = (e) => { if (this.dtpOpen && this.$refs.dtWrap && !this.$refs.dtWrap.contains(e.target)) this.dtpOpen = null }
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
    buildPageUrl(pageNum) {
      const params = new URLSearchParams()
      params.set('pageNum', String(pageNum))
      params.set('pageSize', String(this.pageSize))
      if (this.filter.keyword && this.filter.keyword.trim()) params.set('keyword', this.filter.keyword.trim())
      const timeStart = this.buildDateTime(this.filter.timeStartDate, this.filter.timeStartTime, '00:00')
      if (timeStart) params.set('timeStart', timeStart)
      const timeEnd = this.buildDateTime(this.filter.timeEndDate, this.filter.timeEndTime, '23:59')
      if (timeEnd) params.set('timeEnd', timeEnd)
      return `${this.apiBase}/lab-news/page?${params.toString()}`
    },
    async fetchNewsList(pageNum) {
      try {
        const response = await fetch(this.buildPageUrl(pageNum))
        const data = await response.json()
        if (data.code === 200 && data.data) {
          this.newsList = data.data.records || []
          this.newsPageNum = data.data.current || 1
          this.newsTotal = data.data.total || 0
          this.newsPages = data.data.pages || 0
        }
      } catch (error) {
        console.error('获取新闻列表失败:', error)
      }
    },
    onSearch() {
      this.fetchNewsList(1)
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
    onResetFilter() {
      this.filter = { keyword: '', timeStartDate: '', timeStartTime: '', timeEndDate: '', timeEndTime: '' }
      this.dtpOpen = null
      this.fetchNewsList(1)
    },
    goToDetail(id) {
      this.$parent.navigate('detail', { type: 'news', id })
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
.news {
  min-height: 80vh;
  padding: 64px 0 80px;
  background-color: #f6faf8;
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
  border: 1px solid #dde8e4;
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
  border: 1px solid #dde8e4;
  border-radius: 8px;
  font-size: 14px;
}
.filter-input:focus {
  outline: none;
  border-color: #2d9d78;
}
.filter-input-text { min-width: 160px; }
.filter-time-group { display: inline-flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.filter-sep { font-size: 13px; color: #8a9ba8; }
.date-only-wrap { position: relative; padding: 8px 12px; border: 1px solid #dde8e4; border-radius: 8px; height: 42px; display: flex; align-items: center; cursor: pointer; margin-bottom: 12px; }
.date-only-text { color: #2c3e50; font-size: 14px; pointer-events: none; }
.date-only-input { position: absolute; left: 0; top: 0; width: 100%; height: 100%; opacity: 0; cursor: pointer; z-index: 1; font-size: 16px; }
.dt-wrap { position: relative; display: inline-block; }
.dt-trigger { display: inline-flex; align-items: center; gap: 4px; padding: 8px 12px; min-width: 76px; max-width: 106px; border: 1px solid #dde8e4; border-radius: 8px; background: #fff; color: #5a6c7d; font-size: 13px; cursor: pointer; transition: border-color 0.2s, color 0.2s; }
.dt-trigger:hover { border-color: #2d9d78; color: #2d9d78; }
.dt-trigger.filled { color: #2d9d78; border-color: #2d9d78; background: #e8f5f0; }
.dt-icon { flex-shrink: 0; display: inline-flex; }
.dt-text { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.dt-popover { position: absolute; left: 0; top: calc(100% + 6px); z-index: 1000; min-width: 200px; padding: 14px; background: #fff; border: 1px solid #dde8e4; border-radius: 10px; box-shadow: 0 6px 16px rgba(0,0,0,0.1); }
.dt-popover-title { font-size: 13px; color: #5a6c7d; margin-bottom: 10px; }
.dt-popover-actions { display: flex; justify-content: flex-end; gap: 10px; }
.dt-btn-clear { padding: 6px 12px; font-size: 13px; color: #5a6c7d; background: #fff; border: 1px solid #dde8e4; border-radius: 6px; cursor: pointer; }
.dt-btn-ok { padding: 6px 14px; font-size: 13px; color: #fff; background: #2d9d78; border: none; border-radius: 6px; cursor: pointer; }
.filter-btn {
  padding: 10px 18px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}
.filter-btn-query {
  border: 1px solid #2d9d78;
  background: #2d9d78;
  color: #fff;
}
.filter-btn-query:hover { opacity: 0.9; }
.filter-btn-clear {
  border: 1px solid #dde8e4;
  background: #fff;
  color: #5a6c7d;
}
.filter-btn-clear:hover {
  border-color: #e86c6c;
  color: #e86c6c;
}

.news-list {
  background-color: #fff;
  border-radius: 12px;
  border: 1px solid #dde8e4;
  overflow: hidden;
}

.news-item {
  display: flex;
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
  cursor: pointer;
}

.news-item:last-child {
  border-bottom: none;
}

.news-item:hover {
  background-color: #f0f7f4;
}

.news-date {
  flex-shrink: 0;
  width: 90px;
  text-align: center;
  background: linear-gradient(135deg, #2d9d78 0%, #248f6a 100%);
  color: #fff;
  padding: 10px 12px;
  border-radius: 6px;
  margin-right: 24px;
  font-size: 13px;
  font-weight: 500;
}

.news-content {
  flex: 1;
}

.news-title {
  font-size: 17px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #2c3e50;
}

.news-summary {
  font-size: 14px;
  color: #5a6c7d;
  margin-bottom: 0;
  line-height: 1.6;
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
  border: 1px solid #dde8e4;
  background: #fff;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: border-color 0.2s, color 0.2s;
}
.pagination button:hover:not(:disabled) {
  border-color: #2d9d78;
  color: #2d9d78;
}
.pagination button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}
.page-info {
  font-size: 14px;
  color: #5a6c7d;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .news-item {
    flex-direction: column;
  }
  
  .news-date {
    width: 100%;
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style>