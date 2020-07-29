<template>
  <el-container>
    <el-header>
      <el-row type="flex" style="align-items: center;">
        <el-button-group>
          <el-button @click="onClickUploadFile" size="medium">
            上传文件
          </el-button>
          <el-button @click="onClickUploadFolder" size="medium">
            上传文件夹
          </el-button>
        </el-button-group>
        <el-button-group style="margin-left: 20px">
          <el-button @click="onClickMakeDir" size="medium">
            新建文件夹
          </el-button>
          <el-button v-show="fileSeletedCount>0" @click="onClickDelete" size="medium">
            删除
          </el-button>
          <el-button v-show="fileSeletedCount>0" @click="onClickMove" size="medium">
            移动到
          </el-button>
          <el-button v-show="fileSeletedCount>0" @click="onClickCopy" size="medium">
            复制到
          </el-button>
          <el-button v-show="fileSeletedCount>0" @click="onClickDownLoad" size="medium">下载</el-button>
        </el-button-group>
      </el-row>
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item v-for=" item in navigator">
          <div v-if="item.isLast">{{item.name}}</div>
          <el-link v-else type="primary" :underline="false" @click="onClickNavigatorItem(item.relativePath)">
            {{item.name}}
          </el-link>
        </el-breadcrumb-item>
      </el-breadcrumb>
    </el-header>
    <el-main>
      <el-table :data="files" stripe @selection-change="handleSelectionChange" ref="fileTable">
        <el-table-column type="selection"></el-table-column>
        <el-table-column label="文件名">
          <template slot-scope="scope">
            <el-row type="flex" style="align-items: center">
              <SvgIcon icon-style="icon" style="margin-right: 5px" :icon-name="getIcon(scope.row)"></SvgIcon>

              <el-link :underline="false" v-if="!scope.row.editable"
                       @click="onClickFileName(scope.row)">
                {{scope.row.fileName}}
              </el-link>
              <div v-else>
                <el-row type="flex" style="align-items: center">
                  <el-input clearable v-model="fileNameInput" ref="input" size="small"></el-input>
                  <el-button icon="el-icon-check" type="text" style="margin-left: 5px"
                             @click="onClickInputConfirm(scope.row)"></el-button>
                  <el-button icon="el-icon-close" type="text"
                             @click="onClickInputClose(scope.row,scope.$index)"></el-button>
                </el-row>
              </div>
            </el-row>
          </template>
        </el-table-column>
        <el-table-column prop="size" label="大小">
          <template slot-scope="scope">{{formatSize(scope.row.size)}}</template>
        </el-table-column>
        <el-table-column prop="updateTime" label="最后修改时间"></el-table-column>
      </el-table>
    </el-main>
  </el-container>


</template>
<script>
    import SvgIcon from "../components/SvgIcon";
    import axios from 'axios'
    export default {
        components: {SvgIcon},
        data() {
            return {
                params: {},
                files: [],
                fileMultipleSelection: [],
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
                this.post("list", params, false).then(data => {
                    this.files = data.data.records;
                });
            },
            onClickUploadFile() {

            },
            onClickUploadFolder() {

            },
            onClickDelete() {
                let deletePaths = [];
                this.fileMultipleSelection.forEach(
                    m => deletePaths.push(this.params.relativePath + "/" + m.fileName)
                );
                console.log(deletePaths);
                this.post("delete", {relativePaths: deletePaths,}).then(() => {
                    this.getFileList(this.params);
                })
            },
            onClickMove() {

            },
            onClickCopy() {

            },
            onClickDownLoad() {
                let fidList = [];
                this.fileMultipleSelection.forEach(m => fidList.push(m.id));

                axios.post('download', {fidList: fidList}, {responseType: 'blob'})
                    .then(res => {
                        console.log(res);
                        let data = res.data;
                        let url = window.URL.createObjectURL(data);
                        let link = document.createElement('a');
                        link.style.display = 'none';
                        link.href = url;
                        link.download = decodeURI(res.headers['content-disposition']);
                        link.click();
                        URL.revokeObjectURL(url);
                        document.body.removeChild(link)
                    }).catch((error) => {
                    console.log(error)
                })
            },
            onClickInputConfirm(file) {
                let methodName;
                let params;
                if (file.id) {
                    methodName = 'rename';
                    params = {
                        targetName: this.fileNameInput,
                        relativePath: this.params.relativePath + "/" + file.fileName
                    }
                } else {
                    methodName = 'makeDir';
                    params = {
                        fileName: this.fileNameInput,
                        relativePath: this.params.relativePath + "/" + this.fileNameInput
                    }
                }
                this.post(methodName, params).then(() => {
                    this.getFileList(this.params);
                });
            },
            onClickInputClose(file, idx) {
                if (file.id) {
                    file.editable = false;
                    Vue.set(this.files, idx, file);
                } else {
                    this.files.splice(idx, 1);
                }
            },
            hasEditFile() {
                for (let file of this.files) {
                    if (file.editable) {
                        return true
                    }
                }
                return false;
            },
            handleSelectionChange(val) {
                if (this.hasEditFile()) {
                    this.$refs.fileTable.clearSelection();
                }
                this.fileMultipleSelection = val;
            },
            onClickMakeDir() {
                if (this.hasEditFile()) {
                    this.$refs.input.select();
                }
                this.$refs.fileTable.clearSelection();
                let tmp = {editable: true, type: 0};
                this.files.unshift(tmp);
                this.fileNameInput = '新建文件夹';
                this.$nextTick(() => {
                    this.$refs.input.select();
                })
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
            onClickNavigatorItem(relativePath) {
                let params = Object.assign({}, this.params);
                params.relativePath = relativePath;
                this.redirectFileList(params);
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
                this.$router.push({path: this.$route.path, query: Object.assign({}, params, {_: +new Date()})});
            },
            onClickFileName(file) {
                if (this.isFolder(file)) {
                    this.params.relativePath += "/" + file.fileName;
                    this.redirectFileList(this.params);
                }
            }
        },
        watch: {
            '$route': function (newRoute, oleRoute) {
                this.params = newRoute.query;
                this.getFileList(this.params);
            }
        },
        computed: {
            fileSeletedCount() {
                return this.fileMultipleSelection.length;
            },
            navigator() {
                let relativePath = this.params.relativePath;
                let res = [];
                let strings = relativePath.split('/').filter(s => s !== '');
                strings = ['主目录', ...strings];
                let tmp = "";
                for (let i = 0; i < strings.length; i++) {
                    let name = strings[i];
                    let item = {name: name, relativePath: i === 0 ? '' : tmp + '/' + name};
                    item.isLast = i === strings.length - 1;
                    res.push(item);
                }
                return res;
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
