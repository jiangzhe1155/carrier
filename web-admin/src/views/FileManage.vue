<template>
    <div>
        <FileUpload :relativePath="relativePath" ref="fileUpload"></FileUpload>

        <template>
            <el-button-group>
                <el-button size="mini" type="primary" @click="$refs.btnFile.click()">
                    <i class="el-icon-upload"></i><span>上传文件</span>
                    <label ref="btnFile"></label>
                </el-button>
                <el-button size="mini" type="primary" @click="$refs.btnDir.click();">
                    <i class="el-icon-upload"></i><span>上传文件夹</span>
                    <label ref="btnDir"></label>
                </el-button>
            </el-button-group>
            <el-button size="mini" type="primary" style="margin-left: 20px" @click="onNewDir">
                新建文件夹
            </el-button>
        </template>

        {{multipleSelection}}
        <template>
            <el-table :data="fileList"
                      stripe
                      @selection-change="handleSelectionChange"
            >
                <el-table-column
                        type="selection"
                        width="55">
                </el-table-column>
                <el-table-column label="名称"
                                 width="500px">
                    <template slot-scope="scope">
                        <el-link
                                :icon="getIcon(scope.row.isDir)"
                                @click="onClickFileName(scope.row)"
                                v-if="!scope.row.editable">
                            {{scope.row.fileName}}
                        </el-link>
                        <div v-else>
                            <el-input clearable
                                      v-model="inputValue"
                                      style="width: 200px"
                                      ref="editInput"
                                      size="mini">
                            </el-input>
                            <el-button-group>
                                <el-button icon="el-icon-check" size="mini" style="padding: 7px"
                                           @click="onEditConfirm(scope.row)"></el-button>
                                <el-button icon="el-icon-close" size="mini" style="padding: 7px"
                                           @click="onClickClose(scope.row)"></el-button>
                            </el-button-group>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="type" label="类型"></el-table-column>
                <el-table-column prop="size" label="大小"></el-table-column>
                <el-table-column prop="updateTime" label="最后修改时间"></el-table-column>

                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <el-button size="mini" @click="rename(scope.$index, scope.row)">重命名</el-button>
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
        fileList = [];
        makeDirInput = '';
        dialogVisible = false;
        renameInput = '';
        renameVisible = false;
        relativePath = '';
        multipleSelection = [];
        inputValue = '';


        mounted() {
            this.$nextTick(() => {
                this.init();
                this.$refs.fileUpload.$refs.uploader.uploader.assignBrowse(this.$refs.btnFile, false, false, {});
                this.$refs.fileUpload.$refs.uploader.uploader.assignBrowse(this.$refs.btnDir, true, false, {});
            })
        }

        onInputBlur() {
            this.fileList.splice(0, 1);
        }

        onClickClose(file) {
            if (file.id) {
                file.editable = false;
            } else {
                this.fileList.filter(f => f !== file);
            }
        }

        onEditConfirm(file) {


        }

        onNewDir() {
            for (let file of this.fileList) {
                if (file.editable) {
                    this.$refs.editInput.select();
                    return;
                }
            }

            let tmp = {fileName: "新建文件夹", editable: true};
            this.fileList.unshift(tmp);
            this.inputValue = '新建文件夹';
            this.$nextTick(() => {
                this.$refs.editInput.select();
            })
        }

        handleSelectionChange(val) {
            this.multipleSelection = val;
        }

        deleteFile(index, row) {
            this.http.post("deleteFile", {
                relativePath: this.relativePath + "/" + row.fileName
            }).then((data: R<CommonFile[]>) => {
                Message.success("成功");
                this.init();
            }).catch(() => {
            });
        }

        rename(index, file) {
            for (let file of this.fileList) {
                if (file.editable) {
                    this.$refs.editInput.select();
                    return;
                }
            }

            file.editable = true;

            // this.http.post("rename", {
            //     relativePath: this.relativePath + "/" + row.fileName,
            //     targetName: this.renameInput
            // }).then((data: R<CommonFile[]>) => {
            //     Message.success("成功");
            //     this.init();
            //     this.renameVisible = false;
            // }).catch(() => {
            // });
        }

        init() {
            this.relativePath = this.$route.query.relativePath;
            this.getFileList();
        }

        getFileList() {
            this.http.post("listFile", {relativePath: this.relativePath}).then(data => {
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
