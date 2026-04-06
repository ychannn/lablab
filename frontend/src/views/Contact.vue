<template>
  <div class="contact">
    <div class="container">
      <h2 class="page-title">联系我们</h2>
      
      <div class="contact-content">
        <!-- 联系信息 -->
        <div class="contact-info">
          <h3 class="section-title">联系方式</h3>
          <div class="info-grid">
            <div class="info-item">
              <div class="info-icon">📍</div>
              <div class="info-details">
                <h4>地址</h4>
                <p>{{ contact.address }}</p>
              </div>
            </div>
            <div class="info-item">
              <div class="info-icon">📞</div>
              <div class="info-details">
                <h4>电话</h4>
                <p>{{ contact.phone }}</p>
              </div>
            </div>
            <div class="info-item">
              <div class="info-icon">✉️</div>
              <div class="info-details">
                <h4>邮箱</h4>
                <p>{{ contact.email }}</p>
              </div>
            </div>
            <div class="info-item">
              <div class="info-icon">🕒</div>
              <div class="info-details">
                <h4>工作时间</h4>
                <p>{{ contact.workTime }}</p>
              </div>
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
  name: 'Contact',
  data() {
    return {
      contact: {
        labName: 'XX实验室',
        address: '北京市海淀区学院路30号',
        zipCode: '100000',
        phone: '010-12345678',
        email: 'lab@example.com',
        workTime: '周一至周五 9:00-17:00'
      }
    }
  },
  mounted() {
    this.fetchContactInfo()
  },
  methods: {
    async fetchContactInfo() {
      try {
        const response = await fetch(apiBase + '/config/contact')
        const data = await response.json()
        if (data.code === 200) {
          this.contact = data.data || this.contact
        }
      } catch (error) {
        console.error('获取联系信息失败:', error)
      }
    }
  }
}
</script>

<style scoped>
.contact {
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

.contact-content {
  display: grid;
  grid-template-columns: 1fr;
  gap: 28px;
}

.contact-info {
  background-color: #fff;
  border-radius: 12px;
  border: 1px solid #e8eeff;
  padding: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 28px;
  color: #2c3e50;
  border-bottom: 2px solid #d6e0f0;
  padding-bottom: 12px;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, #d6e0f0, transparent);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background-color: #f9faff;
  border-radius: 8px;
  border: 1px solid #e8eeff;
  transition: all 0.3s ease;
}

.info-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.info-icon {
  font-size: 24px;
  flex-shrink: 0;
  margin-top: 2px;
  color: #5a6c7d;
}

.info-details h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 6px;
  color: #2c3e50;
  display: flex;
  align-items: center;
}

.info-details h4::after {
  content: '';
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, #d6e0f0, transparent);
  margin-left: 12px;
}

.info-details p {
  font-size: 14px;
  color: #5a6c7d;
  margin: 0;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .contact-info {
    padding: 24px;
  }
  
  .info-details h4::after {
    display: none;
  }
}
</style>