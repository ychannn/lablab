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
export default {
  name: 'Achievements',
  props: {
    initialTab: { type: String, default: null }
  },
  data() {
    return {
      apiBase: 'http://localhost:8080',
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
      projects: [],
      projectPageNum: 1,
      projectTotal: 0,
      projectPages: 0,
      awards: [],
      awardPageNum: 1,
      awardTotal: 0,
      awardPages: 0,
      directions: [],
      directionPageNum: 1,
      directionTotal: 0,
      directionPages: 0
    }
  },
  mounted() {
    if (this.initialTab && this.tabs.some(t => t.key === this.initialTab)) {
      this.activeTab = this.initialTab
    }
    this.fetchPapers(1)
    this.fetchProjects(1)
    this.fetchAwards(1)
    this.fetchDirections(1)
  },
  methods: {
    switchTab(tab) {
      this.activeTab = tab
    },
    goToDetail(type, id) {
      if (id == null || id === undefined) return
      this.$parent.navigate('detail', { type, id })
    },
    async fetchPapers(pageNum) {
      try {
        const res = await fetch(`${this.apiBase}/publication/page?pageNum=${pageNum}&pageSize=${this.pageSize}`)
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
    async fetchProjects(pageNum) {
      try {
        const res = await fetch(`${this.apiBase}/topic-project/page?pageNum=${pageNum}&pageSize=${this.pageSize}`)
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
    async fetchAwards(pageNum) {
      try {
        const res = await fetch(`${this.apiBase}/achievement/page?pageNum=${pageNum}&pageSize=${this.pageSize}`)
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
    async fetchDirections(pageNum) {
      try {
        const res = await fetch(`${this.apiBase}/research/direction/page?pageNum=${pageNum}&pageSize=${this.pageSize}`)
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
  margin-bottom: 28px;
  color: #2c3e50;
  border-bottom: 1px solid #dde8e4;
  padding-bottom: 12px;
}

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