<template>
    <div>
        <uploader ref="uploader" :options="options"
                  @file-added="onFileAdded"
                  @file-success="onFileSuccess"
                  @file-complete="onFileComplete"
                  @file-progress="onFileProgress">
            <uploader-unsupport></uploader-unsupport>

            <uploader-btn v-show="false" ref="btn1">上传文件</uploader-btn>
            <uploader-btn :directory="true" v-show="false">上传文件夹</uploader-btn>
            {{drawer}}
            <uploader-files ref="aaaa">
                <el-card slot-scope="props"
                         class="file-panel">
                    <el-button-group>
                        <el-button @click="shousuo">收缩</el-button>
                    </el-button-group>
                    <el-table
                            :data="props.files"
                            stripe
                            style="min-height: 400px"
                            v-show="showTable">
                        <el-table-column prop="name" label="名称">
                        </el-table-column>
                        <el-table-column label="状态">
                            <template slot-scope="scope">
                                {{getStatus(scope.row)}}
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button type="primary" v-show="getStatus(scope.row)==='paused'"
                                           @click="resume(scope.row,scope.$index)">启动
                                </el-button>
                                <el-button type="primary" v-show="getStatus(scope.row)==='uploading'"
                                           @click="pause(scope.row,scope.$index)">暂停
                                </el-button>
                                <el-button type="primary" v-show="getStatus(scope.row)==='error'"
                                           @click="retry(scope.row,scope.$index)">重试
                                </el-button>
                                <el-button type="primary"
                                           v-show="getRemoveStatus(getStatus(scope.row))"
                                           @click="remove(scope.row,scope.$index)">移除
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
        @Prop() relativePath: string;
        drawer = true;
        showTable = true;
        files = [];
        options = {
            target: 'http://127.0.0.1:18080/chunkUploadFile',
            chunkSize: 4 * 1024 * 1024,
            testChunks: true,
            maxChunkRetries: 3,
            allowDuplicateUploads: true,
            simultaneousUploads: 1,
            checkChunkUploadedByResponse: function (chunk, data) {
                let objMessage = JSON.parse(data);
                if (objMessage.code != 0) {
                    chunk.file.error = true;
                    chunk.file.pause();
                }

                const {skipUpload, uploaded, id} = objMessage.data;
                chunk.file.storageId = id;
                if (skipUpload) {
                    chunk.file.skipUpload = true;
                    return true;
                }
                return (uploaded || []).indexOf(chunk.offset + 1) >= 0
            }
        };

        shousuo() {
            this.showTable = !this.showTable
        }

        mounted(): void {
            console.log(this.$refs.aaaa);
            this.files = this.$refs.aaaa.files;
        }

        onBeforeClose() {
            this.showTable = false
        }

        getRemoveStatus(status) {
            return status !== 'skipUpload' && status !== 'success';
        }

        getStatus(file) {
            console.log(file);
            let skipUpload = file.skipUpload;
            let md5 = file.md5;
            if (md5) {
                return 'md5'
            } else if (skipUpload) {
                return 'skipUpload'
            } else if (file.completed) {
                return 'success'
            } else if (file.error) {
                return 'error'
            } else if (file.paused) {
                return 'paused'
            } else if (file.isUploading) {
                return 'uploading'
            } else {
                return 'waiting'
            }
        }

        remove(file, idx) {
            file.cancel();
            // Vue.set(this.fileList, idx, file);
        }

        pause(file, idx) {
            file.pause();
            Vue.set(this.$refs.aaaa.files, idx, file);
        }

        retry(file, idx) {
            file.retry();
            Vue.set(this.$refs.aaaa.files, idx, file);
        }

        resume(file, idx) {
            file.resume();
            Vue.set(this.$refs.aaaa.files, idx, file);
        }

        find(id) {
            let uploaderFile = this.$refs.uploaderFile;
            for (let uploader of uploaderFile) {
                if (uploader.file.id === id) {
                    return uploader;
                }
            }
        }

        statusText = {
            md5: '校验MD5',
            success: '成功了',
            error: '出错了',
            uploading: '上传中',
            paused: '暂停中',
            waiting: '等待中',
            skipUpload: '秒传'
        };

        get uploader() {
            return this.$refs.uploader.uploader;
        }

        onFileComplete(file) {

        }

        onFileProgress(rootFile, file, chunk) {
            console.log(`上传中 ${file.name}，chunk：${chunk.startByte / 1024 / 1024} ~ ${chunk.endByte / 1024 / 1024}`)
        }


        onFileAdded(file) {
            this.drawer = true;
            file.relativePath = this.relativePath + "/" + file.relativePath;
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
            file.md5 = true;
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
                    file.md5 = false;
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