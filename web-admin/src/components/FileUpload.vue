<template>

    <uploader ref="uploader" :options="options" class="uploader-example" :file-status-text="statusText"
              :autoStart="true"
              @file-added="onFileAdded"
              @file-success="onFileSuccess">
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

    @Component
    export default class FileUpload extends Vue {
        @Prop() relativePath ?: string;
        options = {
            target: 'http://127.0.0.1:18080/chunkUploadFile',
            chunkSize: 4 * 1024 * 1024,
            testChunks: false,
            maxChunkRetries: 3,
            allowDuplicateUploads: true,
            simultaneousUploads: 1,
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

        onFileAdded(file) {
            file.relativePath = this.relativePath + '/' + file.relativePath;
            file.uniqueIdentifier = Date.now() + '-' + file.uniqueIdentifier;
        }

        onFileSuccess(rootFile, file, message, chunk) {

            this.http.post("makeDir", {
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
