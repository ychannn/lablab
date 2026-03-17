<template>
  <div class="login-page">
    <div class="login-card">
      <h1 class="login-title">后台管理登录</h1>
      <form @submit.prevent="submit" class="login-form">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="username" type="text" class="form-input" required placeholder="请输入用户名" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="password" type="password" class="form-input" required placeholder="请输入密码" />
        </div>
        <p v-if="error" class="error-msg">{{ error }}</p>
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? '登录中…' : '登录' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script>
import { request, setToken } from '../api/auth'

export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      loading: false,
      error: ''
    }
  },
  methods: {
    async submit() {
      this.error = ''
      this.loading = true
      try {
        const data = await request('/admin/login', {
          method: 'POST',
          body: JSON.stringify({ username: this.username, password: this.password })
        })
        if (data.code === 200 && data.data && data.data.token) {
          setToken(data.data.token)
          this.$emit('login-success')
        } else {
          this.error = data.message || '登录失败'
        }
      } catch (e) {
        this.error = e.message || '登录失败'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
}
.login-card {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  padding: 40px;
}
.login-title {
  font-size: 22px;
  text-align: center;
  margin-bottom: 28px;
  color: #333;
}
.login-form .form-group { margin-bottom: 20px; }
.login-form label { display: block; margin-bottom: 8px; font-weight: 500; color: #333; }
.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}
.form-input:focus { border-color: #1890ff; outline: none; }
.error-msg { color: #ff4d4f; font-size: 14px; margin-bottom: 12px; }
.btn {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}
.btn-primary { background: #1890ff; color: #fff; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
