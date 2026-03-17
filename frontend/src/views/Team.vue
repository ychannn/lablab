<template>
  <div class="team">
    <div class="container">
      <h2 class="page-title">师资队伍</h2>
      
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
export default {
  name: 'Team',
  data() {
    return {
      apiBase: 'http://localhost:8080',
      pageSize: 12,
      pageNum: 1,
      total: 0,
      totalPages: 0,
      teamMembers: []
    }
  },
  mounted() {
    this.fetchTeamMembers(1)
  },
  methods: {
    imageUrl(url) {
      if (!url) return ''
      return url.startsWith('http') ? url : this.apiBase + url
    },
    async fetchTeamMembers(page) {
      if (page != null) this.pageNum = Math.max(1, page)
      try {
        const url = `${this.apiBase}/team/scholar/page?pageNum=${this.pageNum}&pageSize=${this.pageSize}`
        const response = await fetch(url)
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
  margin-bottom: 48px;
  color: #2c3e50;
  letter-spacing: 0.02em;
}

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