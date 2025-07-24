export const constantRoute = [
  {
    path: '/login',
    component: () => import('@/views/login/index.vue'),
    name: 'login',
    meta: {
      title: 'login',
      hidden: true,
    },
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    name: 'layout',
    meta: {
      title: '',
      hidden: false,
      icon: '',
    },
    redirect: '/home',
    children: [
      {
        path: '/home',
        component: () => import('@/views/home/index.vue'),
        meta: {
          title: '首页',
          hidden: false,
          icon: 'HomeFilled',
        },
      },
    ],
  },
  {
    path: '/screen',
    component: () => import('@/views/screen/index.vue'),
    name: 'Screen',
    meta: {
      title: 'Screen',
      hidden: false,
      icon: 'Platform',
    },
  },
  // {
  //   path: '/',
  //   component: () => import('@/layout/index.vue'),
  //   name: 'layout',
  //   meta: {
  //     title: '',
  //     hidden: false,
  //     icon: '',
  //   },
  //   redirect: '/product/sku',
  //   children: [
  //     {
  //       path: '/product/sku',
  //       component: () => import('@/views/product/sku/index.vue'),
  //       name: 'Sku',
  //       meta: {
  //         title: 'Sku',
  //         icon: 'ScaleToOriginal',
  //         hidden: false,
  //       },
  //     },
  //   ]
  // },
  {
    path: '/404',
    component: () => import('@/views/404/index.vue'),
    name: '404',
    meta: {
      title: '404',
      hidden: true,
    },
  },
]
export const asyncRoute = [
  {
    path: '/acl',
    component: () => import('@/layout/index.vue'),
    name: 'Acl',
    meta: {
      title: '权限管理',
      hidden: false,
      icon: 'Lock',
    },
    redirect: '/acl/user',
    children: [
      {
        path: '/acl/user',
        component: () => import('@/views/acl/user/index.vue'),
        name: 'User',
        meta: {
          title: '用户管理',
          hidden: false,
          icon: 'User',
        },
      },
    ]
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    name: 'Product',
    meta: {
      title: '',
      hidden: false,
      icon: '',
    },
    redirect: '/artist',
    children: [
      {
        path: '/artist',
        component: () => import('@/views/product/trademark/index.vue'),
        name: 'Trademark',
        meta: {
          title: '歌曲管理',
          icon: 'ShoppingCart',
          hidden: false,
        },
      },
    ],
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    name: 'Song',
    meta: {
      title: '',
      hidden: false,
      icon: '',
    },
    children: [
      {
        path: '/song',
        component: () => import('@/views/product/sku/index.vue'),
        name: 'SongSku',
        meta: {
          title: '歌曲管理',
          icon: 'ScaleToOriginal',
          hidden: false,
        },
      },
    ]
  },
]

export const anyRoute = {
  path: '/:pathMatch(.*)*',
  redirect: '/404',
  name: 'Any',
  meta: {
    title: '任意路由',
    hidden: true,
  },
}
