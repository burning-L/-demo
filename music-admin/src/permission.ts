import router from '@/router'
import setting from './setting'
import nprogress from 'nprogress'
import 'nprogress/nprogress.css'
import pinia from './stores'
import useUserStore from './stores/modules/user'
import { asyncRoute } from './router/routes'

nprogress.configure({ showSpinner: false })
const userStore = useUserStore(pinia)

// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title + ` | ${setting.title}`
  nprogress.start()
  const token = userStore.token
  const username = userStore.username
  if (token) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      if (username) {
        next()
      } else {
        try {
          await userStore.userInfo()
          // 添加异步路由
          asyncRoute.forEach((route) => {
            router.addRoute(route)
          })
          next({ ...to, replace: true })
        } catch (error) {
          await userStore.userLogout()
          next({ path: '/login', query: { redirect: to.path } })
        }
      }
    }
  } else {
    if (to.path == '/login') {
      next()
    } else {
      next({ path: '/login', query: { redirect: to.path } })
    }
  }
})

// 全局后置守卫
router.afterEach((route) => {
  nprogress.done()
})
