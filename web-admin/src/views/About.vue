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
        <div>{{msg}}</div>
        <div>{{fileList}}</div>
    </div>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from 'vue-property-decorator';
    import http from '@/api'

    function getFileList() {
        http.post("listFile", {
            relativePath: ""
        }).then(data => {
            console.log("asdad")
        });
        return [];
    }

    @Component
    export default class About extends Vue {
        msg: string = 'helloWorld';
        url: string = 'http://127.0.0.1:18080/uploadFile';
        fileList: any[];
        fileData: object = {
            relativePath: "相对路径"
        };

        upload(context) {
            console.log(context)
        }

        beforeCreate(): void {
            this.fileList = getFileList();
        }


    }

</script>
