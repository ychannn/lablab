<template>
  <div class="home">
    <!-- 轮播图：后台配置，丝滑切换 + 指示点 + 左右按钮 -->
    <div class="banner" v-if="bannerList.length" @mouseenter="stopAutoPlay" @mouseleave="startAutoPlay">
      <div class="banner-track" :style="trackStyle">
        <div v-for="(item, index) in bannerList" :key="index" class="banner-item">
          <a v-if="item.link" :href="item.link" class="banner-link" target="_blank" rel="noopener">
            <div class="banner-bg" :style="{ backgroundImage: 'url(' + bannerImageUrl(item.imageUrl) + ')' }"></div>
            <div v-if="item.title" class="banner-caption">{{ item.title }}</div>
          </a>
          <template v-else>
            <div class="banner-bg" :style="{ backgroundImage: 'url(' + bannerImageUrl(item.imageUrl) + ')' }"></div>
            <div v-if="item.title" class="banner-caption">{{ item.title }}</div>
          </template>
        </div>
      </div>
      <button type="button" class="banner-arrow banner-prev" aria-label="上一张" @click="prev">
        <span>‹</span>
      </button>
      <button type="button" class="banner-arrow banner-next" aria-label="下一张" @click="next">
        <span>›</span>
      </button>
      <div class="banner-dots">
        <button
          v-for="(item, index) in bannerList"
          :key="index"
          type="button"
          :class="{ active: currentIndex === index }"
          :aria-label="'第' + (index + 1) + '张'"
          @click="goTo(index)"
        ></button>
      </div>
    </div>

    <!-- 最新公告 -->
    <div v-if="latestNotices.length" class="notices-section">
      <div class="container">
        <div class="section-head">
          <h2 class="section-title">最新公告</h2>
          <button type="button" class="section-more" @click="navigate('notice')">更多</button>
        </div>
        <ul class="notices-list">
          <li
            v-for="item in latestNotices"
            :key="item.id"
            class="notices-item"
            @click="goToNotice(item.id)"
          >
            <span class="notices-date">{{ formatTime(item.time || item.createTime) }}</span>
            <span class="notices-title">{{ item.title }}</span>
          </li>
        </ul>
      </div>
    </div>

    <!-- 最新成果 -->
    <div class="achievements">
      <div class="container">
        <div class="section-head">
          <h2 class="section-title">最新成果</h2>
          <button type="button" class="section-more" @click="navigate('achievements')">更多</button>
        </div>
        <div class="achievements-grid">
          <div
            v-for="(achievement, index) in latestAchievements"
            :key="index"
            class="achievement-card"
            @click="goToAchievement(achievement)"
          >
            <div class="achievement-type">{{ getAchievementType(achievement.type) }}</div>
            <h3 class="achievement-title">{{ achievement.title }}</h3>
            <p class="achievement-time">{{ formatTime(achievement.time) }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 实验室简介 -->
    <div class="lab-intro">
      <div class="container">
        <h2 class="section-title">实验室简介</h2>
        <div class="intro-content">
          <div class="intro-text">
            <p>{{ labIntro.introduction }}</p>
            <button class="intro-btn" @click="navigate('about')">了解更多</button>
          </div>
          <div class="intro-img">
            <img :src="labIntroImageUrl || 'https://via.placeholder.com/500x300'" alt="实验室图片">
          </div>
        </div>
      </div>
    </div>

    <!-- 联系方式 -->
    <div class="contact-info">
      <div class="container">
        <h2 class="section-title">联系我们</h2>
        <div class="contact-grid">
          <div class="contact-item">
            <h3>地址</h3>
            <p>{{ contact.address }}</p>
          </div>
          <div class="contact-item">
            <h3>电话</h3>
            <p>{{ contact.phone }}</p>
          </div>
          <div class="contact-item">
            <h3>邮箱</h3>
            <p>{{ contact.email }}</p>
          </div>
          <div class="contact-item">
            <h3>工作时间</h3>
            <p>{{ contact.workTime }}</p>
          </div>
        </div>
        <div class="contact-more">
          <button type="button" class="intro-btn" @click="navigate('contact')">查看更多</button>
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
      latestNotices: [],
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
      const base = this.apiBase
      const photos = this.labIntro.photos
      if (photos && photos.length) {
        const url = photos[0]
        return url && (url.startsWith('http') ? url : base + (url.startsWith('/') ? '' : '/') + url)
      }
      if (this.labIntro.logo) {
        const url = this.labIntro.logo
        return url.startsWith('http') ? url : base + (url.startsWith('/') ? '' : '/') + url
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
    }
  },
  beforeUnmount() {
    this.stopAutoPlay()
  },
  methods: {
    async fetchHomeData() {
      try {
        const response = await fetch(apiBase + '/home')
        const data = await response.json()
        if (data.code === 200) {
          this.bannerList = data.data.bannerList || []
          this.latestAchievements = data.data.latestAchievements || []
          this.latestNotices = data.data.latestNotices || []
          this.labIntro = data.data.labIntro || this.labIntro
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
      return date.toLocaleDateString()
    },
    bannerImageUrl(url) {
      if (!url) return ''
      return url.startsWith('http') ? url : this.apiBase + url
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
    }
  }
}
</script>

<style>
.home {
  min-height: 100vh;
}

/* 轮播图 */
.banner {
  position: relative;
  height: 480px;
  overflow: hidden;
}

.banner-track {
  display: flex;
  height: 100%;
  width: 100%;
}

.banner-item {
  flex: 0 0 100%;
  width: 100%;
  position: relative;
  height: 100%;
  display: flex;
  align-items: center;
}

.banner-link {
  display: block;
  position: relative;
  width: 100%;
  height: 100%;
  text-decoration: none;
  color: inherit;
}

.banner-caption {
  position: absolute;
  bottom: 20px;
  left: 10%;
  z-index: 2;
  color: #fff;
  font-size: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.banner-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  filter: brightness(0.7);
}

.banner-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 3;
  width: 44px;
  height: 44px;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.25);
  color: #fff;
  font-size: 24px;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}
.banner-arrow:hover {
  background: rgba(0, 0, 0, 0.45);
}
.banner-prev {
  left: 20px;
}
.banner-next {
  right: 20px;
}

.banner-dots {
  position: absolute;
  bottom: 24px;
  left: 0;
  right: 0;
  z-index: 3;
  display: flex;
  justify-content: center;
  gap: 10px;
}
.banner-dots button {
  width: 8px;
  height: 8px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  cursor: pointer;
  padding: 0;
  transition: background 0.2s, transform 0.2s;
}
.banner-dots button:hover {
  background: rgba(255, 255, 255, 0.7);
}
.banner-dots button.active {
  background: #fff;
  transform: scale(1.15);
}

/* 最新成果 */
.achievements {
  padding: 72px 0;
  background-color: #fff;
  border-radius: 0;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 48px;
}

.section-title {
  text-align: center;
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 48px;
  color: #2c3e50;
  letter-spacing: 0.02em;
}

.section-head .section-title {
  margin-bottom: 0;
}

.section-more {
  padding: 6px 14px;
  background: transparent;
  border: 1px solid #2d9d78;
  color: #2d9d78;
  font-size: 14px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.section-more:hover {
  background: #2d9d78;
  color: #fff;
}

.achievements-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.achievement-card {
  background-color: #f6faf8;
  border: 1px solid #dde8e4;
  padding: 24px;
  border-radius: 12px;
  transition: border-color 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.achievement-card:hover {
  border-color: #2d9d78;
  box-shadow: 0 4px 20px rgba(45, 157, 120, 0.15);
}

.achievement-type {
  display: inline-block;
  background: linear-gradient(135deg, #2d9d78 0%, #248f6a 100%);
  color: #fff;
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 12px;
}

.achievement-title {
  font-size: 17px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #2c3e50;
}

.achievement-time {
  font-size: 14px;
  color: #5a6c7d;
}

/* 最新公告 */
.notices-section {
  padding: 48px 0 72px;
  background-color: #f6faf8;
}

.notices-list {
  list-style: none;
  margin: 0;
  padding: 0;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #dde8e4;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}

.notices-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px 24px;
  border-bottom: 1px solid #e8f0ed;
  cursor: pointer;
  transition: background-color 0.2s;
}

.notices-item:last-child {
  border-bottom: none;
}

.notices-item:hover {
  background-color: #f0f7f4;
}

.notices-date {
  flex-shrink: 0;
  font-size: 13px;
  color: #5a6c7d;
}

.notices-title {
  flex: 1;
  font-size: 15px;
  font-weight: 500;
  color: #2c3e50;
}

/* 实验室简介 */
.lab-intro {
  padding: 72px 0;
  background-color: #f6faf8;
}

.intro-content {
  display: flex;
  align-items: center;
  gap: 48px;
}

.intro-text {
  flex: 1;
}

.intro-text p {
  font-size: 16px;
  line-height: 1.7;
  margin-bottom: 24px;
  color: #2c3e50;
}

.intro-btn {
  background: linear-gradient(135deg, #2d9d78 0%, #248f6a 100%);
  color: #fff;
  border: none;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 6px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.intro-btn:hover {
  opacity: 0.9;
}

.intro-img {
  flex: 1;
}

.intro-img img {
  width: 100%;
  height: auto;
  border-radius: 12px;
}

/* 联系方式 */
.contact-info {
  padding: 72px 0;
  background-color: #fff;
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 32px;
  text-align: center;
}

.contact-item h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #2c3e50;
}

.contact-item p {
  font-size: 15px;
  color: #5a6c7d;
}

.contact-more {
  margin-top: 28px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-caption {
    font-size: 16px;
  }
  
  .intro-content {
    flex-direction: column;
  }
  
  .intro-img {
    width: 100%;
  }
}
</style>