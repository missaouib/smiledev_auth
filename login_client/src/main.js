import Vue from 'vue'
import App from './App.vue'
import VueRouter from 'vue-router'
import ErrorPage from "./components/ErrorPage.vue"
import MainPage from "./components/MainPage.vue"
import Login from "./components/Login.vue"
import Join from "./components/Join.vue"
import AdminPage from "./components/AdminPage"
import FindPasswordPage from "./components/FindPasswordPage"
import ChangePasswordPage from "./components/ChangePasswordPage"
import common from './assets/common'

Vue.config.productionTip = false

Vue.use(VueRouter);
Vue.use(common);

const routes = [
    {
        path: "/",
        name: "main",
        component: MainPage,
    },
    {
        path: "/NoAuth",
        name: "ErrorPage",
        component: ErrorPage,
    },
    {
        path: "/login",
        name: "login Page",
        component: Login,
    },
    {
        path: "/join",
        name: "join Page",
        component: Join,
    },
    {
        path: "/admin",
        name: "adminpage",
        component: AdminPage,
    },
    {
        path: "/findPassword",
        name: "find_password",
        component: FindPasswordPage,
    },
    {
        path: "/changePassword",
        name: "change_password",
        component: ChangePasswordPage,
    }
]

const router = new VueRouter({
    routes
});

new Vue({
    render: h => h(App),
    router
}).$mount('#app')
