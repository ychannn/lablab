<template>
  <div class="team">
    <div class="container">
      <h2 class="page-title">师资队伍</h2>
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
            <option v-for="r in rankOptions" :key="r.value" :value="r.value">{{ r.label }}</option>
          </select>
          <button type="submit" class="filter-btn filter-btn-query">查询</button>
          <button type="button" class="filter-btn filter-btn-clear" @click="onResetFilter" title="清空筛选">清空</button>
        </form>
      </div>
      <!-- 团队成员列表 -->
      <div class="team-list">
        <div
          v-for="member in teamMembers"
          :key="member.id"
          class="member-card"
          @click="goToDetail(member.id)"
        >
          <div class="member-photo">
            <img :src="member.photo ? imageUrl(member.photo) : 'https://via.placeholder.com/200x200?text=Photo'" alt="成员照片">
          </div>
          <div class="member-info">
            <h3 class="member-name">{{ member.name }}</h3>
            <p class="member-rank">{{ getRankName(member.rank) }}</p>
          </div>
        </div>
      </div>
      <div class="pagination" v-if="total > 0">
        <button type="button" :disabled="pageNum <= 1" @click="fetchTeamMembers(pageNum - 1)">上一页</button>
        <span class="page-info">第 {{ pageNum }} / {{ totalPages || 1 }} 页，共 {{ total }} 人</span>
        <button type="button" :disabled="pageNum >= (totalPages || 1)" @click="fetchTeamMembers(pageNum + 1)">下一页</button>
      </div>
    </div>
  </div>
</template>

<script>
import { apiBase } from '../api/config.js'
export default {
  name: 'Team',
  data() {
    return {
      apiBase,
      pageSize: 12,
      pageNum: 1,
      total: 0,
      totalPages: 0,
      teamMembers: [],
      areaList: [],
      rankOptions: [
        { value: 1, label: '教授' },
        { value: 2, label: '副教授' },
        { value: 3, label: '讲师' },
        { value: 4, label: '助教' }
      ],
      filter: { keyword: '', areaId: 0, rank: 0 }
    }
  },
  mounted() {
    this.loadAreas()
    this.fetchTeamMembers(1)
  },
  methods: {
    imageUrl(url) {
      if (!url) return ''
      return url.startsWith('http') ? url : url
    },
    async loadAreas() {
      try {
        const res = await fetch(`${this.apiBase}/area/list`)
        const data = await res.json()
        if (data.code === 200 && Array.isArray(data.data)) this.areaList = data.data
      } catch (e) { this.areaList = [] }
    },
    buildPageUrl(page) {
      const params = new URLSearchParams()
      params.set('pageNum', String(page))
      params.set('pageSize', String(this.pageSize))
      if (this.filter.keyword && this.filter.keyword.trim()) params.set('keyword', this.filter.keyword.trim())
      if (this.filter.areaId > 0) params.set('areaId', String(this.filter.areaId))
      if (this.filter.rank > 0) params.set('rank', String(this.filter.rank))
      return `${this.apiBase}/team/scholar/page?${params.toString()}`
    },
    async fetchTeamMembers(page) {
      if (page != null) this.pageNum = Math.max(1, page)
      try {
        const response = await fetch(this.buildPageUrl(this.pageNum))
        const data = await response.json()
        if (data.code === 200 && data.data) {
          this.teamMembers = data.data.records || []
          this.pageNum = data.data.current || 1
          this.total = data.data.total || 0
          this.totalPages = data.data.pages || 0
        }
      } catch (error) {
        console.error('获取团队成员列表失败:', error)
      }
    },
    onSearch() { this.fetchTeamMembers(1) },
    onResetFilter() {
      this.filter = { keyword: '', areaId: 0, rank: 0 }
      this.fetchTeamMembers(1)
    },
    goToDetail(id) {
      if (id == null || id === undefined || id === '') return
      this.$parent.navigate('detail', { type: 'scholar', id })
    },
    getRankName(rank) {
      const rankMap = { 1: '教授', 2: '副教授', 3: '讲师', 4: '助教' }
      return rankMap[rank] || '其他'
    }
  }
}
</script>

<style scoped>
.team {
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
.filter-input {
  padding: 10px 12px;
  border: 1px solid #dde8e4;
  border-radius: 8px;
  font-size: 14px;
}
.filter-input:focus { outline: none; border-color: #2d9d78; }
.filter-input-text { min-width: 140px; }
.filter-select { min-width: 100px; }
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

.team-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 32px;
}

.member-card {
  background-color: #fff;
  border: 1px solid #dde8e4;
  border-radius: 12px;
  padding: 32px 24px;
  text-align: center;
  transition: border-color 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.member-card:hover {
  border-color: #2d9d78;
  box-shadow: 0 4px 20px rgba(45, 157, 120, 0.15);
}

.member-photo {
  margin-bottom: 20px;
}

.member-photo img {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  object-fit: cover;
  background: #f5f5f5;
}

.member-name {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #2c3e50;
}

.member-rank {
  font-size: 14px;
  color: #5a6c7d;
  margin-bottom: 0;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
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

@media (max-width: 768px) {
  .team {
    padding: 40px 0 60px;
  }
  .page-title {
    font-size: 24px;
    margin-bottom: 32px;
  }
  .team-list {
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    gap: 24px;
  }
}
</style>