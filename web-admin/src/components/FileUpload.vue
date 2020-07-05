<template>
    <div>
        <uploader ref="uploader" :options="options"
                  @file-added="onFileAdded"
                  @file-success="onFileSuccess"
                  @file-progress="onFileProgress">
            <uploader-unsupport></uploader-unsupport>

            <uploader-btn v-show="false" ref="btn1">上传文件</uploader-btn>
            <uploader-btn :directory="true" v-show="false">上传文件夹</uploader-btn>
            <uploader-files ref="uploaderFiles">
                <el-card slot-scope="props"
                         :body-style="{ padding: '0 10px' }"
                         class="file-panel"
                         v-show="showDrawer">
                    {{props.files}}
                    <el-row justify="end" type="flex">
                        <el-button :icon="showTable?'el-icon-minus':'el-icon-full-screen'"
                                   type="text"
                                   @click="showTable = !showTable">
                        </el-button>
                        <el-button icon="el-icon-close"
                                   type="text"
                                   @click="cancel">
                        </el-button>
                    </el-row>
                    <el-table
                            :data="props.files"
                            style="min-height: 400px"
                            v-show="showTable">
                        <el-table-column prop="name" label="文件名" min-width="128px">
                        </el-table-column>

                        <el-table-column label="大小">
                            <template slot-scope="scope">
                                {{scope.row.getFormatSize()}}
                            </template>
                        </el-table-column>
                        <el-table-column label="上传目录">
                            <template slot-scope="scope">
                                <el-link type="primary"
                                         @click="onClickUploadPath(scope.row)">
                                    {{scope.row.uploadPathName}}
                                </el-link>
                            </template>
                        </el-table-column>
                        <el-table-column label="状态" min-width="100px">
                            <template slot-scope="scope">
                                <span v-show="!getRemoveStatus(getStatus(scope.row)">
                                    <i class="el-icon-circle-check" style="color: #67C23A"></i>
                                </span>
                                <span v-show="getStatus(scope.row)!=='uploading'">
                                    {{statusText[getStatus(scope.row)]}}
                                </span>
                                <span v-show="getStatus(scope.row)==='uploading'">
                                    {{getProcess(scope.row)}}
                                </span>

                            </template>
                        </el-table-column>
                        <el-table-column min-width="64px">
                            <template slot-scope="scope">
                                <el-button type="text"
                                           size="medium"
                                           v-show="getStatus(scope.row)==='paused'"
                                           icon="el-icon-video-play"
                                           @click="resume(scope.row,scope.$index)">
                                </el-button>
                                <el-button type="text"
                                           v-show="getStatus(scope.row)==='uploading'"
                                           icon="el-icon-video-pause"
                                           @click="pause(scope.row,scope.$index)">
                                </el-button>
                                <el-button type="text"
                                           v-show="getStatus(scope.row)==='error'"
                                           icon="el-icon-refresh-right"
                                           @click="retry(scope.row,scope.$index)">
                                </el-button>
                                <el-button type="text"
                                           v-show="getRemoveStatus(getStatus(scope.row))"
                                           icon="el-icon-circle-close"
                                           @click="remove(scope.row,scope.$index)">
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </uploader-files>
        </uploader>
    </div>
</template>

<script lang="ts">
    import {Component, Prop, Vue, Watch} from 'vue-property-decorator';
    import {Message} from "element-ui";
    import SparkMD5 from 'spark-md5';

    @Component
    export default class FileUpload extends Vue {
        options = {
            target: 'http://127.0.0.1:18080/chunkUploadFile',
            chunkSize: 4 * 1024 * 1024,
            testChunks: true,
            maxChunkRetries: 3,
            allowDuplicateUploads: true,
            simultaneousUploads: 1,
            checkChunkUploadedByResponse: function (chunk, data) {
                let objMessage = JSON.parse(data);
                if (objMessage.code !== 0) {
                    Vue.set(chunk.file, 'error', true);
                    chunk.file.pause();
                }

                const {skipUpload, uploaded, id} = objMessage.data;
                chunk.file.storageId = id;
                if (skipUpload) {
                    Vue.set(chunk.file, 'skipUpload', true);
                    return true;
                }
                return (uploaded || []).indexOf(chunk.offset + 1) >= 0
            }
        };

        @Prop() relativePath: string;
        showDrawer = false;
        showTable = false;
        uploader: any;
        files: any;

        cancel() {
            this.uploader.cancel();
            this.showDrawer = false;
        }

        onClickUploadPath(file) {
            this.$router.push({
                name: "FileManage",
                query: {"relativePath": file.uploadPath}
            })
        }

        mounted() {
            this.uploader = this.$refs.uploader.uploader;
            this.files = this.$refs.uploaderFiles.files;
        }

        formatSize(size) {
            let speed = size < 1024 ? size.toFixed(0) + " bytes" : size < 1048576 ? (size / 1024).toFixed(0) + " KB" : size < 1073741824 ? (size / 1024 / 1024).toFixed(1) + " MB" : (size / 1024 / 1024 / 1024).toFixed(1) + " GB";
            return speed + '/ s';
        }

        getProcess(file) {
            console.log('getProcess', file);
            let speed = this.formatSize(file.averageSpeed);
            let progress = (file.progress() * 100).toFixed(2);
            return `(${progress}%) ${speed}`;
        }

        getRemoveStatus(status) {
            return status !== 'skipUpload' && status !== 'success';
        }

        getStatus(file) {
            const {md5, skipUpload, completed, error, paused, isUploading} = file;
            if (md5) {
                return 'md5'
            } else if (skipUpload) {
                return 'skipUpload'
            } else if (completed) {
                return 'success'
            } else if (error) {
                return 'error'
            } else if (paused) {
                return 'paused'
            } else if (isUploading) {
                return 'uploading'
            } else {
                return 'waiting'
            }
        }


        remove(file, idx) {
            file.cancel();
        }

        pause(file, idx) {
            file.pause();
            Vue.set(this.$refs.uploaderFiles.files, idx, file);
        }

        retry(file, idx) {
            file.retry();
            Vue.set(this.$refs.uploaderFiles.files, idx, file);
        }

        resume(file, idx) {
            file.resume();
            Vue.set(this.$refs.uploaderFiles.files, idx, file);
        }


        statusText = {
            md5: '校验MD5',
            success: '成功',
            error: '出错',
            uploading: '上传中',
            paused: '暂停',
            waiting: '等待',
            skipUpload: '秒传'
        };


        onFileProgress(rootFile, file, chunk) {
            let files = this.$refs.uploaderFiles.files;
            for (let i = 0; i < files.length; i++) {
                if (files[i].id === file.id) {
                    Vue.set(this.$refs.uploaderFiles.files, i, file);
                }
            }
            console.log(`上传中 ${file.name}，chunk：${chunk.startByte / 1024 / 1024} ~ ${chunk.endByte / 1024 / 1024}`)
        }


        onFileAdded(file) {
            this.showDrawer = true;
            this.showTable = true;

            file.relativePath = this.relativePath + "/" + file.relativePath;
            file.uploadPath = this.relativePath;
            let idx = this.relativePath.lastIndexOf("/");
            file.uploadPathName = idx === -1 ? '主目录' : this.relativePath.substring(idx + 1);
            this.computeMD5(file);
        }

        computeMD5(file) {
            let fileReader = new FileReader();
            let time = new Date().getTime();
            let blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice;
            let currentChunk = 0;
            const chunkSize = 10 * 1024 * 1000;
            let chunks = Math.ceil(file.size / chunkSize);
            let spark = new SparkMD5.ArrayBuffer();
            Vue.set(file, 'md5', true);
            file.pause();
            loadNext();
            fileReader.onload = (e => {
                spark.append(e.target.result);
                if (currentChunk < chunks) {
                    currentChunk++;
                    loadNext();
                } else {
                    let md5 = spark.end();
                    file.uniqueIdentifier = md5;
                    console.log(`MD5计算完毕：${file.name} \nMD5：${md5} \n分片：${chunks} 大小:${file.size} 用时：${new Date().getTime() - time} ms`);
                    Vue.delete(file, 'md5');
                    file.resume();
                }
            });

            fileReader.onerror = function () {
                file.error = true;
            };

            function loadNext() {
                let start = currentChunk * chunkSize;
                let end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;
                fileReader.readAsArrayBuffer(blobSlice.call(file.file, start, end));
            }
        }

        onFileSuccess(rootFile, file, message, chunk) {
            let response = JSON.parse(message);
            if (response.code !== 0) {
                Message.error("抱歉失败");
                file.error = true;
                file.pause();
            }
            this.http.post("merge", {
                totalSize: file.size,
                identifier: file.uniqueIdentifier,
                storageId: file.storageId,
                targetPath: this.relativePath,
                filename: file.name,
                relativePath: file.relativePath,
                isDir: false
            }, false).then((data: R<any>) => {

            })
        }

        onFileError(rootFile, file, response, chunk) {

        }

    }
</script>
<style scoped lang="less">

</style>
<style>
    .file-panel {
        left: auto !important;
        position: fixed;
        width: 635px !important;
        bottom: 0;
        right: 30px !important;
        border-top-left-radius: 7px;
        border-top-right-radius: 7px;
        border: 1px solid #e2e2e2;
        box-shadow: 0 0 10px #ccc;
        margin-bottom: -2px;
        z-index: 2000;
    }
</style>