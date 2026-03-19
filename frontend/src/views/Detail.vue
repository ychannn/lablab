<template>
  <div class="detail-page">
    <div class="container">
      <div v-if="loading" class="loading">加载中…</div>
      <div v-else-if="error" class="error">
        <p>{{ error }}</p>
        <button type="button" class="back-btn" @click="goBack">返回</button>
      </div>
      <template v-else-if="data">
        <div class="detail-header">
          <h1 class="detail-title">{{ title }}</h1>
          <p v-if="meta" class="detail-meta">{{ meta }}</p>
        </div>
        <div class="detail-body">
          <!-- 新闻 -->
          <div v-if="type === 'news'" class="detail-content">{{ data.content }}</div>
          <!-- 师资 -->
          <template v-else-if="type === 'scholar'">
            <div v-if="data.photo" class="scholar-photo">
              <img :src="scholarPhotoUrl" alt="">
            </div>
            <div class="scholar-intro" v-if="data.intro && data.intro.length">
              <h4>个人简介</h4>
              <div v-for="(intro, i) in data.intro" :key="i" class="intro-block">{{ intro.content }}</div>
            </div>
            <div class="scholar-papers" v-if="data.paperList && data.paperList.length">
              <h4>论文发表</h4>
              <div v-for="(p, i) in data.paperList" :key="i" class="item-block">{{ p.content || p.title }}</div>
            </div>
            <div class="scholar-projects" v-if="data.projectList && data.projectList.length">
              <h4>科研项目</h4>
              <div v-for="(p, i) in data.projectList" :key="i" class="item-block">{{ p.content || p.title }}</div>
            </div>
            <p v-if="!hasScholarContent" class="empty-hint">暂无更多介绍</p>
          </template>
          <!-- 论文/项目/奖项/研究方向 -->
          <div v-else class="detail-content">{{ contentText }}</div>
        </div>
        <button type="button" class="back-btn" @click="goBack">返回</button>
      </template>
    </div>
  </div>
</template>

<script>
import { apiBase } from '../api/config.js'
export default {
  name: 'Detail',
  props: {
    type: { type: String, required: true },
    id: { type: [Number, String], default: null }
  },
  data() {
    return {
      apiBase,
      loading: true,
      error: '',
      data: null
    }
  },
  computed: {
    scholarPhotoUrl() {
      if (this.type !== 'scholar' || !this.data || !this.data.photo) return ''
      const url = this.data.photo
      return url.startsWith('http') ? url : this.apiBase + url
    },
    title() {
      if (!this.data) return ''
      if (this.type === 'news') return this.data.title || '新闻'
      if (this.type === 'scholar') return this.data.name || '师资'
      if (this.type === 'direction') return this.data.title || '研究方向'
      return this.data.content || this.data.title || '详情'
    },
    meta() {
      if (!this.data) return ''
      if (this.type === 'news' && this.data.time) return this.formatDate(this.data.time)
      if (this.type === 'paper' && this.data.publishTime) return '发布时间：' + this.formatDate(this.data.publishTime)
      if (this.type === 'project') return '项目时间：' + this.formatDate(this.data.startTime) + ' 至 ' + this.formatDate(this.data.endTime)
      if (this.type === 'award' && this.data.createTime) return '时间：' + this.formatDate(this.data.createTime)
      return ''
    },
    contentText() {
      if (!this.data) return ''
      return this.data.content || '暂无内容'
    },
    hasScholarContent() {
      if (this.type !== 'scholar' || !this.data) return false
      const a = this.data.intro && this.data.intro.length
      const b = this.data.paperList && this.data.paperList.length
      const c = this.data.projectList && this.data.projectList.length
      return a || b || c
    }
  },
  watch: {
    type: { handler() { this.fetch() } },
    id: { handler() { this.fetch() } }
  },
  mounted() {
    this.fetch()
  },
  methods: {
    async fetch() {
      if (this.id == null || this.id === undefined || this.id === '') {
        this.loading = false
        this.error = '缺少参数'
        return
      }
      this.loading = true
      this.error = ''
      this.data = null
      let url = ''
      switch (this.type) {
        case 'news': url = `${this.apiBase}/lab-news/${this.id}`; break
        case 'scholar': url = `${this.apiBase}/team/scholar/details/${this.id}`; break
        case 'paper': url = `${this.apiBase}/publication/${this.id}`; break
        case 'project': url = `${this.apiBase}/topic-project/${this.id}`; break
        case 'award': url = `${this.apiBase}/achievement/${this.id}`; break
        case 'direction': url = `${this.apiBase}/research/direction/${this.id}`; break
        default:
          this.loading = false
          this.error = '未知类型'
          return
      }
      try {
        const res = await fetch(url)
        const json = await res.json()
        if (json.code === 200 && json.data) {
          this.data = json.data
        } else {
          this.error = json.message || '获取详情失败'
        }
      } catch (e) {
        console.error(e)
        this.error = '获取详情失败，请稍后重试'
      } finally {
        this.loading = false
      }
    },
    formatDate(v) {
      if (!v) return ''
      return new Date(v).toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
    },
    goBack() {
      this.$emit('back', this.type)
    }
  }
}
</script>

<style scoped>
.detail-page {
  min-height: 80vh;
  padding: 48px 0 80px;
  background-color: #f6faf8;
}

.loading,
.error {
  text-align: center;
  padding: 60px 0;
  color: #6b6b6b;
}

.error .back-btn {
  margin-top: 16px;
}

.detail-header {
  margin-bottom: 32px;
}

.detail-title {
  font-size: 26px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 12px 0;
  line-height: 1.3;
}

.detail-meta {
  font-size: 14px;
  color: #6b6b6b;
  margin: 0;
}

.detail-body {
  background: #fff;
  border: 1px solid #dde8e4;
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 24px;
}

.detail-content {
  font-size: 16px;
  line-height: 1.75;
  color: #2c3e50;
  white-space: pre-wrap;
}

.scholar-photo {
  margin-bottom: 20px;
}

.scholar-photo img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  background: #f5f5f5;
}

.scholar-intro,
.scholar-papers,
.scholar-projects {
  margin-bottom: 24px;
}

.scholar-intro h4,
.scholar-papers h4,
.scholar-projects h4 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 12px 0;
  color: #2c3e50;
}

.intro-block,
.item-block {
  font-size: 15px;
  line-height: 1.6;
  color: #2c3e50;
  margin-bottom: 8px;
}

.empty-hint {
  color: #5a6c7d;
  font-size: 14px;
  margin: 0;
}

.back-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, #2d9d78 0%, #248f6a 100%);
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.2s;
}

.back-btn:hover {
  opacity: 0.9;
}
</style>
