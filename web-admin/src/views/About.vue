<template>
    <div>
        <!--        <el-button @click="dialogVisible = true">上传</el-button>-->

        <!--        <el-dialog-->
        <!--                title="提示"-->
        <!--                :visible.sync="dialogVisible"-->
        <!--                :before-close="handleClose">-->
        <!--            <FileUpload :relativePath="relativePath"></FileUpload>-->
        <!--        </el-dialog>-->

        <FileUpload :relativePath="relativePath"></FileUpload>
        <div>{{relativePath}}</div>
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

    @Component({
        components: {FileUpload}
    })
    export default class About extends Vue {
        url: string = 'http://127.0.0.1:18080/uploadFile';
        fileList = [];
        makeDirInput = '';
        dialogVisible = false;
        renameInput = '';
        renameVisible = false;
        relativePath = '';

        handleClose(done) {
            done()
        }

        deleteFile(index, row) {
            this.http.post("deleteFile", {
                relativePath: this.relativePath+"/"+row.fileName
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
                    name: "About",
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
