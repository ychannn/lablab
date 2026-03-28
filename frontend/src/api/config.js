/**
 * 后端 API 地址。内网穿透时设为 VITE_API_BASE 环境变量（如 ngrok 的后端地址）。
 */
export const apiBase = (typeof import.meta !== 'undefined' && import.meta.env && import.meta.env.VITE_API_BASE)
  ? import.meta.env.VITE_API_BASE
  : ''  // 🔥 改成空字符串！！！