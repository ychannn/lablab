<template>
  <div class="achievements">
    <div class="container">
      <h2 class="page-title">科研成果</h2>
      
      <!-- 成果类型切换 -->
      <div class="tab-container">
        <button 
          v-for="tab in tabs" 
          :key="tab.key" 
          class="tab-btn" 
          :class="{ active: activeTab === tab.key }"
          @click="switchTab(tab.key)"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- 论文发表 -->
      <div v-if="activeTab === 'paper'" class="achievement-section">
        <h3 class="section-title">论文发表</h3>
        <div class="filter-wrap">
          <form class="filter-row" @submit.prevent="onSearchPaper">
            <label class="filter-label">关键词</label>
            <input v-model="filterPaper.keyword" type="text" placeholder="内容" class="filter-input filter-input-text" />
            <span class="filter-label time-label">发表时间</span>
            <div class="filter-time-group" ref="dtWrapPaper">
              <div class="dt-wrap">
                <button type="button" class="dt-trigger" :class="{ filled: filterPaper.pubStartDate }" @click.stop="dtpOpen = dtpOpen === 'paperStart' ? null : 'paperStart'" title="开始">
                  <span class="dt-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span>
                  <span class="dt-text">{{ paperStartDisp }}</span>
                </button>
                <div v-show="dtpOpen === 'paperStart'" class="dt-popover" @click.stop>
                  <div class="dt-popover-title">开始日期</div>
                  <label class="date-only-wrap" for="ach-pub-start" @click="openDatePicker">
                    <span class="date-only-text">{{ filterPaper.pubStartDate ? dateLabel(filterPaper.pubStartDate) : '请选择日期' }}</span>
                    <input id="ach-pub-start" v-model="filterPaper.pubStartDate" type="date" class="date-only-input" />
                  </label>
                  <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtpPaper('start')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
                </div>
              </div>
              <span class="filter-sep">至</span>
              <div class="dt-wrap">
                <button type="button" class="dt-trigger" :class="{ filled: filterPaper.pubEndDate }" @click.stop="dtpOpen = dtpOpen === 'paperEnd' ? null : 'paperEnd'" title="结束">
                  <span class="dt-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span>
                  <span class="dt-text">{{ paperEndDisp }}</span>
                </button>
                <div v-show="dtpOpen === 'paperEnd'" class="dt-popover" @click.stop>
                  <div class="dt-popover-title">结束日期</div>
                  <label class="date-only-wrap" for="ach-pub-end" @click="openDatePicker">
                    <span class="date-only-text">{{ filterPaper.pubEndDate ? dateLabel(filterPaper.pubEndDate) : '请选择日期' }}</span>
                    <input id="ach-pub-end" v-model="filterPaper.pubEndDate" type="date" class="date-only-input" />
                  </label>
                  <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtpPaper('end')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
                </div>
              </div>
            </div>
            <button type="submit" class="filter-btn filter-btn-query">查询</button>
            <button type="button" class="filter-btn filter-btn-clear" @click="onResetFilterPaper" title="清空筛选">清空</button>
          </form>
        </div>
        <div class="achievement-list">
          <div v-for="paper in papers" :key="paper.id" class="achievement-item" @click="goToDetail('paper', paper.id)">
            <div class="item-date">{{ formatDate(paper.publishTime) }}</div>
            <div class="item-content">
              <h4 class="item-title">{{ paper.content }}</h4>
            </div>
          </div>
        </div>
        <div class="pagination" v-if="paperTotal > 0">
          <button type="button" :disabled="paperPageNum <= 1" @click="fetchPapers(paperPageNum - 1)">上一页</button>
          <span class="page-info">第 {{ paperPageNum }} / {{ paperPages || 1 }} 页，共 {{ paperTotal }} 条</span>
          <button type="button" :disabled="paperPageNum >= (paperPages || 1)" @click="fetchPapers(paperPageNum + 1)">下一页</button>
        </div>
      </div>

      <!-- 课题项目 -->
      <div v-if="activeTab === 'project'" class="achievement-section">
        <h3 class="section-title">课题项目</h3>
        <div class="filter-wrap">
          <form class="filter-row" @submit.prevent="onSearchProject">
            <label class="filter-label">关键词</label>
            <input v-model="filterProject.keyword" type="text" placeholder="内容" class="filter-input filter-input-text" />
            <span class="filter-label time-label">开始</span>
            <div class="filter-time-group" ref="dtWrapProject">
              <div class="dt-wrap">
                <button type="button" class="dt-trigger" :class="{ filled: filterProject.startStartDate }" @click.stop="dtpOpen = dtpOpen === 'projStartStart' ? null : 'projStartStart'" title="起"><span class="dt-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span><span class="dt-text">{{ filterProject.startStartDate ? dateLabel(filterProject.startStartDate) : '起' }}</span></button>
                <div v-show="dtpOpen === 'projStartStart'" class="dt-popover" @click.stop>
                  <div class="dt-popover-title">开始日期·起</div>
                  <label class="date-only-wrap" for="ach-ps-start" @click="openDatePicker">
                    <span class="date-only-text">{{ filterProject.startStartDate ? dateLabel(filterProject.startStartDate) : '请选择日期' }}</span>
                    <input id="ach-ps-start" v-model="filterProject.startStartDate" type="date" class="date-only-input" />
                  </label>
                  <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtpProject('startStart')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
                </div>
              </div>
              <span class="filter-sep">至</span>
              <div class="dt-wrap">
                <button type="button" class="dt-trigger" :class="{ filled: filterProject.startEndDate }" @click.stop="dtpOpen = dtpOpen === 'projStartEnd' ? null : 'projStartEnd'" title="止"><span class="dt-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span><span class="dt-text">{{ filterProject.startEndDate ? dateLabel(filterProject.startEndDate) : '止' }}</span></button>
                <div v-show="dtpOpen === 'projStartEnd'" class="dt-popover" @click.stop>
                  <div class="dt-popover-title">开始日期·止</div>
                  <label class="date-only-wrap" for="ach-ps-end" @click="openDatePicker">
                    <span class="date-only-text">{{ filterProject.startEndDate ? dateLabel(filterProject.startEndDate) : '请选择日期' }}</span>
                    <input id="ach-ps-end" v-model="filterProject.startEndDate" type="date" class="date-only-input" />
                  </label>
                  <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtpProject('startEnd')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
                </div>
              </div>
            </div>
            <span class="filter-label time-label">结束</span>
            <div class="filter-time-group" ref="dtWrapProject2">
              <div class="dt-wrap">
                <button type="button" class="dt-trigger" :class="{ filled: filterProject.endStartDate }" @click.stop="dtpOpen = dtpOpen === 'projEndStart' ? null : 'projEndStart'" title="起"><span class="dt-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span><span class="dt-text">{{ filterProject.endStartDate ? dateLabel(filterProject.endStartDate) : '起' }}</span></button>
                <div v-show="dtpOpen === 'projEndStart'" class="dt-popover" @click.stop>
                  <div class="dt-popover-title">结束日期·起</div>
                  <label class="date-only-wrap" for="ach-pe-start" @click="openDatePicker">
                    <span class="date-only-text">{{ filterProject.endStartDate ? dateLabel(filterProject.endStartDate) : '请选择日期' }}</span>
                    <input id="ach-pe-start" v-model="filterProject.endStartDate" type="date" class="date-only-input" />
                  </label>
                  <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtpProject('endStart')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
                </div>
              </div>
              <span class="filter-sep">至</span>
              <div class="dt-wrap">
                <button type="button" class="dt-trigger" :class="{ filled: filterProject.endEndDate }" @click.stop="dtpOpen = dtpOpen === 'projEndEnd' ? null : 'projEndEnd'" title="止"><span class="dt-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span><span class="dt-text">{{ filterProject.endEndDate ? dateLabel(filterProject.endEndDate) : '止' }}</span></button>
                <div v-show="dtpOpen === 'projEndEnd'" class="dt-popover" @click.stop>
                  <div class="dt-popover-title">结束日期·止</div>
                  <label class="date-only-wrap" for="ach-pe-end" @click="openDatePicker">
                    <span class="date-only-text">{{ filterProject.endEndDate ? dateLabel(filterProject.endEndDate) : '请选择日期' }}</span>
                    <input id="ach-pe-end" v-model="filterProject.endEndDate" type="date" class="date-only-input" />
                  </label>
                  <div class="dt-popover-actions"><button type="button" class="dt-btn-clear" @click="clearDtpProject('endEnd')">清空</button><button type="button" class="dt-btn-ok" @click="dtpOpen = null">确定</button></div>
                </div>
              </div>
            </div>
            <button type="submit" class="filter-btn filter-btn-query">查询</button>
            <button type="button" class="filter-btn filter-btn-clear" @click="onResetFilterProject" title="清空筛选">清空</button>
          </form>
        </div>
        <div class="achievement-list">
          <div v-for="project in projects" :key="project.id" class="achievement-item" @click="goToDetail('project', project.id)">
            <div class="item-date">{{ formatDate(project.startTime) }} - {{ formatDate(project.endTime) }}</div>
            <div class="item-content">
              <h4 class="item-title">{{ project.content }}</h4>
            </div>
          </div>
        </div>
        <div class="pagination" v-if="projectTotal > 0">
          <button type="button" :disabled="projectPageNum <= 1" @click="fetchProjects(projectPageNum - 1)">上一页</button>
          <span class="page-info">第 {{ projectPageNum }} / {{ projectPages || 1 }} 页，共 {{ projectTotal }} 条</span>
          <button type="button" :disabled="projectPageNum >= (projectPages || 1)" @click="fetchProjects(projectPageNum + 1)">下一页</button>
        </div>
      </div>

      <!-- 成果奖项 -->
      <div v-if="activeTab === 'award'" class="achievement-section">
        <h3 class="section-title">成果奖项</h3>
        <div class="filter-wrap">
          <form class="filter-row" @submit.prevent="onSearchAward">
            <label class="filter-label">关键词</label>
            <input v-model="filterAward.keyword" type="text" placeholder="内容" class="filter-input filter-input-text" />
            <button type="submit" class="filter-btn filter-btn-query">查询</button>
            <button type="button" class="filter-btn filter-btn-clear" @click="onResetFilterAward" title="清空筛选">清空</button>
          </form>
        </div>
        <div class="achievement-list">
          <div v-for="award in awards" :key="award.id" class="achievement-item" @click="goToDetail('award', award.id)">
            <div class="item-date">{{ formatDate(award.createTime) }}</div>
            <div class="item-content">
              <h4 class="item-title">{{ award.content }}</h4>
            </div>
          </div>
        </div>
        <div class="pagination" v-if="awardTotal > 0">
          <button type="button" :disabled="awardPageNum <= 1" @click="fetchAwards(awardPageNum - 1)">上一页</button>
          <span class="page-info">第 {{ awardPageNum }} / {{ awardPages || 1 }} 页，共 {{ awardTotal }} 条</span>
          <button type="button" :disabled="awardPageNum >= (awardPages || 1)" @click="fetchAwards(awardPageNum + 1)">下一页</button>
        </div>
      </div>

      <!-- 研究方向 -->
      <div v-if="activeTab === 'direction'" class="achievement-section">
        <h3 class="section-title">研究方向</h3>
        <div class="filter-wrap">
          <form class="filter-row" @submit.prevent="onSearchDirection">
            <label class="filter-label">关键词</label>
            <input v-model="filterDirection.keyword" type="text" placeholder="标题/内容" class="filter-input filter-input-text" />
            <button type="submit" class="filter-btn filter-btn-query">查询</button>
            <button type="button" class="filter-btn filter-btn-clear" @click="onResetFilterDirection" title="清空筛选">清空</button>
          </form>
        </div>
        <div class="achievement-list">
          <div v-for="direction in directions" :key="direction.id" class="achievement-item" @click="goToDetail('direction', direction.id)">
            <div class="item-content">
              <h4 class="item-title">{{ direction.title }}</h4>
              <p class="item-description">{{ direction.content }}</p>
            </div>
          </div>
        </div>
        <div class="pagination" v-if="directionTotal > 0">
          <button type="button" :disabled="directionPageNum <= 1" @click="fetchDirections(directionPageNum - 1)">上一页</button>
          <span class="page-info">第 {{ directionPageNum }} / {{ directionPages || 1 }} 页，共 {{ directionTotal }} 条</span>
          <button type="button" :disabled="directionPageNum >= (directionPages || 1)" @click="fetchDirections(directionPageNum + 1)">下一页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { apiBase } from '../api/config.js'
export default {
  name: 'Achievements',
  props: {
    initialTab: { type: String, default: null }
  },
  data() {
    return {
      apiBase,
      pageSize: 10,
      activeTab: 'paper',
      tabs: [
        { key: 'paper', label: '论文发表' },
        { key: 'project', label: '课题项目' },
        { key: 'award', label: '成果奖项' },
        { key: 'direction', label: '研究方向' }
      ],
      papers: [],
      paperPageNum: 1,
      paperTotal: 0,
      paperPages: 0,
      filterPaper: { keyword: '', pubStartDate: '', pubStartTime: '', pubEndDate: '', pubEndTime: '' },
      projects: [],
      projectPageNum: 1,
      projectTotal: 0,
      projectPages: 0,
      filterProject: { keyword: '', startStartDate: '', startStartTime: '', startEndDate: '', startEndTime: '', endStartDate: '', endStartTime: '', endEndDate: '', endEndTime: '' },
      awards: [],
      awardPageNum: 1,
      awardTotal: 0,
      awardPages: 0,
      filterAward: { keyword: '' },
      directions: [],
      directionPageNum: 1,
      directionTotal: 0,
      directionPages: 0,
      filterDirection: { keyword: '' },
      dtpOpen: null
    }
  },
  computed: {
    paperStartDisp() { return this.filterPaper.pubStartDate ? this.dateLabel(this.filterPaper.pubStartDate) : '开始' },
    paperEndDisp() { return this.filterPaper.pubEndDate ? this.dateLabel(this.filterPaper.pubEndDate) : '结束' }
  },
  mounted() {
    if (this.initialTab && this.tabs.some(t => t.key === this.initialTab)) {
      this.activeTab = this.initialTab
    }
    this.fetchPapers(1)
    this.fetchProjects(1)
    this.fetchAwards(1)
    this.fetchDirections(1)
    this.clickOutside = (e) => {
      const inPaper = this.$refs.dtWrapPaper && this.$refs.dtWrapPaper.contains(e.target)
      const inProj = this.$refs.dtWrapProject && this.$refs.dtWrapProject.contains(e.target)
      const inProj2 = this.$refs.dtWrapProject2 && this.$refs.dtWrapProject2.contains(e.target)
      if (this.dtpOpen && !inPaper && !inProj && !inProj2) this.dtpOpen = null
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
    dateLabel(d) {
      if (!d || d.length < 10) return ''
      return parseInt(d.slice(5, 7), 10) + '月' + parseInt(d.slice(8, 10), 10) + '日'
    },
    clearDtpPaper(which) {
      if (which === 'start') { this.filterPaper.pubStartDate = ''; this.filterPaper.pubStartTime = '' }
      else { this.filterPaper.pubEndDate = ''; this.filterPaper.pubEndTime = '' }
      this.dtpOpen = null
    },
    clearDtpProject(which) {
      const m = { startStart: ['startStartDate','startStartTime'], startEnd: ['startEndDate','startEndTime'], endStart: ['endStartDate','endStartTime'], endEnd: ['endEndDate','endEndTime'] }
      const [dKey, tKey] = m[which] || []
      if (dKey) { this.filterProject[dKey] = ''; this.filterProject[tKey] = '' }
      this.dtpOpen = null
    },
    switchTab(tab) {
      this.activeTab = tab
    },
    goToDetail(type, id) {
      if (id == null || id === undefined) return
      this.$parent.navigate('detail', { type, id })
    },
    buildDt(date, time, def) {
      return (!date || !date.trim()) ? '' : (date + 'T' + (time && time.trim() ? time : def) + ':00').slice(0, 19)
    },
    buildPaperUrl(pageNum) {
      const params = new URLSearchParams()
      params.set('pageNum', String(pageNum))
      params.set('pageSize', String(this.pageSize))
      if (this.filterPaper.keyword && this.filterPaper.keyword.trim()) params.set('keyword', this.filterPaper.keyword.trim())
      const ps = this.buildDt(this.filterPaper.pubStartDate, this.filterPaper.pubStartTime, '00:00')
      if (ps) params.set('publishTimeStart', ps)
      const pe = this.buildDt(this.filterPaper.pubEndDate, this.filterPaper.pubEndTime, '23:59')
      if (pe) params.set('publishTimeEnd', pe)
      return `${this.apiBase}/publication/page?${params.toString()}`
    },
    async fetchPapers(pageNum) {
      try {
        const res = await fetch(this.buildPaperUrl(pageNum))
        const data = await res.json()
        if (data.code === 200 && data.data) {
          this.papers = data.data.records || []
          this.paperPageNum = data.data.current || 1
          this.paperTotal = data.data.total || 0
          this.paperPages = data.data.pages || 0
        }
      } catch (error) {
        console.error('获取论文失败:', error)
      }
    },
    onSearchPaper() { this.fetchPapers(1) },
    onResetFilterPaper() {
      this.filterPaper = { keyword: '', pubStartDate: '', pubStartTime: '', pubEndDate: '', pubEndTime: '' }
      this.dtpOpen = null
      this.fetchPapers(1)
    },
    buildProjectUrl(pageNum) {
      const params = new URLSearchParams()
      params.set('pageNum', String(pageNum))
      params.set('pageSize', String(this.pageSize))
      if (this.filterProject.keyword && this.filterProject.keyword.trim()) params.set('keyword', this.filterProject.keyword.trim())
      const s = this.buildDt(this.filterProject.startStartDate, this.filterProject.startStartTime, '00:00')
      if (s) params.set('startTimeStart', s)
      const e = this.buildDt(this.filterProject.startEndDate, this.filterProject.startEndTime, '23:59')
      if (e) params.set('startTimeEnd', e)
      const s2 = this.buildDt(this.filterProject.endStartDate, this.filterProject.endStartTime, '00:00')
      if (s2) params.set('endTimeStart', s2)
      const e2 = this.buildDt(this.filterProject.endEndDate, this.filterProject.endEndTime, '23:59')
      if (e2) params.set('endTimeEnd', e2)
      return `${this.apiBase}/topic-project/page?${params.toString()}`
    },
    async fetchProjects(pageNum) {
      try {
        const res = await fetch(this.buildProjectUrl(pageNum))
        const data = await res.json()
        if (data.code === 200 && data.data) {
          this.projects = data.data.records || []
          this.projectPageNum = data.data.current || 1
          this.projectTotal = data.data.total || 0
          this.projectPages = data.data.pages || 0
        }
      } catch (error) {
        console.error('获取项目失败:', error)
      }
    },
    onSearchProject() { this.fetchProjects(1) },
    onResetFilterProject() {
      this.filterProject = { keyword: '', startStartDate: '', startStartTime: '', startEndDate: '', startEndTime: '', endStartDate: '', endStartTime: '', endEndDate: '', endEndTime: '' }
      this.dtpOpen = null
      this.fetchProjects(1)
    },
    buildAwardUrl(pageNum) {
      const params = new URLSearchParams()
      params.set('pageNum', String(pageNum))
      params.set('pageSize', String(this.pageSize))
      if (this.filterAward.keyword && this.filterAward.keyword.trim()) params.set('keyword', this.filterAward.keyword.trim())
      return `${this.apiBase}/achievement/page?${params.toString()}`
    },
    async fetchAwards(pageNum) {
      try {
        const res = await fetch(this.buildAwardUrl(pageNum))
        const data = await res.json()
        if (data.code === 200 && data.data) {
          this.awards = data.data.records || []
          this.awardPageNum = data.data.current || 1
          this.awardTotal = data.data.total || 0
          this.awardPages = data.data.pages || 0
        }
      } catch (error) {
        console.error('获取奖项失败:', error)
      }
    },
    onSearchAward() { this.fetchAwards(1) },
    onResetFilterAward() { this.filterAward = { keyword: '' }; this.fetchAwards(1) },
    buildDirectionUrl(pageNum) {
      const params = new URLSearchParams()
      params.set('pageNum', String(pageNum))
      params.set('pageSize', String(this.pageSize))
      if (this.filterDirection.keyword && this.filterDirection.keyword.trim()) params.set('keyword', this.filterDirection.keyword.trim())
      return `${this.apiBase}/research/direction/page?${params.toString()}`
    },
    async fetchDirections(pageNum) {
      try {
        const res = await fetch(this.buildDirectionUrl(pageNum))
        const data = await res.json()
        if (data.code === 200 && data.data) {
          this.directions = data.data.records || []
          this.directionPageNum = data.data.current || 1
          this.directionTotal = data.data.total || 0
          this.directionPages = data.data.pages || 0
        }
      } catch (error) {
        console.error('获取研究方向失败:', error)
      }
    },
    onSearchDirection() { this.fetchDirections(1) },
    onResetFilterDirection() { this.filterDirection = { keyword: '' }; this.fetchDirections(1) },
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

<style>
.achievements {
  min-height: 80vh;
  padding: 64px 0 80px;
  background-color: #f6faf8;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 48px;
  color: #2c3e50;
  letter-spacing: 0.02em;
}

.tab-container {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 32px;
  padding: 12px 0;
}

.tab-btn {
  padding: 12px 24px;
  border: 1px solid #dde8e4;
  background: #fff;
  color: #5a6c7d;
  font-size: 14px;
  font-weight: 500;
  border-radius: 6px;
  cursor: pointer;
  transition: border-color 0.2s, color 0.2s, background 0.2s;
}

.tab-btn:hover {
  border-color: #2d9d78;
  color: #2d9d78;
}

.tab-btn.active {
  background: linear-gradient(135deg, #2d9d78 0%, #248f6a 100%);
  border-color: #2d9d78;
  color: #fff;
}

.achievement-section {
  background-color: #fff;
  border-radius: 12px;
  border: 1px solid #dde8e4;
  padding: 40px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #2c3e50;
  border-bottom: 1px solid #dde8e4;
  padding-bottom: 12px;
}

.filter-wrap {
  margin-bottom: 24px;
  padding: 14px 18px;
  background: #f6faf8;
  border: 1px solid #dde8e4;
  border-radius: 10px;
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
.filter-input:focus { outline: none; border-color: #2d9d78; }
.filter-input-text { min-width: 160px; }
.filter-date { width: 140px; }
.filter-time { width: 96px; }
.filter-time-group { display: inline-flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.filter-sep { font-size: 13px; color: #8a9ba8; }
.dt-wrap { position: relative; display: inline-block; }
.dt-trigger { display: inline-flex; align-items: center; gap: 4px; padding: 8px 12px; min-width: 72px; max-width: 100px; border: 1px solid #dde8e4; border-radius: 8px; background: #fff; color: #5a6c7d; font-size: 13px; cursor: pointer; }
.dt-trigger:hover { border-color: #2d9d78; color: #2d9d78; }
.dt-trigger.filled { color: #2d9d78; border-color: #2d9d78; background: #e8f5f0; }
.dt-icon { flex-shrink: 0; display: inline-flex; }
.dt-text { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.dt-popover { position: absolute; left: 0; top: calc(100% + 6px); z-index: 1000; min-width: 200px; padding: 14px; background: #fff; border: 1px solid #dde8e4; border-radius: 10px; box-shadow: 0 6px 16px rgba(0,0,0,0.1); }
.dt-popover-title { font-size: 13px; color: #5a6c7d; margin-bottom: 10px; }
.date-only-wrap { position: relative; padding: 8px 12px; border: 1px solid #dde8e4; border-radius: 8px; height: 42px; display: flex; align-items: center; cursor: pointer; margin-bottom: 12px; }
.date-only-text { color: #2c3e50; font-size: 14px; pointer-events: none; }
.date-only-input { position: absolute; left: 0; top: 0; width: 100%; height: 100%; opacity: 0; cursor: pointer; z-index: 1; font-size: 16px; }
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
.filter-btn-clear:hover { border-color: #e86c6c; color: #e86c6c; }

.achievement-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.achievement-item {
  display: flex;
  padding: 20px 24px;
  background-color: #f6faf8;
  border: 1px solid #dde8e4;
  border-radius: 10px;
  transition: border-color 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.achievement-item:hover {
  border-color: #2d9d78;
  box-shadow: 0 2px 12px rgba(45, 157, 120, 0.12);
}

.item-date {
  flex-shrink: 0;
  width: 140px;
  text-align: center;
  background: linear-gradient(135deg, #2d9d78 0%, #248f6a 100%);
  color: #fff;
  padding: 10px 12px;
  border-radius: 6px;
  margin-right: 24px;
  font-size: 13px;
  font-weight: 500;
}

.item-content {
  flex: 1;
}

.item-title {
  font-size: 17px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #2c3e50;
}

.item-description {
  font-size: 14px;
  color: #5a6c7d;
  line-height: 1.6;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid #dde8e4;
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
  .tab-container {
    flex-wrap: wrap;
  }
  
  .tab-btn {
    margin: 5px;
  }
  
  .achievement-item {
    flex-direction: column;
  }
  
  .item-date {
    width: 100%;
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style>