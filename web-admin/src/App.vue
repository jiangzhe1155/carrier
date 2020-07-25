<template>
    <div id="app">
        <el-container>
            <el-header>
                <router-link to="/">Home</router-link>
            </el-header>
            <el-container>
                <el-aside>
                    <el-menu>
                        <el-menu-item index="1" @click="directToFileManage">
                            <i class="el-icon-files"></i><span>文件管理</span>
                        </el-menu-item>
                    </el-menu>
                </el-aside>
                <el-main>
                    <router-view/>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>
<script lang="ts">
    import {Component, Prop, Vue} from 'vue-property-decorator';

    @Component
    export default class App extends Vue {

        directToFileManage() {
            this.$router.push({
                name: "FileManage",
                query: {"relativePath": ""}
            })
        }

    }
</script>

<style lang="less" type="text/less">
    #app {
        font-family: Avenir, Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        /*text-align: center;*/
        color: #2c3e50;
    }
</style>
