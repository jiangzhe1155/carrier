<template>

    <uploader ref="uploader" :options="options" class="uploader-example" :file-status-text="statusText"
              :autoStart="true"
              @file-added="onFileAdded"
              @file-success="onFileSuccess"
              @file-progress="onFileProgress"
    >
        <uploader-unsupport></uploader-unsupport>
        <uploader-drop>
            <uploader-btn>上传文件</uploader-btn>
            <uploader-btn :directory="true">上传文件夹</uploader-btn>
        </uploader-drop>
        <uploader-list></uploader-list>
    </uploader>
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
            testChunks: false,
            maxChunkRetries: 3,
            allowDuplicateUploads: true,
            simultaneousUploads: 3,
            checkChunkUploadedByResponse: function (chunk, message) {
                let objMessage = JSON.parse(message);
                console.log(chunk, message)
                return true;
            },
        };
        statusText = {
            success: '成功了',
            error: '出错了',
            uploading: '上传中',
            paused: '暂停中',
            waiting: '等待中'
        };

        get uploader() {
            return this.$refs.uploader.uploader;
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

            file.error = true
            file.pause();
            loadNext();
            fileReader.onload = (e => {
                spark.append(e.target.result);
                if (currentChunk < chunks) {
                    currentChunk++;
                    loadNext();
                } else {
                    let md5 = spark.end();
                    file.md5 = md5;
                    this.http.post("preCreate", {
                        "identifier": file.uniqueIdentifier,
                        "totalSize": file.size,
                        "md5": file.md5
                    }).then(data => {
                        file.storageId = data.data.id;
                    });
                    console.log(`MD5计算完毕：${file.name} \nMD5：${md5} \n分片：${chunks} 大小:${file.size} 用时：${new Date().getTime() - time} ms`);
                    // file.resume()
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
                file.status = "error";
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
