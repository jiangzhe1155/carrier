<template>
    <div>
        <!--        <FileUpload :relativePath="relativePath"></FileUpload>-->


        <template>

            <el-dropdown>
                <el-button size="medium" type="primary">
                    <i class="el-icon-upload"></i><span>上传</span>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item>上传文件</el-dropdown-item>
                    <el-dropdown-item>上传文件夹</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>

            <el-button size="medium" type="primary" style="margin-left: 20px">
                新建文件夹
            </el-button>
        </template>


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
                <el-table-column prop="type" label="类型"></el-table-column>
                <el-table-column prop="size" label="大小"></el-table-column>
                <el-table-column prop="updateTime" label="最后修改时间"></el-table-column>

                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <el-popover
                                ref="ref"
                                trigger="click"
                                @hide="renameInput=''">
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
    import FileUpload from '@/components/FileUpload.vue'
    import {Component, Vue, Watch} from 'vue-property-decorator';
    import {Message} from 'element-ui';

    @Component({components: {FileUpload}})
    export default class FileManage extends Vue {
        url: string = 'http://127.0.0.1:18080/uploadFile';
        fileList = [];
        makeDirInput = '';
        dialogVisible = false;
        renameInput = '';
        renameVisible = false;
        relativePath = '';

        deleteFile(index, row) {
            this.http.post("deleteFile", {
                relativePath: this.relativePath + "/" + row.fileName
            }).then((data: R<CommonFile[]>) => {
                Message.success("成功");
                this.init();
            }).catch(() => {
            });
        }

        rename(index, row) {
            this.http.post("rename", {
                relativePath: this.relativePath + "/" + row.fileName,
                targetName: this.renameInput
            }).then((data: R<CommonFile[]>) => {
                Message.success("成功");
                this.init();
                this.renameVisible = false;
            }).catch(() => {
            });
        }


        created() {
            this.init();
        }

        init() {
            this.relativePath = this.$route.query.relativePath;
            this.getFileList();
        }

        getFileList() {
            this.http.post("listFile", {relativePath: this.relativePath}).then((data: R<CommonFile[]>) => {
                this.fileList = data.data;
            });
        }

        getIcon(isDir: boolean) {
            return isDir ? 'el-icon-folder' : 'el-icon-document';
        }

        makeDir() {
            this.http.post("makeDir", {
                relativePath: this.relativePath + "/" + this.makeDirInput,
            }).then((data: R<CommonFile[]>) => {
                Message.success("成功");
                this.dialogVisible = false;
                this.init();
            }).catch(() => {
            });
        }

        onClickFileName(file) {
            if (file.type === 0) {
                this.$router.push({
                    name: "FileManage",
                    query: {"relativePath": this.relativePath + "/" + file.fileName}
                })
            }
        }

        @Watch('$route.query')
        onRouterChange(newVal: any, oldVal: any) {
            this.relativePath = newVal.relativePath;
            this.getFileList()
        }
    }

</script>
