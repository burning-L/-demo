import { fileURLToPath, URL } from 'node:url'
import {ConfigEnv, defineConfig, loadEnv, UserConfigExport} from 'vite'
import vue from '@vitejs/plugin-vue'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons';
import path from "path";
import {viteMockServe} from "vite-plugin-mock";
import svgLoader from 'vite-svg-loader';

// https://vite.dev/config/
export default ({ command, mode }: ConfigEnv): UserConfigExport => {
  // 获取各种环境下对应的变量
  const env = loadEnv(mode, process.cwd())
  // 关键：将字符串转换为数字
  const port = parseInt(env.VITE_PORT, 10);
  return {
    base: '/',
    plugins: [
      vue(),
      svgLoader(),
      createSvgIconsPlugin({
        iconDirs: [path.resolve(process.cwd(), 'src/assets/icons')],
        symbolId: 'icon-[dir]-[name]',
      }),
      // viteMockServe({
      //   localEnabled: command === 'serve',
      // }),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      },
    },
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: '@import "@/styles/variable.scss";',// 替换为 @use
        },
      },
    },
    // 代理跨域
    server: {
      // 端口号
      port:port,
      proxy: {
        [env.VITE_APP_BASE_API]: {
          target: env.VITE_MOCK_SERVE,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
      },
    },
  }
}
