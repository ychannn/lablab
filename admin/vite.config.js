import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  // ✅ 核心：添加这一行，指定前端运行在 /home/ 路径下
  base: '/admin/',
  server: {
    port: 5174,
    host: true
  }
})