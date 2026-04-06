<template>
  <div class="about">
    <div class="container">
      <h2 class="page-title">实验室介绍</h2>
      
      <!-- 实验室介绍 -->
      <div class="about-section">
        <h3>实验室简介</h3>
        <div class="intro-content">
          <p>{{ labIntro.introduction || '实验室致力于科学研究，追求卓越，为社会做出贡献。' }}</p>
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
      <div v-if="labIntro.photos && labIntro.photos.length" class="about-section">
        <h3>实验室环境</h3>
        <div class="photo-grid">
          <div v-for="(photo, index) in labIntro.photos" :key="index" class="photo-item">
            <img :src="imageUrl(photo)" alt="实验室照片">
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
  background-color: #f5f7ff;
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
  border: 1px solid #e8eeff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.about-section h3 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #2c3e50;
  border-bottom: 2px solid #d6e0f0;
  padding-bottom: 12px;
  position: relative;
}

.about-section h3::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, #d6e0f0, transparent);
}

.intro-content {
  padding: 24px;
  background-color: #f5f7ff;
  border-radius: 8px;
  border-left: 4px solid #d6e0f0;
}

.intro-content p {
  font-size: 16px;
  line-height: 1.7;
  color: #2c3e50;
  margin: 0;
  text-align: justify;
}

.leader-info {
  display: flex;
  gap: 40px;
  align-items: flex-start;
  padding: 24px;
  background-color: #f5f7ff;
  border-radius: 8px;
  border: 1px solid #d6e0f0;
}

.leader-photo {
  flex-shrink: 0;
  position: relative;
}

.leader-photo::before {
  content: '';
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border: 2px solid #d6e0f0;
  border-radius: 50%;
  opacity: 0.3;
  transition: all 0.3s ease;
}

.leader-photo:hover::before {
  opacity: 1;
  transform: scale(1.05);
}

.leader-photo img {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  object-fit: cover;
  background: #f5f7ff;
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.leader-details {
  flex: 1;
}

.leader-details h4 {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #2c3e50;
  display: flex;
  align-items: center;
}

.leader-details h4::after {
  content: '';
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, #d6e0f0, transparent);
  margin-left: 20px;
}

.leader-details p {
  font-size: 16px;
  line-height: 1.7;
  color: #2c3e50;
  margin: 0;
  text-align: justify;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.photo-item {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.photo-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.photo-item img {
  width: 100%;
  height: 220px;
  object-fit: cover;
  transition: all 0.3s ease;
}

.photo-item:hover img {
  transform: scale(1.05);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .leader-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .leader-details h4::after {
    display: none;
  }
  
  .photo-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
  
  .about-section {
    padding: 24px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>