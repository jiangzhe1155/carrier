<template>
    <div>
        <el-upload
                class="upload-demo"
                drag
                name="multipartFile"
                action=""
                :http-request="uploadFile"
                :on-change="fileChange"
                multiple>
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        </el-upload>
        <div>{{fileData.relativePath}}</div>

        <el-popover
                placement="bottom"
                width="30%"
                trigger="click"
                @hide="dialogVisible = false;makeDirInput=''">
            <el-form inline>
                <el-form-item>
                    <el-input v-model="makeDirInput" placeholder="文件夹名称"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="makeDir">确定</el-button>
                </el-form-item>
            </el-form>
            <el-button slot="reference">新建文件夹</el-button>
        </el-popover>

        <template>
            <el-table :data="fileList" stripe style="width: 100%">
                <el-table-column label="名称">
                    <template slot-scope="scope">
                        <el-link
                                :icon="getIcon(scope.row.isDir)"
                                @click="onClickFileName(scope.row)">
                            {{scope.row.fileName}}
                        </el-link>
                    </template>
                </el-table-column>
                <el-table-column prop="fileType" label="类型"></el-table-column>
                <el-table-column prop="size" label="大小"></el-table-column>
                <el-table-column prop="lastModifyTime" label="最后修改时间"></el-table-column>

                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <el-popover
                                trigger="click"
                                @hide="renameVisible = false;renameInput=''">
                            <el-form inline>
                                <el-form-item>
                                    <el-input v-model="renameInput" placeholder="文件(夹)名称"></el-input>
                                </el-form-item>
                                <el-form-item>
                                    <el-button type="primary" @click="rename(scope.$index, scope.row)">确定</el-button>
                                </el-form-item>
                            </el-form>
                            <el-button slot="reference" size="mini">重命名</el-button>
                        </el-popover>
                        <el-button
                                size="mini"
                                type="danger"
                                @click="deleteFile(scope.$index, scope.row)">删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </template>
    </div>
</template>

<script lang="ts">

    import {Component, Vue, Watch} from 'vue-property-decorator';
    import {Message} from 'element-ui';

    @Component
    export default class About extends Vue {
        url: string = 'http://127.0.0.1:18080/uploadFile';
        fileList: CommonFile[] = [];
        fileData: any = {};
        makeDirInput = '';
        dialogVisible = false;
        renameInput = '';
        renameVisible = false;

        fileChange(){
            console.log("ad")
        }
        uploadFile(param) {
            let data = new FormData();
            data.append("multipartFile", param.file);
            data.append("relativePath", this.fileData.relativePath);
            this.http.post("uploadFile", data).then((data: R<CommonFile[]>) => {
                Message.success("成功");
                param.onSuccess();
                this.init();
            }).catch(c => {
                param.onError();
            });
        }

        deleteFile(index, row) {
            this.http.post("deleteFile", {
                relativePath: this.fileData.relativePath,
                fileName: row.fileName
            }).then((data: R<CommonFile[]>) => {
                Message.success("成功");
                this.renameVisible = false;
                this.init();
            }).catch(c => {
            });
        }

        rename(index, row) {
            this.http.post("rename", {
                relativePath: this.fileData.relativePath,
                originName: row.fileName,
                targetName: this.renameInput
            }).then((data: R<CommonFile[]>) => {
                Message.success("成功");
                this.renameVisible = false;
                this.init();
            }).catch(c => {
            });
        }


        created() {
            this.init();
        }

        init() {
            this.fileData = {relativePath: this.$route.query.relativePath};
            this.getFileList();
        }

        getFileList() {
            this.http.post("listFile", this.fileData).then((data: R<CommonFile[]>) => {
                this.fileList = data.data;
            });
        }

        getIcon(isDir: boolean) {
            return isDir ? 'el-icon-folder' : 'el-icon-document';
        }

        makeDir() {
            this.http.post("makeDir", {
                relativePath: this.fileData.relativePath,
                fileName: this.makeDirInput
            }).then((data: R<CommonFile[]>) => {
                Message.success("成功");
                this.dialogVisible = false;
                this.init();
            }).catch(c => {
            });
        }

        onClickFileName(file: CommonFile) {
            if (file.isDir) {
                this.$router.push({
                    name: "About",
                    query: {"relativePath": this.fileData.relativePath + '/' + file.fileName}
                })
            }
        }

        @Watch('$route.query')
        onRouterChange(newVal: any, oldVal: any) {
            this.fileData.relativePath = newVal.relativePath;
            this.getFileList()
        }
    }

</script>
