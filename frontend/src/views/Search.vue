<template>
  <div class="search-page">
    <div class="container">
      <div class="search-header">
        <h2 class="page-title">站内搜索</h2>
        <div class="search-box">
          <input
            v-model="query"
            type="text"
            class="search-input"
            placeholder="输入关键词搜索新闻、师资、成果…"
            @keyup.enter="doSearch"
          >
          <button type="button" class="search-btn" @click="doSearch">搜索</button>
        </div>
      </div>

      <div v-if="searched" class="search-result">
        <p v-if="!loading && result.items.length === 0" class="no-result">
          {{ query ? '未找到相关结果' : '请输入关键词' }}
        </p>
        <div v-else-if="loading" class="loading">搜索中…</div>
        <template v-else>
          <p class="result-summary">共找到 {{ result.total }} 条结果</p>
          <ul class="result-list">
            <li
              v-for="item in result.items"
              :key="item.id"
              class="result-item"
              @click="goTo(item)"
            >
              <span class="item-type">{{ getTypeLabel(item.type) }}</span>
              <div class="item-body">
                <h4 class="item-title" v-html="item.highlightTitle || escapeHtml(item.title || item.content || '无标题')"></h4>
                <p v-if="(item.highlightContent || item.content) && (item.content || '') !== (item.title || '')" class="item-content" v-html="item.highlightContent || escapeHtml(item.content)"></p>
                <span v-if="item.createTime" class="item-time">{{ item.createTime }}</span>
              </div>
            </li>
          </ul>
          <div v-if="result.totalPages > 1" class="pagination">
            <button type="button" :disabled="result.page <= 1" @click="fetchPage(result.page - 1)">上一页</button>
            <span class="page-info">第 {{ result.page }} / {{ result.totalPages }} 页</span>
            <button type="button" :disabled="result.page >= result.totalPages" @click="fetchPage(result.page + 1)">下一页</button>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import { apiBase } from '../api/config.js'
export default {
  name: 'Search',
  props: {
    keyword: { type: String, default: '' }
  },
  data() {
    return {
      apiBase,
      query: '',
      searched: false,
      loading: false,
      result: {
        total: 0,
        page: 1,
        size: 20,
        totalPages: 0,
        items: []
      }
    }
  },
  watch: {
    keyword: {
      immediate: true,
      handler(v) {
        this.query = v || ''
        if (v && v.trim()) {
          this.result.page = 1
          this.fetchSearch()
        } else {
          this.searched = false
        }
      }
    }
  },
  methods: {
    escapeHtml(s) {
      if (s == null) return ''
      const t = String(s)
      return t.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;')
    },
    doSearch() {
      const q = (this.query || '').trim()
      if (!q) return
      this.$emit('search', q)
      this.result.page = 1
      this.fetchSearch()
    },
    async fetchSearch() {
      const q = (this.query || '').trim()
      if (!q) {
        this.searched = true
        this.result.items = []
        this.result.total = 0
        this.result.totalPages = 0
        return
      }
      this.searched = true
      this.loading = true
      try {
        const url = `${this.apiBase}/search/query?keyword=${encodeURIComponent(q)}&page=${this.result.page}&size=${this.result.size}`
        const res = await fetch(url)
        const data = await res.json()
        if (data.code === 200 && data.data) {
          this.result = {
            total: data.data.total ?? 0,
            page: data.data.page ?? 1,
            size: data.data.size ?? 20,
            totalPages: data.data.totalPages ?? 0,
            items: data.data.items || []
          }
        }
      } catch (e) {
        console.error(e)
        this.result.items = []
        this.result.total = 0
        this.result.totalPages = 0
      } finally {
        this.loading = false
      }
    },
    fetchPage(page) {
      if (page < 1 || page > this.result.totalPages) return
      this.result.page = page
      this.fetchSearch()
    },
    getTypeLabel(type) {
      const map = {
        news: '新闻',
        notice: '公告',
        scholar: '师资',
        achievement: '成果',
        paper: '论文',
        project: '项目'
      }
      return map[type] || type || '其他'
    },
    goTo(item) {
      const idStr = (item.id || '').toString()
      const numId = idStr.replace(/^\w+_/, '')
      const num = parseInt(numId, 10)
      const id = isNaN(num) ? null : num
      switch (item.type) {
        case 'news':
          if (id != null) this.$emit('go', { page: 'detail', type: 'news', id })
          else this.$emit('go', { page: 'news' })
          break
        case 'notice':
          this.$emit('go', { page: 'news' })
          break
        case 'scholar':
          if (id != null) this.$emit('go', { page: 'detail', type: 'scholar', id })
          else this.$emit('go', { page: 'team' })
          break
        case 'paper':
          if (id != null) this.$emit('go', { page: 'detail', type: 'paper', id })
          else this.$emit('go', { page: 'achievements', tab: 'paper' })
          break
        case 'project':
          if (id != null) this.$emit('go', { page: 'detail', type: 'project', id })
          else this.$emit('go', { page: 'achievements', tab: 'project' })
          break
        case 'achievement':
          if (id != null) this.$emit('go', { page: 'detail', type: 'award', id })
          else this.$emit('go', { page: 'achievements', tab: 'award' })
          break
        default:
          this.$emit('go', { page: 'home' })
      }
    }
  }
}
</script>

<style scoped>
.search-page {
  min-height: 80vh;
  padding: 48px 0 80px;
  background-color: #f5f7ff;
}

.search-header {
  margin-bottom: 40px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 24px;
  color: #2c3e50;
  letter-spacing: 0.02em;
}

.search-box {
  display: flex;
  max-width: 560px;
  margin: 0 auto;
  gap: 12px;
}

.search-input {
  flex: 1;
  padding: 14px 18px;
  border: 1px solid #e8eeff;
  border-radius: 8px;
  font-size: 15px;
  outline: none;
  transition: border-color 0.2s;
}

.search-input:focus {
  border-color: #165DFF;
}

.search-btn {
  padding: 14px 28px;
  background: #f5f7ff;
  color: #2c3e50;
  border: 1px solid #d6e0f0;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.search-btn:hover {
  background: #e8eeff;
  border-color: #b8c6d9;
}

.search-result {
  background-color: #fff;
  border: 1px solid #e8eeff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.no-result,
.loading {
  text-align: center;
  color: #5a6c7d;
  padding: 48px 0;
}

.result-summary {
  font-size: 14px;
  color: #5a6c7d;
  margin-bottom: 20px;
}

.result-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.result-item {
  display: flex;
  gap: 16px;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.15s;
}

.result-item:last-child {
  border-bottom: none;
}

.result-item:hover {
  background-color: #f0f7ff;
  margin: 0 -16px;
  padding-left: 16px;
  padding-right: 16px;
  border-radius: 8px;
}

.item-type {
  flex-shrink: 0;
  padding: 2px 8px;
  font-size: 12px;
  font-weight: 500;
  color: #5a6c7d;
  text-align: center;
  border-right: 1px solid #e8eeff;
  margin-right: 12px;
  padding-right: 12px;
  min-width: 40px;
}

.item-body {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 6px 0;
  color: #2c3e50;
  line-height: 1.4;
}

.item-content {
  font-size: 14px;
  color: #5a6c7d;
  margin: 0 0 8px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-title :deep(.search-highlight),
.item-content :deep(.search-highlight) {
  background: linear-gradient(135deg, #e8f0ff 0%, #d6e4ff 100%);
  padding: 0 2px;
  border-radius: 2px;
  font-style: normal;
  font-weight: 600;
  color: #165DFF;
}

.item-time {
  font-size: 12px;
  color: #999;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid #e8eeff;
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
  .search-box {
    flex-direction: column;
  }
}
</style>
