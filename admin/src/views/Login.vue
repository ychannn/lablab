<template>
  <div class="login-page">
    <div class="login-card">
      <template v-if="viewMode === 'login'">
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
          <p class="forgot-row">
            <button type="button" class="btn-link" @click="viewMode = 'forgot'">忘记密码？</button>
          </p>
          <p v-if="error" class="error-msg">{{ error }}</p>
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? '登录中…' : '登录' }}
          </button>
        </form>
      </template>
      <template v-else-if="viewMode === 'forgot'">
        <h1 class="login-title">忘记密码</h1>
        <p class="login-desc">请输入您已绑定的管理员邮箱，我们将发送验证码到该邮箱。</p>
        <form v-if="!forgotCodeSent" @submit.prevent="sendForgotCode" class="login-form">
          <div class="form-group">
            <label>绑定邮箱</label>
            <input v-model="forgotEmail" type="email" class="form-input" required placeholder="请输入绑定邮箱" />
          </div>
          <p v-if="forgotError" class="error-msg">{{ forgotError }}</p>
          <div class="form-actions">
            <button type="button" class="btn btn-ghost" @click="viewMode = 'login'">返回登录</button>
            <button type="button" class="btn btn-code" :disabled="forgotCooldown > 0" @click="sendForgotCode">
              {{ forgotCooldown > 0 ? forgotCooldown + '秒后重发' : '获取验证码' }}
            </button>
          </div>
        </form>
        <form v-else @submit.prevent="submitForgotReset" class="login-form">
          <div class="form-group">
            <label>邮箱</label>
            <input :value="forgotEmail" type="email" class="form-input" readonly />
          </div>
          <div class="form-group form-group-code">
            <label>验证码</label>
            <input v-model="forgotCode" type="text" class="form-input" placeholder="6位验证码" maxlength="8" />
          </div>
          <div class="form-group">
            <label>新密码</label>
            <input v-model="forgotNewPassword" type="password" class="form-input" placeholder="6-32位" />
          </div>
          <div class="form-group">
            <label>确认密码</label>
            <input v-model="forgotConfirmPassword" type="password" class="form-input" placeholder="再次输入" />
          </div>
          <p v-if="forgotError" class="error-msg">{{ forgotError }}</p>
          <div class="form-actions">
            <button type="button" class="btn btn-ghost" @click="forgotCodeSent = false; forgotError = ''">上一步</button>
            <button type="submit" class="btn btn-primary" :disabled="forgotSubmitting">{{ forgotSubmitting ? '提交中…' : '确认重置' }}</button>
          </div>
        </form>
      </template>
    </div>
  </div>
</template>

<script>
import { request, setToken, API_BASE } from '../api/auth'

export default {
  name: 'Login',
  data() {
    return {
      viewMode: 'login',
      username: '',
      password: '',
      loading: false,
      error: '',
      forgotEmail: '',
      forgotCode: '',
      forgotNewPassword: '',
      forgotConfirmPassword: '',
      forgotCodeSent: false,
      forgotCooldown: 0,
      forgotError: '',
      forgotSubmitting: false,
      forgotCooldownTimer: null
    }
  },
  beforeUnmount() {
    if (this.forgotCooldownTimer) clearInterval(this.forgotCooldownTimer)
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
    },
    async sendForgotCode() {
      const email = (this.forgotEmail || '').trim()
      if (!email) {
        this.forgotError = '请输入邮箱'
        return
      }
      this.forgotError = ''
      try {
        const res = await fetch(API_BASE + '/admin/forgot-password/send-code', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email })
        })
        const data = await res.json().catch(() => ({}))
        if (data.code === 200) {
          this.forgotCodeSent = true
          this.forgotCooldown = 60
          this.forgotCooldownTimer = setInterval(() => {
            this.forgotCooldown--
            if (this.forgotCooldown <= 0 && this.forgotCooldownTimer) {
              clearInterval(this.forgotCooldownTimer)
              this.forgotCooldownTimer = null
            }
          }, 1000)
        } else {
          this.forgotError = data.message || '发送失败'
        }
      } catch (e) {
        this.forgotError = e.message || '网络错误'
      }
    },
    async submitForgotReset() {
      const { forgotEmail, forgotCode, forgotNewPassword, forgotConfirmPassword } = this
      this.forgotError = ''
      if (!forgotCode || !forgotCode.trim()) {
        this.forgotError = '请输入验证码'
        return
      }
      if (!forgotNewPassword || forgotNewPassword.length < 6 || forgotNewPassword.length > 32) {
        this.forgotError = '密码长度为6-32位'
        return
      }
      if (forgotNewPassword !== forgotConfirmPassword) {
        this.forgotError = '两次输入的密码不一致'
        return
      }
      this.forgotSubmitting = true
      try {
        const res = await fetch(API_BASE + '/admin/forgot-password/reset', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            email: forgotEmail.trim(),
            code: forgotCode.trim(),
            newPassword: forgotNewPassword
          })
        })
        const data = await res.json().catch(() => ({}))
        if (data.code === 200) {
          alert('密码已重置，请使用新密码登录')
          this.viewMode = 'login'
          this.forgotCodeSent = false
          this.forgotEmail = ''
          this.forgotCode = ''
          this.forgotNewPassword = ''
          this.forgotConfirmPassword = ''
        } else {
          this.forgotError = data.message || '重置失败'
        }
      } catch (e) {
        this.forgotError = e.message || '网络错误'
      } finally {
        this.forgotSubmitting = false
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
.login-desc { font-size: 13px; color: #666; margin-bottom: 20px; text-align: center; }
.login-form .form-group { margin-bottom: 20px; }
.login-form .form-group-code { display: flex; gap: 8px; align-items: flex-end; }
.login-form .form-group-code .form-input { flex: 1; }
.login-form label { display: block; margin-bottom: 8px; font-weight: 500; color: #333; }
.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}
.form-input:focus { border-color: #1890ff; outline: none; }
.forgot-row { margin-bottom: 12px; }
.btn-link { background: none; border: none; color: #1890ff; cursor: pointer; padding: 0; font-size: 14px; }
.btn-link:hover { text-decoration: underline; }
.error-msg { color: #ff4d4f; font-size: 14px; margin-bottom: 12px; }
.form-actions { display: flex; gap: 12px; justify-content: flex-end; margin-top: 20px; }
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}
.btn-primary { background: #1890ff; color: #fff; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-ghost { background: #f5f5f5; color: #333; border: 1px solid #d9d9d9; }
.btn-code { background: #1890ff; color: #fff; }
.btn-code:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
