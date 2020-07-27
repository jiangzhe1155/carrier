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
              <i class="el-icon-files"></i>
              <span>文件管理</span>
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
<script>
  export default {
    data() {
      return {
        params: {
          relativePath: "",
          asc: false,
          page: 1,
          pageSize: 100,
          order: "name"
        }
      }
    },
    mounted() {
      const s = document.createElement('script');
      s.type = 'text/javascript';
      s.src = '//at.alicdn.com/t/font_1969873_ay6q0e2hr0t.js\n';
      document.body.appendChild(s);
    },
    methods: {
      directToFileManage() {
        this.$router.push({
          name: "FileManage",
          query: this.params
        })
      }
    }
  }
</script>

<style lang="less">
  @import "//at.alicdn.com/t/font_1969873_ay6q0e2hr0t.css";

  #app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
  }
</style>
