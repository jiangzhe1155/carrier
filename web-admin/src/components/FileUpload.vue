<template>
    <div>
        <uploader ref="uploader" :options="options"
                  @file-added="onFileAdded"
                  @file-success="onFileSuccess"
                  @file-complete="onFileComplete"
                  @file-progress="onFileProgress">
            <uploader-unsupport></uploader-unsupport>
            <uploader-drop>
                <uploader-btn>上传文件</uploader-btn>
                <uploader-btn :directory="true">上传文件夹</uploader-btn>
            </uploader-drop>

            <uploader-files>
                <template slot-scope="props">
                    <uploader-file :file="file" v-for="file in props.files" ref="uploaderFile">
                        <template slot-scope="props">
                            <span>{{props.file.name}}</span>
                            <span>{{props.formatedSize}}</span>
                            {{statusText[getStatus(props)]}}
                            {{props.formatedAverageSpeed}}
                            {{props.progress}}
                            <el-button type="primary" v-show="getStatus(props)==='paused'"
                                       @click="resume(props.file.id)">启动
                            </el-button>
                            <el-button type="primary" v-show="getStatus(props)==='uploading'"
                                       @click="pause(props.file.id)">暂停
                            </el-button>
                            <el-button type="primary" v-show="getStatus(props)==='error'"
                                       @click="retry(props.file.id)">重试
                            </el-button>
                            <el-button type="primary"
                                       v-show="getRemoveStatus(getStatus(props))"
                                       @click="remove(props.file.id)">移除
                            </el-button>
                        </template>
                    </uploader-file>
                </template>
            </uploader-files>
        </uploader>
    </div>


</template>

<script lang="ts">
    import {Component, Prop, Vue} from 'vue-property-decorator';
    import {Message} from "element-ui";
    import SparkMD5 from 'spark-md5';

    @Component
    export default class FileUpload extends Vue {
        @Prop() relativePath: string;

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

        getRemoveStatus(status) {
            return status !== 'skipUpload' && status !== 'success';
        }

        getStatus(props) {
            let skipUpload = props.file.skipUpload;
            let md5 = props.file.md5;
            if (md5) {
                return 'md5'
            } else if (skipUpload) {
                return 'skipUpload'
            }
            return props.status;
        }

        remove(id) {
            let file = this.find(id);
            if (file != null) {
                file.remove();

            }
        }

        pause(id) {
            let file = this.find(id);
            if (file != null) {
                file.pause();
            }
        }

        retry(id) {
            let file = this.find(id);
            if (file != null) {
                file.retry();
            }
        }

        resume(id) {
            let file = this.find(id);
            if (file != null) {
                file.resume();
            }
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
