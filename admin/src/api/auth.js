
const API_BASE = (typeof import.meta !== 'undefined' && import.meta.env && import.meta.env.VITE_API_BASE) ? import.meta.env.VITE_API_BASE : ''
const TOKEN_KEY = 'admin_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  if (token) localStorage.setItem(TOKEN_KEY, token)
  else localStorage.removeItem(TOKEN_KEY)
}

export function isLoggedIn() {
  return !!getToken()
}

/**
 * 带鉴权的请求，自动附加 Authorization: Bearer <token>
 */
export async function request(url, options = {}) {
  const token = getToken()
  const headers = {
    ...(options.headers || {}),
  }
  if (token) {
    headers['Authorization'] = 'Bearer ' + token
  }
  if (options.body != null && !headers['Content-Type']) {
    if (typeof options.body === 'string') {
      headers['Content-Type'] = 'application/json'
    } else if (typeof options.body === 'object' && !(options.body instanceof FormData)) {
      headers['Content-Type'] = 'application/json'
    }
  }
  const fullUrl = url.startsWith('http') ? url : API_BASE + url
  const res = await fetch(fullUrl, { ...options, headers })
  const data = await res.json().catch(() => ({}))
  if (data.code === 401 || res.status === 401) {
    setToken(null)
    if (typeof window !== 'undefined') window.dispatchEvent(new Event('admin-logout'))
    throw new Error('登录已过期，请重新登录')
  }
  return data
}

export { API_BASE }