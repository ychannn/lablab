<template>
  <div class="news">
    <div class="container">
      <h2 class="page-title">新闻公告</h2>
      
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
      newsPages: 0
    }
  },
  mounted() {
    this.fetchNewsList(1)
  },
  methods: {
    async fetchNewsList(pageNum) {
      try {
        const response = await fetch(`${this.apiBase}/lab-news/page?pageNum=${pageNum}&pageSize=${this.pageSize}`)
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
  margin-bottom: 48px;
  color: #2c3e50;
  letter-spacing: 0.02em;
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