<template>

    <uploader
            ref="upload"
            :options="options"
            :autoStart="true"
            @file-added="onFileAdded"
            @file-success="onFileSuccess"
            @file-progress="onFileProgress"
            @file-error="onFileError"
            @upload-start="onUploadStart"
    >
        <uploader-unsupport></uploader-unsupport>
        <uploader-drop>
            <p>Drop files here to upload or</p>
            <uploader-btn>选择文件</uploader-btn>
            <uploader-btn :directory="true">选择文件夹</uploader-btn>
        </uploader-drop>
        <uploader-list></uploader-list>
    </uploader>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from 'vue-property-decorator';
    import {Message} from "element-ui";

    @Component
    export default class FileUpload extends Vue {
        @Prop() relativePath: string;
        fileList = [];
        eachSize = 4 * 1024 * 1024;
        options = {};

        onFileSuccess(rootFile, file, response, chunk) {
            console.log("onFileSuccess ", rootFile, file, response, chunk);
        }

        onUploadStart(rootFile) {
            console.log('onUploadStart', this.$refs.upload);
        }

        onFileProgress(rootFile, file, chunk) {
            console.log('onFileProgress', rootFile, file.chunk)
        }

        onFileError(rootFile, file, response, chunk) {
            console.log('onFileError', rootFile, file, response, chunk);
            console.log('onFileError', error)
        }

        onFileAdded(file) {
            console.log('onFileAdded', file);
        }


    }
</script>

<style scoped lang="less">

</style>
