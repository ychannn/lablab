<template>
  <div class="about">
    <div class="container">
      <h2 class="page-title">实验室介绍</h2>
      
      <!-- 实验室基本信息 -->
      <div class="about-section">
        <h3>基本信息</h3>
        <div v-if="labIntro.logo" class="intro-logo-wrap">
          <img :src="imageUrl(labIntro.logo)" alt="实验室Logo" class="intro-logo">
        </div>
        <div class="info-grid">
          <div class="info-item">
            <label>实验室名称</label>
            <p>{{ labIntro.labName || 'XX实验室' }}</p>
          </div>
          <div class="info-item">
            <label>英文名称</label>
            <p>{{ labIntro.englishName || 'XX Laboratory' }}</p>
          </div>
          <div class="info-item">
            <label>成立时间</label>
            <p>{{ labIntro.establishedDate || '2020年' }}</p>
          </div>
          <div class="info-item">
            <label>负责人</label>
            <p>{{ labIntro.leaderName || 'XXX教授' }}</p>
          </div>
        </div>
      </div>

      <!-- 实验室介绍 -->
      <div class="about-section">
        <h3>实验室简介</h3>
        <div class="intro-content">
          <p>{{ labIntro.introduction || '实验室致力于科学研究，追求卓越，为社会做出贡献。' }}</p>
        </div>
      </div>

      <!-- 研究方向 -->
      <div class="about-section">
        <h3>研究方向</h3>
        <div class="research-areas">
          <div v-for="(area, index) in labIntro.researchAreas || ['研究方向1', '研究方向2', '研究方向3']" :key="index" class="area-item">
            <span class="area-number">{{ index + 1 }}</span>
            <span class="area-name">{{ area }}</span>
          </div>
        </div>
      </div>

      <!-- 实验室负责人 -->
      <div class="about-section">
        <h3>实验室负责人</h3>
        <div class="leader-info">
          <div class="leader-photo">
            <img :src="imageUrl(labIntro.leaderPhoto) || 'https://via.placeholder.com/200x200'" alt="负责人照片">
          </div>
          <div class="leader-details">
            <h4>{{ labIntro.leaderName || 'XXX教授' }}</h4>
            <p>{{ labIntro.leaderIntroduction || 'XXX教授是实验室负责人，具有丰富的科研经验和深厚的学术造诣。' }}</p>
          </div>
        </div>
      </div>

      <!-- 实验室照片 -->
      <div class="about-section">
        <h3>实验室环境</h3>
        <div class="photo-grid">
          <div v-for="(photo, index) in labIntro.photos && labIntro.photos.length ? labIntro.photos : Array(4).fill('')" :key="index" class="photo-item">
            <img :src="imageUrl(photo) || 'https://via.placeholder.com/400x300'" alt="实验室照片">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { apiBase } from '../api/config.js'
export default {
  name: 'About',
  data() {
    return {
      labIntro: {}
    }
  },
  mounted() {
    this.fetchLabIntro()
  },
  methods: {
    imageUrl(url) {
      if (!url) return ''
      return url.startsWith('http') ? url : url
    },
    async fetchLabIntro() {
      try {
        const response = await fetch(apiBase + '/config/lab-intro')
        const data = await response.json()
        if (data.code === 200) {
          this.labIntro = data.data || {}
        }
      } catch (error) {
        console.error('获取实验室介绍失败:', error)
      }
    }
  }
}
</script>

<style scoped>
.about {
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

.about-section {
  background-color: #fff;
  padding: 40px;
  margin-bottom: 24px;
  border-radius: 12px;
  border: 1px solid #dde8e4;
}

.about-section h3 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #2c3e50;
  border-bottom: 1px solid #dde8e4;
  padding-bottom: 12px;
}

.intro-logo-wrap { margin-bottom: 24px; }
.intro-logo { max-height: 64px; max-width: 200px; object-fit: contain; }
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-item label {
  font-weight: 500;
  margin-bottom: 6px;
  color: #5a6c7d;
  font-size: 14px;
}

.info-item p {
  font-size: 16px;
  color: #2c3e50;
}

.intro-content p {
  font-size: 16px;
  line-height: 1.7;
  color: #2c3e50;
}

.research-areas {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.area-item {
  display: flex;
  align-items: center;
  background-color: #f0f7f4;
  padding: 12px 20px;
  border-radius: 8px;
  border: 1px solid #dde8e4;
}

.area-number {
  display: inline-block;
  width: 26px;
  height: 26px;
  background: linear-gradient(135deg, #2d9d78 0%, #248f6a 100%);
  color: #fff;
  border-radius: 50%;
  text-align: center;
  line-height: 26px;
  margin-right: 12px;
  font-size: 13px;
  font-weight: 500;
}

.area-name {
  font-size: 15px;
  color: #2c3e50;
}

.leader-info {
  display: flex;
  gap: 40px;
  align-items: flex-start;
}

.leader-photo {
  flex-shrink: 0;
}

.leader-photo img {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  object-fit: cover;
  background: #f0f7f4;
}

.leader-details h4 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #2c3e50;
}

.leader-details p {
  font-size: 16px;
  line-height: 1.7;
  color: #2c3e50;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.photo-item img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 12px;
  transition: opacity 0.2s;
}

.photo-item img:hover {
  opacity: 0.92;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .leader-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .photo-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }
}
</style>