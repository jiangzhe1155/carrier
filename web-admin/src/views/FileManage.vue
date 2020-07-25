<template>
    <div>
        <FileUpload :relativePath="relativePath" ref="fileUpload" @refresh="init()"></FileUpload>
        <template>
            <el-button-group>
                <el-button size="mini" type="primary" @click="$refs.btnFile.click()">
                    <i class="el-icon-upload"></i><span>上传文件</span>
                    <label ref="btnFile"></label>
                </el-button>
                <el-button size="mini" type="primary" @click="$refs.btnDir.click()">
                    <i class="el-icon-upload"></i><span>上传文件夹</span>
                    <label ref="btnDir"></label>
                </el-button>
            </el-button-group>
            <el-button size="mini" type="primary" style="margin-left: 20px" @click="onMakeDir">
                新建文件夹
            </el-button>
            <el-button size="mini" type="primary" @click="onDelete">
                删除
            </el-button>
            <el-button size="mini" type="primary" @click="onMove('move')">
                移动到
            </el-button>
            <el-button size="mini" type="primary" @click="onMove('copy')">
                复制到
            </el-button>
            <el-button size="mini" type="primary" @click="onDownLoad">
                下载
            </el-button>
        </template>
        {{multipleSelection}}

        <el-dialog
                title="提示"
                :visible.sync="centerDialogVisible"
                width="30%"
                center>
            <el-tree
                    :props="{label: 'fileName'}"
                    :load="loadNode"
                    lazy
                    v-if="centerDialogVisible"
                    @node-click="handleCheckChange">
            </el-tree>
            <span slot="footer" class="dialog-footer">
                <el-button @click="centerDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="onConfirm">确 定</el-button>
            </span>
        </el-dialog>
        <template>
            <el-table :data="fileList"
                      stripe
                      @selection-change="handleSelectionChange">
                <el-table-column
                        type="selection"
                        width="55">
                </el-table-column>
                <el-table-column label="名称"
                                 width="500px">
                    <template slot-scope="scope">
                        <el-link
                                :icon="getIcon(scope.row.type)"
                                @click="onClickFileName(scope.row)"
                                v-if="!scope.row.editable">
                            {{scope.row.fileName}}
                        </el-link>
                        <div v-else>
                            <el-input clearable
                                      v-model="inputValue"
                                      style="width: 324px"
                                      ref="editInput"
                                      size="mini">
                            </el-input>
                            <el-button-group>
                                <el-button icon="el-icon-check" size="mini" style="padding: 7px"
                                           @click="onEditConfirm(scope.row,scope.$index)"></el-button>
                                <el-button icon="el-icon-close" size="mini" style="padding: 7px"
                                           @click="onClickClose(scope.row,scope.$index)"></el-button>
                            </el-button-group>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="type" label="类型"></el-table-column>
                <el-table-column prop="size" label="大小">
                    <template slot-scope="scope">
                        {{formatSize(scope.row)}}
                    </template>
                </el-table-column>
                <el-table-column prop="updateTime" label="最后修改时间"></el-table-column>

                <el-table-column label="操作">
                    <template slot-scope="scope" v-if="scope.row.id">
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
    import axios from 'axios'

    @Component({components: {FileUpload}})
    export default class FileManage extends Vue {
        fileList = [];
        relativePath = '';
        multipleSelection = [];
        inputValue = '';
        centerDialogVisible = false;
        methodType = '';
        targetPath = '';

        handleCheckChange(data) {
            this.targetPath = data.relativePath;
            console.log(data);
        }

        loadNode(node, resolve) {
            console.log(node, resolve);
            if (node.level === 0) {
                return resolve([{fileName: "主目录", relativePath: ""}]);
            }

            let relativePath = node.data.relativePath;
            this.http.post("list", {
                relativePath: relativePath,
                type: 0,
                page: 1,
                pageSize: 100
            }, true).then(data => {
                resolve(data.data.records);
            });
        }

        onDownLoad() {
            let fidList = [];
            this.multipleSelection.forEach(
                m => fidList.push(m.id));

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
                }).catch((error) => {
                console.log(error)
            })
        }

        onConfirm() {
            let targetPath = [];
            this.multipleSelection.forEach(
                m => targetPath.push({relativePath: m.relativePath, targetPath: this.targetPath})
            );
            this.http.post(this.methodType, {getFileList: targetPath}, true, true, true).then(data => {
                this.getFileList();
            }).catch(() => {
            });
            this.centerDialogVisible = false
        }

        onDelete() {
            let deletePaths = [];
            this.multipleSelection.forEach(
                m => deletePaths.push(this.relativePath + "/" + m.fileName)
            );

            this.http.post("delete", {
                relativePaths: deletePaths,
            }).then((data: R<CommonFile[]>) => {
                this.init();
                Message.success("成功");
            })
        }

        onMove(methodType) {
            this.methodType = methodType;
            this.centerDialogVisible = true
        }

        mounted() {
            this.$nextTick(() => {
                this.init();
                this.$refs.fileUpload.$refs.uploader.uploader.assignBrowse(this.$refs.btnFile, false, false, {});
                this.$refs.fileUpload.$refs.uploader.uploader.assignBrowse(this.$refs.btnDir, true, false, {});
            })
        }

        onClickClose(file, idx) {
            if (file.id) {
                file.editable = false;
                Vue.set(this.getFileList, idx, file);
            } else {
                this.getFileList.splice(idx, 1);
            }
        }

        formatSize(file) {
            if (file.type === 0) {
                return '-'
            }
            let size = file.size;
            return size < 1024 ? size.toFixed(0) + " B" : size < 1048576 ? (size / 1024).toFixed(0) + " KB" : size < 1073741824 ? (size / 1024 / 1024).toFixed(1) + " MB" : (size / 1024 / 1024 / 1024).toFixed(1) + " GB";
        }

        onEditConfirm(file, idx) {
            if (file.id) {
                this.http.post("rename", {
                    relativePath: this.relativePath + "/" + file.fileName,
                    targetName: this.inputValue
                }).then((data: R<CommonFile[]>) => {
                    Message.success("成功");
                    this.init();
                }).catch(() => {
                });
            } else {
                this.http.post("makeDir", {
                    relativePath: this.relativePath + "/" + this.inputValue,
                    fileName: this.inputValue
                }).then((data: R<CommonFile[]>) => {
                    Message.success("成功");
                    this.init();
                }).catch(() => {

                });
            }

        }

        onMakeDir() {
            for (let file of this.getFileList) {
                if (file.editable) {
                    this.$refs.editInput.select();
                    return;
                }
            }

            let tmp = {editable: true};
            this.getFileList.unshift(tmp);
            this.inputValue = '新建文件夹';
            this.$nextTick(() => {
                this.$refs.editInput.select();
            })
        }

        handleSelectionChange(val) {
            this.multipleSelection = val;
        }

        deleteFile(index, row) {
            this.http.post("delete", {
                relativePaths: [this.relativePath + "/" + row.fileName]
            }).then((data: R<CommonFile[]>) => {
                this.init();
            }).catch(() => {
            });
        }

        rename(index, file) {
            for (let file of this.getFileList) {
                if (file.editable === true) {
                    this.$refs.editInput.select();
                    return;
                }
            }

            file.editable = true;
            Vue.set(this.getFileList, index, file);

            this.inputValue = file.fileName;
            this.$nextTick(() => {
                this.$refs.editInput.select();
            });
        }

        init() {
            this.relativePath = this.$route.query.relativePath;
            this.getFileList();
        }

        getFileList() {
            let self = this;
            this.http.post("list", {
                relativePath: this.relativePath,
                asc: true,
                order: 'name',
                page: 1,
                pageSize: 100
            }, false).then(data => {
                this.fileList = data.data.records;
            });
        }

        getIcon(type) {
            if (type === 0) {
                return 'el-icon-folder'
            }
            return 'el-icon-document';
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
