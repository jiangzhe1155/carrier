<template>
    <div>
        <el-upload
                class="upload-demo"
                drag
                name="multipartFile"
                :data="fileData"
                :action="url"
                multiple>
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <!--            <div class="el-upload__tip" slot="tip">只能上传jpg/png文件，且不超过500kb</div>-->
        </el-upload>
        <div>{{fileData.relativePath}}</div>


        <el-dialog :visible.sync="dialogVisible" width="30%">
            <el-input v-model="makeDirInput" placeholder="文件夹名称" style="padding-bottom: 10px"></el-input>
            <div style="text-align: right; margin: 0">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="makeDir">确 定</el-button>
            </div>
        </el-dialog>
        <el-button @click="dialogVisible = true">新建文件夹</el-button>

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
            }).catch((data: R<CommonFile[]>) => {

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
