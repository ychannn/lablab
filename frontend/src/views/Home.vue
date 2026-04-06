<template>
  <div class="home">
    <!-- 主体内容 -->
    <div class="main-content">
      <div class="container">
        <div class="content-row">
          <!-- 左侧内容 -->
          <div class="left-content">
            <!-- 新闻轮播 -->
            <div class="news-carousel-section">
              <h3 class="section-title">新闻动态</h3>
              <div v-if="latestNews.length" class="news-carousel" @mouseenter="stopNewsAuto" @mouseleave="startNewsAuto">
                <div class="news-track" :style="{ transform: 'translateX(-' + newsIndex * 100 + '%)' }">
                  <div
                    v-for="(item, idx) in latestNews"
                    :key="item.id"
                    class="news-slide"
                    @click="goToNews(item.id)"
                  >
                    <div class="news-slide-img" :style="{ backgroundImage: item.imageUrl ? 'url(' + newsImageUrl(item.imageUrl) + ')' : 'none' }"></div>
                    <div class="news-slide-body">
                      <h4 class="news-slide-title">{{ item.title }}</h4>
                      <p class="news-slide-meta">{{ formatTime(item.time) }}</p>
                      <p class="news-slide-snippet">{{ getPlainTextSummary(item.contentSnippet || item.content) }}</p>
                    </div>
                  </div>
                </div>
                <button v-if="latestNews.length > 1" type="button" class="news-arrow news-prev" @click="newsPrev">‹</button>
                <button v-if="latestNews.length > 1" type="button" class="news-arrow news-next" @click="newsNext">›</button>
                <div v-if="latestNews.length > 1" class="news-dots">
                  <button v-for="(item, idx) in latestNews" :key="idx" type="button" :class="{ active: newsIndex === idx }" @click="newsIndex = idx"></button>
                </div>
              </div>
              <p v-else class="empty-tip">暂无新闻</p>
              <a href="/home/news" class="more-link">更多 &gt;</a>
            </div>
            
            <!-- 校园风景展示 -->
            <div class="campus-carousel-section">
              <h3 class="section-title">校园风景</h3>
              <div class="main-image">
                <div v-if="bannerList.length" class="campus-carousel" @mouseenter="stopAutoPlay" @mouseleave="startAutoPlay">
                  <div class="campus-track" :style="{ transform: 'translateX(-' + currentIndex * 100 + '%)' }">
                    <div
                      v-for="(item, idx) in bannerList"
                      :key="idx"
                      class="campus-slide"
                    >
                      <img :src="bannerImageUrl(item.imageUrl)" alt="校园风景" />
                    </div>
                  </div>
                  <button v-if="bannerList.length > 1" type="button" class="campus-arrow campus-prev" @click="prev">‹</button>
                  <button v-if="bannerList.length > 1" type="button" class="campus-arrow campus-next" @click="next">›</button>
                  <div v-if="bannerList.length > 1" class="campus-dots">
                    <button v-for="(item, idx) in bannerList" :key="idx" type="button" :class="{ active: currentIndex === idx }" @click="goTo(idx)"></button>
                  </div>
                </div>
                <div v-else class="campus-placeholder">
                  <img src="https://via.placeholder.com/800x450" alt="校园风景" />
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧信息栏 -->
          <div class="right-content">
            <!-- 实验室动态 -->
            <div class="info-section">
              <h3 class="section-title">实验室动态</h3>
              <ul class="info-list">
                <li 
                  v-for="item in latestNews" 
                  :key="item.id" 
                  class="info-item"
                  @click="goToNews(item.id)"
                >
                  <span class="info-date">{{ formatTime(item.time) }}</span>
                  <span class="info-title">{{ item.title }}</span>
                </li>
                <li v-if="latestNews.length === 0" class="info-item empty">
                  <span class="empty-tip">暂无动态</span>
                </li>
              </ul>
              <a href="/home/news" class="more-link">更多 &gt;</a>
            </div>

            <!-- 公告通知 -->
            <div class="info-section">
              <h3 class="section-title">公告通知</h3>
              <ul class="info-list">
                <li 
                  v-for="item in latestNotices" 
                  :key="item.id" 
                  class="info-item"
                  @click="goToNotice(item.id)"
                >
                  <span class="info-date">{{ formatTime(item.time || item.createTime) }}</span>
                  <span class="info-title">{{ item.title }}</span>
                </li>
                <li v-if="latestNotices.length === 0" class="info-item empty">
                  <span class="empty-tip">暂无公告</span>
                </li>
              </ul>
              <a href="/home/news?type=notice" class="more-link">更多 &gt;</a>
            </div>

            <!-- 友情链接 -->
            <div class="info-section">
              <h3 class="section-title">友情链接</h3>
              <ul class="link-list">
                <li><a href="#" class="friend-link">广东外语外贸大学</a></li>
                <li><a href="#" class="friend-link">信息科学与技术学院</a></li>
                <li><a href="#" class="friend-link">教育部</a></li>
                <li><a href="#" class="friend-link">广东省教育厅</a></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>


  </div>
</template>

<script>
import { apiBase } from '../api/config.js'
export default {
  name: 'Home',
  data() {
    return {
      apiBase,
      bannerList: [],
      currentIndex: 0,
      autoPlayTimer: null,
      latestAchievements: [],
      latestNews: [],
      latestNotices: [],
      newsIndex: 0,
      newsAutoTimer: null,
      labIntro: {
        introduction: '实验室致力于科学研究，追求卓越，为社会做出贡献。'
      },
      contact: {
        address: '北京市海淀区学院路30号',
        phone: '010-12345678',
        email: 'lab@example.com',
        workTime: '周一至周五 9:00-17:00'
      }
    }
  },
  computed: {
    trackStyle() {
      return {
        transform: `translateX(-${this.currentIndex * 100}%)`,
        transition: 'transform 0.5s ease'
      }
    },
    labIntroImageUrl() {
      const photos = this.labIntro.photos
      if (photos && photos.length) {
        const url = photos[0]
        return url && (url.startsWith('http') ? url : url)
      }
      if (this.labIntro.logo) {
        const url = this.labIntro.logo
        return url.startsWith('http') ? url : url
      }
      return ''
    }
  },
  mounted() {
    this.fetchHomeData()
    this.startAutoPlay()
  },
  watch: {
    bannerList(list) {
      if (this.currentIndex >= (list && list.length)) {
        this.currentIndex = Math.max(0, (list && list.length) - 1)
      }
      this.startAutoPlay()
    },

  },
  beforeUnmount() {
    this.stopAutoPlay()
    this.stopNewsAuto()
  },
  methods: {
    async fetchHomeData() {
      try {
        const response = await fetch(apiBase + '/home')
        const data = await response.json()
        if (data.code === 200) {
          this.bannerList = data.data.bannerList || []
          this.latestAchievements = data.data.latestAchievements || []
          this.latestNews = data.data.latestNews || []
          this.latestNotices = data.data.latestNotices || []
          this.labIntro = data.data.labIntro || this.labIntro
          this.newsIndex = 0
          this.startNewsAuto()
          this.contact = data.data.contact || this.contact
        }
      } catch (error) {
        console.error('获取首页数据失败:', error)
      }
    },
    getAchievementType(type) {
      const typeMap = {
        paper: '论文',
        project: '项目',
        award: '奖项'
      }
      return typeMap[type] || type
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const year = date.getFullYear()
      const month = date.getMonth() + 1
      const day = date.getDate()
      return `${year}年${month}月${day}日`
    },
    bannerImageUrl(url) {
      if (!url) return ''
      return url.startsWith('http') ? url : url
    },
    prev() {
      if (this.bannerList.length <= 1) return
      this.currentIndex = this.currentIndex <= 0 ? this.bannerList.length - 1 : this.currentIndex - 1
    },
    next() {
      if (this.bannerList.length <= 1) return
      this.currentIndex = this.currentIndex >= this.bannerList.length - 1 ? 0 : this.currentIndex + 1
    },
    goTo(index) {
      if (index >= 0 && index < this.bannerList.length) this.currentIndex = index
    },
    startAutoPlay() {
      this.stopAutoPlay()
      if (this.bannerList.length <= 1) return
      this.autoPlayTimer = setInterval(() => this.next(), 4000)
    },
    stopAutoPlay() {
      if (this.autoPlayTimer) {
        clearInterval(this.autoPlayTimer)
        this.autoPlayTimer = null
      }
    },
    navigate(page, params) {
      this.$parent.navigate(page, params)
    },
    goToAchievement(achievement) {
      if (!achievement || !achievement.type) return
      const id = achievement.id
      if (id != null) {
        this.navigate('detail', { type: achievement.type, id })
      } else {
        this.navigate('achievements', { tab: achievement.type })
      }
    },
    goToNotice(id) {
      if (id != null) this.navigate('detail', { type: 'notice', id })
    },
    goToNews(id) {
      if (id != null) this.navigate('detail', { type: 'news', id })
    },
    newsImageUrl(url) {
      if (!url) return ''
      return url.startsWith('http') ? url : url
    },
    imageUrl(url) {
      if (!url) return ''
      return url.startsWith('http') ? url : url
    },
    newsPrev() {
      if (this.latestNews.length <= 1) return
      this.newsIndex = this.newsIndex <= 0 ? this.latestNews.length - 1 : this.newsIndex - 1
    },
    newsNext() {
      if (this.latestNews.length <= 1) return
      this.newsIndex = this.newsIndex >= this.latestNews.length - 1 ? 0 : this.newsIndex + 1
    },
    startNewsAuto() {
      this.stopNewsAuto()
      if (this.latestNews.length <= 1) return
      this.newsAutoTimer = setInterval(() => this.newsNext(), 4500)
    },
    stopNewsAuto() {
      if (this.newsAutoTimer) {
        clearInterval(this.newsAutoTimer)
        this.newsAutoTimer = null
      }
    },
    getPlainTextSummary(html) {
      if (!html) return ''
      // 创建一个临时元素来解析HTML
      const tempElement = document.createElement('div')
      tempElement.innerHTML = html
      // 提取纯文本
      const plainText = tempElement.textContent || tempElement.innerText || ''
      // 去除多余的空白字符
      const cleanText = plainText.replace(/\s+/g, ' ').trim()
      // 截取前150个字符作为摘要
      return cleanText.substring(0, 150) + (cleanText.length > 150 ? '...' : '')
    }
  }
}
</script>

<style>
.home {
  min-height: 100vh;
  font-family: Arial, sans-serif;
}

/* 顶部导航栏 */
.header {
  background-color: #fff;
  border-bottom: 1px solid #e0e0e0;
  padding: 15px 0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo img {
  width: 60px;
  height: 60px;
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.lab-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.lab-english {
  font-size: 12px;
  color: #666;
  margin: 5px 0 0 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-box {
  display: flex;
  align-items: center;
}

.search-box input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px 0 0 4px;
  font-size: 14px;
}

.search-btn {
  padding: 8px 12px;
  border: 1px solid #165DFF;
  background-color: #165DFF;
  color: #fff;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  font-size: 14px;
}

.admin-link {
  font-size: 14px;
  color: #333;
  text-decoration: none;
}

.main-nav {
  border-top: 1px solid #e0e0e0;
  padding-top: 15px;
}

.nav-links {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 30px;
}

.nav-link {
  color: #333;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  padding: 5px 0;
  position: relative;
}

.nav-link.active {
  color: #2c3e50;
  border-bottom: 2px solid #d6e0f0;
  font-weight: 600;
}

.nav-link:hover {
  color: #2c3e50;
  border-bottom: 2px solid #d6e0f0;
}

/* 主体内容 */
.main-content {
  padding: 20px 0;
}

.content-row {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 30px;
  align-items: start;
}

/* 左侧内容 */
.left-content {
  min-width: 0;
}

.main-image {
  position: relative;
  border: 1px solid #e0e0e0;
  overflow: hidden;
}

.main-image img {
  width: 100%;
  height: auto;
  display: block;
}

.campus-carousel {
  position: relative;
  overflow: hidden;
}

.campus-track {
  display: flex;
  transition: transform 0.35s ease;
}

.campus-slide {
  flex: 0 0 100%;
  width: 100%;
}

.campus-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 2;
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.2);
  color: #fff;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.campus-prev { left: 10px; }
.campus-next { right: 10px; }

.campus-dots {
  position: absolute;
  bottom: 40px;
  left: 0;
  right: 0;
  z-index: 2;
  display: flex;
  justify-content: center;
  gap: 5px;
}

.campus-dots button {
  width: 8px;
  height: 8px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  padding: 0;
}

.campus-dots button.active {
  background: #d6e0f0;
}

.campus-placeholder {
  width: 100%;
}

.campus-carousel-section {
  border: 1px solid #e0e0e0;
  padding: 20px;
  background-color: #fff;
  margin-bottom: 30px;
}

.campus-carousel-section .section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
}

.news-carousel-section {
  border: 1px solid #e0e0e0;
  padding: 20px;
  background-color: #fff;
}

.news-carousel-section .section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
}

.news-carousel {
  position: relative;
  overflow: hidden;
  margin-bottom: 15px;
}

.news-track {
  display: flex;
  transition: transform 0.35s ease;
}

.news-slide {
  flex: 0 0 100%;
  width: 100%;
  cursor: pointer;
}

.news-slide-img {
  width: 100%;
  height: 200px;
  background-size: cover;
  background-position: center;
  background-color: #f0f0f0;
}

.news-slide-body {
  padding: 15px;
  background-color: #f9f9f9;
}

.news-slide-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin: 0 0 10px 0;
}

.news-slide-meta {
  font-size: 12px;
  color: #999;
  margin: 0 0 10px 0;
}

.news-slide-snippet {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  margin: 0;
}

.news-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 2;
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.2);
  color: #fff;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.news-prev { left: 10px; }
.news-next { right: 10px; }

.news-dots {
  position: absolute;
  bottom: 10px;
  left: 0;
  right: 0;
  z-index: 2;
  display: flex;
  justify-content: center;
  gap: 5px;
}

.news-dots button {
  width: 8px;
  height: 8px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  padding: 0;
}

.news-dots button.active {
  background: #d6e0f0;
}

.more-link {
  display: block;
  text-align: right;
  font-size: 14px;
  color: #5a6c7d;
  text-decoration: none;
  margin-top: 10px;
  transition: color 0.2s;
}

.more-link:hover {
  color: #2c3e50;
  text-decoration: underline;
}

/* 右侧信息栏 */
.right-content {
  min-width: 0;
}

.info-section {
  border: 1px solid #e0e0e0;
  padding: 20px;
  background-color: #fff;
  margin-bottom: 20px;
}

.info-section .section-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
}

.info-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item:hover {
  background-color: #f9f9f9;
}

.info-date {
  font-size: 12px;
  color: #999;
}

.info-title {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
}

.link-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.link-list li {
  margin-bottom: 10px;
}

.friend-link {
  font-size: 14px;
  color: #333;
  text-decoration: none;
}

.friend-link:hover {
  color: #165DFF;
  text-decoration: underline;
}

.empty-tip {
  color: #999;
  font-size: 14px;
  margin: 0;
  padding: 10px 0;
}

/* 页脚 */
.footer {
  background-color: #f5f5f5;
  border-top: 1px solid #e0e0e0;
  padding: 20px 0;
  margin-top: 30px;
}

.footer p {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-row {
    grid-template-columns: 1fr;
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .nav-links {
    flex-wrap: wrap;
    gap: 15px;
  }

  .main-image img {
    height: auto;
  }

  .news-slide-img {
    height: 150px;
  }
}
</style>