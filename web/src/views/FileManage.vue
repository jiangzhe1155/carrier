<template>
  <el-table :data="files" stripe @selection-change="handleSelectionChange">
    <el-table-column type="selection"></el-table-column>
    <el-table-column label="文件名">
      <template slot-scope="scope">
        <el-link :icon="getIcon(scope.row)" @click="onClickFileName(scope.row)" v-if="!scope.row.editable">
          {{scope.row.fileName}}
        </el-link>
        <div v-else>
          <el-input clearable v-model="fileNameInput" ref="editInput" size="mini"></el-input>
          <el-button-group>
            <el-button icon="el-icon-check" size="mini" @click="onEditConfirm(scope.row,scope.$index)"></el-button>
            <el-button icon="el-icon-close" size="mini" @click="onClickClose(scope.row,scope.$index)"></el-button>
          </el-button-group>
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="size" label="大小">
      <template slot-scope="scope">{{formatSize(scope.row.size)}}</template>
    </el-table-column>
    <el-table-column prop="updateTime" label="最后修改时间"></el-table-column>
  </el-table>
</template>
<script>
  export default {
    data() {
      return {
        params: {},
        files: [],
        fileSelections: [],
        fileNameInput: ""
      }
    },
    created() {
      this.params = this.$route.query;
      this.getFileList(this.params);
    },
    methods: {
      getFileList(params) {
        this.post("list", params).then(data => {
          this.files = data.data.records;
        });
      },
      handleSelectionChange(val) {
        this.fileSelections = val;
      },
      getIcon(file) {
        if (file.type === 0) {


          console.log(file);
          return "folder";
        }

      }
    },
    watch: {
      '$route': function () {
        this.params = this.$route.query;
      }
    }
  }
</script>

<style lang="less">

</style>
