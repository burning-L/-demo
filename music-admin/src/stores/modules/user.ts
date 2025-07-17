import {reactive} from 'vue'
import { defineStore } from 'pinia'
import {UserState} from "@/stores/modules/types/types.ts";
import { constantRoute} from '@/router/routes';
import { reqLogin, reqUserInfo, reqLogOut } from '@/api/user'
import { SET_TOKEN, GET_TOKEN, REMOVE_TOKEN } from '@/utils/token'
import type {
    LoginFormData,
    LoginResponseData,
    userInfoResponseData,
} from '@/api/user/type'
const useUserStore = defineStore('User', {
    // 小仓库存储数据的地方
    state: (): UserState => {
        return {
            token: GET_TOKEN()!,
            menuRoutes: constantRoute,
            username: '',
            avatar: '',
            buttons: [],
        }
    },
    // 异步|逻辑的地方
    actions: {
        //用户登录方法
        async userLogin(data: LoginFormData) {
            const res: LoginResponseData = await reqLogin(data)
            // success=>token
            // error=>error.message
            console.log('Login Response:', res)
            if (res.code === 0) {
                this.token = res.data as string
                // 持久化
                SET_TOKEN(res.data as string)
                return 'ok'
            } else {
                const msg = (res.data as { message?: string })?.message || '登录失败'
                return Promise.reject(new Error(msg))
            }
        },
        //信息处理
        // async userInfo() {
        //     const res: userInfoResponseData = await reqUserInfo()
        //
        //     if (res.code === 200) {
        //         this.username = res.data.name as string
        //         this.avatar = res.data.avatar as string
        //
        //         const userAsyncRoute = filterAsyncRoute(
        //             cloneDeep(asyncRoute),
        //             res.data.routes,
        //         )
        //         this.menuRoutes = [...constantRoute, ...userAsyncRoute, anyRoute]
        //         dynamicRoutes = [...userAsyncRoute, anyRoute] // 记录动态添加的路由
        //         dynamicRoutes.forEach((route) => {
        //             router.addRoute(route) // 动态添加路由
        //         })
        //         return 'ok'
        //     } else {
        //         return Promise.reject(new Error(res.message))
        //     }
        // },
    //     async userLogout() {
    //         const res = await reqLogOut()
    //         if (res.code === 200) {
    //             this.token = ''
    //             this.username = ''
    //             this.avatar = ''
    //             REMOVE_TOKEN()
    //             dynamicRoutes.forEach((route) => {
    //                 if (route.name) {
    //                     router.removeRoute(route.name)
    //                 }
    //             })
    //         } else {
    //             return Promise.reject(new Error(res.message))
    //         }
    //     },
    },
    getters: {},
})

export default useUserStore
