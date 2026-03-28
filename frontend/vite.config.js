import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
    // 👇 只加了这一行！解决所有问题
    base: '/home/',
    server: {
        port: 5173,
        host: true
    }
})