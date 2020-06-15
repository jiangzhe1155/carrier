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
        <template>
            <el-table :data="fileList" stripe style="width: 100%">
                <el-table-column prop="fileName" label="名称" width="180"></el-table-column>
                <el-table-column prop="fileType" label="类型" width="180"></el-table-column>
                <el-table-column prop="size" label="大小"></el-table-column>
            </el-table>
        </template>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from 'vue-property-decorator';
    import http from '@/api'

    export interface Response<T> {
        data: T;
        code: number;
        msg: string;
    }

    export interface CommonFile {
        fileName: string;
        fileType: string;
        isDir: boolean;
        size: string;
    }

    @Component
    export default class About extends Vue {
        msg: string = 'helloWorld';
        url: string = 'http://127.0.0.1:18080/uploadFile';
        fileList: CommonFile[] = [];
        fileData: object = {
            relativePath: "相对路径"
        };

        beforeCreate(): void {
            http.post<Response<CommonFile[]>>("listFile", {
                relativePath: ""
            }).then(data => {
                this.fileList = data.data;
            });
        }
    }

</script>
