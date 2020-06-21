<template>
    <div>
        <el-upload
                drag
                action=""
                multiple
                :show-file-list="false"
                :http-request="uploadFile"
                :file-list="fileList"
                :before-upload="beforeUploadFile"
                :on-progress="uploadFileProcess"
                :on-success="handleFileSuccess">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        </el-upload>
        <el-table :data="fileList" stripe style="width: 100%">

            <el-table-column prop="name">
            </el-table-column>
            <el-table-column>
                <template slot-scope="scope">
                    {{formatFileSize(scope.row.size)}}
                </template>
            </el-table-column>
            <el-table-column>
                <template slot-scope="scope">
                    <el-progress :width="40"
                                 :stroke-width="1"
                                 type="circle"
                                 :status="scope.row.status==='success'?'success':null"
                                 :percentage="scope.row.percentage"
                                 :show-text="true"></el-progress>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from 'vue-property-decorator';
    import {Message} from "element-ui";

    @Component
    export default class FileUpload extends Vue {
        @Prop() relativePath: string;
        fileList = [];
        eachSize = 5 * 1024 * 1024;

        beforeUploadFile(file) {
            console.log("beforeUploadFile ", file);
        }

        formatFileSize(fileSize) {
            let temp;
            if (fileSize < 1024) {
                return fileSize + 'B';
            } else if (fileSize < (1024 * 1024)) {
                temp = fileSize / 1024;
                temp = temp.toFixed(2);
                return temp + 'KB';
            } else if (fileSize < (1024 * 1024 * 1024)) {
                temp = fileSize / (1024 * 1024);
                temp = temp.toFixed(2);
                return temp + 'MB';
            } else {
                temp = fileSize / (1024 * 1024 * 1024);
                temp = temp.toFixed(2);
                return temp + 'GB';
            }
        }

        uploadFileProcess(event, file, fileList) {
            this.fileList = fileList;
        }

        handleFileSuccess(res, file, fileList) {
            console.log('handleFileSuccess ', res, file, fileList)
        }

        singleUploadFile(param) {
            const {file, onProgress, onSuccess, onError} = param;
            let data = new FormData();
            data.append("file", file);
            data.append("relativePath", this.relativePath);
            this.http.post("uploadFile", data, false).then((data: R<CommonFile[]>) => {
                Message.success("成功");
                onProgress({percent: 99});
                onSuccess();
            }).catch(() => {
                onError()
            });
        }

        chunkUploadFile(param) {
            const {file, onProgress, onSuccess} = param;
            const {eachSize, http} = this;
            let chunks = Math.ceil(file.size / this.eachSize);
            const httpTasks: any[] = [];
            const fileReaderTask = [];
            let curPro = 0;
            for (let i = 0; i < chunks; i++) {
                fileReaderTask.push(new Promise(function (resolve, reject) {
                    let chunkFile = file.slice(i * eachSize, (i + 1) * eachSize);
                    let fileReader = new FileReader();
                    fileReader.readAsArrayBuffer(chunkFile);
                    fileReader.onload = (e: any) => {
                        resolve(e.target.result)
                    };
                }).then(result => {
                    let params = {
                        chunk: i,
                        chunks,
                        eachSize,
                        fileName: file.name,
                        fullSize: file.size,
                        id: file.uid,
                        relativePath: this.relativePath,
                        file: new window.File([result], file.name, {type: file.type})
                    };
                    let data = new FormData();
                    for (let p in params) {
                        data.append(p, params[p]);
                    }
                    httpTasks.push(http.post("chunkUploadFile", data, false, {
                        onUploadProgress: e => {
                            e.percent = Number(((e.loaded / e.total) * (1 / (chunks || 1)) * 100).toFixed(2));
                            onProgress(e);
                        }
                    }));
                }));
            }
            Promise.all(fileReaderTask).then(
                () => {
                    Promise.all(httpTasks).then(tt => {
                        http.post("mergeUploadFile", {
                            relativePath: this.relativePath,
                            fileName: file.name,
                            id: file.uid,
                            chunks,
                        }, false).then(data => {
                            console.log(data);
                            onProgress({percent: 100});
                            onSuccess();
                            Message.success("校验成功")
                        })
                    }).catch(err => {
                        Message.error("文件上传失败")
                    });
                }
            ).catch(() => {
                Message.error("文件解析异常")
            })
        }

        uploadFile(param) {
            console.log(param);
            const {file, onProgress, onSuccess, onError} = param;
            this.fileList.push(file);
            const uploadFunc = file.size < 5 * 1024 * 1024 ? this.singleUploadFile : this.chunkUploadFile;
            uploadFunc(param)
        }

    }
</script>

<style scoped lang="less">

</style>
