import { defineConfig } from "@vue/cli-service";
import AutoImport from "unplugin-auto-import/webpack";
import Components from "unplugin-vue-components/webpack";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";

module.exports = defineConfig({
    transpileDependencies: true,
    lintOnSave: false,
    publicPath: "./",
    // devServer: {
    // port: 9090,

    // https: true,
    // proxy: {
    //     // "/api": {
    //     "^/maps": {
    //         // target: 'http://localhost:9090',//跨域的域名（不需要写路径）
    //         target: "https://maps.googleapis.com", //跨域的域名（不需要写路径）
    //         changeOrigin: true, //是否开启跨域
    //         ws: true, //是否代理websocked
    //         // pathRewrite: {
    //         //重写路径
    //         // "^/api": "", //把 /api 变为空字符
    //         // },
    //     },
    // },
    // },
    css: {
        loaderOptions: {
            scss: {
                additionalData: `@import "./src/styles/styles.scss";`,
            },
            css: {},
            postcss: {
                plugins: [
                    // eslint-disable-next-line @typescript-eslint/no-var-requires
                    require("px2rem-loader")({
                        // 以设计稿750为例， 750 / 10 = 75
                        remUnit: 75,
                    }),
                ],
            },
        },
    },
    configureWebpack: {
        plugins: [
            AutoImport({
                resolvers: [ElementPlusResolver()],
            }),
            Components({
                resolvers: [ElementPlusResolver()],
            }),
        ],
    },
});
