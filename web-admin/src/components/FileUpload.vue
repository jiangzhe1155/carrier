<template>
    <div>
        <uploader ref="uploader" :options="options" class="uploader-example" :file-status-text="statusText"
                  :autoStart="true"
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
                    <ul>
                        <li v-for="file in props.files" :key="file.id" class="liItem">
                            <uploader-file :file="file" :list="true" ref="uploaderFile">
                                <template slot-scope="props">
                                    {{props.file.name}}
                                    {{statusText[getStatus(props)]}}
                                    {{props.formatedSize}}
                                    {{props.formatedAverageSpeed}}
                                    {{props.progress}}
                                    <el-button type="primary">asdas</el-button>
                                    <!--                                    <el-button-group v-show="!props.isComplete">-->
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
                                               @click="retry(props.file.id)">移除
                                    </el-button>
                                    <!--                                    </el-button-group>-->
                                </template>
                            </uploader-file>
                        </li>
                    </ul>
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


        getInfo(props) {
            console.log(JSON.stringify(props));
            return props;
        }

        getRemoveStatus(status) {
            return status !== 'skipUpload' && status !== 'success';
        }

        options = {
            target: 'http://127.0.0.1:18080/chunkUploadFile',
            chunkSize: 4 * 1024 * 1024,
            testChunks: true,
            maxChunkRetries: 3,
            allowDuplicateUploads: true,
            simultaneousUploads: 1,
            checkChunkUploadedByResponse: function (chunk, data) {
                let objMessage = JSON.parse(data);
                const {skipUpload, uploaded, id} = objMessage.data;
                chunk.file.storageId = id;
                if (skipUpload) {
                    chunk.file.skipUpload = true;
                    return true;
                }
                return (uploaded || []).indexOf(chunk.offset + 1) >= 0
            },
            parseTimeRemaining: function (timeRemaining, parsedTimeRemaining) {
                return parsedTimeRemaining
                    .replace(/\syears?/, '年')
                    .replace(/\days?/, '天')
                    .replace(/\shours?/, '小时')
                    .replace(/\sminutes?/, '分钟')
                    .replace(/\sseconds?/, '秒')
            }
        };
        activeNames = '1';

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

        pause(id) {
            let uploaderFile = this.$refs.uploaderFile;
            for (let f of uploaderFile) {
                if (f.file.id === id) {
                    f.pause();
                }
            }
        }

        retry(id) {
            let uploaderFile = this.$refs.uploaderFile;
            console.log(uploaderFile)
            for (let f of uploaderFile) {
                if (f.file.id === id) {
                    f.retry();
                }
            }
        }

        resume(id) {
            let uploaderFile = this.$refs.uploaderFile;
            for (let f of uploaderFile) {
                if (f.file.id === id) {
                    f.resume();
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

        get fileList() {
            return this.$refs.files;
        }

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
                    file.resume();
                    file.md5 = false;
                }
            });

            fileReader.onerror = function () {
                this.error(`文件${file.name}读取出错，请检查该文件`);
                file.cancel();
            };

            function loadNext() {
                let start = currentChunk * chunkSize;
                let end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;
                fileReader.readAsArrayBuffer(blobSlice.call(file.file, start, end));
            }
        }

        onFileSuccess(rootFile, file, message, chunk) {
            let res = JSON.parse(message);
            if (res.code !== 0) {
                Message.error("抱歉失败");
            }
            this.http.post("merge", {
                totalSize: file.size,
                identifier: file.uniqueIdentifier,
                storageId: file.storageId,
                targetPath: this.relativePath,
                filename: file.name,
                relativePath: file.relativePath,
                isDir: false
            }).then((data: R<any>) => {
                if (file.skipUpload) {
                    file.status = 'skipUpload';
                }
            })
        }

        onFileError(rootFile, file, response, chunk) {
            this.$message({
                message: response,
                type: 'error'
            })
        }
    }
</script>

<style scoped lang="less">

</style>
