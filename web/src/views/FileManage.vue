<template>
  <div>
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>活动管理</el-breadcrumb-item>
      <el-breadcrumb-item>活动列表</el-breadcrumb-item>
      <el-breadcrumb-item>活动详情</el-breadcrumb-item>
    </el-breadcrumb>
    <el-table :data="files" stripe @selection-change="handleSelectionChange">
      <el-table-column type="selection"></el-table-column>
      <el-table-column label="文件名">
        <template slot-scope="scope">
          <div style="display: flex;flex-direction: row;align-items: center;">
            <SvgIcon icon-style="icon" :icon-name="getIcon(scope.row)"></SvgIcon>
            <el-link :underline="false" v-if="!scope.row.editable" style="padding-left: 5px"
                     @click="onClickFileName(scope.row)">
              {{scope.row.fileName}}
            </el-link>
            <div v-else>
              <el-input clearable v-model="fileNameInput" ref="editInput" size="mini"></el-input>
              <el-button-group>
                <el-button icon="el-icon-check" size="mini"
                           @click="onEditConfirm(scope.row,scope.$index)"></el-button>
                <el-button icon="el-icon-close" size="mini" @click="onClickClose(scope.row,scope.$index)"></el-button>
              </el-button-group>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="size" label="大小">
        <template slot-scope="scope">{{formatSize(scope.row.size)}}</template>
      </el-table-column>
      <el-table-column prop="updateTime" label="最后修改时间"></el-table-column>
    </el-table>
  </div>
</template>
<script>
  import SvgIcon from "../components/SvgIcon";

  export default {
    components: {SvgIcon},
    data() {
      return {
        params: {},
        files: [],
        fileSelections: [],
        fileNameInput: "",
        iconTypeMap: new Map()
          .set('pdf', '#el-icon-alifile_pdf')
          .set('txt', '#el-icon-alifile_txt')
          .set('zip', '#el-icon-alifile_zip')
          .set('csv', '#el-icon-alifile_excel')
          .set('mp3', '#el-icon-alifile_music')
          .set('jpg', '#el-icon-alifile_img')
          .set('cad', '#el-icon-alifile_cad')
          .set('word', '#el-icon-alifile_word')
          .set('mp4', '#el-icon-alifile_video')
      }
    },
    created() {
      this.params = this.$route.query;
      this.getFileList(this.params);
    },
    methods: {
      getFileList(params) {
        this.post("list", params).then(data => {
          this.files = data.data.records;
        });
      },
      handleSelectionChange(val) {
        this.fileSelections = val;
      },
      extName(fileName) {
        let lastIndexOf = fileName.lastIndexOf('.');
        if (lastIndexOf === -1) {
          return "";
        }
        return fileName.substr(lastIndexOf + 1)
      },
      isFolder(file) {
        return file.type === 0;
      },
      getIcon(file) {
        if (this.isFolder(file)) {
          return "#el-icon-alifolder";
        }
        let ext = this.extName(file.fileName);
        if (!this.iconTypeMap.has(ext)) {
          return '#el-icon-alifile_cloud-copy';
        }
        return this.iconTypeMap.get(ext);
      },
      redirectFileList(params) {
        this.$router.push({name: "FileManage", query: params})
      },
      onClickFileName(file) {
        if (this.isFolder(file)) {
          this.params.relativePath += "/" + file.fileName;
          console.log(this.params);
          this.$router.push({name: "FileManage", query: this.params});

          // this.redirectFileList(this.params);
        }
      }
    },
    watch: {
      '$route': function () {
        this.params = this.$route.query;
      }
    },
    computed: {
      navigator() {
        let relativePath = this.params.relativePath;
        let strings = relativePath.split('/');
        strings.add('主目录');
        let res = [];
        return '';
      }
    }
  }
</script>

<style lang="less">
  .icon {
    width: 2em;
    height: 2em;
    fill: currentColor;
    overflow: hidden;
  }
</style>
